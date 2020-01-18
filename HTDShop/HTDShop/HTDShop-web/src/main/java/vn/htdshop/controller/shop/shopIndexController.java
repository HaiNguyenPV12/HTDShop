/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import javax.ejb.EJB;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.sb.CategoryFacadeLocal;


/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("")
public class shopIndexController {

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getHome(ModelMap modelMap) {
        modelMap.addAttribute("categories", categoryFacade.findAll());
        return "HTDShop/index";
    }

    @RequestMapping(value="test", method=RequestMethod.GET)
    public String getTest() {
        return "HTDShop/test";
    }
    

}
