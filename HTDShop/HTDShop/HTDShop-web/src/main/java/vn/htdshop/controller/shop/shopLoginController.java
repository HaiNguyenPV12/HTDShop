package vn.htdshop.controller.shop;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
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

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ShopService;

/**
 * shopLoginController
 */
@Controller
@RequestMapping("login")
public class shopLoginController {

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewLogin(@ModelAttribute("customer") Customer customer, Model model, ModelMap modelMap,
            HttpSession session) {
        if (shopService.checkLogin()) {
            return "redirect:/";
        }
        model.addAttribute("customer", new Customer());
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.customer", model.asMap().get("error"));
        }
        return "HTDShop/login";
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    public String postLogin(@ModelAttribute("customer") Customer customer, RedirectAttributes redirect,
            @RequestParam(value = "remember", required = false) String remember,
            @RequestParam(value = "redirect", required = false) String redirectUrl, BindingResult error,
            HttpSession session, HttpServletResponse response) {

        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            error.rejectValue("email", "customer", "Username cannot be blank.");
        }
        // Mannually check blank password
        if (customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
            error.rejectValue("password", "customer", "Password cannot be blank.");
        }

        if (!error.hasErrors()) {
            Customer result = customerFacade.checkLogin(customer.getEmail(), customer.getPassword());
            if (result != null) {
                // If ok, save staff's session
                session.setAttribute("loggedInCustomer", result);
                shopService.getLoggedInCart();
                if (remember != null) {
                    Cookie cookie = new Cookie("loggedInCustomer", result.getId().toString());
                    response.addCookie(cookie);
                }
                redirect.addFlashAttribute("goodAlert", "Successfully logged in as \"" + result.getFirstName() + "\".");
                if (redirectUrl == null || redirectUrl.isEmpty()) {
                    return "redirect:/";
                } else {
                    return "redirect:/cart/checkout";
                }
            }
            error.rejectValue("email", "customer", "Invalid Login");
        }

        redirect.addFlashAttribute("error", error);
        redirect.addFlashAttribute("customer", customer);
        return "redirect:/login";
    }

}