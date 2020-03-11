/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Collection;

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
 * @author Thien
 */
@Controller
@RequestMapping("manager/role")
public class managerRoleController {

    private final String redirectRoleHome = "redirect:/manager/role";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "RoleFacade")
    RoleFacadeLocal roleFacade;

    @EJB(mappedName = "RoleRightsFacade")
    RoleRightsFacadeLocal roleRightsFacade;

    @EJB(mappedName = "RightsDetailFacade")
    RightsDetailFacadeLocal rightsDetailFacade;

    final private String[] rightsNames = new String[] {"category", "product", "prebuilt", "comment", "order", "customer",
            "promotion", "imageslide", "staff", "role", "revenue", "delivery"};

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // ==== ROLE INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("role_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass role list to session
        model.asMap().put("roles", roleFacade.findAll());
        // Pass rights name list to session
        model.asMap().put("rightsNames", rightsNames);

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "role");
        return "HTDManager/role";
    }

    // ==== ROLE ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(Model model) {
        if (!managerService.checkLoginWithRole("role_add")) {
            return redirectRoleHome;
        }
        // Prepare model
        Role r = new Role();
        // Check if old input exists
        String[] rights = (String[]) model.asMap().get("rights");
        if (rights != null) {
            Collection<RoleRights> rightsList = new ArrayList<>();
            for (String right : rights) {
                RightsDetail rightsDetail = new RightsDetail();
                rightsDetail.setTag(right);
                RoleRights roleRights = new RoleRights();
                roleRights.setRightsDetail(rightsDetail);

                rightsList.add(roleRights);
            }
            r.setRoleRightsCollection(rightsList);
        }
        model.addAttribute("role", r);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doAdd");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.role", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }
        // Pass rights name list to session
        model.asMap().put("rightsNames", rightsNames);
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "role");
        // Continue to login page
        return "HTDManager/role_template";
    }

    // ==== ROLE ADD - PROCESS ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("role") Role role,
            @RequestParam(value = "rights", required = false) String[] rights, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("role_add")) {
            return redirectRoleHome;
        }
        // Check role name exists
        if (roleFacade.findByName(role.getName()) != null) {
            error.rejectValue("name", "role", "This name is exists.");
        }

        // Check right exists
        if (rights == null || rights.length <= 0) {
            error.reject("common", "Please choose rights for this role.");
        }

        // If there is no error
        if (!error.hasErrors()) {
            // Insert into database
            roleFacade.create(role);

            // Insert rights
            for (String right : rights) {
                RoleRights rr = new RoleRights();
                rr.setRole(role);
                rr.setRightsDetail(rightsDetailFacade.findByTag(right));
                roleRightsFacade.create(rr);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully added \"" + role.getName() + "\"!");
            return redirectRoleHome;
        }
        // Show common error message
        error.reject("common", "Error adding new role.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("role", role);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("rights", rights);

        // Redirect to add page
        return "redirect:/manager/role/add";
    }

    // ==== ROLE EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") Integer id) {
        if (!managerService.checkLoginWithRole("role_edit")) {
            return redirectRoleHome;
        }
        // Prepare model
        Role r = roleFacade.find(id);
        // Check if old input exists
        String[] rights = (String[]) model.asMap().get("rights");
        if (rights != null) {
            Collection<RoleRights> rightsList = new ArrayList<>();
            for (String right : rights) {
                RightsDetail rightsDetail = new RightsDetail();
                rightsDetail.setTag(right);
                RoleRights roleRights = new RoleRights();
                roleRights.setRightsDetail(rightsDetail);

                rightsList.add(roleRights);
            }

            r.setRoleRightsCollection(rightsList);

        }
        model.addAttribute("role", r);
        // Prevent editing admin
        if (r.getName().equals("Administrator")) {
            return redirectRoleHome;
        }

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doEdit");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.role", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Pass rights name list to session
        model.asMap().put("rightsNames", rightsNames);

        // Add edit identifier for form
        model.asMap().put("update", "update");
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "role");
        // Continue to login page
        return "HTDManager/role_template";
    }

    // ==== ROLE EDIT - PROCESS ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("role") Role role, BindingResult error, HttpSession session,
            Model model, @RequestParam(value = "rights", required = false) String[] rights,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("role_edit")) {
            return redirectRoleHome;
        }
        
        // Prevent editing admin
        if (role.getName().equals("Administrator")) {
            error.reject("common", "Cannot rename to \"Administrator\"");
        }

        // Check role name exists
        if (!roleFacade.find(role.getId()).getName().equals(role.getName())) {
            if (roleFacade.findByName(role.getName()) != null) {
                error.rejectValue("name", "role", "This name is exists.");
            }
        }

        // Check right exists
        if (rights == null || rights.length <= 0) {
            error.reject("common", "Please choose rights for this role.");
        }

        // If there is no error
        if (!error.hasErrors()) {
            // Update in database
            roleFacade.edit(role);

            // Remove rights
            for (RoleRights rr : role.getRoleRightsCollection()) {
                roleRightsFacade.remove(rr);
            }

            // Insert rights
            for (String right : rights) {
                RoleRights rr = new RoleRights();
                rr.setRole(role);
                rr.setRightsDetail(rightsDetailFacade.findByTag(right));
                roleRightsFacade.create(rr);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + role.getName() + "\"!");
            return redirectRoleHome;
        }

        // Show common error message
        error.reject("common", "Error updating this role.");
        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("role", role);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("rights", rights);

        // Redirect to add page
        return "redirect:/manager/role/edit?id=" + role.getId();
    }

    // ==== ROLE DELETE - PROCESS ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    public String doDelete(HttpSession session, Model model, @RequestParam(required = true) Integer id,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("role_delete")) {
            return redirectRoleHome;
        }

        // Initialize object
        Role role = roleFacade.find(id);
        // If object is not null
        if (role != null) {
            // Then check if this exists in staff
            if (role.getName().equals("Administrator")) {
                redirect.addFlashAttribute("badAlert", "Cannot delete admin!");
                return redirectRoleHome;
            }
            if (role.getStaffCollection().size() > 0) {
                redirect.addFlashAttribute("badAlert", "Cannot delete! This role exists in staffs.");
                return redirectRoleHome;
            }

            // If not, then start to delete
            // First, delete right (from rolerights)
            for (RoleRights right : role.getRoleRightsCollection()) {
                roleRightsFacade.remove(right);
            }

            // Finally, delete role
            roleFacade.remove(role);

            // And pass alert attribute
            redirect.addFlashAttribute("goodAlert", "Successfully deleted \"" + role.getName() + "\"!");

        } else {
            // In case product is not found
            redirect.addFlashAttribute("badAlert", "This id is not exists!");
        }

        return redirectRoleHome;
    }
}
