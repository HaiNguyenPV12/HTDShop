package vn.htdshop.controller.shop;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
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
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build")
public class shopBuildController {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    PreBuilt preBuilt;

    BuildValues partValues;

    List<Product> buildProductList = null;

    @PostConstruct
    public void init() {
        buildProductList = productFacade.findAll();
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuild(HttpSession session) {
        if (isBuildStarted(session)) {
            session.setAttribute("isBuilding", true);
            session.setAttribute("currentBuild", new PreBuilt());
        }

        for (String socket : cpuSockets()) {
            System.out.println(socket);
        }
        // TODO handle build all in session.
        return "HTDShop/build";
    }

    @RequestMapping(value = "cpu", method = RequestMethod.GET)
    public String getCPUList(HttpSession session, Model model) {
        if (partValues == null || !partValues.getPartCategory().equals("cpu")) {
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

    private List<String> cpuSockets() {
        // TODO check if motherboard is picked.
        List<String> sockets = new ArrayList<>();
        sockets = buildProductList.stream().filter(p -> p.getCategory().getId() == 1).map(s -> s.getSocket())
                .collect(Collectors.toList());
        // SELECT DISTINCT
        sockets = sockets.stream().distinct().collect(Collectors.toList());
        return sockets;
    }
}
