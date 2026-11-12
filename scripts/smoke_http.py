"""End-to-end HTTP/JSP checks for the local H2 demo; Python standard library only.

Run while the app is running: python scripts/smoke_http.py [http://localhost:8080]
Creates one synthetic reader with a random username in the demo database.
"""
import html
import re
import sys
import uuid
from http.cookiejar import CookieJar
from urllib.error import HTTPError
from urllib.parse import urlencode, urlparse
from urllib.request import HTTPCookieProcessor, Request, build_opener

base = (sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:8080").rstrip("/")
client = build_opener(HTTPCookieProcessor(CookieJar()))
checks = 0


def fetch(path, data=None):
    request = Request(base + path, data=None if data is None else urlencode(data).encode("utf-8"))
    try:
        response = client.open(request, timeout=30)
    except HTTPError as error:
        response = error
    with response:
        return response.code, response.read().decode("utf-8"), response.geturl(), response.headers


def check(condition, label):
    global checks
    if not condition:
        raise AssertionError(label)
    checks += 1
    print("PASS:", label, flush=True)


def csrf(body):
    return re.search(r'name="csrf" value="([^"]+)"', body).group(1)


def rows(body):
    tbody = re.search(r"<tbody>(.*?)</tbody>", body, re.S)
    if not tbody:
        return []
    return [[html.unescape(re.sub(r"<[^>]+>", " ", cell)).strip()
             for cell in re.findall(r"<td[^>]*>(.*?)</td>", row, re.S)]
            for row in re.findall(r"<tr>(.*?)</tr>", tbody.group(1), re.S)]


status, body, _, demo_headers = fetch("/LoginUI.jsp")
check(status == 200 and demo_headers.get("X-Library-Demo") == "true", "Demo guard: refuse to mutate a non-demo database")
status, body, _, headers = fetch("/MainCustomerUI.jsp")
check(status == 200 and "DỊCH VỤ THƯ VIỆN" in body and "HÃ" not in body, "Home renders Vietnamese correctly")
check("default-src 'self'" in headers.get("Content-Security-Policy", ""), "Security headers")
status, body, _, headers = fetch("/assets/app.css")
check(status == 200 and ".registration-layout" in body and "text/css" in headers.get("Content-Type", ""), "CSS is served")
for path in ("MainManagerUI.jsp", "SelectDocStatUI.jsp", "DocumentBorrowingStatUI.jsp", "BorrowingReceiptDetailUI.jsp", "BorrowingReceiptUI.jsp"):
    status, body, url, _ = fetch("/" + path)
    check(url.endswith("/LoginUI.jsp"), "Anonymous access blocked: " + path)

status, body, _, _ = fetch("/RegisterCardUI.jsp")
check(status == 200 and "Thông tin đăng kí" in body, "Registration form renders")
token = csrf(body)
username = "smoke_" + uuid.uuid4().hex[:12]
form = dict(userName=username, passWord="Smoke@12345", confirmPass="Smoke@12345",
            fullName="Bạn đọc kiểm thử <script>alert(1)</script>", dayOfBirth="2005-11-11",
            email="smoke@example.com", phone="0987654321", csrf=token)
status, body, _, _ = fetch("/doRegisterCard.jsp", dict(form, csrf="invalid"))
check(status == 403, "CSRF blocks invalid token")
status, body, _, _ = fetch("/doRegisterCard.jsp", dict(form, confirmPass="different"))
check(status == 400 and "Mật khẩu xác nhận không khớp" in body and "Smoke@12345" not in body, "Invalid confirmation, passwords never re-displayed")
status, body, _, _ = fetch("/doRegisterCard.jsp", form)
check(status == 200 and "Đăng kí thẻ thành công!" in body and username in body, "Registration creates account and active card")
check("&lt;script&gt;" in body and "<script>" not in body, "User-provided name is HTML escaped")
check("Hoạt động" in body and "Ngày hết hạn" in body, "Issued card displays validity and status")
status, body, _, _ = fetch("/RegisterCardUI.jsp")
check(status == 200 and "Thông tin đăng kí" in body and "Đăng kí thẻ thành công!" not in body, "Refreshing success does not repeat registration")
status, body, _, _ = fetch("/doRegisterCard.jsp", form)
check(status == 400 and "Tên đăng nhập đã tồn tại" in body, "Duplicate username handled")

status, body, _, _ = fetch("/LoginUI.jsp")
token = csrf(body)
status, body, _, _ = fetch("/LoginUI.jsp", dict(userName="bandoc_demo", passWord="Reader@123", csrf=token))
check(status == 401, "Reader cannot log in as manager")
status, body, _, _ = fetch("/LoginUI.jsp", dict(userName="manager", passWord="wrong", csrf=token))
check(status == 401, "Wrong password rejected")
status, body, url, _ = fetch("/LoginUI.jsp", dict(userName="manager", passWord="Manager@123", csrf=token))
check(status == 200 and url.endswith("MainManagerUI.jsp") and "Không gian quản lý" in body, "Manager login")
_, login_body, _, _ = fetch("/LoginUI.jsp")
manager_token = csrf(login_body)
status, body, _, _ = fetch("/SelectDocStatUI.jsp")
check(status == 200 and "Khoảng thời gian thống kê" in body, "Date selection page")
query = "startDate=2026-08-11&endDate=2026-09-11"
status, body, _, _ = fetch("/DocumentBorrowingStatUI.jsp?" + query)
report_rows = rows(body)
check(status == 200 and len(report_rows) >= 4 and [int(row[3]) for row in report_rows] == sorted([int(row[3]) for row in report_rows], reverse=True), "Report totals and descending order")
check( "11/08/2026" in body and "11/09/2026" in body, "Total count and inclusive interval displayed")
status, body, _, _ = fetch("/BorrowingReceiptDetailUI.jsp?documentId=1&" + query)
detail_rows = rows(body)
check(status == 200 and len(detail_rows) == next(int(row[3]) for row in report_rows if row[1] == "TL001") and "BD125" in body, "Details match report borrow count and reader code")
check("PM001" in detail_rows[0][1] and "PM001" in detail_rows[1][1], "Two copies on one receipt remain separate detail rows")
check("startDate=2026-08-11" in body and "borrowingReceiptId=1" in body, "Drill-down preserves date context and receipt ID")
status, body, _, _ = fetch("/BorrowingReceiptUI.jsp?borrowingReceiptId=1&documentId=1&" + query)
check(status == 200 and body.count('class="receipt-list document-fields"') == 5 and "Khuất Văn Nam" in body and "Ngày trả thực tế" in body and "Không Gia Đình" in body, "Complete receipt includes all titles, reader and librarian")
status, body, _, _ = fetch("/BorrowingReceiptUI.jsp?borrowingReceiptId=999999")
check(status == 404 and "Không tìm thấy phiếu mượn" in body, "Missing receipt")
status, body, _, _ = fetch("/BorrowingReceiptDetailUI.jsp?documentId=999999&" + query)
check(status == 404 and "Không tìm thấy tài liệu" in body, "Missing document")
status, body, _, _ = fetch("/BorrowingReceiptUI.jsp?borrowingReceiptId=bad")
check(status == 400, "Malformed ID")
status, body, _, _ = fetch("/DocumentBorrowingStatUI.jsp?startDate=2026-09-12&endDate=2026-09-11")
check(status == 400 and "ngày bắt đầu phải trước" in body, "Reversed dates")
status, body, _, _ = fetch("/DocumentBorrowingStatUI.jsp?startDate=2026-02-30&endDate=2026-09-11")
check(status == 400, "Invalid calendar date")
status, body, _, _ = fetch("/DocumentBorrowingStatUI.jsp?startDate=2027-01-01&endDate=2027-02-01")
check(status == 200 and "Không có dữ liệu thống kê phù hợp" in body and not rows(body), "Empty report")
status, body, _, _ = fetch("/BorrowingReceiptDetailUI.jsp?documentId=5&" + query)
check(status == 200 and "Không có lượt mượn" in body, "Empty document details")
status, body, url, _ = fetch("/logout", dict(csrf=manager_token))
check(status == 200 and url.endswith("MainCustomerUI.jsp"), "Logout returns home")
status, body, url, _ = fetch("/BorrowingReceiptUI.jsp?borrowingReceiptId=1")
check(url.endswith("LoginUI.jsp"), "Logout revokes protected access")
print(f"\n{checks} HTTP checks passed. Synthetic reader: {username}")
