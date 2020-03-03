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
        model.addAttribute("formUrl","doAdd");
        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.customer", model.asMap().get("error"));
        }
        return "HTDShop/register";
    }

    // == Add new Customer - Process ==\
    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String doAdd(@Valid @ModelAttribute("customer") Customer customer, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {
        if (shopService.checkLogin()) {
            return "redirect:/";
        }
        // Check email exists
        if (customerFacade.findByEmail(customer.getEmail()) != null) {
            error.rejectValue("email", "customer", "This email is exists.");
        }

        if (!error.hasErrors()) {
            // Insert into database
            customerFacade.create(customer);
            return "HTDShop/login";
        }        
        // Pass alert attribute to notify successful process
        redirect.addFlashAttribute("goodAlert", "Successfully Created \"" + customer.getEmail() + "\"!");
        return "HTDShop/register";
    }

}