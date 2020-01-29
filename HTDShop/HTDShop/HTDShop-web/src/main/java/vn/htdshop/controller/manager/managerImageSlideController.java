/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
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

    // ==== PRODUCT INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            return redirectLogin;
        }
        // Check if staff have appropriate role
        if (!rightsList.contains("imageslide_read")) {
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
        model.asMap().put("imageslides", imageSlideFacade.findAll().stream().sorted(Comparator.comparing(ImageSlide::getStatus,Comparator.reverseOrder())).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "imageslide");
        return "HTDManager/imageslide";
    }

    private Boolean checkLogin(HttpSession session) {
        if (session.getAttribute("loggedInStaff") != null) {
            if (rightsList == null || rightsList.size() <= 0) {
                rightsList = new ArrayList<>();
                rightsList = (ArrayList<String>) session.getAttribute("rightsList");
            }
            return true;
        }
        return false;
    }

    private Boolean uploadImages(MultipartFile[] uploadimg, Product product, boolean deleteOldImages) {
        try {
            // Remove image
            if (deleteOldImages) {
                for (ProductImage img : productFacade.find(product.getId()).getProductImageCollection()) {
                    // First, delete real file.
                    File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                    if (deleteFile.delete()) {
                        System.out.println("Deleted image: " + deleteFile.getPath());
                    } else {
                        System.out.println("Cannot delete image: " + deleteFile.getPath());
                    }
                    // Then, delete record in database
                    productImageFacade.remove(img);
                }
            }

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            File imagePath = new File(System.getProperty("catalina.base") + "\\img\\product");
            // Check if path is not exists, create path to it
            if (!imagePath.exists()) {
                imagePath.mkdirs();
            }
            // With each of file, do following

            for (MultipartFile multipartFile : uploadimg) {
                // File name: [product id]_[time in milis].[extension]
                // TODO: check extensions
                String fileName = product.getId() + "_" + Calendar.getInstance().getTimeInMillis() + multipartFile
                        .getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
                // file path:
                // Path_to_glassfish/domains/domain_name/img/product/file_name.extension
                String filePath = System.getProperty("catalina.base") + "\\img\\product\\" + fileName;
                // Use Files to copy multipartFile's input stream to declared path
                Files.copy(multipartFile.getInputStream(), Paths.get(filePath));

                // Create image data in database
                ProductImage pimg = new ProductImage();
                pimg.setMainImage(false);
                pimg.setImagePath("product/" + fileName);
                pimg.setProduct(product);
                productImageFacade.create(pimg);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
