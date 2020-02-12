/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
@RequestMapping("manager")
public class managerIndexController {

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model, HttpServletRequest request) {
        // Check if logged in session is exists
        if (!managerService.checkLogin()) {
            // If not, return to login page
            return "redirect:/manager/login";
        }
        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            System.out.println(model.asMap().get("goodAlert"));
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }

        // Else, continue to index
        return "HTDManager/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String getLogin(@ModelAttribute("staff") Staff staff, Model model, ModelMap modelMap, HttpSession session) {
        // Check if logged in session is exists
        if (managerService.checkLogin()) {
            // If yes, redirect to index
            return "redirect:/manager";
        }
        // Prepare staff model if not exists (for postLogin process)
        model.addAttribute("staff", new Staff());
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            // Here is using for staff attribute that is declared, for other attribute,
            // please change
            // "org.springframework.validation.BindingResult.staff" to
            // "org.springframework.validation.BindingResult.your_attribute's_name_here"
            model.addAttribute("org.springframework.validation.BindingResult.staff", model.asMap().get("error"));
        }
        // Continue to login page
        return "HTDManager/login";
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    // Add @Valid before @ModelAttribute to validate base on entity annotation
    // For example: public String postLogin(@Valid @ModelAttribute("staff") Staff
    // staff...){}
    // Here we just have to check username and password, not all so we check
    // manually
    public String postLogin(@ModelAttribute("staff") Staff staff,
            @RequestParam(value = "remember", required = false) String remember, Model model, BindingResult error,
            RedirectAttributes redirect, HttpSession session, HttpServletResponse response) {
        // Mannually check blank username
        if (staff.getUsername() == null || staff.getUsername().isEmpty()) {
            error.rejectValue("username", "staff", "Username cannot be blank.");
        }
        // Mannually check blank password
        if (staff.getPassword().isEmpty()) {
            error.rejectValue("password", "staff", "Password cannot be blank.");
        }

        // Check if error exists
        if (!error.hasErrors()) {
            // If not, start to check login
            Staff result = staffFacade.checkLogin(staff.getUsername(), staff.getPassword());
            if (result != null) {
                // If ok, save staff's session
                session.setAttribute("loggedInStaff", result);
                if (remember != null) {
                    Cookie cookie = new Cookie("loggedInStaff", staff.getUsername());
                    response.addCookie(cookie);
                }

                redirect.addFlashAttribute("goodAlert", "Successfully logged in as \"" + result.getFirstName() + "\".");
                // Then redirect to index
                return "redirect:/manager/index";
            }
            // If checking is false, manually add error
            error.rejectValue("username", "staff", "Invalid Login.");
        }

        // Add error and staff's input info to redirect session
        redirect.addFlashAttribute("error", error);
        redirect.addFlashAttribute("staff", staff);
        // redirect to login page
        return "redirect:/manager/login";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String getLogout(HttpSession session, HttpServletResponse response) {
        // remove session
        session.removeAttribute("loggedInStaff");
        // remove cookie
        Cookie cookie = new Cookie("loggedInStaff", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        // redirect to login
        return "redirect:/manager/login";
    }
}
