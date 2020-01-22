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
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    // ==== PRODUCT INDEX ==== \\
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
        model.asMap().put("products", productFacade.findAll().stream()
                .sorted(Comparator.comparingInt(Product::getStatus)).collect(Collectors.toList()));

        model.asMap().put("menu", "product");
        return "HTDManager/product";
    }

    // ==== PRODUCT ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    // Adding optional "cate" parameter by using @RequestParam(required = false)
    public String viewAdd(HttpSession session, Model model, @RequestParam(required = false) Integer cate) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return "redirect:/manager/login";
        }
        // Check if staff have appropriate role
        if (!rightList.contains("product_add")) {
            // If not, redirect to product index
            return "redirect:/manager/product";
        }
        // Prepare product model if not exists (prepare for process)
        if (!model.containsAttribute("cpu")) {
            model.addAttribute("cpu", new Product());
        }
        String category = "";

        if (cate != null) {
            switch (cate) {
            case 2:
                category = "cpu";
                model.addAttribute("product", new CPU());
                break;
            case 8:
                category = "cpucooler";
                model.addAttribute("cpucooler", new CPU());
                break;
            default:
                break;
            }
        }

        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult." + category, model.asMap().get("error"));
        }

        // Pass category list to view
        model.asMap().put("categories", categoryFacade.findAll());

        model.asMap().put("menu", "product");
        // Continue to login page
        return "HTDManager/product_add";
    }

    // ==== PRODUCT ADD - PROCESS - CPU ==== \\
    @RequestMapping(value = "doAddCPU", method = RequestMethod.POST)
    // Adding optional "cate" parameter by using @RequestParam(required = false)
    public String doAddCPU(@Valid @ModelAttribute("cpu") CPU cpu, BindingResult error, HttpSession session, Model model, @RequestParam(required = false) Integer cate, RedirectAttributes redirect) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return "redirect:/manager/login";
        }
        // Check if staff have appropriate role
        if (!rightList.contains("product_add")) {
            // If not, redirect to product index
            return "redirect:/manager/product";
        }
        // If there is no error
        if (!error.hasErrors()){
            // Custom method that create Product object from CPU class
            Product p = cpu.toNewProduct();
            productFacade.create(p);
            return "redirect:/manager/product";
        }
        // Show common error message
        error.reject("common","Error adding new product");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("cpu", cpu);
        System.out.println(error);
        model.asMap().put("menu", "product");
        // Redirect to add page
        return "redirect:/manager/product/add?cate=" + cate;
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
