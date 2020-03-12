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
    public String viewLogin(Model model, ModelMap modelMap, HttpSession session) {
        if (shopService.checkLogin()) {
            return "redirect:/";
        }
        // model.addAttribute("customer", new Customer());
        // if (model.asMap().containsKey("error")) {
        // model.addAttribute("org.springframework.validation.BindingResult.customer",
        // model.asMap().get("error"));
        // }
        return "HTDShop/login";
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    public String postLogin(@RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password, RedirectAttributes redirect,
            @RequestParam(value = "remember", required = false) String remember,
            @RequestParam(value = "redirect", required = false) String redirectUrl,
            HttpSession session, HttpServletResponse response) {

        // Check if error exists
        // If not, start to check login

        Customer result = customerFacade.checkLogin(email, password);
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
            
        return "redirect:/login";
    }

}