package vn.htdshop.controller.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

@Controller
@RequestMapping("manager/staff")
public class managerStaffController {

    private final String redirectStaffHome = "redirect:/manager/staff";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "RoleFacade")
    RoleFacadeLocal roleFacade;

    @EJB(mappedName = "RoleRightsFacade")
    RoleRightsFacadeLocal roleRightsFacade;

    @EJB(mappedName = "RightsDetailFacade")
    RightsDetailFacadeLocal rightsDetailFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // Staff index
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("staff_read")) {
            return redirectHome;
        }

        // Pass staff list to session
        model.asMap().put("staffs", staffFacade.findAll());

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "staff");

        return "HTDManager/staff";
    }

    // Staff add
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(Model model) {
        if (!managerService.checkLoginWithRole("staff_add")) {
            return redirectHome;
        }
        // prepare model
        Staff staff = new Staff();
        model.addAttribute("staff", staff);
        model.addAttribute("staffRoles", getRoles());

        return "HTDManager/staff_template";
    }

    // Staff edit
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") String id) {
        if (!managerService.checkLoginWithRole("staff_edit")) {
            return redirectHome;
        }
        // prepare model
        Staff staff = staffFacade.find(id);
        model.addAttribute("staff", staff);
        model.addAttribute("staffRoles", getRoles());
        model.asMap().put("update", "update");

        return "HTDManager/staff_template";
    }

    // Staff details
    @RequestMapping(value = "details", method = RequestMethod.GET)
    public String viewDetails(Model model, @RequestParam(value = "id") String id) {
        if (!managerService.checkLoginWithRole("staff_read")) {
            return redirectHome;
        }
        // prepare model
        Staff staff = staffFacade.find(id);
        if (staff == null) {
            System.out.println("Staff is null");
            return redirectHome;
        }
        model.addAttribute("staff", staff);

        return "HTDManager/staff_details";
    }

    private List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        roles = roleFacade.findAll();
        return roles;
    }
}