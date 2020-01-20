/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.entity.PreBuilt;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.PreBuiltFacadeLocal;

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

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome() {
        return "HTDShop/index";
    }

    @RequestMapping(value = "build", method = RequestMethod.GET)
    public String getBuild(HttpSession session) {
        if (isBuildStarted(session)) {
            session.setAttribute("isBuilding", true);
            session.setAttribute("currentBuild", new PreBuilt());
        }

        //TODO handle build all in session.
        return "HTDShop/build";
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String getTest() {
        return "HTDShop/test";
    }

    private boolean isBuildStarted(HttpSession session) {
        return session.getAttribute("isBuilding") == null;
    }
}
