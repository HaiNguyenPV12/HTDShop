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
        model.asMap().put("cust", session.getAttribute("loggedInCustomer"));       
        return "redirect:/profile";
    }

    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public String doEdit(@Valid @ModelAttribute("customer") Customer customer, BindingResult error, HttpSession session,
            Model model, RedirectAttributes redirect) {
        if (!shopService.checkLogin()) {
            return "redirect:/";
        }
        model.addAttribute("error", error);
        
        if (!error.hasErrors()) {
            // Update in database
            customerFacade.edit(customer);
            return "redirect:/profile";
        }
        redirect.addFlashAttribute("goodAlert", "Successfully updated \"" + customer.getId() + "\"!");
        return "redirect:/profile";
    }   
}