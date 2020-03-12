/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.htdshop.entity.OrderDetail;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.ProductComment;
import vn.htdshop.entity.ProductCommentReply;
import vn.htdshop.sb.OrderDetailFacadeLocal;
import vn.htdshop.sb.ProductCommentFacadeLocal;
import vn.htdshop.sb.ProductCommentReplyFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.ShopService;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("product")
public class shopProductDetailController {
    final private String redirectHome = "redirect:";
    @EJB(mappedName = "OrderDetailFacade")
    OrderDetailFacadeLocal orderDetailFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "ProductCommentFacade")
    ProductCommentFacadeLocal productCommentFacade;

    @EJB(mappedName = "ProductCommentReplyFacade")
    ProductCommentReplyFacadeLocal productCommentReplyFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getProduct(Model model, @RequestParam(value = "id", required = false) Integer id) {
        shopService.checkLogin();

        Product product = null;
        if (id == null) {
            return redirectHome;
        } else {
            product = productFacade.find(id);
            if (product == null) {
                return redirectHome;
            }
        }

        Double discount = shopService.getDiscountPrice(product);
        if (discount < product.getPrice()) {
            model.asMap().put("discountPrice", discount);
        }
        Map<Integer, Integer> topProductInCategory = orderDetailFacade.getTopProductInCategory("year", 8,
                product.getCategory().getId());
        List<Product> topProducts = new ArrayList<>();

        for (Map.Entry<Integer, Integer> pid : topProductInCategory.entrySet()) {
            Product p = productFacade.find(pid.getKey());
            topProducts.add(p);
        }

        Integer sold = 0;
        for (OrderDetail od : product.getOrderDetailCollection()) {
            if (od.getOrder1().getOrderStatus() == 4) {
                sold += od.getQuantity();
            }
        }

        model.asMap().put("sold", sold);
        model.asMap().put("topProducts", topProducts);
        model.asMap().put("shopSv", shopService);
        model.asMap().put("product", product);
        return "HTDShop/productdetail";

    }

    @RequestMapping(value = "doComment", method = RequestMethod.POST)
    public @ResponseBody Boolean doComment(@RequestParam(required = false) Map<String, String> formData) {
        if (shopService.verifyReCaptcha(formData.get("g-recaptcha-response"))) {
            ProductComment c = new ProductComment();
            c.setId(null);
            c.setCustomer(shopService.getLoggedInCustomer());
            c.setContent(formData.get("comment"));
            c.setProduct(new Product(Integer.parseInt(formData.get("productid"))));
            c.setCreatedAt(new Date());
            productCommentFacade.create(c);
            return true;
        }
        return false;
    }

    @RequestMapping(value = "doReply", method = RequestMethod.POST)
    public @ResponseBody Boolean doReply(@RequestParam(required = false) Map<String, String> formData) {
        if (shopService.verifyReCaptcha(formData.get("g-recaptcha-response"))) {
            ProductCommentReply r = new ProductCommentReply();
            r.setId(null);
            r.setStaff(null);
            r.setCustomer(shopService.getLoggedInCustomer());
            r.setContent(formData.get("reply"));
            r.setProductComment(new ProductComment(Integer.parseInt(formData.get("commentid"))));
            r.setCreatedAt(new Date());
            productCommentReplyFacade.create(r);
            return true;
        }

        return false;
    }

}
