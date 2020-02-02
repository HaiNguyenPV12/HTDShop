package vn.htdshop.controller.shop;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.entity.BuildValues;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build")
public class shopBuildController {
    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

    @RequestMapping(value = "cpu", method = RequestMethod.GET)
    public String getCPUList(HttpSession session, Model model) {
        if (model.asMap().get("cpuValues") == null) {
            model.addAttribute("cpuValues", new BuildValues(1, "test manu"));
        }
        return "HTDShop/pickCPU";
    }

    @RequestMapping(value = "filterCpu", method = RequestMethod.POST)
    public String requestMethodName(@ModelAttribute("cpuValues") BuildValues cpuValues, BindingResult error,
            RedirectAttributes redirect) {
        redirect.addAttribute("cpuValues", cpuValues);
        return "redirect:/build/cpu";
    }

}