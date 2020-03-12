/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Calendars;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("manager/revenue")
public class managerRevenueController {
    private final String redirectOrderHome = "redirect:/manager/revenue";
    private final String redirectHome = "redirect:/manager";
    private final String redirectLogin = "redirect:/manager/login";

    

    @EJB(mappedName = "Order1Facade")
    Order1FacadeLocal order1Facade;

    @EJB(mappedName =  "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "OrderDetailFacade")
    OrderDetailFacadeLocal orderDetailFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;


    //===Revenue Index===\\

    @RequestMapping(value = {"","index"},method = RequestMethod.GET)
    public String getHome(final HttpSession session, Model model) {
         // Check login with role
        if (!managerService.checkLoginWithRole("revenue_read")) {
            return redirectHome;
        }
        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass order list to session
        model.asMap().put("managerSv", managerService);
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "revenue");   
        return "HTDManager/revenue";
    }   
    
}
