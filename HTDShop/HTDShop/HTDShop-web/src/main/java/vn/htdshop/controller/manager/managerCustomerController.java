/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.Comparator;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Admin
 */
@Controller
@RequestMapping("manager/customer")
public class managerCustomerController {
    private final String redirectCustomerHome = "redirect:/manager/customer";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // ===Customer Index===\\

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login with role
        if (!managerService.checkLoginWithRole("customer_read")) {
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
                .sorted(Comparator.comparingInt(Customer::getId).reversed()).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "customer");

        return "HTDManager/customer";
    }

    // === User-Detail View ===\\
    @RequestMapping(value = "details", method = RequestMethod.GET)
    public String viewDetails(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!managerService.checkLoginWithRole("customer_read")) {
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

    // === Customer-EDIT VIEW ===\\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!managerService.checkLoginWithRole("customer_edit")) {
            return redirectCustomerHome;
        }

        // Initialize object for checking
        Customer c = null;
        // First, check parameter "id"
        if (id != null) {
            c = customerFacade.find(id);
            if (c == null) {
                return redirectCustomerHome;
            }
        } else {
            return redirectCustomerHome;
        }
        model.addAttribute("customer", c);
        model.addAttribute("formUrl", "doEdit");

        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.customer", model.asMap().get("error"));
        }
        // Indicator for update form
        model.addAttribute("update", "update");
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "customer");
        // Continue to login page
        return "HTDManager/customer_edit";
    }

    // === CUSTOMER-EDIT PROCESS ===\\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("customer") Customer customer, BindingResult error, HttpSession session,
            Integer id, Model model, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("customer_edit")) {
            return redirectCustomerHome;
        }
        Customer custOld = customerFacade.find(id);
        if (!error.hasErrors()) {
            boolean updated = false;
            if (!custOld.getFirstName().equals(customer.getFirstName())) {
                custOld.setFirstName(customer.getFirstName());
                updated = true;
            }
            if (!custOld.getLastName().equals(customer.getLastName())) {
                custOld.setLastName(customer.getLastName());
                updated = true;
            }
            if (!custOld.getAddress().equals(customer.getAddress())) {
                custOld.setAddress(customer.getAddress());
                updated = true;
            }
            if (custOld.getPassword() != null) {
                if (!custOld.getPassword().equals(customer.getPassword())) {
                    if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                        custOld.setPassword(custOld.getPassword());
                    } else {
                        custOld.setPassword(customer.getPassword());
                    }
                    updated = true;
                }
                if (!custOld.getBirthday().equals(customer.getBirthday())){
                    custOld.setBirthday(customer.getBirthday());
                    updated = true;
                }

                if (!custOld.getGender().equals(customer.getGender())) {
                    custOld.setGender(customer.getGender());
                    updated = true;
                }
            }

            
            
            if (!custOld.getPhone().equals(customer.getPhone())) {
                custOld.setPhone(customer.getPhone());
                updated = true;
            }
            if (updated) {
                // Update in database
                customerFacade.edit(custOld);
            }

        }
        model.addAttribute("error", error);
        redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + customer.getId() + "\"!");
        return redirectCustomerHome;
    }

}
