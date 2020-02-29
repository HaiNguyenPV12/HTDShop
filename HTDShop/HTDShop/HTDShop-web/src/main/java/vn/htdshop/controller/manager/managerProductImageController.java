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
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ImageThumbnail;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager/product/image")
public class managerProductImageController {
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

    // ==== PRODUCT IMAGE INDEX ==== \\
    @RequestMapping(value = { "" }, method = RequestMethod.GET)
    public String getHome(Model model, @RequestParam(value = "id", required = false) Integer id) {
        if (!managerService.checkLoginWithRole("product_read")) {
            return redirectHome;
        }
        Product p = null;
        if (id != null) {
            p = productFacade.find(id);
            if (p == null) {
                return redirectProductHome;
            }
        } else {
            return redirectProductHome;
        }

        if (model.asMap().containsKey("goodAlert")) {
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }
        if (model.asMap().containsKey("badAlert")) {
            model.addAttribute("badAlert", model.asMap().get("badAlert"));
        }

        // model.asMap().put("productImages",
        // p.getProductImageCollection().stream()
        // .sorted(Comparator.comparing(ProductImage::getMainImage,
        // Comparator.reverseOrder()))
        // .collect(Collectors.toList()));

        model.asMap().put("menu", "product");
        return "HTDManager/productimage";
    }

    // ==== PRODUCT IMAGE LIST ==== \\
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public @ResponseBody List<ProductImageView> getList(Model model, HttpServletResponse response,
            @RequestParam(value = "id", required = false) Integer id) {
        response.setContentType("application/json");
        // Check login with role
        if (!managerService.checkLoginWithRole("product_read")) {
            return null;
        }

        Product p = null;
        if (id != null) {
            p = productFacade.find(id);
            if (p == null) {
                return null;
            }
        } else {
            return null;
        }
        Collection<ProductImage> productImages = p.getProductImageCollection();
        List<ProductImageView> result = new ArrayList<ProductImageView>();

        for (ProductImage productImage : productImages) {
            result.add(new ProductImageView(productImage));
        }
        result = result.stream().sorted(Comparator.comparing(ProductImageView::getMainImage, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return result;
    }

    // ==== PRODUCT IMAGE - MAKE MAIN IMAGE ==== \\
    @RequestMapping(value = "makeMainImage", method = RequestMethod.POST)
    public @ResponseBody Boolean doSetMainImage(Model model, HttpServletResponse response,
            @RequestParam(value = "id", required = false) Integer id) {
        response.setContentType("application/json");
        // Check login with role
        if (!managerService.checkLoginWithRole("product_edit")) {
            return false;
        }

        ProductImage pi = null;
        if (id != null) {
            pi = productImageFacade.find(id);
            if (pi == null) {
                return false;
            }
        } else {
            return false;
        }

        Collection<ProductImage> productImages = pi.getProduct().getProductImageCollection();
        for (ProductImage productImage : productImages) {
            if (productImage.getId() == pi.getId()) {
                productImage.setMainImage(true);
            } else {
                productImage.setMainImage(false);
            }
            productImageFacade.edit(productImage);
        }

        return true;
    }

    // ==== PRODUCT IMAGE - DELETE IMAGE ==== \\
    @RequestMapping(value = "doDelete", method = RequestMethod.POST)
    public @ResponseBody Boolean doDelete(Model model, HttpServletResponse response,
            @RequestParam(value = "id", required = false) Integer id) {
        response.setContentType("application/json");
        // Check login with role
        if (!managerService.checkLoginWithRole("product_edit")) {
            return false;
        }

        ProductImage pi = null;
        if (id != null) {
            pi = productImageFacade.find(id);
            if (pi == null) {
                return false;
            }
        } else {
            return false;
        }
        Product product = pi.getProduct();

        File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + pi.getImagePath());
        File deleteThumbnailFile = new File(System.getProperty("catalina.home") + "/img/" + pi.getThumbnailPath());
        deleteFile.delete();
        deleteThumbnailFile.delete();

        productImageFacade.remove(pi);

        if (pi.getMainImage() == true && !product.getProductImageCollection().isEmpty()) {
            ProductImage pimain = ((ProductImage) product.getProductImageCollection().toArray()[0]);
            pimain.setMainImage(true);
            productImageFacade.edit(pimain);
        }
        return true;

    }

    // ==== PRODUCT IMAGE - ADD NEW IMAGE ==== \\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public @ResponseBody String doAdd(Model model,
            @RequestParam(value = "uploadimg", required = false) MultipartFile[] uploadimg,
            @RequestParam(value = "id", required = false) Integer id) {
        if (!managerService.checkLoginWithRole("product_edit")) {
            return "No permission!";
        }
        Product product = null;
        if (id != null) {
            product = productFacade.find(id);
            if (product == null) {
                return "Invalid product's id";
            }
        } else {
            return "Invalid product's id";
        }

        for (MultipartFile multipartFile : uploadimg) {
            String contentType = multipartFile.getContentType().substring(0,
                    multipartFile.getContentType().lastIndexOf("/"));
            if (!multipartFile.isEmpty() && !contentType.equals("image")) {
                return "Please choose valid image.";
            }
        }

        // Process images
        if (uploadimg != null && uploadimg.length > 0) {
            if (uploadImages(uploadimg, product)) {
                return "ok";
            } else {
                return "Error while uploading images.";
            }

        } else {
            return "Please choose valid image.";
        }
    }

    private Boolean uploadImages(MultipartFile[] uploadimg, Product product) {
        try {

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            // Initialize folder path
            String folderPath = System.getProperty("catalina.base") + "/img/product/" + product.getId();
            File checkPath = new File(folderPath);
            // Check if path is not exists, create path to it
            if (!checkPath.exists()) {
                checkPath.mkdirs();
            }
            // With each of file, do following
            for (MultipartFile multipartFile : uploadimg) {
                String originName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                String fileName = originName + "." + ext;
                String thumbnailFileName = originName + "_th." + ext;

                // file path:
                // Path_to_glassfish/domains/domain_name/img/product/product_id/filename.extension
                String filePath = folderPath + "/" + fileName;
                String thumbnailFilePath = folderPath + "/" + thumbnailFileName;
                // Use Files to copy multipartFile's input stream to declared path
                Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(ImageThumbnail.getImageThumbnail(multipartFile), Paths.get(thumbnailFilePath),
                        StandardCopyOption.REPLACE_EXISTING);

                // Create image data in database
                ProductImage pimg = new ProductImage();
                if (productFacade.find(product.getId()).getProductImageCollection().isEmpty()) {
                    pimg.setMainImage(true);
                } else {
                    pimg.setMainImage(false);
                }
                pimg.setImagePath("product/" + product.getId() + "/" + fileName);
                pimg.setThumbnailPath("product/" + product.getId() + "/" + thumbnailFileName);
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
