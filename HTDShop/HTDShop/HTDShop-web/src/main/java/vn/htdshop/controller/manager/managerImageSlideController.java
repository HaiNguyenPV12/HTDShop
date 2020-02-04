/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/imageslide")
public class managerImageSlideController {

    private final String redirectImageSlideHome = "redirect:/manager/imageslide";
    private final String redirectHome = "redirect:/manager";
    private final String redirectLogin = "redirect:/manager/login";

    List<String> rightsList = null;

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @EJB(mappedName = "ProductImageFacade")
    ProductImageFacadeLocal productImageFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "ImageSlideFacade")
    ImageSlideFacadeLocal imageSlideFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    // ==== IMAGE SLIDE INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!checkLoginWithRole("imageslide_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass image slide list to session
        model.asMap().put("imageslides",
                imageSlideFacade.findAll().stream()
                        .sorted(Comparator.comparing(ImageSlide::getStatus, Comparator.reverseOrder())
                                .thenComparing(ImageSlide::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "imageslide");
        return "HTDManager/imageslide";
    }

    // ==== IMAGE SLIDE ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(HttpSession session, Model model) {
        if (!checkLoginWithRole("imageslide_add")) {
            return redirectImageSlideHome;
        }
        // Prepare model
        ImageSlide imageSlide = new ImageSlide();
        imageSlide.setStatus(true);
        model.addAttribute("imageSlide", imageSlide);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doAdd");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.imageSlide", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        model.asMap().put("imageslides", imageSlideFacade.findAll().stream().filter(is -> is.getStatus() == true)
                .sorted(Comparator.comparing(ImageSlide::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList()));

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "imageslide");
        // Continue to login page
        return "HTDManager/imageslide_template";
    }

    // ==== IMAGE SLIDE ADD - PROCESS ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE
            + "; charset=utf-8")
    public String doAdd(@Valid @ModelAttribute("imageSlide") ImageSlide imageSlide, BindingResult error,
            HttpSession session, Model model,
            @RequestParam(value = "uploadimg", required = false) MultipartFile uploadimg, RedirectAttributes redirect) {
        if (!checkLoginWithRole("imageslide_add")) {
            return redirectImageSlideHome;
        }
        // Check if image exists
        if (uploadimg == null || uploadimg.getSize() <= 0 || uploadimg.isEmpty()) {
            error.reject("common", "Please choose image.");
        }

        // If there is no error
        if (!error.hasErrors()) {
            int order = imageSlide.getOrder();
            imageSlide.setOrder(null);
            // Insert into database
            imageSlideFacade.create(imageSlide);
            // Process images
            if (uploadimg != null) {
                uploadImage(uploadimg, imageSlide, false);
            }
            // Process order
            reorder(imageSlide.getId(), order, false);

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully added \"" + imageSlide.getTitle() + "\"!");
            return redirectImageSlideHome;
        }
        // Show common error message
        error.reject("common", "Error adding new product.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        System.out.println(error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("imageslide", imageSlide);

        // Redirect to add page
        return "redirect:/manager/imageslide/add";
    }

    // ==== IMAGE SLIDE ACTIVATE - PROCESS ==== \\
    @RequestMapping(value = "doActivate", method = RequestMethod.GET)
    public String doActivate(Model model, HttpSession session, @RequestParam(value = "id") Integer id,
            RedirectAttributes redirect) {
        if (!checkLoginWithRole("imageslide_edit")) {
            return redirectImageSlideHome;
        }
        ImageSlide imageSlide = imageSlideFacade.find(id);
        imageSlide.setStatus(true);
        imageSlideFacade.edit(imageSlide);
        redirect.addFlashAttribute("goodAlert", "Successfully activated \"" + imageSlide.getTitle() + "\"!");
        return redirectImageSlideHome;
    }

    // ==== IMAGE SLIDE DE-ACTIVATE - PROCESS ==== \\
    @RequestMapping(value = "doDeactivate", method = RequestMethod.GET)
    public String doDeactivate(Model model, HttpSession session, @RequestParam(value = "id") Integer id,
            RedirectAttributes redirect) {
        if (!checkLoginWithRole("imageslide_edit")) {
            return redirectImageSlideHome;
        }

        reorder(id, 0, true);

        redirect.addFlashAttribute("goodAlert",
                "Successfully de-activated \"" + imageSlideFacade.find(id).getTitle() + "\"!");
        return redirectImageSlideHome;
    }

    // ==== IMAGE SLIDE RE-ORDER - PROCESS ==== \\
    @RequestMapping(value = "doReorder", method = RequestMethod.GET)
    public String doReorder(Model model, HttpSession session, @RequestParam(value = "id") Integer id,
            @RequestParam(value = "order") Integer order, RedirectAttributes redirect) {
        if (!checkLoginWithRole("imageslide_edit")) {
            return redirectImageSlideHome;
        }

        reorder(id, order, false);

        return redirectImageSlideHome;
    }

    private Boolean checkLogin() {
        if (session.getAttribute("loggedInStaff") != null) {
            if (rightsList == null || rightsList.size() <= 0) {
                rightsList = new ArrayList<>();
                rightsList = (ArrayList<String>) session.getAttribute("rightsList");
            }
            return true;
        } else {
            String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInStaff"))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (cookie != null) {
                Staff staff = staffFacade.find(cookie);
                if (staff != null) {
                    session.setAttribute("loggedInStaff", staff);
                    List<String> rightsList = new ArrayList<String>();
                    for (RoleRights roleRights : staff.getRole().getRoleRightsCollection()) {
                        rightsList.add(roleRights.getRightsDetail().getTag());
                    }
                    this.rightsList = rightsList;
                    session.setAttribute("rightsList", rightsList);
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean checkLoginWithRole(String role) {
        if (checkLogin() && rightsList.contains(role)) {
            return true;
        }
        return false;
    }

    private void reorder(int id, int order, boolean disable) {
        ImageSlide curImageSlide = imageSlideFacade.find(id);
        int curOrder = curImageSlide.getOrder() == null ? 0 : curImageSlide.getOrder();

        if (order == 0) {
            List<ImageSlide> minusList1 = imageSlideFacade.findAll().stream().filter(is -> is.getOrder() != null)
                    .filter(is -> is.getOrder() >= curOrder).collect(Collectors.toList());
            for (ImageSlide imageSlide : minusList1) {
                imageSlide.setOrder(imageSlide.getOrder() - 1);
                imageSlideFacade.edit(imageSlide);
            }
        } else if (imageSlideFacade.findAll().stream().filter(is -> is.getOrder() != null)
                .filter(is -> is.getOrder() == order).collect(Collectors.toList()).size() <= 0) {
            // do nothing
        } else if (curOrder == 0) {
            List<ImageSlide> plusList1 = imageSlideFacade.findAll().stream().filter(is -> is.getOrder() != null)
                    .filter(is -> is.getOrder() >= order).collect(Collectors.toList());
            for (ImageSlide imageSlide : plusList1) {
                imageSlide.setOrder(imageSlide.getOrder() + 1);
                imageSlideFacade.edit(imageSlide);
            }
        } else if (order < curOrder) {
            List<ImageSlide> plusList2 = imageSlideFacade.findAll().stream().filter(is -> is.getOrder() != null)
                    .filter(is -> is.getOrder() >= order).filter(is -> is.getOrder() < curOrder)
                    .collect(Collectors.toList());
            for (ImageSlide imageSlide : plusList2) {
                imageSlide.setOrder(imageSlide.getOrder() + 1);
                imageSlideFacade.edit(imageSlide);
            }
        } else if (order > curOrder) {
            List<ImageSlide> minusList2 = imageSlideFacade.findAll().stream().filter(is -> is.getOrder() != null)
                    .filter(is -> is.getOrder() <= order).filter(is -> is.getOrder() > curOrder)
                    .collect(Collectors.toList());
            for (ImageSlide imageSlide : minusList2) {
                imageSlide.setOrder(imageSlide.getOrder() - 1);
                imageSlideFacade.edit(imageSlide);
            }
        }

        if (order == 0) {
            curImageSlide.setOrder(null);
        } else {
            curImageSlide.setOrder(order);
        }
        if (disable) {
            curImageSlide.setStatus(false);
        }
        imageSlideFacade.edit(curImageSlide);
    }

    private Boolean uploadImage(MultipartFile uploadimg, ImageSlide imageSlide, boolean deleteOldImages) {
        try {
            // Remove image
            if (deleteOldImages) {
                // First, delete real file.
                File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + imageSlide.getImage());
                if (deleteFile.delete()) {
                    System.out.println("Deleted image: " + deleteFile.getPath());
                } else {
                    System.out.println("Cannot delete image: " + deleteFile.getPath());
                }
            }

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            File imagePath = new File(System.getProperty("catalina.base") + "/img/imageslide");
            // Check if path is not exists, create path to it
            if (!imagePath.exists()) {
                imagePath.mkdirs();
            }

            // File name: [image slide id].[extension]
            // TODO: check extensions
            String fileName = imageSlide.getId()
                    + uploadimg.getOriginalFilename().substring(uploadimg.getOriginalFilename().lastIndexOf("."));
            // Path_to_glassfish/domains/domain_name/img/imageslide/file_name.extension
            String filePath = System.getProperty("catalina.base") + "/img/imageslide/" + fileName;
            // Use Files to copy multipartFile's input stream to declared path
            Files.copy(uploadimg.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            // Save image path
            imageSlide.setImage("imageslide/" + fileName);
            imageSlideFacade.edit(imageSlide);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
