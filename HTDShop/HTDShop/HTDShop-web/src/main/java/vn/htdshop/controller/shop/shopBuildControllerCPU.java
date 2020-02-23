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

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build/cpu")
public class shopBuildControllerCPU {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    boolean isFilteringCPU = false;

    BuildValues cpuValues = null;

    PreBuilt preBuiltCPU = null;

    List<Product> buildProductList = null;

    @Autowired
    BuildService buildService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCPUList(HttpSession session, Model model) {
        buildService.initBuildApp();
        buildProductList = buildService.getProductList();

        if (cpuValues == null || !cpuValues.getPartCategory().equals("cpu")) {
            cpuValues = new BuildValues();
            initFilterValues();
        }

        model.addAttribute("cpuSocketList", cpuSockets());
        model.addAttribute("cpuManufacturerList", cpuManufacturers());
        model.addAttribute("cpuSeriesList", cpuSeries());
        model.addAttribute("cpuValues", cpuValues);
        model.addAttribute("filteredCPU", filterCPU());
        return "HTDShop/pickCPU";
    }

    @RequestMapping(value = "filterCpu", method = RequestMethod.POST)
    public String requestMethodName(@ModelAttribute("cpuValues") BuildValues cpuValues, BindingResult error,
            RedirectAttributes redirect) {
        if (!cpuValues.getManufacturer().equals("all")) {
            isFilteringCPU = true;
        } else {
            isFilteringCPU = false;
        }
        // apply values to form
        this.cpuValues = cpuValues;
        return "redirect:/build/cpu";
    }

    private List<String> cpuSockets() {
        // TODO check if motherboard is picked.
        List<String> sockets = new ArrayList<>();
        // filter manufacturer
        if (!cpuValues.getManufacturer().equals("all")) {
            sockets = buildProductList.stream()
                    .filter(p -> p.getCategory().getId() == 1
                            && p.getManufacturer().equals(cpuValues.getManufacturer()))
                    .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        } else {
            sockets = buildProductList.stream().filter(p -> p.getCategory().getId() == 1).map(s -> s.getSocket())
                    .distinct().collect(Collectors.toList());
        }
        return sockets;
    }

    private List<String> cpuManufacturers() {
        // TODO check if motherboard is picked.
        if (buildService.getPreBuilt() == null) {
            cpuValues.setManufacturer("all");
        }
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildProductList.stream().filter(p -> p.getCategory().getId() == 1)
                .map(s -> s.getManufacturer()).distinct().collect(Collectors.toList());
        return manufacturers;
    }

    private List<String> cpuSeries() {
        // TODO check if motherboard is picked
        List<String> series = new ArrayList<>();
        if (!cpuValues.getManufacturer().equals("all")) {
            series = buildProductList.stream()
                    .filter(p -> p.getCategory().getId() == 1
                            && p.getManufacturer().equals(cpuValues.getManufacturer()))
                    .map(s -> s.getSeries()).distinct().collect(Collectors.toList());
        } else {
            series = buildProductList.stream().filter(p -> p.getCategory().getId() == 1).map(s -> s.getSeries())
                    .distinct().collect(Collectors.toList());
        }
        // series = buildProductList.stream().filter(p -> p.getCategory().getId() ==
        // 1).map(s -> s.getSeries()).distinct()
        // .collect(Collectors.toList());
        return series;
    }

    private List<Product> filterCPU() {
        List<Product> cpus = new ArrayList<>();
        if (cpuValues == null) {
            return buildProductList.stream().filter(p -> p.getCategory().getId() == 1).collect(Collectors.toList());
        }
        cpus = buildProductList.stream().filter(p -> p.getCategory().getId() == 1).collect(Collectors.toList());
        // filter by manufacturers
        if (!cpuValues.getManufacturer().equals("all")) {
            cpus = cpus.stream().filter(m -> m.getManufacturer().equals(cpuValues.getManufacturer()))
                    .collect(Collectors.toList());
        }
        // filter by socket

        // filter by series

        // filter by core&threads

        // filter by price
        if (cpuValues.getPriceMin() == 0 && cpuValues.getPriceMax() == 0) {
            cpuValues.setPriceMax(2000);
        } else if (cpuValues.getPriceMin() > cpuValues.getPriceMax()) {
            cpuValues.setPriceMax(cpuValues.getPriceMin());
        }
        cpus = cpus.stream()
                .filter(p -> p.getPrice() >= cpuValues.getPriceMin() && p.getPrice() <= cpuValues.getPriceMax())
                .collect(Collectors.toList());

        return cpus;
    }

    private void initFilterValues() {
        cpuValues.setPartCategory("cpu");
        cpuValues.setManufacturer("all");
        cpuValues.setPriceMax(2000);
    }
}
