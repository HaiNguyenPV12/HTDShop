/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/product")
public class managerProductController {
    List<String> rightList = null;

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            return "redirect:/manager/login";
        }
        // Check if staff have appropriate role
        if (!rightList.contains("product_read")) {
            return "redirect:/manager";
        }

        // Pass product list to session
        model.asMap().put("products", productFacade.findAll().stream().sorted(Comparator.comparingInt(Product::getStatus)).collect(Collectors.toList()));

        model.asMap().put("menu", "product");
        return "HTDManager/product";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(HttpSession session, Model model) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If yes, redirect to index
            return "redirect:/mamager/login";
        }
        // Check if staff have appropriate role
        if (!rightList.contains("product_add")) {
            return "redirect:/manager/product";
        }
        // Prepare product model if not exists (prepare for process)
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new Product());
        }
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.product", model.asMap().get("error"));
        }

        model.asMap().put("menu", "product");
        // Continue to login page
        return "HTDManager/product_add";
    }

    private Boolean checkLogin(HttpSession session) {
        if (session.getAttribute("loggedInStaff") != null) {
            if (rightList == null || rightList.size() <= 0) {
                rightList = new ArrayList<>();
                rightList = (ArrayList<String>) session.getAttribute("rightList");
            }
            return true;
        }
        return false;
    }
}
