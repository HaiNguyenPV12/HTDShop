/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session) {
        // Check if logged in session is exists 
        if (!checkLogin(session)) {
            // If not, return to login page
            return "redirect:/manager/login";
        }
        // Else, continue to index
        return "HTDManager/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String getLogin(@ModelAttribute("staff") Staff staff, Model model, ModelMap modelMap, HttpSession session) {
        // Check if logged in session is exists
        if (checkLogin(session)) {
            // If yes, redirect to index
            return "redirect:/manager";
        }
        // Prepare staff model if not exists (for postLogin process)
        if (!model.containsAttribute("staff")) {
            model.addAttribute("staff", new Staff());
        }
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.staff", model.asMap().get("error"));
        }
        // Continue to login page
        return "HTDManager/login";
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    // Add @Valid before @ModelAttribute to validate base on entity annotation
    public String postLogin(@ModelAttribute("staff") Staff staff, Model model, BindingResult error,
            RedirectAttributes redirect, HttpSession session) {
        // Mannually check blank username
        if (staff.getUserName().isEmpty()) {
            error.rejectValue("userName", "staff", "Username cannot be blank.");
        }
        // Mannually check blank password
        if (staff.getPassword().isEmpty()) {
            error.rejectValue("password", "staff", "Password cannot be blank.");
        }

        // Check if error exists
        if (!error.hasErrors()) {
            // If not, start to check login
            Staff result = staffFacade.checkLogin(staff.getUserName(), staff.getPassword());
            if (result != null) {
                // If ok, save staff's session
                session.setAttribute("loggedInStaff", result);

                // And save staff's rightlist for fast right-checking
                List<String> rightList = new ArrayList<String>();
                for (RoleRights roleRights : result.getRole().getRoleRightsCollection()) {
                    rightList.add(roleRights.getRightsDetail().getTag());
                }
                session.setAttribute("rightList", rightList);

                // Then redirect to index
                return "redirect:/manager/index";
            }
            // If checking is false, manually add error
            error.rejectValue("userName", "staff", "Invalid Login.");
        }

        // Add error and staff's input info to redirect session
        redirect.addFlashAttribute("error", error);
        redirect.addFlashAttribute("staff", staff);
        // redirect to login page
        return "redirect:/manager/login";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String getLogout(HttpSession session) {
        // remove session
        session.removeAttribute("loggedInStaff");
        // remove rightlist
        session.removeAttribute("loggedInStaff");
        // reidect to login
        return "redirect:/manager/login";
    }

    private Boolean checkLogin(HttpSession session) {
        return session.getAttribute("loggedInStaff") != null;
    }
}
