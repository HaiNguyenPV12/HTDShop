package vn.htdshop.controller.shop;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ShopService;

/**
 * shopRegisterController
 */
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("profile")
public class shopProfileController {

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewProfile(Model model, ModelMap modelMap, HttpSession session) {
        shopService.checkLogin();
        Customer custom = shopService.getLoggedInCustomer();
        model.addAttribute("custom", custom);
        model.asMap().put("shopSv", shopService);
        return "HTDShop/profile";
    }

    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("custom") Customer custom, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {

        model.addAttribute("error", error);

        Customer customerOld = shopService.getLoggedInCustomer();
        boolean updated = false;
        if (!customerOld.getFirstName().equals(custom.getFirstName())) {
            customerOld.setFirstName(custom.getFirstName());
            updated = true;
        }
        if (!customerOld.getLastName().equals(custom.getLastName())) {
            customerOld.setLastName(custom.getLastName());
            updated = true;
        }
        if (!customerOld.getAddress().equals(custom.getAddress())) {
            customerOld.setAddress(custom.getAddress());
            updated = true;
        }
        if (!customerOld.getPassword().equals(custom.getPassword())) {
            customerOld.setPassword(custom.getPassword());
            updated = true;
        }
        if (!customerOld.getGender().equals(custom.getGender())) {
            customerOld.setGender(custom.getGender());
            updated = true;
        }
        if (!customerOld.getBirthday().equals(custom.getBirthday())) {
            customerOld.setBirthday(custom.getBirthday());
            updated = true;
        }
        if (!customerOld.getPhone().equals(custom.getPhone())) {
            customerOld.setPhone(custom.getPhone());
            updated = true;
        }
        if (updated) {
            customerFacade.edit(customerOld);
            session.setAttribute("loggedInCustomer", customerOld);
        }
        redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + custom.getEmail() + "\"!");
        return "HTDShop/profile";
    }

    @RequestMapping(value = "orderchecker", method = RequestMethod.GET)
    public String viewOrder(Model model, ModelMap modelMap, HttpSession session) {
        if (shopService.checkLogin() == true) {
            Customer custom = shopService.getLoggedInCustomer();
            model.addAttribute("custom", custom);
            return "HTDShop/orderChecker";
        }
        ;
        return "redirect:/";
    }

}