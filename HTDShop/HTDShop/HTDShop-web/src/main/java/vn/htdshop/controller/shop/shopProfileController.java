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

    @EJB(mappedName = "Order1Facade")
    Order1FacadeLocal order1Facade;

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
            if (custom.getPassword() == null || custom.getPassword().isEmpty()) {
                customerOld.setPassword(customerOld.getPassword());
            }else{
                customerOld.setPassword(custom.getPassword());
            }            
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
            customerOld.setPhone(custom.getPhone().trim());
            updated = true;
        }
        if (updated) {
            customerFacade.edit(customerOld);
            session.setAttribute("loggedInCustomer", customerOld);
        }
        redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + custom.getEmail() + "\"!");
        return "redirect:/profile";
    }

    @RequestMapping(value = "orderChecker", method = RequestMethod.GET)
    public String viewOrder(Model model, ModelMap modelMap, HttpSession session) {
        shopService.checkLogin();
        Customer custom = shopService.getLoggedInCustomer();
        model.addAttribute("custom", custom);
        return "HTDShop/orderChecker";

    }

    @RequestMapping(value = "ordertracking", method = RequestMethod.GET)
    public String viewEdit(HttpSession session, Model model, @RequestParam(required = true) Integer id) {
        shopService.checkLogin();
        Order1 o = null;
        if (id != null) {
            o = order1Facade.find(id);
            if (o == null) {
                return "HTDShop/profile";
            }
        } else {
            return "HTDShop/profile";
        }
        model.addAttribute("order", o);
        return "HTDShop/orderTracking";
    }
    

}