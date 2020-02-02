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
import vn.htdshop.entity.PreBuilt;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build")
public class shopBuildController {

    PreBuilt preBuilt;

    BuildValues partValues;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuild(HttpSession session) {
        if (isBuildStarted(session)) {
            session.setAttribute("isBuilding", true);
            session.setAttribute("currentBuild", new PreBuilt());
        }

        // TODO handle build all in session.
        return "HTDShop/build";
    }

    @RequestMapping(value = "cpu", method = RequestMethod.GET)
    public String getCPUList(HttpSession session, Model model) {
        if (partValues == null || partValues.getPartCategory() != "cpu") {
            partValues = new BuildValues();
            partValues.setPartCategory("cpu");
        }
        model.addAttribute("cpuValues", partValues);
        return "HTDShop/pickCPU";
    }

    @RequestMapping(value = "filterCpu", method = RequestMethod.POST)
    public String requestMethodName(@ModelAttribute("cpuValues") BuildValues cpuValues, BindingResult error,
            RedirectAttributes redirect) {
        partValues = cpuValues;
        return "redirect:/build/cpu";
    }

    private boolean isBuildStarted(HttpSession session) {
        return session.getAttribute("isBuilding") == null;
    }

}