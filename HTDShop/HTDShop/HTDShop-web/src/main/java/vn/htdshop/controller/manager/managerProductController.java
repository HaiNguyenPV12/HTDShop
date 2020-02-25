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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ImageThumbnail;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/product")
public class managerProductController {

    private final String redirectProductHome = "redirect:/manager/product";
    private final String redirectHome = "redirect:/manager";

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

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    // ==== PRODUCT INDEX ==== \\
    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model) {
        // Check login with role
        if (!managerService.checkLoginWithRole("product_read")) {
            return redirectHome;
        }

        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // Pass product list to session -- No need for now (using ajax)
        // model.asMap().put("products",
        // productFacade.findAll().stream().sorted(Comparator.comparingInt(Product::getStatus))
        // .sorted(Comparator.comparing(Product::getId, Comparator.reverseOrder()))
        // .collect(Collectors.toList()));

        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        return "HTDManager/product";
    }

    // ==== PRODUCT LIST ==== \\
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public @ResponseBody List<ProductView> getList(Model model, HttpServletResponse response) {
        response.setContentType("application/json");
        // Check login with role
        if (!managerService.checkLoginWithRole("product_read")) {
            return new ArrayList<ProductView>();
        }
        List<Product> productList = productFacade.findAll().stream()
                .sorted(Comparator.comparing(Product::getId, Comparator.reverseOrder()))
                .sorted(Comparator.comparingInt(Product::getStatus)).collect(Collectors.toList());
        List<ProductView> result = new ArrayList<>();
        // For testing
        // for (int i = 0; i < 15; i++) {
        // for (Product product : productList) {
        // result.add(new ProductView(product));
        // }
        // }
        for (Product product : productList) {
            result.add(new ProductView(product));
        }
        return result;
    }

    // ==== PRODUCT - SET STATUS MULTIPLE PRODUCTS ==== \\
    @RequestMapping(value = "setStatus", method = RequestMethod.POST)
    public @ResponseBody String setStatus(Model model, HttpServletResponse response,
            @RequestParam(value = "id", required = false) Integer[] id,
            @RequestParam(value = "status", required = false) Integer status) {
        response.setContentType("application/json");
        // Check login with role
        if (!managerService.checkLoginWithRole("product_edit")) {
            return "No permission.";
        }

        if (id == null || id.length <= 0 || status == null) {
            return "Error submiting form.";
        }

        for (Integer i : id) {
            Product p = productFacade.find(i);
            p.setStatus(status);
            productFacade.edit(p);
        }
        return "ok";
    }

    // ==== PRODUCT AUTOCOMPLETE LIST ==== \\
    @RequestMapping(value = "autolist", method = RequestMethod.POST)
    public @ResponseBody List<String> getAutoList(Model model, HttpServletResponse response,
            @RequestParam(required = false) Map<String, String> params) {
        response.setContentType("application/json");
        // Check login with role
        if (!managerService.checkLoginWithRole("product_read")) {
            return new ArrayList<String>();
        }
        if (params.get("attr") == null) {
            return new ArrayList<String>();
        }
        String category = params.get("cate");
        if (category == null) {
            category = "";
        }
        List<String> result = new ArrayList<String>();
        result = productFacade.getStringList(params.get("attr") + category);
        return result;
    }

    // ==== PRODUCT ADD - VIEW ==== \\
    @RequestMapping(value = "addOld", method = RequestMethod.GET)
    // Adding optional "cate" parameter by using @RequestParam(required = false)
    public String viewAdd(HttpSession session, Model model, @RequestParam(required = false) Integer cate) {

        if (!managerService.checkLoginWithRole("product_add")) {
            return redirectProductHome;
        }
        // Prepare product model
        Category c = new Category();
        Product p = new Product();
        String cateName = "";
        if (cate != null) {
            c.setId(cate);
            switch (cate) {
                case 1:
                    cateName = "Cpu";
                    break;
                case 2:
                    cateName = "Motherboard";
                    break;
                case 3:
                    cateName = "Gpu";
                    break;
                case 4:
                    cateName = "Memory";
                    break;
                case 5:
                    cateName = "Psu";
                    break;
                case 6:
                    cateName = "Storage";
                    break;
                case 7:
                    cateName = "CpuCooler";
                    break;
                case 8:
                    cateName = "Case";
                    break;
                case 9:
                    cateName = "Monitor";
                    break;
                default:
                    break;
            }
        }
        p.setCategory(c);
        p.setStatus(1);
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
        model.asMap().put("cateName", cateName);
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        // Continue to login page
        return "HTDManager/product_add_old";
    }

    // ==== PRODUCT ADD - VIEW 2 ==== \\
    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd2(Model model) {
        if (!managerService.checkLoginWithRole("product_add")) {
            return redirectProductHome;
        }
        // Prepare product model
        Category c = new Category();
        Product p = new Product();

        p.setCategory(c);
        p.setStatus(1);
        model.addAttribute("product", p);

        // Prepare form url for form submit
        // model.addAttribute("formUrl", "doAdd" + category);
        model.addAttribute("formUrl", "doAdd");

        // Pass category list to view
        model.asMap().put("categories", categoryFacade.findAll());
        // Add indicator attribute for sidemenu highlight
        model.asMap().put("menu", "product");
        // Continue to login page
        return "HTDManager/product_add";
    }

    // ==== PRODUCT ADD - PROCESS 2 ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public @ResponseBody Object doAdd2(@Valid @ModelAttribute("product") Product product, BindingResult error,
            Model model) {
        if (!managerService.checkLoginWithRole("product_add")) {
            return null;
        }
        if (product.getCategory() == null) {
            error.rejectValue("category", "product", "Please choose category");
        }

        if (!error.hasErrors()) {
            productFacade.create(product);

            // Image
            String folderPath = System.getProperty("catalina.base") + "/img/product/" + product.getId();
            File checkPath = new File(folderPath);
            // Check if path is not exists, create path to it
            if (!checkPath.exists()) {
                checkPath.mkdirs();
            }
            // Move image from temp to product folder
            try {
                for (ProductImage img : (List<ProductImage>) session.getAttribute("addProductImage")) {
                    File uploadFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                    File targetFile = new File(System.getProperty("catalina.home") + "/img/product/" + product.getId()
                            + "/" + FilenameUtils.getName(img.getImagePath()));
                    // FileCopyUtils.copy(uploadFile, targetFile);
                    Files.copy(uploadFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(uploadFile.getPath() + " -> " + targetFile.getPath());
                    File thumbUploadFile = new File(
                            System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                    File thumbTargetFile = new File(System.getProperty("catalina.home") + "/img/product/"
                            + product.getId() + "/" + FilenameUtils.getName(img.getThumbnailPath()));
                    Files.copy(thumbUploadFile.toPath(), thumbTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    // FileCopyUtils.copy(thumbUploadFile, thumbTargetFile);
                    System.out.println(thumbUploadFile.getPath() + " -> " + thumbTargetFile.getPath());
                    ProductImage pi = new ProductImage();
                    pi.setImagePath("product/" + product.getId() + "/" + FilenameUtils.getName(img.getImagePath()));
                    pi.setThumbnailPath(
                            "product/" + product.getId() + "/" + FilenameUtils.getName(img.getThumbnailPath()));
                    pi.setMainImage(img.getMainImage());
                    pi.setProduct(product);
                    productImageFacade.create(pi);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // Map<String, String> errorMap = error.getFieldErrors().stream()
        // .collect(Collectors.toMap(e -> e.getField(), e -> e.toString()));
        return error.getAllErrors();
    }

    // ==== PRODUCT TEMPLATE - CATEGORY ==== \\
    @RequestMapping(value = "template", method = RequestMethod.POST)
    public String getTempCate(Model model, @RequestParam(value = "category", required = false) Integer category) {
        model.asMap().put("curCate", category);
        String cateName = "";
        switch (category) {
            case 1:
                cateName = "Cpu";
                break;
            case 2:
                cateName = "Motherboard";
                break;
            case 3:
                cateName = "Gpu";
                break;
            case 4:
                cateName = "Memory";
                break;
            case 5:
                cateName = "Psu";
                break;
            case 6:
                cateName = "Storage";
                break;
            case 7:
                cateName = "CpuCooler";
                break;
            case 8:
                cateName = "Case";
                break;
            case 9:
                cateName = "Monitor";
                break;
            default:
                break;
        }
        model.asMap().put("cateName", cateName);
        // Continue to login page
        return "HTDManager/product_template_2";
    }

    @RequestMapping(value = "clearUploadImage", method = RequestMethod.POST)
    private @ResponseBody Boolean doClearUploadImage() {
        if (session.getAttribute("addProductImage") != null) {
            for (ProductImage img : (List<ProductImage>) session.getAttribute("addProductImage")) {
                File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                if (deleteFile.delete()) {
                    System.out.println("Deleted image: " + deleteFile.getPath());
                } else {
                    System.out.println("Cannot delete image: " + deleteFile.getPath());
                }
                File deleteThumbnailFile = new File(
                        System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                deleteThumbnailFile.delete();
            }
            session.removeAttribute("addProductImage");
        }
        return true;
    }

    @RequestMapping(value = "doUploadImage", method = RequestMethod.POST)
    private @ResponseBody List<ProductImage> uploadImages(@RequestParam("uploadimg") MultipartFile[] uploadimg) {
        List<ProductImage> result = new ArrayList<>();
        if (session.getAttribute("addProductImage") != null) {
            result = (List<ProductImage>) session.getAttribute("addProductImage");
        }
        try {
            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            // Initialize folder path
            String folderPath = System.getProperty("catalina.base") + "/img/product/temp";
            File checkPath = new File(folderPath);
            // Check if path is not exists, create path to it
            if (!checkPath.exists()) {
                checkPath.mkdirs();
            }

            // With each of file, do following
            int count = result.size();
            for (MultipartFile multipartFile : uploadimg) {
                String originName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                String fileName = originName + "." + ext;
                String thumbnailFileName = originName + "_th." + ext;
                String filePath = folderPath + "/" + fileName;
                String thumbnailFilePath = folderPath + "/" + thumbnailFileName;
                // Use Files to copy multipartFile's input stream to declared path
                Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(ImageThumbnail.getImageThumbnail(multipartFile), Paths.get(thumbnailFilePath),
                        StandardCopyOption.REPLACE_EXISTING);
                // Create image data in database
                ProductImage pimg = new ProductImage();
                if (count == 0) {
                    pimg.setMainImage(true);
                } else {
                    pimg.setMainImage(false);
                }
                pimg.setImagePath("product/temp/" + fileName);
                pimg.setThumbnailPath("product/temp/" + thumbnailFileName);
                result.add(pimg);

                count++;
            }
            session.setAttribute("addProductImage", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==== PRODUCT ADD - PROCESS ==== \\
    @RequestMapping(value = "doAddOld", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("product") Product product, BindingResult error, HttpSession session,
            Model model, @RequestParam(value = "uploadimg", required = false) MultipartFile[] uploadimg,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("product_add")) {
            return redirectProductHome;
        }
        for (MultipartFile multipartFile : uploadimg) {
            String contentType = multipartFile.getContentType().substring(0,
                    multipartFile.getContentType().lastIndexOf("/"));
            if (!multipartFile.isEmpty() && !contentType.equals("image")) {
                error.reject("common", "Please choose valid image.");
                break;
            }
        }

        // If there is no error
        if (!error.hasErrors()) {
            // Custom method that create Product object from CPU class
            // Product p = product.toNewProduct();
            productFacade.create(product);

            // Process images
            if (uploadimg != null && uploadimg.length > 0) {
                uploadImages(uploadimg, product, false);
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
        return "redirect:/manager/product/addOld?cate=" + product.getCategory().getId();
    }

    // ==== PRODUCT EDIT - VIEW ==== \\
    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        if (!managerService.checkLoginWithRole("product_edit")) {
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
        String cateName = "";
        switch (p.getCategory().getId()) {
            case 1:
                cateName = "Cpu";
                break;
            case 2:
                cateName = "Motherboard";
                break;
            case 3:
                cateName = "Gpu";
                break;
            case 4:
                cateName = "Memory";
                break;
            case 5:
                cateName = "Psu";
                break;
            case 6:
                cateName = "Storage";
                break;
            case 7:
                cateName = "CpuCooler";
                break;
            case 8:
                cateName = "Case";
                break;
            case 9:
                cateName = "Monitor";
                break;
            default:
                break;
        }

        // Prepare product model
        model.addAttribute("product", p);
        model.asMap().put("cateName", cateName);

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

    // ==== PRODUCT EDIT - PROCESS ==== \\
    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEditCPU(@Valid @ModelAttribute("product") Product product, BindingResult error, Model model,
            @RequestParam(value = "uploadimg", required = false) MultipartFile[] uploadimg,
            RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("product_edit")) {
            return redirectProductHome;
        }

        for (MultipartFile multipartFile : uploadimg) {
            String contentType = multipartFile.getContentType().substring(0,
                    multipartFile.getContentType().lastIndexOf("/"));
            if (!multipartFile.isEmpty() && !contentType.equals("image")) {
                error.reject("common", "Please choose valid image.");
                break;
            }
        }
        // If there is no error
        if (!error.hasErrors()) {
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
        if (!managerService.checkLoginWithRole("product_delete")) {
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
                    File deleteThumbnailFile = new File(
                            System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                    if (deleteFile.delete()) {
                        System.out.println("Deleted image: " + deleteFile.getPath());
                    } else {
                        System.out.println("Cannot delete image: " + deleteFile.getPath());
                    }
                    deleteThumbnailFile.delete();
                    productImageFacade.remove(img);
                }
                try {
                    File deleteFolder = new File(System.getProperty("catalina.home") + "/img/" + id + "/");
                    FileUtils.deleteDirectory(deleteFolder);
                } catch (Exception e) {
                    e.printStackTrace();
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
                    File deleteThumbnailFile = new File(
                            System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                    deleteThumbnailFile.delete();
                    // Then, delete record in database
                    productImageFacade.remove(img);
                }
            }

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            // Initialize folder path
            String folderPath = System.getProperty("catalina.base") + "/img/product/" + product.getId();
            File checkPath = new File(folderPath);
            // Check if path is not exists, create path to it
            if (!checkPath.exists()) {
                checkPath.mkdirs();
            }

            // With each of file, do following
            int count = 0;
            for (MultipartFile multipartFile : uploadimg) {
                // File name: [count].[extension]
                String originName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                String fileName = originName + "." + ext;
                String thumbnailFileName = originName + "_th." + ext;
                // file path:
                // Path_to_glassfish/domains/domain_name/img/product/product_id/count.extension
                String filePath = folderPath + "/" + fileName;
                String thumbnailFilePath = folderPath + "/" + thumbnailFileName;
                // Use Files to copy multipartFile's input stream to declared path
                Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(ImageThumbnail.getImageThumbnail(multipartFile), Paths.get(thumbnailFilePath),
                        StandardCopyOption.REPLACE_EXISTING);

                // Create image data in database
                ProductImage pimg = new ProductImage();
                if (count == 0) {
                    pimg.setMainImage(true);
                } else {
                    pimg.setMainImage(false);
                }
                pimg.setImagePath("product/" + product.getId() + "/" + fileName);
                pimg.setThumbnailPath("product/" + product.getId() + "/" + thumbnailFileName);
                pimg.setProduct(product);
                productImageFacade.create(pimg);
                count++;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
