/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.htdshop.entity.CartItem;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.ShopService;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("cart")
public class shopCartController {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

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
            @RequestParam(value = "quan", required = false) Integer quan, HttpServletResponse response) {
        if (id == null || quan == null) {
            return "error";
        }
        Product p = productFacade.find(id);
        if (p == null) {
            return "Cannot find this product!";
        }
        if (p.getStatus() != 1) {
            return "Cannot add this product!";
        }
        List<CartItem> cart = shopService.getCart();
        if (cart == null) {
            cart = new ArrayList<CartItem>();
        }
        Boolean found = false;
        for (CartItem cartItem : cart) {
            if (("a" + id).equals(cartItem.getId())) {
                found = true;
                if (cartItem.getQuan() + quan > p.getStock()) {
                    return "Cannot add because added number is exceeding product's stock.";
                }
                cartItem.setQuan(cartItem.getQuan() + quan);
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem("a" + id, quan));
        }

        if (shopService.checkLogin()) {
            shopService.saveUserCart(cart);
        } else {
            shopService.saveNonUserCart(cart);
            // Cookie cookie = new Cookie("cart", shopService.getCookieCartString(cart));
            // cookie.setMaxAge(60 * 60 * 24 * 7 * 4);
            // response.addCookie(cookie);
        }
        return "ok";
    }

    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody String doRemove(Model model, @RequestParam(value = "id", required = false) String id,
            HttpServletResponse response) {
        if (id == null) {
            return "error";
        }
        boolean isPrebuilt = false;
        if (id.substring(0, 1).equals("b")) {
            isPrebuilt = true;
        }

        if (!isPrebuilt && productFacade.find(Integer.parseInt(id.substring(1))) == null) {
            return "Cannot find this product.";
        } else if (isPrebuilt && preBuiltFacade.find(Integer.parseInt(id.substring(1))) == null) {
            return "Cannot find this prebuilt.";
        }

        List<CartItem> cart = shopService.getCart();
        if (cart == null) {
            return "There's no cart item in cart.";
        }
        boolean found = false;
        Iterator<CartItem> it = cart.iterator();
        while (it.hasNext()) {
            CartItem ci = it.next();
            if (id.equals(ci.getId())) {
                found = true;
                it.remove();
                break;
            }
        }
        if (!found) {
            return "Cannot find this item in your cart.";
        } else {
            if (shopService.checkLogin()) {
                shopService.saveUserCart(cart);
            } else {
                shopService.saveNonUserCart(cart);
            }
        }
        return "ok";
    }

    @RequestMapping(value = "addPreBuilt", method = RequestMethod.POST)
    public @ResponseBody String doAddPreBuilt(Model model, @RequestParam(value = "id", required = false) Integer id,
            @RequestParam(value = "quan", required = false) Integer quan, HttpServletResponse response) {
        if (id == null || quan == null) {
            return "error";
        }
        PreBuilt p = preBuiltFacade.find(id);
        if (p == null) {
            return "Cannot find this pre-built PC!";
        }
        // Check parts is null?
        if (p.getCpu() == null || p.getMotherboard() == null || p.getCases() == null || p.getMemory() == null
                || p.getPsu() == null || p.getStorage() == null) {
            return "Some parts is not exists!";
        }

        // Check parts's status?
        if (p.getCpu().getStatus() != 1 || p.getMotherboard().getStatus() != 1
                || (p.getVga() != null && p.getVga().getStatus() != 1) || p.getCases().getStatus() != 1
                || p.getMemory().getStatus() != 1 || p.getPsu().getStatus() != 1 || p.getStorage().getStatus() != 1
                || (p.getCpucooler() != null && p.getCpucooler().getStatus() != 1)
                || (p.getMonitor() != null && p.getMonitor().getStatus() != 1)) {
            return "Some parts is unavailable!";
        }

        // Check part's stock with current quan
        if (p.getStaff() != null && p.getStock() < quan) {
            return "Stock is not enough! Please try lowering quantity.";
        } else if (p.getCpu().getStock() < quan || p.getMotherboard().getStock() < quan
                || (p.getVga() != null && p.getVga().getStock() < quan) || p.getCases().getStock() < quan
                || p.getMemory().getStock() < quan || p.getPsu().getStock() < quan || p.getStorage().getStock() < quan
                || (p.getCpucooler() != null && p.getCpucooler().getStock() < quan)
                || (p.getMonitor() != null && p.getMonitor().getStock() < quan)) {
            return "Some part's stock is not enough! Please try lowering quantity.";
        }

        List<CartItem> cart = shopService.getCart();
        if (cart == null) {
            cart = new ArrayList<CartItem>();
        }

        // Check stock with total quantity
        String error = "Cannot add ";
        Boolean foundError = false;
        if (p.getStaff() == null) {
            for (CartItem cartItem : cart) {
                if (cartItem.getId().equals("a" + p.getCpu().getId())) {
                    if (cartItem.getQuan() + quan > p.getCpu().getStock()) {
                        error += "|" + p.getCpu().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getCases().getId())) {
                    if (cartItem.getQuan() + quan > p.getCases().getStock()) {
                        error += "|" + p.getCases().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (p.getCpucooler() != null && cartItem.getId().equals("a" + p.getCpucooler().getId())) {
                    if (cartItem.getQuan() + quan > p.getCpucooler().getStock()) {
                        error += "|" + p.getCpucooler().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getMemory().getId())) {
                    if (cartItem.getQuan() + quan > p.getMemory().getStock()) {
                        error += "|" + p.getMemory().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (p.getMonitor() != null && cartItem.getId().equals("a" + p.getMonitor().getId())) {
                    if (cartItem.getQuan() + quan > p.getMonitor().getStock()) {
                        error += "|" + p.getMonitor().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getMotherboard().getId())) {
                    if (cartItem.getQuan() + quan > p.getMotherboard().getStock()) {
                        error += "|" + p.getMotherboard().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getPsu().getId())) {
                    if (cartItem.getQuan() + quan > p.getPsu().getStock()) {
                        error += "|" + p.getPsu().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getStorage().getId())) {
                    if (cartItem.getQuan() + quan > p.getStorage().getStock()) {
                        error += "|" + p.getStorage().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
                if (p.getVga() != null && cartItem.getId().equals("a" + p.getVga().getId())) {
                    if (cartItem.getQuan() + quan > p.getVga().getStock()) {
                        error += "|" + p.getVga().getName() + "| ";
                        foundError = true;
                    }
                    continue;
                }
            }
        } else {
            for (CartItem cartItem : cart) {
                if (cartItem.getId().equals("b" + p.getId())) {
                    if (cartItem.getQuan() + quan > p.getStock()) {
                        return "Not enough stock! Please try lowering quantity.";
                    }
                    break;
                }
            }
        }

        error += " (Not enough stock! Please try lowering quantity.)";
        if (p.getStaff() == null && foundError) {
            return error;
        }
        // Then add

        if (p.getStaff() != null) {
            Boolean found = false;
            for (CartItem cartItem : cart) {
                if (cartItem.getId().equals("b" + p.getId())) {
                    found = true;
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem("b" + p.getId(), quan));
            }
        } else {
            Boolean foundCpu = false, foundMb = false, foundGpu = false, foundCase = false, foundMemory = false,
                    foundPsu = false, foundStorage = false, foundCpuCooler = false, foundMonitor = false;
            for (CartItem cartItem : cart) {
                if (cartItem.getId().equals("a" + p.getCpu().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundCpu = true;
                    continue;
                }
                if (p.getCpucooler() != null && cartItem.getId().equals("a" + p.getCpucooler().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundCpuCooler = true;
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getCases().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundCase = true;
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getMemory().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundMemory = true;
                    continue;
                }
                if (p.getMonitor() != null && cartItem.getId().equals("a" + p.getMonitor().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundMonitor = true;
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getMotherboard().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundMb = true;
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getPsu().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundPsu = true;
                    continue;
                }
                if (cartItem.getId().equals("a" + p.getStorage().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundStorage = true;
                    continue;
                }
                if (p.getVga() != null && cartItem.getId().equals("a" + p.getVga().getId())) {
                    cartItem.setQuan(cartItem.getQuan() + quan);
                    foundGpu = true;
                    continue;
                }
            }

            if (!foundCase) {
                cart.add(new CartItem("a" + p.getCases().getId(), quan));
            }
            if (!foundCpu) {
                cart.add(new CartItem("a" + p.getCpu().getId(), quan));
            }
            if (!foundCpuCooler && p.getCpucooler() != null) {
                cart.add(new CartItem("a" + p.getCpucooler().getId(), quan));
            }
            if (!foundGpu && p.getVga() != null) {
                cart.add(new CartItem("a" + p.getVga().getId(), quan));
            }
            if (!foundMb) {
                cart.add(new CartItem("a" + p.getMotherboard().getId(), quan));
            }
            if (!foundMemory) {
                cart.add(new CartItem("a" + p.getMemory().getId(), quan));
            }
            if (!foundMonitor && p.getMonitor() != null) {
                cart.add(new CartItem("a" + p.getMonitor().getId(), quan));
            }
            if (!foundPsu) {
                cart.add(new CartItem("a" + p.getPsu().getId(), quan));
            }
            if (!foundStorage) {
                cart.add(new CartItem("a" + p.getStorage().getId(), quan));
            }
        }

        if (shopService.checkLogin()) {
            shopService.saveUserCart(cart);
        } else {
            shopService.saveNonUserCart(cart);
        }
        return "ok";
    }

}
