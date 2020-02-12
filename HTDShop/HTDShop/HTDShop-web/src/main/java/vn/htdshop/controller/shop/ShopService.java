package vn.htdshop.controller.shop;

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

import vn.htdshop.entity.Customer;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Promotion;
import vn.htdshop.entity.User;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.PromotionFacadeLocal;
import vn.htdshop.sb.UserFacadeLocal;

/**
 * shopService
 */
@Service("shopService")
public class ShopService {
    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "UserFacade")
    UserFacadeLocal userFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    public boolean checkLogin() {
        // System.out.println("Service called.");
        Boolean result = false;
        if (session.getAttribute("loggedInCustomer") != null) {
            // System.out.println("Have session.");
            result = true;
        } else {
            // System.out.println("No session.");
            String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInCustomer"))
                    .findFirst().map(Cookie::getValue).orElse(null);
            if (cookie != null) {
                User user = userFacade.find(Integer.parseInt(cookie));
                // System.out.println("Have cookie.");
                if (user != null) {
                    session.setAttribute("loggedInCustomer", user.getCustomerCollection().toArray()[0]);
                    result = true;
                }
            }
            // System.out.println("No cookie.");
        }
        if (session.getAttribute("categories") == null) {
            session.setAttribute("categories", categoryFacade.findAll());
        }
        return result;
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

    public Double getPreBuiltPrice(PreBuilt prebuilt) {
        Double result = 0d;
        if (prebuilt.getCpu() != null) {
            result += getDiscountPrice(prebuilt.getCpu());
        }
        if (prebuilt.getMotherboard() != null) {
            result += getDiscountPrice(prebuilt.getMotherboard());
        }
        if (prebuilt.getVga() != null) {
            result += getDiscountPrice(prebuilt.getVga());
        }
        if (prebuilt.getMemory() != null) {
            result += getDiscountPrice(prebuilt.getMemory());
        }
        if (prebuilt.getPsu() != null) {
            result += getDiscountPrice(prebuilt.getPsu());
        }
        if (prebuilt.getStorage() != null) {
            result += getDiscountPrice(prebuilt.getStorage());
        }
        if (prebuilt.getCpucooler() != null) {
            result += getDiscountPrice(prebuilt.getCpucooler());
        }
        if (prebuilt.getCases() != null) {
            result += getDiscountPrice(prebuilt.getCases());
        }
        if (prebuilt.getMonitor() != null) {
            result += getDiscountPrice(prebuilt.getMonitor());
        }
        return result;
    }

    public Double getPreBuiltDiscountPrice(PreBuilt prebuilt) {
        Double discount = 0d;
        List<Promotion> promoList = promotionFacade.findAll().stream().filter(p -> p.getPreBuiltTarget() != null)
                .collect(Collectors.toList());
        Double oPrice = getPreBuiltPrice(prebuilt);
        for (Promotion promo : promoList) {
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
                if (promo.getPreBuiltTarget() == 1 && prebuilt.getStaff() == null) {
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
                discount += percentageDiscount + exactDiscount;
            }
        }
        return getPreBuiltPrice(prebuilt) - discount;
    }

}