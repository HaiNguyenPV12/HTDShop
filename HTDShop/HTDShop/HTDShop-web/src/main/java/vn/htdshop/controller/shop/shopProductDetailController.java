/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.Date;

import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.htdshop.entity.Product;
import vn.htdshop.entity.ProductComment;
import vn.htdshop.entity.ProductCommentReply;
import vn.htdshop.sb.ProductCommentFacadeLocal;
import vn.htdshop.sb.ProductCommentReplyFacadeLocal;
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

        model.asMap().put("product", product);
        return "HTDShop/productdetail";

    }

    @RequestMapping(value = "doComment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String doComment(@RequestBody MultiValueMap<String, String> formData) {
        ProductComment c = new ProductComment();
        c.setId(null);
        c.setCustomer(shopService.getLoggedInCustomer());
        c.setContent(formData.getFirst("comment"));
        c.setProduct(new Product(Integer.parseInt(formData.getFirst("productid"))));
        c.setCreatedAt(new Date());
        productCommentFacade.create(c);
        return "redirect:/product?id=" + formData.getFirst("productid");
    }

    @RequestMapping(value = "doReply", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String doReply(@RequestBody MultiValueMap<String, String> formData) {
        ProductCommentReply r = new ProductCommentReply();
        r.setId(null);
        r.setStaff(null);
        r.setCustomer(shopService.getLoggedInCustomer());
        r.setContent(formData.getFirst("reply"));
        r.setProductComment(new ProductComment(Integer.parseInt(formData.getFirst("commentid"))));
        r.setCreatedAt(new Date());
        productCommentReplyFacade.create(r);
        return "redirect:/product?id=" + formData.getFirst("productid");
    }
}
