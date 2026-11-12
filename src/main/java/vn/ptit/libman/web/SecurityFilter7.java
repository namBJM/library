package vn.ptit.libman.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import vn.ptit.libman.model.Manager7;

public final class SecurityFilter7 implements Filter {
    private static final Set<String> MANAGER_PAGES=Set.of("/MainManagerUI.jsp","/SelectDocStatUI.jsp","/DocumentBorrowingStatUI.jsp","/BorrowingReceiptDetailUI.jsp","/BorrowingReceiptUI.jsp");
    @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException,ServletException {
        HttpServletRequest req=(HttpServletRequest)request; HttpServletResponse res=(HttpServletResponse)response;
        req.setCharacterEncoding("UTF-8"); res.setCharacterEncoding("UTF-8");
        res.setHeader("X-Content-Type-Options","nosniff"); res.setHeader("X-Frame-Options","DENY");
        res.setHeader("Referrer-Policy","same-origin");
        res.setHeader("Content-Security-Policy","default-src 'self'; style-src 'self'; img-src 'self'; script-src 'none'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'");
        if (req.getServletPath().startsWith("/assets/")) { chain.doFilter(req,res); return; }
        res.setHeader("Cache-Control","no-store");
        res.setHeader("X-Library-Demo",String.valueOf(req.getServletContext().getAttribute("demoMode")));
        HttpSession session=req.getSession();
        if (session.getAttribute("csrf")==null) session.setAttribute("csrf",UUID.randomUUID().toString());
        if (MANAGER_PAGES.contains(req.getServletPath()) && !(session.getAttribute("manager") instanceof Manager7)) {
            res.sendRedirect(req.getContextPath()+"/LoginUI.jsp"); return;
        }
        if ("POST".equals(req.getMethod()) && !session.getAttribute("csrf").equals(req.getParameter("csrf"))) {
            res.setStatus(403); req.setAttribute("message","Phiên biểu mẫu đã hết hạn. Vui lòng mở lại trang và thử lại.");
            req.getRequestDispatcher("/WEB-INF/views/ErrorUI.jsp").forward(req,res); return;
        }
        chain.doFilter(req,res);
    }
}
