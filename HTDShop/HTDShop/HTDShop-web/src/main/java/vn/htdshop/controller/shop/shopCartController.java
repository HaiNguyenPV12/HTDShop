/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.htdshop.entity.CartItem;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Search;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("cart")
public class shopCartController {
    final private String redirectHome = "redirect:";

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @Autowired
    ShopService shopService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "quick", method = RequestMethod.POST)
    public String getQuickCart(Model model) {

        model.asMap().put("cart", shopService.getCart());
        model.asMap().put("shopSv", shopService);
        return "HTDShop/cart_quick";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody String doAdd(Model model, @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "quan", required = false) Integer quan) {
        if (id == null || quan == null) {
            return "error";
        }
        Product p = productFacade.find(id);
        if (p == null) {
            return "error";
        }
        List<CartItem> cart = shopService.getCart();
        if (cart == null) {
            cart = new ArrayList<CartItem>();
        }
        Boolean found = false;
        for (CartItem cartItem : cart) {
            if (cartItem.getId() == id) {
                found = true;
                if (cartItem.getQuan() + quan > p.getStock()) {
                    return "error";
                }
                cartItem.setQuan(cartItem.getQuan() + quan);
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(id, quan));
        }
        session.setAttribute("cart", cart);
        return "ok";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody String doRemove(Model model, @RequestParam(value = "id", required = false) Integer id) {
        if (id == null) {
            return "error";
        }
        if (productFacade.find(id) == null) {
            return "error";
        }
        List<CartItem> cart = shopService.getCart();
        if (cart == null) {
            return "error";
        }
        Boolean found = false;
        for (CartItem cartItem : cart) {
            if (cartItem.getId() == id) {
                found = true;
                cart.remove(cartItem);
                break;
            }
        }
        if (!found) {
            return "error";
        }
        session.setAttribute("cart", cart);
        return "ok";
    }

}
