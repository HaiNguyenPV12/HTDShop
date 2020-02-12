/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
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

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/imageslide")
public class managerImageSlideController {

    private final String redirectImageSlideHome = "redirect:/manager/imageslide";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "ImageSlideFacade")
    ImageSlideFacadeLocal imageSlideFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // ==== IMAGE SLIDE INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("imageslide_read")) {
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
    public String viewAdd(Model model) {
        if (!managerService.checkLoginWithRole("imageslide_add")) {
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
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("imageSlide") ImageSlide imageSlide, BindingResult error,
            HttpSession session, Model model,
            @RequestParam(value = "uploadimg", required = false) MultipartFile uploadimg, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("imageslide_add")) {
            return redirectImageSlideHome;
        }
        // Check if image exists
        String contentType = uploadimg.getContentType().substring(0, uploadimg.getContentType().lastIndexOf("/"));
        if (uploadimg.getSize() <= 0 || uploadimg.isEmpty() || !contentType.equals("image")) {
            error.reject("common", "Please choose valid image.");
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
        error.reject("common", "Error adding new image slide.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        System.out.println(error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("imageslide", imageSlide);

        // Redirect to add page
        return "redirect:/manager/imageslide/add";
    }

    // ==== IMAGE SLIDE EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") Integer id) {
        if (!managerService.checkLoginWithRole("imageslide_edit")) {
            return redirectImageSlideHome;
        }
        // Prepare model
        ImageSlide imageSlide = imageSlideFacade.find(id);
        model.addAttribute("imageSlide", imageSlide);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doEdit");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.imageSlide", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        model.asMap().put("imageslides", imageSlideFacade.findAll().stream().filter(is -> is.getStatus() == true)
                .sorted(Comparator.comparing(ImageSlide::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList()));

        // Add edit identifier for form
        model.asMap().put("update", "update");
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "imageslide");
        // Continue to login page
        return "HTDManager/imageslide_template";
    }

    // ==== IMAGE SLIDE EDIT - PROCESS ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("imageSlide") ImageSlide imageSlide, BindingResult error,
            HttpSession session, Model model,
            @RequestParam(value = "uploadimg", required = false) MultipartFile uploadimg, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("imageslide_edit")) {
            return redirectImageSlideHome;
        }
        String contentType = uploadimg.getContentType().substring(0, uploadimg.getContentType().lastIndexOf("/"));
        if (!uploadimg.isEmpty() && !contentType.equals("image")) {
            error.reject("common", "Please choose valid image.");
        }
        // If there is no error
        if (!error.hasErrors()) {
            // Process order
            reorder(imageSlide.getId(), imageSlide.getOrder(), false);
            if (imageSlide.getOrder() == 0) {
                imageSlide.setOrder(null);
            }
            // Update in database
            imageSlideFacade.edit(imageSlide);

            // Process images
            if (!uploadimg.isEmpty() && uploadimg.getSize() > 0) {
                uploadImage(uploadimg, imageSlide, true);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + imageSlide.getTitle() + "\"!");
            return redirectImageSlideHome;
        }
        if (uploadimg == null || uploadimg.getSize() <= 0 || uploadimg.isEmpty()) {
            error.reject("common", "Please choose image again.");
        }
        // Show common error message
        error.reject("common", "Error updating this image slide.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        System.out.println(error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("imageslide", imageSlide);

        // Redirect to add page
        return "redirect:/manager/imageslide/edit?id=" + imageSlide.getId();
    }

    // ==== IMAGE SLIDE DELETE - PROCESS ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    public String doDelete(HttpSession session, Model model, @RequestParam(required = true) Integer id,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("imageslide_delete")) {
            return redirectImageSlideHome;
        }

        // Initialize object
        ImageSlide imageSlide = imageSlideFacade.find(id);
        // If onject is not null
        if (imageSlide != null) {
            // If not, then start to delete
            // First, delete image
            File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + imageSlide.getImage());
            if (deleteFile.delete()) {
                System.out.println("Deleted image: " + deleteFile.getPath());
            } else {
                System.out.println("Cannot delete image: " + deleteFile.getPath());
            }

            // Then, reorder and disable
            reorder(id, 0, true);

            // Finally, delete image slide
            imageSlideFacade.remove(imageSlide);

            // And pass alert attribute
            redirect.addFlashAttribute("goodAlert", "Successfully deleted \"" + imageSlide.getTitle() + "\"!");

        } else {
            // In case product is not found
            redirect.addFlashAttribute("badAlert", "This id is not exists!");
        }

        return redirectImageSlideHome;
    }

    // ==== IMAGE SLIDE ACTIVATE - PROCESS ==== \\
    @RequestMapping(value = "doActivate", method = RequestMethod.GET)
    public String doActivate(Model model, HttpSession session, @RequestParam(value = "id") Integer id,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("imageslide_edit")) {
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
        if (!managerService.checkLoginWithRole("imageslide_edit")) {
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
        if (!managerService.checkLoginWithRole("imageslide_edit")) {
            return redirectImageSlideHome;
        }

        reorder(id, order, false);

        return redirectImageSlideHome;
    }

    private void reorder(int id, int order, boolean disable) {
        ImageSlide curImageSlide = imageSlideFacade.find(id);
        int curOrder = curImageSlide.getOrder() == null ? 0 : curImageSlide.getOrder();
        if (order != curOrder) {
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

    private Boolean uploadImage(MultipartFile uploadimg, ImageSlide imageSlide, boolean deleteOldImage) {
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
