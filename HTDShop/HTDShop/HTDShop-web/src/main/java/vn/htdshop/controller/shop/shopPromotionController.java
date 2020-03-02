/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.htdshop.entity.PromotionDetail;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.sb.PromotionDetailFacadeLocal;
import vn.htdshop.sb.PromotionFacadeLocal;
import vn.htdshop.utility.ShopService;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("promotion")
public class shopPromotionController {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PromotionFacade")
    PromotionFacadeLocal promotionFacade;

    @EJB(mappedName = "PromotionDetailFacade")
    PromotionDetailFacadeLocal promotionDetailFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getProduct(Model model, @RequestParam(value = "id", required = false) Integer id) {
        shopService.checkLogin();

        List<PromotionDetail> promolist = promotionDetailFacade.findAll().stream()
                .filter(p -> p.getIsDisabled() == false)
                .filter(p -> p.getIsAlways()
                        || (!p.getIsAlways() && p.getEndDate().compareTo(new LocalDate().toDate()) >= 0))
                .collect(Collectors.toList());

        model.asMap().put("promolist", promolist);
        return "HTDShop/promotion";

    }

    // @RequestMapping(value = "doReply", method = RequestMethod.POST)
    // public @ResponseBody Boolean doReply(@RequestParam(required = false)
    // Map<String, String> formData) {
    // if (shopService.verifyReCaptcha(formData.get("g-recaptcha-response"))) {
    // ProductCommentReply r = new ProductCommentReply();
    // r.setId(null);
    // r.setStaff(null);
    // r.setCustomer(shopService.getLoggedInCustomer());
    // r.setContent(formData.get("reply"));
    // r.setProductComment(new
    // ProductComment(Integer.parseInt(formData.get("commentid"))));
    // r.setCreatedAt(new Date());
    // productCommentReplyFacade.create(r);
    // return true;
    // }

    // return false;
    // }

}
