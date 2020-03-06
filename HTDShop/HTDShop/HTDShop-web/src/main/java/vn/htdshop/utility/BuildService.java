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

    // public void setProductList() {
    // buildProductList = productFacade.findAll();
    // }

    // public List<Product> getProductList() {
    // if (buildProductList == null) {
    // setProductList();
    // }
    // return buildProductList;
    // }

    public boolean isBuildAppStarted() {
        return isSessionBuilding();
    }

    public PreBuilt getPreBuilt() {
        return preBuilt;
    }
}
