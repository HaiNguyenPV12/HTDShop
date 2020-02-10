package vn.htdshop.controller.shop;

import java.util.Arrays;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.htdshop.entity.Customer;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Promotion;
import vn.htdshop.entity.User;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.UserFacadeLocal;

/**
 * shopService
 */
@Service("shopService")
public class ShopService {
    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "UserFacade")
    UserFacadeLocal userFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    public boolean checkLogin() {
        // System.out.println("Service called.");
        if (session.getAttribute("loggedInCustomer") != null) {
            // System.out.println("Have session.");
            return true;
        } else {
            // System.out.println("No session.");
            String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInCustomer"))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (cookie != null) {
                User user = userFacade.find(Integer.parseInt(cookie));
                // System.out.println("Have cookie.");
                if (user != null) {
                    session.setAttribute("loggedInCustomer", user.getCustomerCollection().toArray()[0]);
                    return true;
                }
            }
            // System.out.println("No cookie.");
        }
        return false;
    }

    public Customer getLoggedInCustomer() {
        if (checkLogin()) {
            return (Customer) session.getAttribute("loggedInCustomer");
        }
        return null;
    }

    public Double getDiscountPrice(Product product) {
        Double discount = 0d;
        if (product.getStatus() != 3) {
            // Discount price calculation
            Double oPrice = product.getPrice();
            // Collection<Promotion> productList = product.getPromotionCollection();
            // product's discount
            for (Promotion promo : product.getPromotionCollection()) {
                if (promo.getMaxQuantity() == null && promo.getMinQuantity() == null) {
                    if (promo.getLimitedQuantity() != null && promo.getQuantityLeft() == 0) {
                        continue;
                    }
                    if (promo.getPromotionDetail().getIsDisabled()) {
                        continue;
                    }
                    if (!(promo.getPromotionDetail().getIsAlways())
                            && (promo.getPromotionDetail().getEndDate().compareTo(new Date()) < 0)) {
                        continue;
                    }
                    Double percentageDiscount = 0d;
                    Double exactDiscount = 0d;
                    if (promo.getPercentage() != null) {
                        percentageDiscount = oPrice * promo.getPercentage() / 100;
                        if (promo.getMaxSaleOff() != null && percentageDiscount > promo.getMaxSaleOff()) {
                            percentageDiscount = promo.getMaxSaleOff();
                        }
                    }
                    if (promo.getExactSaleOff() != null) {
                        exactDiscount = promo.getExactSaleOff();
                    }
                    // System.out.println("\"" + promo.getPromotionDetail().getName() + "\" -
                    // product's discount %: "
                    // + percentageDiscount);
                    // System.out.println("\"" + promo.getPromotionDetail().getName() + "\" -
                    // product's discount exact: "
                    // + exactDiscount);
                    discount += percentageDiscount + exactDiscount;
                }
            }

            // category's percentage discount
            // Init valid promotion list
            for (Promotion promo : product.getCategory().getPromotionCollection()) {
                if (promo.getMaxQuantity() == null && promo.getMinQuantity() == null) {
                    if (promo.getLimitedQuantity() != null && promo.getQuantityLeft() == 0) {
                        continue;
                    }
                    if (promo.getPromotionDetail().getIsDisabled()) {
                        continue;
                    }
                    if (!(promo.getPromotionDetail().getIsAlways())
                            && (promo.getPromotionDetail().getEndDate().compareTo(new Date()) < 0)) {
                        continue;
                    }
                    Double percentageDiscount = 0d;
                    Double exactDiscount = 0d;
                    if (promo.getPercentage() != null) {
                        percentageDiscount = oPrice * promo.getPercentage() / 100;
                        if (promo.getMaxSaleOff() != null && percentageDiscount > promo.getMaxSaleOff()) {
                            percentageDiscount = promo.getMaxSaleOff();
                        }
                    }
                    if (promo.getExactSaleOff() != null) {
                        exactDiscount = promo.getExactSaleOff();
                    }
                    // System.out.println("\"" + promo.getPromotionDetail().getName() + "\" -
                    // category's discount %: "
                    // + percentageDiscount);
                    // System.out.println("\"" + promo.getPromotionDetail().getName() + "\" -
                    // category's discount exact: "
                    // + exactDiscount);
                    discount += percentageDiscount + exactDiscount;
                }
            }
        }
        return product.getPrice() - discount;
    }

    public static String test() {
        return "test";
    }
}