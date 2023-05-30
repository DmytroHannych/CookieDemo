package org.example;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;

import static java.util.Objects.isNull;


@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getPrefix());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timeZone = getTimeZoneFromRequest(req);
        resp.addCookie(new Cookie("lastTimezone",  req.getParameter("timezone")));
        TimeZone utc = TimeZone.getTimeZone(timeZone);
        String currencyTime = LocalDateTime.now(utc.toZoneId())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss ")) + timeZone;
        resp.setContentType("text/html; charset=utf-8");
        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("currencyTime", currencyTime)
        );
        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private String getTimeZoneFromRequest(HttpServletRequest req) {
        String cookies = req.getHeader("lastTimezone");
        String timezone = req.getParameter("timezone");
        if(isNull(timezone)){
            if(!isNull(cookies)){
                return cookies;
            } else {
                return "UTC";
            }
        } else{
            return timezone;
        }
    }

    private String getPrefix()  {
        try {
            URI uri = getClass().getClassLoader().getResource("templates").toURI();
            return Paths.get(uri).toFile().getAbsolutePath() + "/";
        } catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }
}

