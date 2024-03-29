package vn.htdshop.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.htdshop.entity.CartItem;
import vn.htdshop.entity.Customer;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Promotion;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.sb.PromotionFacadeLocal;

/**
 * shopService
 */
@Service("buildService")
public class BuildService {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    PreBuilt preBuilt; // current build.

    List<Product> buildProductList = null;

    // boolean isBuilding = false;
    // Current build in session
    public PreBuilt getSessionPrebuilt() {
        if (session.getAttribute("prebuiltSession") == null) {
            session.setAttribute("prebuiltSession", new PreBuilt());
        }
        return (PreBuilt) session.getAttribute("prebuiltSession");
    }

    public void setSessionPrebuilt(PreBuilt prebuilt) {
        session.setAttribute("prebuiltSession", prebuilt);
    }

    public boolean isSessionBuilding() {
        if (session.getAttribute("isBuilding") == null) {
            return false;
        }
        return (boolean) session.getAttribute("isBuilding");
    }

    public void setSessionBuilding(Boolean isBuilding) {
        session.setAttribute("isBuilding", isBuilding);
    }

    public List<Product> getSessionProductList() {
        try {
            List<Product> result = (List<Product>) session.getAttribute("buildProductList");
            if (result == null || result.size() == 0) {
                result = productFacade.findAll();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("buildProductList", productFacade.findAll());
            return (List<Product>) session.getAttribute("buildProductList");
        }
    }

    public void setSessionProductList() {
        session.setAttribute("buildProductList", productFacade.findAll());
    }

    public void initBuildApp() {
        boolean isBuilding = isSessionBuilding();
        if (!isBuilding) {
            setSessionProductList();
            PreBuilt preBuilt = new PreBuilt();
            setSessionPrebuilt(preBuilt);
            setSessionBuilding(true);
        }
    }

    public boolean isBuildAppStarted() {
        return isSessionBuilding();
    }

    public PreBuilt getPreBuilt() {
        return preBuilt;
    }

    public List<String> getList(String type) {
        return productFacade.getStringList(type);
    }

    public List<String> getList2(String type, String options) {
        System.out.println(type + "_" + options);
        return productFacade.getStringList(type, options);

    }

    public List<String> motherboardFormFactorSizes() {
        List<String> sizes = new ArrayList<>();
        sizes.add("Pico ITX");
        sizes.add("Nano ITX");
        sizes.add("Mini ITX");
        sizes.add("Micro ATX");
        sizes.add("ATX");
        sizes.add("EATX");
        return sizes;
    }

    public List<String> psuFormFactorSizes() {
        List<String> sizes = new ArrayList<>();
        sizes.add("SFX");
        sizes.add("ATX");
        return sizes;
    }
}
