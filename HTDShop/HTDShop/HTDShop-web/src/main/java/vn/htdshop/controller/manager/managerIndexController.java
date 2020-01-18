/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        if (!checkLogin(session)) {
            return "redirect:/manager/login";
        }
        return "HTDManager/index";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String getLogin(@ModelAttribute("staff") Staff staff, Model model, ModelMap modelMap) {
        if (model.asMap().get("loggedInStaff") != null) {
            return "redirect:index";
        }
        if (!model.containsAttribute("staff")) {
            model.addAttribute("staff", new Staff());
        }
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.staff", model.asMap().get("error"));
        }
        return "HTDManager/login";
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    public String postLogin(@ModelAttribute("staff") Staff staff, Model model, BindingResult error,
            RedirectAttributes redirect, HttpServletRequest request) {
        if (staff.getUserName().isEmpty()) {
            error.rejectValue("userName", "staff", "Username cannot be blank.");
        }
        if (staff.getPassword().isEmpty()) {
            error.rejectValue("password", "staff", "Password cannot be blank.");
        }

        if (!error.hasErrors()) {
            Staff result = staffFacade.checkLogin(staff.getUserName(), staff.getPassword());
            if (result != null) {
                request.getSession().setAttribute("loggedInStaff", result);
                return "redirect:index";
            }
            error.rejectValue("userName", "staff", "Invalid Login.");
        }

        redirect.addFlashAttribute("error", error);
        redirect.addFlashAttribute("staff", staff);

        return "redirect:/manager/login";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String getLogout(HttpSession session) {
        session.removeAttribute("loggedInStaff");
        return "redirect:/manager/login";
    }
    
    private Boolean checkLogin(HttpSession session){
        return session.getAttribute("loggedInStaff") != null;
    }
}
