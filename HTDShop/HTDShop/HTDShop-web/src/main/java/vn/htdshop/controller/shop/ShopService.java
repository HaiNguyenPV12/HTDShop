package vn.htdshop.controller.shop;

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
@Service("shopService")
public class ShopService {

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    public List<CartItem> getCart() {
        // Compare quantity with product's stock
        List<CartItem> result = (List<CartItem>) session.getAttribute("cart");
        if (result != null) {
            for (CartItem item : result) {
                Product p = productFacade.find(item.getId());
                if (item.getQuan() > p.getStock()) {
                    item.setQuan(p.getStock());
                }
                if (p.getStatus() != 1) {
                    item.setQuan(0);
                }
            }
        }
        session.setAttribute("cart", result);
        return result;
    }

    public Double getCartTotal() {
        Double result = 0d;
        if (getCart() != null && getCart().size() > 0) {
            for (CartItem item : getCart()) {
                result += (getDiscountPrice(productFacade.find(item.getId())) * item.getQuan());
            }
        }

        return result;
    }

    public Product getProduct(Integer id) {
        return productFacade.find(id);
    }

    public boolean checkLogin() {
        Boolean result = false;
        if (session.getAttribute("loggedInCustomer") != null) {
            result = true;
        } else {
            if (request.getCookies() != null) {
                String cookie = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("loggedInCustomer"))
                        .findFirst().map(Cookie::getValue).orElse(null);
                if (cookie != null) {
                    Customer customer = customerFacade.find(Integer.parseInt(cookie));
                    if (customer != null) {
                        session.setAttribute("loggedInCustomer", customer);
                        result = true;
                    }
                }
            }

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

    public List<String> getList(String attr) {
        List<String> result = new ArrayList<>();

        if (attr.equals("sk")) {

        }
        return result;
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
