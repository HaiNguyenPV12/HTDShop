/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.Comparator;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.htdshop.entity.PreBuilt;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import vn.htdshop.entity.Customer;
import vn.htdshop.entity.ImageSlide;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.CustomerFacadeLocal;
import vn.htdshop.sb.ImageSlideFacadeLocal;
import vn.htdshop.utility.ShopService;

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

        model.asMap().put("imageslides",
                imageSlideFacade.findAll().stream().filter(img->img.getStatus())
                        .sorted(Comparator.comparing(ImageSlide::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.toList()));
        // modelMap.addAttribute("categories", categoryFacade.findAll());
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
        String referer = request.getHeader("Referer");
        System.out.println(referer);
        if (referer == null || referer.isEmpty()) {
            return "redirect:";
        } else {
            return "redirect:" + referer;
        }
    }

}
