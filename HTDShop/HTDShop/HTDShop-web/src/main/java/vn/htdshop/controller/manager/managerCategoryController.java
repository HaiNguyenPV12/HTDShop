/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
@RequestMapping("manager/category")
public class managerCategoryController {

    private final String redirectCategoryHome = "redirect:/manager/category";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @EJB(mappedName = "CategoryOtherFacade")
    CategoryOtherFacadeLocal categoryOtherFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // ==== CATEGORY INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("category_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass category list to session
        model.asMap().put("categories", categoryFacade.findAll());

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "category");
        return "HTDManager/category";
    }

    // ==== CATEGORY ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(Model model) {
        if (!managerService.checkLoginWithRole("category_add")) {
            return redirectCategoryHome;
        }
        // Prepare model
        Category c = new Category();
        // Check if old input exists
        String[] others = (String[]) model.asMap().get("others");
        Collection<CategoryOther> otherList = new ArrayList<>();
        if (others != null) {
            for (String other : others) {
                CategoryOther cateOther = new CategoryOther();
                cateOther.setName(other);
                otherList.add(cateOther);
            }
        }
        c.setCategoryOtherCollection(otherList);
        model.addAttribute("category", c);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doAdd");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.category", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "category");
        // Continue to login page
        return "HTDManager/category_template";
    }

    // ==== CATEGORY ADD - PROCESS ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("category") Category category,
            @RequestParam(value = "others", required = false) String[] others, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("category_add")) {
            return redirectCategoryHome;
        }
        // Check category name exists
        if (categoryFacade.findByName(category.getName()) != null) {
            error.rejectValue("name", "category", "This name is exists.");
        }

        // Check attribute exists
        if (others == null || others.length <= 0) {
            error.reject("common", "Please add at least 1 attribute for this category.");
        }

        // Check attribute duplicates
        if (null != others) {
            String duplicatedAttr = "|";
            Set<String> duplicates = new HashSet<>();
            for (String string : others) {
                if (duplicates.add(string) == false) {
                    duplicatedAttr += string + "|";
                }
            }
            if (!duplicatedAttr.equals("|")) {
                error.reject("common", "Duplicated: " + duplicatedAttr);
            }
        }

        // If there is no error
        if (!error.hasErrors()) {
            // Insert into database
            categoryFacade.create(category);

            // Insert rights
            for (String o : others) {
                if (o != null && !o.trim().isEmpty()) {
                    CategoryOther other = new CategoryOther();
                    other.setCategory(category);
                    other.setName(o);
                    categoryOtherFacade.create(other);
                }
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully added \"" + category.getName() + "\"!");
            return redirectCategoryHome;
        }
        // Show common error message
        error.reject("common", "Error adding new category.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("category", category);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("others", others);

        // Redirect to add page
        return "redirect:/manager/category/add";
    }

    // ==== CATEGORY EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") Integer id) {
        if (!managerService.checkLoginWithRole("category_edit")) {
            return redirectCategoryHome;
        }
        // Prepare model
        Category c = categoryFacade.find(id);
        // Check if old input exists
        String[] others = (String[]) model.asMap().get("others");
        if (others != null) {
            Collection<CategoryOther> otherList = new ArrayList<>();
            for (String other : others) {
                CategoryOther cateOther = new CategoryOther();
                cateOther.setName(other);
                otherList.add(cateOther);
            }
            c.setCategoryOtherCollection(otherList);
        }
        model.addAttribute("category", c);
        // Prevent editing admin
        if (c.getId() <= 9) {
            return redirectCategoryHome;
        }

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doEdit");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.category", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Add edit identifier for form
        model.asMap().put("update", "update");
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "category");
        // Continue to login page
        return "HTDManager/category_template";
    }

    // ==== CATEGORY EDIT - PROCESS ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("category") Category category, BindingResult error, HttpSession session,
            Model model, @RequestParam(value = "others", required = false) String[] others,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("category_edit")) {
            return redirectCategoryHome;
        }

        // Check category name exists
        if (!categoryFacade.find(category.getId()).getName().equals(category.getName())) {
            if (categoryFacade.findByName(category.getName()) != null) {
                error.rejectValue("name", "category", "This name is exists.");
            }
        }

        // Check attribute exists
        if (others == null || others.length <= 0) {
            error.reject("common", "Please add at least 1 attribute for this category.");
        }

        // Check attribute duplicates
        if (null != others) {
            String duplicatedAttr = "|";
            Set<String> duplicates = new HashSet<>();
            for (String string : others) {
                if (duplicates.add(string) == false) {
                    duplicatedAttr += string + "|";
                }
            }
            if (!duplicatedAttr.equals("|")) {
                error.reject("common", "Duplicated: " + duplicatedAttr);
            }
        }

        // If there is no error
        if (!error.hasErrors()) {
            // Update in database
            categoryFacade.edit(category);

            // Remove attr
            for (CategoryOther cateOther : categoryFacade.find(category.getId()).getCategoryOtherCollection()) {
                categoryOtherFacade.remove(cateOther);
            }

            // Insert attr
            for (String o : others) {
                if (o != null && !o.trim().isEmpty()) {
                    CategoryOther other = new CategoryOther();
                    other.setCategory(category);
                    other.setName(o);
                    categoryOtherFacade.create(other);
                }
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + category.getName() + "\"!");
            return redirectCategoryHome;
        }

        // Show common error message
        error.reject("common", "Error updating this category.");
        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("category", category);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("others", others);

        // Redirect to add page
        return "redirect:/manager/category/edit?id=" + category.getId();
    }

    // ==== CATEGORY DELETE - PROCESS ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    public String doDelete(HttpSession session, Model model, @RequestParam(required = true) Integer id,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("category_delete")) {
            return redirectCategoryHome;
        }

        // Initialize object
        Category category = categoryFacade.find(id);
        // If object is not null
        if (category != null) {
            // Then check if this exists in other tables?
            if (category.getId() <= 9) {
                redirect.addFlashAttribute("badAlert", "Cannot delete!");
                return redirectCategoryHome;
            }
            if (category.getProductCollection().size() > 0) {
                redirect.addFlashAttribute("badAlert", "Cannot delete! Has product.");
                return redirectCategoryHome;
            }

            // If not, then start to delete
            // First, delete category other
            for (CategoryOther o : category.getCategoryOtherCollection()) {
                categoryOtherFacade.remove(o);
            }

            // Finally, delete category
            categoryFacade.remove(category);

            // And pass alert attribute
            redirect.addFlashAttribute("goodAlert", "Successfully deleted \"" + category.getName() + "\"!");

        } else {
            // In case product is not found
            redirect.addFlashAttribute("badAlert", "This id is not exists!");
        }

        return redirectCategoryHome;
    }
}
