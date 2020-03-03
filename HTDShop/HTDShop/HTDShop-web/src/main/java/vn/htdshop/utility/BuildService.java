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

    boolean isBuilding = false;

    public PreBuilt getSessionPrebuilt() {
        return (PreBuilt) session.getAttribute("prebuiltSession");
    }

    public void setSessionPrebuilt(PreBuilt prebuilt) {
        session.setAttribute("prebuiltSession", prebuilt);
    }

    public void initBuildApp() {
        if (!isBuilding) {
            setProductList();
            preBuilt = new PreBuilt();
            isBuilding = true;
        }
    }

    public void setProductList() {
        buildProductList = productFacade.findAll();
    }

    public List<Product> getProductList() {
        if (buildProductList == null) {
            setProductList();
        }
        return buildProductList;
    }

    public boolean isBuildAppStarted() {
        return isBuilding;
    }

    public PreBuilt getPreBuilt() {
        return preBuilt;
    }
}
