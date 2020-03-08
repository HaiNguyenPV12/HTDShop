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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/promotion")
public class managerPromotionController {

    private final String redirectPromotionHome = "redirect:/manager/promotion";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "PromotionDetailFacade")
    PromotionDetailFacadeLocal promotionDetailFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

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

    // ==== PROMOTION INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("promotion_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        return "HTDManager/promotion";
    }

    // ==== PROMOTION INDEX ==== \\
    @RequestMapping(value = "doDisable", method = RequestMethod.POST)
    public @ResponseBody Boolean doDisable(@RequestParam(value = "id", required = false) Integer id) {
        if (!managerService.checkLoginWithRole("promotion_edit")) {
            return false;
        }
        if (id == null) {
            return false;
        }
        PromotionDetail promo = promotionDetailFacade.find(id);
        if (promo == null) {
            return false;
        }
        promo.setIsDisabled(true);
        promotionDetailFacade.edit(promo);
        return true;
    }

    // ==== PROMOTION INDEX OLD ==== \\
    @RequestMapping(value = "old", method = RequestMethod.GET)
    public String getHomeOld(HttpSession session, Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("promotion_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass promotion detail list to session
        model.asMap().put("promotions",
                promotionDetailFacade.findAll().stream()
                        .sorted(Comparator.comparing(PromotionDetail::getIsDisabled, Comparator.naturalOrder()))
                        .collect(Collectors.toList()));

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        return "HTDManager/promotion_old";
    }

    @RequestMapping(value = "list", method = RequestMethod.POST)
    public @ResponseBody List<PromotionView> getPromotionList(
            @RequestParam(value = "status", required = false) Integer status) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("promotion_read")) {
            return new ArrayList<>();
        }
        if (status == null || (0 != status && 1 != status)) {
            return new ArrayList<>();
        }
        List<PromotionView> result = new ArrayList<PromotionView>();

        // Pass promotion detail list to session
        Collection<PromotionDetail> promolist = promotionDetailFacade.findAll();
        if (status == 1) {
            promolist = promolist.stream().filter(p -> p.getIsDisabled() == false)
                    .sorted(Comparator.comparing(PromotionDetail::getId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            promolist = promolist.stream().filter(p -> p.getIsDisabled() == true)
                    .sorted(Comparator.comparing(PromotionDetail::getId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        for (PromotionDetail promo : promolist) {
            PromotionView promoView = new PromotionView(promo);
            promoView.setHasImageSlide(managerService.checkImageSlideExists(promo.getId()));
            result.add(promoView);
        }

        return result;
    }

    @RequestMapping(value = "doCreateImageSlide", method = RequestMethod.POST)
    public @ResponseBody String doReply(@RequestParam(value = "id", required = false) Integer id) {
        if (!managerService.checkLoginWithRole("imageslide_add")) {
            return "No permission.";
        }
        if (id == null) {
            return "Promotion not found!";
        }
        PromotionDetail promo = promotionDetailFacade.find(id);
        // Create data
        ImageSlide img = new ImageSlide();
        img.setTitle(promo.getName());
        img.setDescription(promo.getDetail());
        img.setStatus(true);
        img.setOrder(null);
        img.setLink("promotion?id=" + id);
        imageSlideFacade.create(img);

        // Copy image
        try {
            String folderPath = System.getProperty("catalina.base") + "/img/imageslide";
            File folderCheck = new File(folderPath);
            if (!folderCheck.exists()) {
                folderCheck.mkdirs();
            }
            String ext = FilenameUtils.getExtension(promo.getImage());
            String fileName = img.getId() + "." + ext;
            String originPath = System.getProperty("catalina.base") + "/img/" + promo.getImage();
            String filePath = folderPath + "/" + fileName;
            Files.copy(Paths.get(originPath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            img.setImage("imageslide/" + fileName);
            imageSlideFacade.edit(img);
        } catch (Exception e) {
            e.printStackTrace();
            return "Cannot copy image: " + e.getMessage();
        }

        return "ok";
    }

    // ==== PROMOTION ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(Model model) {
        if (!managerService.checkLoginWithRole("promotion_add")) {
            return redirectPromotionHome;
        }
        // Prepare model
        PromotionDetail p = new PromotionDetail();

        // Check if old input exists
        if (model.asMap().containsKey("conditions")) {
            p.setPromotionCollection((Collection<Promotion>) model.asMap().get("conditions"));
        }
        model.addAttribute("promotion", p);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doAdd");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.promotion", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Product list
        model.addAttribute("productList", productFacade.findAll());
        // Category list
        model.addAttribute("categoryList", categoryFacade.findAll());

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        // Continue to login page
        return "HTDManager/promotion_template";
    }

    // ==== PROMOTION ADD - PROCESS ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("promotion") PromotionDetail promotion, BindingResult error,
            HttpSession session, Model model,
            @RequestParam(value = "uploadimg", required = false) MultipartFile uploadimg,
            @RequestParam(value = "conCount", required = false) Integer[] conCount,
            @RequestParam(value = "conCategory", required = false) Integer[] conCategory,
            @RequestParam(value = "conProduct", required = false) Integer[] conProduct,
            @RequestParam(value = "conPreBuilt", required = false) Integer[] conPreBuilt,
            @RequestParam(value = "conLimit", required = false) Integer[] conLimit,
            @RequestParam(value = "conMin", required = false) Integer[] conMin,
            @RequestParam(value = "conMax", required = false) Integer[] conMax,
            @RequestParam(value = "conPercentage", required = false) Double[] conPercentage,
            @RequestParam(value = "conMaxSale", required = false) Double[] conMaxSale,
            @RequestParam(value = "conExact", required = false) Double[] conExact, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("promotion_add")) {
            return redirectPromotionHome;
        }
        // Check image extension
        String contentType = uploadimg.getContentType().substring(0, uploadimg.getContentType().lastIndexOf("/"));
        if (!contentType.equals("image")) {
            error.reject("common", "Please choose valid extension.");
        }
        // Create promotion collection (for keeping input after redirect)
        Collection<Promotion> conditions = new ArrayList<>();
        if (conCount != null && conCount.length > 0) {

            if (conCount.length > 1) {
                for (int i = 0; i < conCount.length; i++) {
                    Promotion p = new Promotion();
                    p.setCategory(conCategory[i] != null ? categoryFacade.find(conCategory[i]) : null);
                    p.setProduct(conProduct[i] != null ? productFacade.find(conProduct[i]) : null);
                    p.setPreBuiltTarget(conPreBuilt[i]);
                    p.setLimitedQuantity(conLimit[i]);
                    if (conLimit[i] != null) {
                        p.setQuantityLeft(conLimit[i]);
                    }
                    p.setMinQuantity(conMin[i]);
                    p.setMaxQuantity(conMax[i]);
                    p.setPercentage(conPercentage[i]);
                    p.setMaxSaleOff(conMaxSale[i]);
                    p.setExactSaleOff(conExact[i]);
                    conditions.add(p);
                    System.out.println("cate:" + conCategory[i] + "|product:" + conProduct[i] + "|prebuilt:"
                            + conPreBuilt[i] + "|limit:" + conLimit[i] + "|min:" + conMin[i] + "|max:" + conMax[i]
                            + "|percentage:" + conPercentage[i] + "|maxSale:" + conMaxSale[i] + "|exact:"
                            + conExact[i]);
                }
            } else {
                Promotion p = new Promotion();
                p.setCategory(conCategory.length > 0 ? categoryFacade.find(conCategory[0]) : null);
                p.setProduct(conProduct.length > 0 ? productFacade.find(conProduct[0]) : null);
                p.setPreBuiltTarget(conPreBuilt.length > 0 ? conPreBuilt[0] : null);
                p.setLimitedQuantity(conLimit.length > 0 ? conLimit[0] : null);
                if (conLimit.length > 0) {
                    p.setQuantityLeft(conLimit[0]);
                }
                p.setMinQuantity(conMin.length > 0 ? conMin[0] : null);
                p.setMaxQuantity(conMax.length > 0 ? conMax[0] : null);
                p.setPercentage(conPercentage.length > 0 ? conPercentage[0] : null);
                p.setMaxSaleOff(conMaxSale.length > 0 ? conMaxSale[0] : null);
                p.setExactSaleOff(conExact.length > 0 ? conExact[0] : null);
                conditions.add(p);
                System.out.println("cate:" + (conCategory.length > 0 ? conCategory[0] : "null") + "|product:"
                        + (conProduct.length > 0 ? conProduct[0] : "null") + "|prebuilt:"
                        + (conPreBuilt.length > 0 ? conPreBuilt[0] : "null") + "|limit:"
                        + (conLimit.length > 0 ? conLimit[0] : "null") + "|min:" + (conMin.length > 0 ? conMin : "null")
                        + "|max:" + (conMax.length > 0 ? conMax[0] : "null") + "|percentage:"
                        + (conPercentage.length > 0 ? conPercentage[0] : "null") + "|maxSale:"
                        + (conMaxSale.length > 0 ? conMaxSale[0] : "null") + "|exact:"
                        + (conExact.length > 0 ? conExact[0] : "null"));
            }

        } else {
            error.reject("common", "Please add condition(s).");
        }

        // If there is no error
        if (!error.hasErrors()) {
            // Insert into database
            promotion.setIsDisabled(false);
            if (promotion.getIsAlways()) {
                promotion.setStartDate(null);
                promotion.setEndDate(null);
            }
            promotionDetailFacade.create(promotion);

            // Insert image
            uploadImage(uploadimg, promotion, false);

            // Insert conditions
            for (Promotion p : conditions) {
                p.setPromotionDetail(promotion);
                promotionFacade.create(p);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully added \"" + promotion.getName() + "\"!");
            return redirectPromotionHome;
        }
        // Show error for image
        error.reject("common", "Please choose image again.");
        // Show common error message
        error.reject("common", "Error adding new promotion.");

        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("conditions", conditions);
        redirect.addFlashAttribute("promotion", promotion);

        // Redirect to add page
        return "redirect:/manager/promotion/add";
    }

    // ==== PROMOTION EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") Integer id) {
        if (!managerService.checkLoginWithRole("promotion_edit")) {
            return redirectPromotionHome;
        }
        // Prepare model
        PromotionDetail p = promotionDetailFacade.find(id);

        // Check if old input exists
        if (model.asMap().containsKey("conditions")) {
            p.setPromotionCollection((Collection<Promotion>) model.asMap().get("conditions"));
        }
        model.addAttribute("promotion", p);

        // Prepare form url for form submit
        model.addAttribute("formUrl", "doEdit");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.promotion", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }
        // Product list
        model.addAttribute("productList", productFacade.findAll());
        // Category list
        model.addAttribute("categoryList", categoryFacade.findAll());

        // Add edit identifier for form
        model.asMap().put("update", "update");
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "promotion");
        // Continue to login page
        return "HTDManager/promotion_template";
    }

    // ==== PROMOTION EDIT - PROCESS ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("promotion") PromotionDetail promotion, BindingResult error,
            HttpSession session, @RequestParam(value = "uploadimg", required = false) MultipartFile uploadimg,
            @RequestParam(value = "conCount", required = false) Integer[] conCount,
            @RequestParam(value = "conCategory", required = false) Integer[] conCategory,
            @RequestParam(value = "conProduct", required = false) Integer[] conProduct,
            @RequestParam(value = "conPreBuilt", required = false) Integer[] conPreBuilt,
            @RequestParam(value = "conLimit", required = false) Integer[] conLimit,
            @RequestParam(value = "conMin", required = false) Integer[] conMin,
            @RequestParam(value = "conMax", required = false) Integer[] conMax,
            @RequestParam(value = "conPercentage", required = false) Double[] conPercentage,
            @RequestParam(value = "conMaxSale", required = false) Double[] conMaxSale,
            @RequestParam(value = "conExact", required = false) Double[] conExact, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("promotion_edit")) {
            return redirectPromotionHome;
        }
        // Check image extension if image exists
        if (!uploadimg.isEmpty() && uploadimg.getSize() > 0) {
            String contentType = uploadimg.getContentType().substring(0, uploadimg.getContentType().lastIndexOf("/"));
            if (!contentType.equals("image")) {
                error.reject("common", "Please choose valid extension.");
            }
        }

        // Create promotion collection (for keeping input after redirect)
        Collection<Promotion> conditions = new ArrayList<>();
        if (conCount != null && conCount.length > 0) {

            if (conCount.length > 1) {
                for (int i = 0; i < conCount.length; i++) {
                    Promotion p = new Promotion();
                    p.setCategory(conCategory[i] != null ? categoryFacade.find(conCategory[i]) : null);
                    p.setProduct(conProduct[i] != null ? productFacade.find(conProduct[i]) : null);
                    p.setPreBuiltTarget(conPreBuilt[i]);
                    p.setLimitedQuantity(conLimit[i]);
                    if (conLimit[i] != null) {
                        p.setQuantityLeft(conLimit[i]);
                    }
                    p.setMinQuantity(conMin[i]);
                    p.setMaxQuantity(conMax[i]);
                    p.setPercentage(conPercentage[i]);
                    p.setMaxSaleOff(conMaxSale[i]);
                    p.setExactSaleOff(conExact[i]);
                    conditions.add(p);
                }
            } else {
                Promotion p = new Promotion();
                p.setCategory(conCategory.length > 0 ? categoryFacade.find(conCategory[0]) : null);
                p.setProduct(conProduct.length > 0 ? productFacade.find(conProduct[0]) : null);
                p.setPreBuiltTarget(conPreBuilt.length > 0 ? conPreBuilt[0] : null);
                p.setLimitedQuantity(conLimit.length > 0 ? conLimit[0] : null);
                if (conLimit.length > 0) {
                    p.setQuantityLeft(conLimit[0]);
                }
                p.setMinQuantity(conMin.length > 0 ? conMin[0] : null);
                p.setMaxQuantity(conMax.length > 0 ? conMax[0] : null);
                p.setPercentage(conPercentage.length > 0 ? conPercentage[0] : null);
                p.setMaxSaleOff(conMaxSale.length > 0 ? conMaxSale[0] : null);
                p.setExactSaleOff(conExact.length > 0 ? conExact[0] : null);
                conditions.add(p);
            }

        } else {
            error.reject("common", "Please add condition(s).");
        }
        // Check name exists
        // If there is no error
        if (!error.hasErrors()) {
            // Update in database
            promotionDetailFacade.edit(promotion);

            // Replace image if exists
            if (!uploadimg.isEmpty() && uploadimg.getSize() > 0) {
                uploadImage(uploadimg, promotion, true);
            }

            // Remove promotions
            for (Promotion p : promotionDetailFacade.find(promotion.getId()).getPromotionCollection()) {
                promotionFacade.remove(p);
            }
            // Insert promotions
            for (Promotion p : conditions) {
                p.setPromotionDetail(promotion);
                promotionFacade.create(p);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + promotion.getName() + "\"!");
            return redirectPromotionHome;
        }
        if (!uploadimg.isEmpty() && uploadimg.getSize() > 0) {
            error.reject("common", "Please choose image again.");
        }
        // Show common error message
        error.reject("common", "Error updating this promotion.");
        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("promotion", promotion);

        // Redirect to add page
        return "redirect:/manager/promotion/edit?id=" + promotion.getId();
    }

    // ==== PROMOTION DELETE - PROCESS ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    public String doDelete(HttpSession session, Model model, @RequestParam(required = true) Integer id,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("promotion_delete")) {
            return redirectPromotionHome;
        }

        // Initialize object
        PromotionDetail promotion = promotionDetailFacade.find(id);
        // If object is not null
        if (promotion != null) {
            // First, delete promotion
            for (Promotion p : promotion.getPromotionCollection()) {
                promotionFacade.remove(p);
            }

            // Then delete real image
            File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + promotion.getImage());
            if (deleteFile.delete()) {
                System.out.println("Deleted image: " + deleteFile.getPath());
            } else {
                System.out.println("Cannot delete image: " + deleteFile.getPath());
            }

            // Finally, delete promotion detail
            promotionDetailFacade.remove(promotion);

            // And pass alert attribute
            redirect.addFlashAttribute("goodAlert", "Successfully deleted \"" + promotion.getName() + "\"!");

        } else {
            // In case product is not found
            redirect.addFlashAttribute("badAlert", "This id is not exists!");
        }

        return redirectPromotionHome;
    }

    private Boolean uploadImage(MultipartFile uploadimg, PromotionDetail promotionDetail, boolean deleteOldImage) {
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
                File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + promotionDetail.getImage());
                if (deleteFile.delete()) {
                    System.out.println("Deleted image: " + deleteFile.getPath());
                } else {
                    System.out.println("Cannot delete image: " + deleteFile.getPath());
                }
            }

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            File imagePath = new File(System.getProperty("catalina.base") + "/img/promotion");
            // Check if path is not exists, create path to it
            if (!imagePath.exists()) {
                imagePath.mkdirs();
            }

            // File name: [image slide id].[extension]
            String fileName = promotionDetail.getId()
                    + uploadimg.getOriginalFilename().substring(uploadimg.getOriginalFilename().lastIndexOf("."));
            // Path_to_glassfish/domains/domain_name/img/imageslide/file_name.extension
            String filePath = System.getProperty("catalina.base") + "/img/promotion/" + fileName;
            // Use Files to copy multipartFile's input stream to declared path
            Files.copy(uploadimg.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            // Save image path
            promotionDetail.setImage("promotion/" + fileName);
            promotionDetailFacade.edit(promotionDetail);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
