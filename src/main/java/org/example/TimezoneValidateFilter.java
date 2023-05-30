package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.isNull;


@WebFilter(value = "/time/*")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String utc = "UTC";
        String timezone = req.getParameter("timezone");
        if( isNull(timezone) || timezone.contains(utc)){
            chain.doFilter(req,res);
        } else {
            res.setStatus(404);
            res.getWriter().write("Invalid timezone");
            res.getWriter().close();
        }
    }
}
