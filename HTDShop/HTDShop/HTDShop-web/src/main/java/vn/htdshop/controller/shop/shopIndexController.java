/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.Product;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import vn.htdshop.entity.Customer;
import vn.htdshop.entity.ImageSlide;
import vn.htdshop.entity.Order1;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.ImageSlideFacadeLocal;
import vn.htdshop.utility.ShopService;

import vn.htdshop.sb.*;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("")
public class shopIndexController {

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal prebuiltFacade;

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @EJB(mappedName = "Order1Facade")
    Order1FacadeLocal order1Facade;

    @EJB(mappedName = "OrderDetailFacade")
    OrderDetailFacadeLocal orderDetailFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "ImageSlideFacade")
    ImageSlideFacadeLocal imageSlideFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getHome(Model model) {
        shopService.checkLogin();

        model.asMap().put("imageslides", imageSlideFacade.findAll().stream().filter(img -> img.getStatus())
                .sorted(Comparator.comparing(ImageSlide::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList()));
        Map<Integer, Integer> top8Year = orderDetailFacade.getTopProduct("year", 8); // Best selling
        Map<Integer, Integer> top8Month = orderDetailFacade.getTopProduct("month", 8); // Hot product
        List<Product> bestsellings = new ArrayList<Product>();
        List<Integer> exists = new ArrayList<>();
        for (Map.Entry<Integer, Integer> pid : top8Year.entrySet()) {
            if (!exists.contains(pid.getKey())) {
                Product p = productFacade.find(pid.getKey());
                bestsellings.add(p);
                exists.add(pid.getKey());
            }
        }
        List<Product> hotproducts = new ArrayList<Product>();
        exists = new ArrayList<>();
        for (Map.Entry<Integer, Integer> pid : top8Month.entrySet()) {
            if (!exists.contains(pid.getKey())) {
                Product p = productFacade.find(pid.getKey());
                hotproducts.add(p);
                exists.add(pid.getKey());
            }
        }
        model.asMap().put("bestsellings", bestsellings);
        model.asMap().put("hotproducts", hotproducts);
        model.addAttribute("shopSv", shopService);
        return "HTDShop/index";
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String getTest() {
        return "HTDShop/test";
    }

    @RequestMapping(value = "termsAndServices", method = RequestMethod.GET)
    public String getTermsAndServices() {
        return "HTDShop/termsandservices";
    }

    @RequestMapping(value = "aboutUs", method = RequestMethod.GET)
    public String getAboutUs() {
        return "HTDShop/about";
    }

    @RequestMapping(value = "privacy", method = RequestMethod.GET)
    public String getPrivacyPolicy() {
        return "HTDShop/privacy";
    }

    @RequestMapping(value = "testlogin", method = RequestMethod.GET)
    public String getLogin(@RequestParam(value = "id", required = false) Integer id) {
        Customer c = customerFacade.find(id);
        session.setAttribute("loggedInCustomer", c);
        shopService.getLoggedInCart();
        String referer = request.getHeader("referer");
        System.out.println(referer);
        if (referer == null || referer.isEmpty()) {
            return "redirect:";
        } else {
            return "redirect:" + referer;
        }

    }

    @RequestMapping(value = "testlogincookie", method = RequestMethod.GET)
    public String getLoginCookie(HttpServletResponse response,
            @RequestParam(value = "id", required = false) Integer id) {
        Customer c = customerFacade.find(id);
        session.setAttribute("loggedInCustomer", c);
        shopService.getLoggedInCart();
        Cookie cookie = new Cookie("loggedInCustomer", "" + c.getId());
        cookie.setMaxAge(60 * 60 * 24 * 7 * 4);
        response.addCookie(cookie);

        String referer = request.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            return "redirect:";
        } else {
            return referer;
        }
    }

    @RequestMapping(value = "testlogout", method = RequestMethod.GET)
    public String getLogout(HttpServletResponse response) {
        session.removeAttribute("loggedInCustomer");
        Cookie cookie = new Cookie("loggedInCustomer", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:";
    }

    @RequestMapping(value = "checkOrder", method = RequestMethod.GET)
    public String viewCheckOrder(@ModelAttribute("order") Order1 order, Model model, ModelMap modelMap, HttpSession session) {
        shopService.checkLogin();
        model.addAttribute("order", new Order1());
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.order", model.asMap().get("error"));
        }
        return "HTDShop/checkOrder";
    }

    // @RequestMapping(value = "doCheck", method = RequestMethod.GET)
    // public String viewOrderTracking(Model model, ModelMap modelMap, HttpSession session,
    //         @RequestParam(value = "id", required = false) Integer id,
    //         @RequestParam(value = "phone", required = false) String phone,
    //         RedirectAttributes redirect
    //         ) {
    //     shopService.checkLogin();
    //     Order1 order = order1Facade.findByPhoneAndId(id, phone);
    //     if (order == null) {
    //         // error.reject("error","Order not found");
    //         return "redirect:/checkOrder";
    //     } else {
    //         if (order.getOrderStatus() == 5) {
    //             return "redirect:/checkOrder";
    //         } else {
    //             model.addAttribute("order", order);
    //         }
    //     }
    //     // redirect.addFlashAttribute("error", error);
    //     return "HTDShop/orderTracking";
    // }

    @RequestMapping(value = "doCheck", method = RequestMethod.GET)
    public String postLogin(@ModelAttribute("order") Order1 order, Model model, RedirectAttributes redirect, BindingResult error,
            HttpSession session, HttpServletResponse response) {

        if (order.getId() == null ) {
            error.rejectValue("id", "order", "ID cannot be blank.");
        }
        // Mannually check blank password
        if (order.getCustomer().getPhone() == null ||order.getCustomer().getPhone().isEmpty()) {
            error.rejectValue("phone", "order", "Phone cannot be blank.");
        }

        if (!error.hasErrors()) {
            Order1 result = order1Facade.findByPhoneAndId(order.getId(), order.getCustomer().getPhone());
            if (result != null && result.getOrderStatus() != 5) {
                model.addAttribute("order", result);
                return "HTDShop/orderTracking";
            }
            error.rejectValue("customer.phone", "order", "Not Found Order or Order cancelled");
        }

        redirect.addFlashAttribute("error", error);
        return "redirect:/checkOrder";
    }

    @RequestMapping(value = "contact", method = RequestMethod.GET)
    public String viewContactPage() {
        // return "HTDShop/about";
        return "HTDShop/contact";
    }

    @RequestMapping(value = "services", method = RequestMethod.GET)
    public String viewServicePageType(@RequestParam(value = "type", required = false) String type,
            RedirectAttributes redirect) {
        // return "HTDShop/about";
        if (type != null && !type.trim().isEmpty()) {
            return "redirect:/services#" + type + "Service";
        }
        return "HTDShop/services";
    }

}
