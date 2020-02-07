/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.expression.Calendars;

import vn.htdshop.entity.Product;
import vn.htdshop.entity.Promotion;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("product")
public class shopProductDetailController {
    final private String redirectHome = "redirect:";
    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getProduct(Model model, @RequestParam(value = "id") Integer id) {
        Product product = null;
        if (id == null) {
            return redirectHome;
        } else {
            product = productFacade.find(id);
            if (product == null) {
                return redirectHome;
            }
        }
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
                    System.out.println("\"" + promo.getPromotionDetail().getName() + "\" - product's discount %: "
                            + percentageDiscount);
                    System.out.println("\"" + promo.getPromotionDetail().getName() + "\" - product's discount exact: "
                            + exactDiscount);
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
                    System.out.println("\"" + promo.getPromotionDetail().getName() + "\" - category's discount %: "
                            + percentageDiscount);
                    System.out.println("\"" + promo.getPromotionDetail().getName() + "\" - category's discount exact: "
                            + exactDiscount);
                    discount += percentageDiscount + exactDiscount;
                }
            }

        }
        System.out.println("Total discount: " + discount);
        System.out.println("Discounted price: " + (product.getPrice() - discount));
        if (discount != 0) {
            model.asMap().put("discountPrice", product.getPrice() - discount);
        }

        model.asMap().put("product", product);
        return "HTDShop/product";

    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String getTest() {
        return "HTDShop/test";
    }
}
