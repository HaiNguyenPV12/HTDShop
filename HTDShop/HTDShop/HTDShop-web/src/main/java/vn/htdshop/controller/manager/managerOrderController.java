/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Arrays;
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

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("manager/order")
public class managerOrderController {
    private final String redirectOrderHome = "redirect:/manager/order";
    private final String redirectHome = "redirect:/manager";
    private final String redirectLogin = "redirect:/manager/login";

    List<String> rightsList = null;

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


    //===Order Index===\\

    @RequestMapping(value = {"","index"},method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model){
         // Check login with role
        if (!checkLoginWithRole("order_read")) {
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
        model.asMap().put("orders", order1Facade.findAll().stream()
                .sorted(Comparator.comparingInt(Order1::getOrderStatus)).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "order");   

        return "HTDManager/order";
    }

    //===Canceling Order===\\

    @RequestMapping(value = "doCancel", method = RequestMethod.GET)
    public String doCancel(HttpSession session, Model model, @RequestParam(required = true) Integer id)
    {
        if (!checkLoginWithRole("order_cancel")){
            return redirectOrderHome;
        }
        Order1 o = order1Facade.find(id);
        o.setOrderStatus(3);
        order1Facade.edit(o);
        return redirectOrderHome;
    }

    //==Order_Detail==\\

    @RequestMapping(value = "details", method = RequestMethod.GET)
    public String viewDetails(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!checkLoginWithRole("order_details")) {
            return redirectOrderHome;
        }
        Order1 o = null;
        if (id != null) {
            o = order1Facade.find(id);
            if (o == null) {
                return redirectOrderHome;
            }
        } else {
            return redirectOrderHome;
        }
        model.addAttribute("order", o);
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.product", model.asMap().get("error"));
        }
        model.asMap().put("menu", "order");
        return "HTDManager/order_detail";
    }

    private Boolean checkLogin() {
        if (session.getAttribute("loggedInStaff") != null) {
            if (rightsList == null || rightsList.size() <= 0) {
                rightsList = new ArrayList<>();
                rightsList = (ArrayList<String>) session.getAttribute("rightsList");
            }
            return true;
        } else {
            String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInStaff"))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (cookie != null) {
                Staff staff = staffFacade.find(cookie);
                if (staff != null) {
                    session.setAttribute("loggedInStaff", staff);
                    List<String> rightsList = new ArrayList<String>();
                    for (RoleRights roleRights : staff.getRole().getRoleRightsCollection()) {
                        rightsList.add(roleRights.getRightsDetail().getTag());
                    }
                    this.rightsList = rightsList;
                    session.setAttribute("rightsList", rightsList);
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean checkLoginWithRole(String role) {
        if (checkLogin() && rightsList.contains(role)) {
            return true;
        }
        return false;
    }

    
    
}
