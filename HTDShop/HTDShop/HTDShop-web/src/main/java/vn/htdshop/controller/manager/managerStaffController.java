package vn.htdshop.controller.manager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;
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
        List<Staff> staffList = staffFacade.findAll();
        if (!"Administrator".equals(managerService.getLoggedInStaff().getRole().getName())) {
            staffList = staffList.stream().filter(p -> !"Administrator".equals(p.getRole().getName()))
                    .collect(Collectors.toList());
        }
        model.asMap().put("staffs", staffList);

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
        Staff staff = new Staff();
        model.addAttribute("staff", staff);

        model.addAttribute("formUrl", "doAdd");
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.staff", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }
        model.addAttribute("staffRoles", getRoles());
        model.asMap().put("menu", "staff");
        return "HTDManager/staff_template";
    }

    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("staff") Staff staff,
            @RequestParam(value = "un", required = false) String un,
            @RequestParam(value = "pw", required = false) String pw,
            @RequestParam(value = "img", required = false) MultipartFile img, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("staff_add")) {
            return redirectStaffHome;
        }
        if (un == null || un.trim().isEmpty()) {
            error.rejectValue("username", "staff", "Please enter valid username.");
        }
        if (pw == null || pw.trim().isEmpty()) {
            error.rejectValue("password", "staff", "Please enter valid password.");
        }
        if (staffFacade.find(un) != null) {
            error.rejectValue("username", "staff", "This username is exists.");
        }
        if (img.isEmpty() || img.getSize() == 0) {
            error.rejectValue("image", "staff", "Please choose image to upload.");
        }
        String contentType = img.getContentType().substring(0, img.getContentType().lastIndexOf("/"));
        if (!contentType.equals("image")) {
            error.rejectValue("image", "staff", "Please choose valid image to upload.");
        }
        if (!error.hasErrors()) {
            // Insert into database
            staff.setUsername(un);
            staff.setPassword(pw);
            staff.setImage("noimage.png");
            staffFacade.create(staff);
            if (!uploadImage(img, staff, false)) {
                redirect.addFlashAttribute("badAlert", "Staff added but error while uploading image!");
            } else {
                redirect.addFlashAttribute("goodAlert", "Successfully added \"" + staff.getUsername() + "\"!");
            }

            return redirectStaffHome;
        }
        // Show common error message
        error.reject("common", "Error adding new staff. Please check again, re-enter password and choose image again.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("staff", staff);

        // Redirect to add page
        return "redirect:/manager/staff/add";
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
        model.addAttribute("formUrl", "doEdit");
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.staff", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }
        model.addAttribute("staffRoles", getRoles());
        model.asMap().put("menu", "staff");
        model.asMap().put("update", "update");
        return "HTDManager/staff_template";
    }

    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("staff") Staff staff,
            @RequestParam(value = "pw", required = false) String pw,
            @RequestParam(value = "img", required = false) MultipartFile img, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("staff_edit")) {
            return redirectStaffHome;
        }

        if (!error.hasErrors()) {
            Staff oldStaff = staffFacade.find(staff.getUsername());
            // Insert into database
            if (pw != null && !pw.trim().isEmpty()) {
                staff.setPassword(pw);
            } else {
                staff.setPassword(oldStaff.getPassword());
            }
            staff.setImage(oldStaff.getImage());
            staffFacade.edit(staff);
            uploadImage(img, staff, true);

            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + staff.getUsername() + "\"!");
            return redirectStaffHome;
        }
        // Show common error message
        error.reject("common", "Error editing staff. Please check again.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("staff", staff);

        // Redirect to add page
        return "redirect:/manager/staff/add";
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
        roles = roleFacade.findAll().stream().filter(p -> !"Administrator".equals(p.getName()))
                .collect(Collectors.toList());
        return roles;
    }

    private Boolean uploadImage(MultipartFile uploadimg, Staff staff, boolean deleteOldImage) {
        try {
            if (uploadimg.isEmpty() || uploadimg.getSize() == 0) {
                return false;
            }
            String contentType = uploadimg.getContentType().substring(0, uploadimg.getContentType().lastIndexOf("/"));
            if (!contentType.equals("image")) {
                return false;
            }
            // Remove image
            if (deleteOldImage) {
                // First, delete real file.
                File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + staff.getImage());
                if (deleteFile.delete()) {
                    System.out.println("Deleted image: " + deleteFile.getPath());
                } else {
                    System.out.println("Cannot delete image: " + deleteFile.getPath());
                }
            }

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            File imagePath = new File(System.getProperty("catalina.base") + "/img/staff");
            // Check if path is not exists, create path to it
            if (!imagePath.exists()) {
                imagePath.mkdirs();
            }

            // File name: [image slide id].[extension]
            String fileName = staff.getUsername()
                    + uploadimg.getOriginalFilename().substring(uploadimg.getOriginalFilename().lastIndexOf("."));
            // Path_to_glassfish/domains/domain_name/img/imageslide/file_name.extension
            String filePath = System.getProperty("catalina.base") + "/img/staff/" + fileName;
            // Use Files to copy multipartFile's input stream to declared path
            Files.copy(uploadimg.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            // Save image path
            staff.setImage("staff/" + fileName);
            staffFacade.edit(staff);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}