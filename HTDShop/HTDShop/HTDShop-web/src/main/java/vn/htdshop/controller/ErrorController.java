package vn.htdshop.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import vn.htdshop.utility.ManagerService;

@Controller
public class ErrorController {

    @Autowired
    private ManagerService managerService;

    @RequestMapping(value = "error", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest, HttpSession session) {

        ModelAndView errorPage = new ModelAndView("error");

        if (managerService.checkLogin()) {
            errorPage.addObject("hasStaff", "hasStaff");
        }

        // switch (httpErrorCode) {
        // case 400: {
        //     errorMsg = "Http Error Code: 400. Bad Request";
        //     break;
        // }
        // case 401: {
        //     errorMsg = "Http Error Code: 401. Unauthorized";
        //     break;
        // }
        // case 404: {
        //     errorMsg = "Http Error Code: 404. Resource not found";
        //     break;
        // }
        // case 500: {
        //     errorMsg = "Http Error Code: 500. Internal Server Error";
        //     break;
        // }
        // }
        errorPage.addObject("errorCode", httpRequest.getAttribute("javax.servlet.error.status_code"));
        errorPage.addObject("errorMsg", httpRequest.getAttribute("javax.servlet.error.message"));
        return errorPage;
    }
}
