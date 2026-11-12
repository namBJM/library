package vn.ptit.libman.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.util.*;
import vn.ptit.libman.config.Database7;
import vn.ptit.libman.dao.*;
import vn.ptit.libman.model.*;
import vn.ptit.libman.service.*;

/** Controllers use the URL/page names from the diagrams; JSPs only render views. */
public final class LibraryServlet7 extends HttpServlet {
    private Database7 database() { return (Database7)getServletContext().getAttribute("database"); }
    private Clock clock() { return (Clock)getServletContext().getAttribute("clock"); }
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        try {
            switch(req.getServletPath()) {
                case "/", "/MainCustomerUI.jsp" -> view(req,res,"MainCustomerUI");
                case "/RegisterCardUI.jsp" -> {
                    req.setAttribute("today",LocalDate.now(clock()));
                    req.setAttribute("registrationResult",req.getSession().getAttribute("registrationResult"));
                    req.getSession().removeAttribute("registrationResult"); view(req,res,"RegisterCardUI");
                }
                case "/LoginUI.jsp" -> view(req,res,"LoginUI");
                case "/MainManagerUI.jsp" -> view(req,res,"MainManagerUI");
                case "/SelectDocStatUI.jsp" -> view(req,res,"SelectDocStatUI");
                case "/DocumentBorrowingStatUI.jsp" -> statistics(req,res);
                case "/BorrowingReceiptDetailUI.jsp" -> details(req,res);
                case "/BorrowingReceiptUI.jsp" -> receipt(req,res);
                default -> res.sendError(405);
            }
        } catch (ValidationException7 ex) {
            res.setStatus(400); req.setAttribute("errors",ex.getErrors()); view(req,res,"SelectDocStatUI");
        } catch (IllegalArgumentException ex) { error(req,res,400,"Tham số không hợp lệ. Vui lòng quay lại và chọn từ danh sách."); }
        catch (SQLException ex) { getServletContext().log("Read failed",ex); error(req,res,503,"Chưa thể tải dữ liệu. Vui lòng thử lại sau."); }
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        switch(req.getServletPath()) {
            case "/doRegisterCard.jsp" -> register(req,res);
            case "/LoginUI.jsp" -> login(req,res);
            case "/logout" -> { req.getSession().invalidate(); res.sendRedirect(req.getContextPath()+"/MainCustomerUI.jsp"); }
            default -> res.sendError(405);
        }
    }
    private void register(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        RegistrationForm7 form=new RegistrationForm7(req.getParameter("userName"),req.getParameter("passWord"),req.getParameter("confirmPass"),req.getParameter("fullName"),req.getParameter("dayOfBirth"),req.getParameter("email"),req.getParameter("phone"));
        try {
            RegistrationService7 service=(RegistrationService7)getServletContext().getAttribute("registrationService");
            ReaderCard7 card=service.register(form);
            req.getSession().setAttribute("registrationResult",card);
            // Post/Redirect/Get: refreshing the success screen does not submit again.
            res.sendRedirect(req.getContextPath()+"/RegisterCardUI.jsp"); return;
        } catch (ValidationException7 ex) { res.setStatus(400); req.setAttribute("errors",ex.getErrors()); }
        catch (SQLException ex) {
            getServletContext().log("Registration failed",ex); res.setStatus(503);
            req.setAttribute("errors",Map.of("global","Đăng kí thất bại. Vui lòng thử lại sau."));
        }
        req.setAttribute("values",form.publicValues()); req.setAttribute("today",LocalDate.now(clock())); view(req,res,"RegisterCardUI");
    }
    private void login(HttpServletRequest req,HttpServletResponse res) throws IOException,ServletException {
        String user=Optional.ofNullable(req.getParameter("userName")).orElse("").strip().toLowerCase(Locale.ROOT);
        String password=req.getParameter("passWord");
        if (user.length()>50 || password==null || password.length()>128) { loginError(req,res); return; }
        try(Connection con=database().open()) {
            Manager7 manager=new MemberDAO7(con).authenticateManager(user,password);
            if (manager==null) { loginError(req,res); return; }
            req.changeSessionId(); req.getSession().setAttribute("manager",manager);
            req.getSession().setAttribute("csrf",UUID.randomUUID().toString());
            res.sendRedirect(req.getContextPath()+"/MainManagerUI.jsp");
        } catch(SQLException ex) { getServletContext().log("Login failed",ex); error(req,res,503,"Chưa thể đăng nhập. Vui lòng thử lại sau."); }
    }
    private void loginError(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException {
        res.setStatus(401); req.setAttribute("loginError","Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản không có quyền quản lý."); view(req,res,"LoginUI");
    }
    private DateRange7 range(HttpServletRequest req) throws ValidationException7 {
        DateRange7 range=DateRange7.parse(req.getParameter("startDate"),req.getParameter("endDate"));
        req.setAttribute("startDate",range.startDate()); req.setAttribute("endDate",range.endDate()); return range;
    }
    private void statistics(HttpServletRequest req,HttpServletResponse res) throws SQLException,ValidationException7,ServletException,IOException {
        DateRange7 range=range(req);
        try(Connection con=database().open()) {
            var stats=new DocumentBorrowingStatDAO7(con).getDocumentBorrowingStat(range.startDate(),range.endDate());
            req.setAttribute("listStat",stats); req.setAttribute("totalBorrows",stats.stream().mapToInt(DocumentBorrowingStat7::getBorrowQuantity).sum());
        }
        view(req,res,"DocumentBorrowingStatUI");
    }
    private void details(HttpServletRequest req,HttpServletResponse res) throws SQLException,ValidationException7,ServletException,IOException {
        DateRange7 range=range(req); int id=positiveId(req,"documentId");
        try(Connection con=database().open()) {
            Document7 document=new DocumentBorrowingStatDAO7(con).getDocument(id);
            if (document==null) { error(req,res,404,"Không tìm thấy tài liệu."); return; }
            req.setAttribute("document",document);
            req.setAttribute("listBorrowing",new BorrowingReceiptDetailDAO7(con).getDocumentBorrowingDetail(id,range.startDate(),range.endDate()));
        }
        view(req,res,"BorrowingReceiptDetailUI");
    }
    private void receipt(HttpServletRequest req,HttpServletResponse res) throws SQLException,ServletException,IOException,ValidationException7 {
        int id=positiveId(req,"borrowingReceiptId");
        // Optional report context is validated before constructing a back link.
        if (req.getParameter("startDate")!=null || req.getParameter("endDate")!=null) {
            range(req); req.setAttribute("documentId",positiveId(req,"documentId"));
        }
        try(Connection con=database().open()) {
            BorrowingReceipt7 receipt=new BorrowingReceiptDAO7(con).getBorrowingReceipt(id);
            if (receipt==null) { error(req,res,404,"Không tìm thấy phiếu mượn."); return; }
            req.setAttribute("receipt",receipt);
        }
        view(req,res,"BorrowingReceiptUI");
    }
    private static int positiveId(HttpServletRequest req,String field) {
        int value=Integer.parseInt(req.getParameter(field)); if (value<=0) throw new IllegalArgumentException(field); return value;
    }
    private void error(HttpServletRequest req,HttpServletResponse res,int status,String message) throws ServletException,IOException {
        res.setStatus(status); req.setAttribute("message",message); view(req,res,"ErrorUI");
    }
    private void view(HttpServletRequest req,HttpServletResponse res,String name) throws ServletException,IOException {
        req.getRequestDispatcher("/WEB-INF/views/"+name+".jsp").forward(req,res);
    }
}
