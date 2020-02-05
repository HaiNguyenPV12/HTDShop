/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
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

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/promotion")
public class managerPromotionController {

    private final String redirectPromotionHome = "redirect:/manager/promotion";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "PromotionDetailFacade")
    PromotionDetailFacadeLocal promotionDetailFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    // ==== PROMOTION INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!checkLoginWithRole("promotion_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass promotion detail list to session
        model.asMap().put("promotions",
                promotionDetailFacade.findAll().stream()
                        .sorted(Comparator.comparing(PromotionDetail::getIsDisabled, Comparator.naturalOrder()))
                        .collect(Collectors.toList()));

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        return "HTDManager/promotion";
    }

    // ==== PROMOTION ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(Model model) {
        if (!checkLoginWithRole("promotion_add")) {
            return redirectPromotionHome;
        }
        // Prepare model
        PromotionDetail p = new PromotionDetail();

        // Check if old input exists
        model.addAttribute("promotion", p);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doAdd");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.promotion", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        // Continue to login page
        return "HTDManager/promotion_template";
    }

    // ==== PROMOTION ADD - PROCESS ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("promotion") PromotionDetail promotion, BindingResult error,
            HttpSession session, Model model, RedirectAttributes redirect) {
        if (!checkLoginWithRole("promotion_add")) {
            return redirectPromotionHome;
        }
        // Check name exists

        // Check right exists

        // If there is no error
        if (!error.hasErrors()) {
            // Insert into database
            promotionDetailFacade.create(promotion);

            // Insert promotions

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully added \"" + promotion.getName() + "\"!");
            return redirectPromotionHome;
        }
        // Show common error message
        error.reject("common", "Error adding new promotion.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("promotion", promotion);

        // Redirect to add page
        return "redirect:/manager/promotion/add";
    }

    // ==== PROMOTION EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") Integer id) {
        if (!checkLoginWithRole("promotion_edit")) {
            return redirectPromotionHome;
        }
        // Prepare model
        PromotionDetail p = promotionDetailFacade.find(id);
        // Check if old input exists
        model.addAttribute("promotion", p);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doEdit");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.promotion", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Add edit identifier for form
        model.asMap().put("update", "update");
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        // Continue to login page
        return "HTDManager/promotion_template";
    }

    // ==== PROMOTION EDIT - PROCESS ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("promotion") PromotionDetail promotion, BindingResult error,
            HttpSession session, RedirectAttributes redirect) {
        if (!checkLoginWithRole("promotion_edit")) {
            return redirectPromotionHome;
        }

        // Check name exists

        // If there is no error
        if (!error.hasErrors()) {
            // Update in database
            promotionDetailFacade.edit(promotion);

            // Remove promotions

            // Insert promotions

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + promotion.getName() + "\"!");
            return redirectPromotionHome;
        }

        // Show common error message
        error.reject("common", "Error updating this promotion.");
        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("promotion", promotion);

        // Redirect to add page
        return "redirect:/manager/promotion/edit?id=" + promotion.getId();
    }

    // ==== PROMOTION DELETE - PROCESS ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    public String doDelete(HttpSession session, Model model, @RequestParam(required = true) Integer id,
            RedirectAttributes redirect) {
        if (!checkLoginWithRole("promotion_delete")) {
            return redirectPromotionHome;
        }

        // Initialize object
        PromotionDetail promotion = promotionDetailFacade.find(id);
        // If object is not null
        if (promotion != null) {
            // Then check if this exists in staff

            // If not, then start to delete
            // First, delete promotion
            for (Promotion p : promotion.getPromotionCollection()) {
                promotionFacade.remove(p);
            }

            // Finally, delete promotion detail
            promotionDetailFacade.remove(promotion);

            // And pass alert attribute
            redirect.addFlashAttribute("goodAlert", "Successfully deleted \"" + promotion.getName() + "\"!");

        } else {
            // In case product is not found
            redirect.addFlashAttribute("badAlert", "This id is not exists!");
        }

        return redirectPromotionHome;
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
