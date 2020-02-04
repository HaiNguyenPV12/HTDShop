package vn.htdshop.controller;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @RequestMapping(value = "error", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest, HttpSession session) {

        ModelAndView errorPage = new ModelAndView("error");
        String cookie = Arrays.stream(httpRequest.getCookies()).filter(c -> c.getName().equals("loggedInStaff"))
                .findFirst().map(Cookie::getValue).orElse(null);
        if (cookie != null || session.getAttribute("loggedInStaff") != null) {
            errorPage.addObject("hasStaff", "hasStaff");
        }
        String errorMsg = "";
        int httpErrorCode = getErrorCode(httpRequest);

        switch (httpErrorCode) {
        case 400: {
            errorMsg = "Http Error Code: 400. Bad Request";
            break;
        }
        case 401: {
            errorMsg = "Http Error Code: 401. Unauthorized";
            break;
        }
        case 404: {
            errorMsg = "Http Error Code: 404. Resource not found";
            break;
        }
        case 500: {
            errorMsg = "Http Error Code: 500. Internal Server Error";
            break;
        }
        }
        errorPage.addObject("errorCode", httpRequest.getAttribute("javax.servlet.error.status_code"));
        errorPage.addObject("errorMsg", httpRequest.getAttribute("javax.servlet.error.message"));
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
    }
}