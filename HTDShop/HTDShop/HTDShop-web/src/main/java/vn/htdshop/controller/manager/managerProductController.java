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
@RequestMapping("manager/product")
public class managerProductController {

    private final String redirectProductHome = "redirect:/manager/product";
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
        if (!rightsList.contains("product_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass product list to session
        model.asMap().put("products", productFacade.findAll().stream()
                .sorted(Comparator.comparingInt(Product::getStatus)).collect(Collectors.toList()));
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        return "HTDManager/product";
    }

    // ==== PRODUCT ADD - VIEW ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    // Adding optional "cate" parameter by using @RequestParam(required = false)
    public String viewAdd(HttpSession session, Model model, @RequestParam(required = false) Integer cate) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return redirectLogin;
        }
        // Check if staff have appropriate role
        if (!rightsList.contains("product_add")) {
            // If not, redirect to product index
            return redirectProductHome;
        }
        // Prepare product model
        Category c = new Category();
        Product p = new Product();
        if (cate != null) {
            switch (cate) {
            case 2:
                c.setId(2);
                // model.addAttribute("product", new CPU());
                break;
            case 8:

                break;
            default:
                break;
            }
        }
        p.setCategory(c);
        model.addAttribute("product", p);

        // Prepare form url for form submit
        // model.addAttribute("formUrl", "doAdd" + category);
        model.addAttribute("formUrl", "doAdd");
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.product", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // Pass category list to view
        model.asMap().put("categories", categoryFacade.findAll());
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        // Continue to login page
        return "HTDManager/product_add";
    }

    // ==== PRODUCT ADD - PROCESS - CPU ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    // Adding optional "cate" parameter by using @RequestParam(required = false)
    public String doAddCPU(@Valid @ModelAttribute("product") Product product, BindingResult error, HttpSession session,
            Model model, @RequestParam(value = "uploadimg", required = false) MultipartFile[] uploadimg,
            RedirectAttributes redirect) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return redirectLogin;
        }
        // Check if staff have appropriate role
        if (!rightsList.contains("product_add")) {
            // If not, redirect to product index
            return redirectProductHome;
        }
        // If there is no error
        if (!error.hasErrors()) {
            // Custom method that create Product object from CPU class
            // Product p = product.toNewProduct();
            productFacade.create(product);
            // Process images
            if (uploadimg != null && uploadimg.length > 0) {
                uploadImages(uploadimg, product,false);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully added \"" + product.getName() + "\"!");
            return redirectProductHome;
        }
        // Show common error message
        error.reject("common", "Error adding new product.");
        if (uploadimg != null && uploadimg[0].getSize() > 0) {
            error.reject("common", "Please choose image again.");
        }
        // Pass binding result to redirect page (to show errors)
        redirect.addFlashAttribute("error", error);
        System.out.println(error);
        // Pass current input to redirect page (to keep old input)
        redirect.addFlashAttribute("product", product);
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        // Redirect to add page
        return "redirect:/manager/product/add?cate=2";
    }

    // ==== PRODUCT EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return redirectLogin;
        }
        // Check if staff have appropriate role
        if (!rightsList.contains("product_edit")) {
            // If not, redirect to product index
            return redirectProductHome;
        }

        // Initialize object for checking
        Product p = null;
        // First, check parameter "id"
        if (id != null) {
            // In case not null, find product by id and continue
            // to check if product is exists or not
            p = productFacade.find(id);
            if (p == null) {
                // If not exists, redirect to product index
                return redirectProductHome;
            }
        } else {
            // In case id is null, redirect to product index
            return redirectProductHome;
        }
        // Continue if everything is ok

        // Prepare product model
        switch (p.getCategory().getId()) {
        case 2:
            // CPU cpu = new CPU();
            // cpu.fromProduct(p);
            // model.addAttribute("product", cpu);
            break;
        case 8:
            break;
        default:
            break;
        }
        model.addAttribute("product", p);
        // Prepare form url for form submit
        // model.addAttribute("formUrl", "doEdit" + category);
        model.addAttribute("formUrl", "doEdit");
        // Indicator for update form
        model.addAttribute("update", "update");
        // Pass something to show in page
        model.addAttribute("curCate", p.getCategory().getId());
        model.addAttribute("category", p.getCategory().getName());
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.product", model.asMap().get("error"));
        }

        // Pass category list to view
        model.asMap().put("categories", categoryFacade.findAll());
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        // Continue to login page
        return "HTDManager/product_edit";
    }

    // ==== PRODUCT EDIT - PROCESS - CPU ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEditCPU(@Valid @ModelAttribute("product") Product product, BindingResult error, HttpSession session,
            Model model, @RequestParam(value = "uploadimg", required = false) MultipartFile[] uploadimg,
            RedirectAttributes redirect) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return redirectLogin;
        }
        // Check if staff have appropriate role
        if (!rightsList.contains("product_edit")) {
            // If not, redirect to product index
            return redirectProductHome;
        }
        // If there is no error
        if (!error.hasErrors()) {
            // Custom method that create Product object from CPU class
            // Product p = product.toProduct();
            // productFacade.edit(p);
            productFacade.edit(product);
            // Check if upload img exists then replace images
            if (uploadimg != null && uploadimg[0].getSize() > 0) {
                // Add new image
                uploadImages(uploadimg, product, true);
            }

            // Pass alert attribute to notify successful process
            redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + product.getName() + "\"!");
            return redirectProductHome;
        }
        // Show common error message
        error.reject("common", "Error while updating this product.");

        // Pass binding result to redirect-page (for showing errors)
        redirect.addFlashAttribute("error", error);
        // Pass current input to redirect-page (for keeping old input)
        redirect.addFlashAttribute("product", product);
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        // Redirect to add page
        return "redirect:/manager/product/edit?id=" + product.getId();
    }

    // ==== PRODUCT DELETE - PROCESS ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.GET)
    public String doDelete(HttpSession session, Model model, @RequestParam(required = true) Integer id,
            RedirectAttributes redirect) {
        // Check if logged in session is exists
        if (!checkLogin(session)) {
            // If not, redirect to index
            return redirectLogin;
        }
        // Check if staff have appropriate role
        if (!rightsList.contains("product_delete")) {
            // If not, redirect to product index
            return redirectProductHome;
        }

        // Initialize product object
        Product p = productFacade.find(id);
        // If product is not null
        if (p != null) {
            String exists = "|";
            // Check if product exists in any order/prebuilt or product have any comments
            if (!p.getOrderDetailCollection().isEmpty()) {
                exists += "order|";
            }
            if (!p.getPreBuiltCPUCollection().isEmpty()) {
                exists += "prebuilt-cpu|";
            }
            if (!p.getPreBuiltCPUCollection().isEmpty()) {
                exists += "prebuilt-cpucooler|";
            }
            if (!p.getPreBuiltCaseCollection().isEmpty()) {
                exists += "prebuilt-case|";
            }
            if (!p.getPreBuiltMemoryCollection().isEmpty()) {
                exists += "prebuilt-memory|";
            }
            if (!p.getPreBuiltMonitorCollection().isEmpty()) {
                exists += "prebuilt-monitor|";
            }
            if (!p.getPreBuiltMotherboardCollection().isEmpty()) {
                exists += "prebuilt-motherboard|";
            }
            if (!p.getPreBuiltPSUCollection().isEmpty()) {
                exists += "prebuilt-psu|";
            }
            if (!p.getPreBuiltStorageCollection().isEmpty()) {
                exists += "prebuilt-storage|";
            }
            if (!p.getPreBuiltVGACollection().isEmpty()) {
                exists += "prebuilt-vga|";
            }
            if (!p.getProductCommentCollection().isEmpty()) {
                exists += "have comments|";
            }

            if (exists.equals("|")) {
                // If not, then start to delete

                // First, delete product's images
                for (ProductImage img : p.getProductImageCollection()) {
                    File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                    if (deleteFile.delete()) {
                        System.out.println("Deleted image: " + deleteFile.getPath());
                    } else {
                        System.out.println("Cannot delete image: " + deleteFile.getPath());
                    }
                    productImageFacade.remove(img);
                }

                // Then, delete product's promotion
                for (Promotion promo : p.getPromotionCollection()) {
                    promotionFacade.remove(promo);
                }
                // Finally delete product
                productFacade.remove(p);

                // And pass alert attribute
                redirect.addFlashAttribute("goodAlert", "Successfully deleted \"" + p.getName() + "\"!");
            } else {
                // If exists in any, pass alert attribute
                redirect.addFlashAttribute("badAlert",
                        "Cannot delete \"" + p.getName() + "\"! (Reason: exists in " + exists + ")");
            }
        } else {
            // In case product is not found
            redirect.addFlashAttribute("badAlert", "This id is not exists!");
        }
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        return redirectProductHome;
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
