/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.Calendar;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("manager/order")
public class managerOrderController {
    private final String redirectOrderHome = "redirect:/manager/order";
    private final String redirectHome = "redirect:/manager";

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


    //===Order Index===\\

    @RequestMapping(value = {"","index"},method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model){
         // Check login with role
        if (!managerService.checkLoginWithRole("order_read")) {
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
                .sorted(Comparator.comparingInt(Order1::getId).reversed()).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "order");   

        return "HTDManager/order";
    }

    //===Canceling Order===\\

    @RequestMapping(value = "doCancel", method = RequestMethod.GET)
    public String doCancel(HttpSession session, Model model, @RequestParam(required = true) Integer id)
    {
        if (!managerService.checkLoginWithRole("order_edit")){
            return redirectOrderHome;
        }
        Order1 o = order1Facade.find(id);
        o.setOrderStatus(5);
        o.setCancelledDate(Calendar.getInstance().getTime());
        order1Facade.edit(o);
        return redirectOrderHome;
    }
    
    //==Confirm Order==\\
    @RequestMapping(value = "doConfirmed", method = RequestMethod.GET)
    public String doConfirm(HttpSession session, Model model, @RequestParam(required = true) Integer id)
    {
        if (!managerService.checkLoginWithRole("order_edit")){
            return redirectOrderHome;
        }
        Order1 o = order1Facade.find(id);
        o.setOrderStatus(2);
        order1Facade.edit(o);
        return redirectOrderHome;
    }

    //==Shipping Order==\\
    @RequestMapping(value = "doShipping", method = RequestMethod.GET)
    public String doShip(HttpSession session, Model model, @RequestParam(required = true) Integer id)
    {
        if (!managerService.checkLoginWithRole("order_edit")){
            return redirectOrderHome;
        }
        Order1 o = order1Facade.find(id);
        o.setOrderStatus(3);
        order1Facade.edit(o);
        return redirectOrderHome;
    }

    //==Done Order==\\
    @RequestMapping(value = "doDone", method = RequestMethod.GET)
    public String doDone(HttpSession session, Model model, @RequestParam(required = true) Integer id)
    {
        if (!managerService.checkLoginWithRole("order_edit")){
            return redirectOrderHome;
        }
        Order1 o = order1Facade.find(id);
        o.setOrderStatus(4);
        o.setPaidDate(Calendar.getInstance().getTime());
        order1Facade.edit(o);
        return redirectOrderHome;
    }

    //==Order_Detail==\\

    @RequestMapping(value = "details", method = RequestMethod.GET)
    public String viewDetails(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!managerService.checkLoginWithRole("order_read")) {
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
        model.asMap().put("menu", "order");
        return "HTDManager/order_detail";
    }
}
