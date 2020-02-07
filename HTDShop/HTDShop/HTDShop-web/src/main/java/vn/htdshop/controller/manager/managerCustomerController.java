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

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("manager/customer")
public class managerCustomerController {
    private final String redirectCustomerHome = "redirect:/manager/customer";
    private final String redirectHome = "redirect:/manager";
    private final String redirectLogin = "redirect:/manager/login";

    

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName =  "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "UserFacade")
    UserFacadeLocal userFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;


    //===Customer Index===\\

    @RequestMapping(value = {"","index"},method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model){
         // Check login with role
        if (!checkLoginWithRole("customer_read")) {
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
        model.asMap().put("customers", customerFacade.findAll().stream()
                .sorted(Comparator.comparingInt(Customer::getId)).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "customer");   

        return "HTDManager/customer";
    }   
    
    //=== User-Detail View ===\\
    @RequestMapping(value = "details", method = RequestMethod.GET)
    public String viewDetails(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!checkLoginWithRole("customer_read")) {
            return redirectCustomerHome;
        }
        Customer cust = null;
        if (id != null) {
            cust = customerFacade.find(id);
            if (cust == null) {
                return redirectCustomerHome;
            }
        } else {
            return redirectCustomerHome;
        }
        model.addAttribute("customer", cust);
        model.asMap().put("menu", "customer");
        return "HTDManager/user_detail";
    }

    //=== Customer-EDIT  VIEW ===\\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!checkLoginWithRole("customer_edit")) {
            return redirectCustomerHome;
        }

        // Initialize object for checking
        Customer c = null;
        // First, check parameter "id"
        if (id != null) {
            // In case not null, find product by id and continue
            // to check if product is exists or not
            c = customerFacade.find(id);
            if (c == null) {
                // If not exists, redirect to product index
                return redirectCustomerHome;
            }
        } else {
            // In case id is null, redirect to product index
            return redirectCustomerHome;
        }
        // Continue if everything is ok

        // Prepare product model
        model.addAttribute("customer", c);
        // Prepare form url for form submit
        // model.addAttribute("formUrl", "doEdit" + category);
        model.addAttribute("formUrl", "doEdit");
        // Indicator for update form
        model.addAttribute("update", "update");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.customer", model.asMap().get("error"));
        }
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "customer");
        // Continue to login page
        return "HTDManager/customer_edit";
    }

    private Boolean checkLogin() {
        if (session.getAttribute("loggedInStaff") != null) {
            return true;
        } else {
            String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInStaff"))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (cookie != null) {
                Staff staff = staffFacade.find(cookie);
                if (staff != null) {
                    session.setAttribute("loggedInStaff", staff);
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean checkLoginWithRole(String role) {
        if (checkLogin()) {
            String user = ((Staff) session.getAttribute("loggedInStaff")).getUserName();
            for (RoleRights roleRight : staffFacade.find(user).getRole().getRoleRightsCollection()) {
                if (roleRight.getRightsDetail().getTag().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    
    
}
