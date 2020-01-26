/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
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
    //String imageValue = "iVBORw0KGgoAAAANSUhEUgAAAB8AAAAiCAIAAAAoKJUdAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEE0AABBNAWeMAeAAAADmSURBVEhL7Y/bDoMwDEP5/59mbex6Ib2ygbRJnAcSu7WBbb+Tp73H097jt9u3I3QLS+3NJAh+lJxDkMlvMGCCIBPeOdEOBu1ph3wv5k/AVeGldtYFCTHAd4lkAu3y/dJIBhQI5MpL2pUBcACkN+1KkRAD6mty/DPAI4geSqbFAzMRpIC52g6sKkP9TbuPWYkFylPI9NCHaGJZQqsq0qn3tR+uTvEVQNXAm3nBWEQxMXYubo+Sc43Jl9annGvU+YTMG9vbR5zLND6wQO34pD3Ag3JEYZxun3J4H+el4CcyNO7hf9v3/QWDAr3PbDmp4gAAAABJRU5ErkJggg==";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @Autowired
    ServletContext context;

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model, HttpServletRequest request) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, return to login page
            return "redirect:/manager/login";
        }
        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            System.out.println(model.asMap().get("goodAlert"));
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }

        /*
        try {
            byte[] imageByte = Base64.getDecoder().decode(imageValue);

            String directory = context.getRealPath("/") + "HTDManager/img/sample.jpg";
    
            FileOutputStream fos = new FileOutputStream(directory);
            fos.write(imageByte);
            fos.close();
            System.out.println("saved image.");
        } catch (Exception e) {
            e.printStackTrace();
        }
       */

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

                // And save staff's rightsList for fast right-checking
                List<String> rightsList = new ArrayList<String>();
                for (RoleRights roleRights : result.getRole().getRoleRightsCollection()) {
                    rightsList.add(roleRights.getRightsDetail().getTag());
                }
                session.setAttribute("rightsList", rightsList);
                redirect.addFlashAttribute("goodAlert", "Successfully logged in as \"" + result.getFirstName() + "\".");
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
        // remove rightsList
        session.removeAttribute("rightsList");
        // reidect to login
        return "redirect:/manager/login";
    }

    private Boolean checkLogin(HttpSession session) {
        return session.getAttribute("loggedInStaff") != null;
    }
}
