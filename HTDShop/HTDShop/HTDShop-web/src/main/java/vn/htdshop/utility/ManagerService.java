package vn.htdshop.utility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.htdshop.entity.Product;
import vn.htdshop.entity.ProductImage;
import vn.htdshop.entity.RoleRights;
import vn.htdshop.entity.Staff;
import vn.htdshop.sb.ProductImageFacadeLocal;
import vn.htdshop.sb.StaffFacadeLocal;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
/**
 * managerService
 */
@Service("managerService")
public class ManagerService {

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "ProductImageFacade")
    ProductImageFacadeLocal productImageFacade;

    @EJB(mappedName = "Order1Facade")
    Order1FacadeLocal order1Facade;

    List<ProductImage> tempUploadImage;

    public Boolean checkLogin() {
        if (session.getAttribute("loggedInStaff") != null) {
            return true;
        } else {
            if (request.getCookies() != null) {
                String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInStaff"))
                        .findFirst().map(Cookie::getValue).orElse(null);
                if (cookie != null) {
                    Staff staff = staffFacade.find(cookie);
                    if (staff != null) {
                        session.setAttribute("loggedInStaff", staff);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Boolean checkLoginWithRole(String role) {
        if (checkLogin()) {
            String user = ((Staff) session.getAttribute("loggedInStaff")).getUsername();
            for (RoleRights roleRight : staffFacade.find(user).getRole().getRoleRightsCollection()) {
                if (roleRight.getRightsDetail().getTag().equals(role)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Staff getLoggedInStaff() {
        if (checkLogin()) {
            return (Staff) session.getAttribute("loggedInStaff");
        }
        return null;
    }

    public List<ProductImage> getTempImages() {
        return tempUploadImage;
    }

    public boolean setTempImages(Collection<ProductImage> list) {
        tempUploadImage = new ArrayList<>();
        for (ProductImage img : list) {
            ProductImage pi = new ProductImage();
            pi.setId(img.getId());
            pi.setImagePath(img.getImagePath());
            pi.setThumbnailPath(img.getThumbnailPath());
            pi.setMainImage(img.getMainImage());
            pi.setProduct(new Product(img.getProduct().getId()));
            tempUploadImage.add(pi);
        }
        return true;
    }

    public List<ProductImage> uploadTempImages(MultipartFile[] uploadimg) {
        if (tempUploadImage == null) {
            tempUploadImage = new ArrayList<>();
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
            int count = tempUploadImage.size();
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
                if (count == 0 || tempUploadImage.stream().filter(p -> p.getMainImage()).collect(Collectors.toList())
                        .size() == 0) {
                    pimg.setMainImage(true);
                } else {
                    pimg.setMainImage(false);
                }
                pimg.setImagePath("product/temp/" + fileName);
                pimg.setThumbnailPath("product/temp/" + thumbnailFileName);
                tempUploadImage.add(pimg);

                count++;
            }
            return tempUploadImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public boolean deleteTempImage(String imgPath) {
        Iterator<ProductImage> itr = tempUploadImage.iterator();
        while (itr.hasNext()) {
            ProductImage img = itr.next();
            if (img.getImagePath().equals(imgPath)) {
                File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                deleteFile.delete();
                if (img.getThumbnailPath() != null && !img.getThumbnailPath().isEmpty()) {
                    File deleteThumbnailFile = new File(
                            System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                    deleteThumbnailFile.delete();
                }
                if (img.getId() != null) {
                    productImageFacade.remove(img);
                }
                itr.remove();
                break;
            }
        }
        if (tempUploadImage.size() > 0
                && tempUploadImage.stream().filter(p -> p.getMainImage()).collect(Collectors.toList()).size() == 0) {
            ProductImage pi = tempUploadImage.get(0);
            pi.setMainImage(true);
            tempUploadImage.set(0, pi);
            if (pi.getId() != null) {
                productImageFacade.edit(pi);
            }
        }
        return true;
    }

    public boolean deleteAllTempImage(boolean deleteAll) {
        if (tempUploadImage != null) {
            Iterator<ProductImage> itr = tempUploadImage.iterator();
            while (itr.hasNext()) {
                ProductImage img = itr.next();
                if (!deleteAll && img.getId() != null) {
                    itr.remove();
                } else {
                    File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                    deleteFile.delete();
                    if (img.getThumbnailPath() != null && !img.getThumbnailPath().isEmpty()) {
                        File deleteThumbnailFile = new File(
                                System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                        deleteThumbnailFile.delete();
                    }
                    if (img.getId() != null) {
                        productImageFacade.remove(img);
                    }
                    itr.remove();
                }
            }
        }
        return true;
    }

    public boolean moveTempImageToProduct(ProductImage img, Integer id) {
        try {
            if (img.getId() == null) {
                File uploadFile = new File(System.getProperty("catalina.home") + "/img/" + img.getImagePath());
                File targetFile = new File(System.getProperty("catalina.home") + "/img/product/" + id + "/"
                        + FilenameUtils.getName(img.getImagePath()));
                Files.copy(uploadFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                File thumbUploadFile = new File(System.getProperty("catalina.home") + "/img/" + img.getThumbnailPath());
                File thumbTargetFile = new File(System.getProperty("catalina.home") + "/img/product/" + id + "/"
                        + FilenameUtils.getName(img.getThumbnailPath()));
                Files.copy(thumbUploadFile.toPath(), thumbTargetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCateName(Integer id) {
        String cateName = "";
        switch (id) {
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
        return cateName;
    }

    public Double getRevenue() {
        Double result = 0d;
        List<Order1> order = order1Facade.findByOrderStatus();
        if (order!=null && order.size()>0) {
            for (Order1 item : order) {
                List<OrderDetail> orderDetails = (List<OrderDetail>) item.getOrderDetailCollection();
                for (OrderDetail orderdDetail : orderDetails) {
                    result += orderdDetail.getPrice();
                }
            }
        }
        return result;
    }

}
