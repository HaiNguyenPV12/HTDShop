package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.htdshop.utility.BuildService;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build")
public class shopBuildController {

    boolean isFilteringCPU = false;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    PreBuilt preBuilt; // current build.

    BuildValues partValues; // current part filter values

    List<Product> buildProductList = null;

    @Autowired
    BuildService buildService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuild(HttpSession session) {
        // buildService.initBuildApp();
        if (isBuildStarted(session)) {
            session.setAttribute("isBuilding", true);
            session.setAttribute("currentBuild", new PreBuilt());
            preBuilt = new PreBuilt();
        }
        // TODO handle build all in session.
        return "HTDShop/build";
    }

    private boolean isBuildStarted(HttpSession session) {
        return session.getAttribute("isBuilding") == null;
    }

    // @RequestMapping(value = "cpu", method = RequestMethod.GET)
    // public String getCPUList(HttpSession session, Model model) {
    // if (partValues == null || !partValues.getPartCategory().equals("cpu")) {
    // partValues = new BuildValues();
    // partValues.setPartCategory("cpu");
    // }
    // model.addAttribute("cpuSocketList", cpuSockets());
    // model.addAttribute("cpuManufacturerList", cpuManufacturers());
    // model.addAttribute("cpuSeriesList", cpuSeries());
    // model.addAttribute("cpuValues", partValues);
    // model.addAttribute("filteredCPU", filterCPU());
    // return "HTDShop/pickCPU";
    // }

    // @RequestMapping(value = "filterCpu", method = RequestMethod.POST)
    // public String requestMethodName(@ModelAttribute("cpuValues") BuildValues
    // cpuValues, BindingResult error,
    // RedirectAttributes redirect) {
    // if (!cpuValues.getManufacturer().equals("all")) {
    // isFilteringCPU = true;
    // } else {
    // isFilteringCPU = false;
    // }
    // partValues = cpuValues;
    // return "redirect:/build/cpu";
    // }

    // private List<String> cpuSockets() {
    // // TODO check if motherboard is picked.
    // // TODO check filtered manufacturer
    // List<String> sockets = new ArrayList<>();
    // sockets = buildProductList.stream().filter(p -> p.getCategory().getId() ==
    // 1).map(s -> s.getSocket()).distinct()
    // .collect(Collectors.toList());
    // // SELECT DISTINCT
    // // sockets = sockets.stream().distinct().collect(Collectors.toList());
    // return sockets;
    // }

    // private List<String> cpuManufacturers() {
    // // TODO check if motherboard is picked.

    // if (preBuilt == null || !isFilteringCPU) {
    // partValues.setManufacturer("all");
    // }
    // List<String> manufacturers = new ArrayList<>();
    // manufacturers = buildProductList.stream().filter(p -> p.getCategory().getId()
    // == 1)
    // .map(s -> s.getManufacturer()).distinct().collect(Collectors.toList());
    // return manufacturers;
    // }

    // private List<String> cpuSeries() {
    // List<String> series = new ArrayList<>();
    // series = buildProductList.stream().filter(p -> p.getCategory().getId() ==
    // 1).map(s -> s.getSeries()).distinct()
    // .collect(Collectors.toList());
    // return series;
    // }

    // private List<Product> filterCPU() {
    // List<Product> cpus = new ArrayList<>();
    // if (partValues == null) {
    // return buildProductList.stream().filter(p -> p.getCategory().getId() ==
    // 1).collect(Collectors.toList());
    // }
    // cpus = buildProductList.stream().filter(p -> p.getCategory().getId() ==
    // 1).collect(Collectors.toList());
    // // filter by manufacturers
    // if (!partValues.getManufacturer().equals("all")) {
    // cpus = cpus.stream().filter(m ->
    // m.getManufacturer().equals(partValues.getManufacturer()))
    // .collect(Collectors.toList());
    // }
    // // filter by socket
    // // filter by series
    // // filter by core&threads
    // return cpus;
    // }
}
