/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager")
public class managerIndexController {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getHome() {
        return "HTDManager/index";
    }
}