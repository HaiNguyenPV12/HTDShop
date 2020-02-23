package vn.htdshop.controller.shop;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
/**
 * shopRegisterController
 */
@Controller
@RequestMapping("register")
public class shopRegisterController {

    @EJB(mappedName = "CustomerFacade")
    CustomerFacadeLocal customerFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewRegister(Model model, ModelMap modelMap, HttpSession session) {
        if (shopService.checkLogin()) {
            return "redirect:/";
        }
        model.addAttribute("customer", new Customer());
        if (model.asMap().containsKey("error")) {
        model.addAttribute("org.springframework.validation.BindingResult.customer",
        model.asMap().get("error"));
        }
        return "HTDShop/register";
    }

    //== Add new Customer - Process ==\
    
}