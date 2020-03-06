package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCPUList(HttpSession session, Model model) {
        buildService.initBuildApp();
        buildProductList = buildService.getSessionProductList();

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
    public String filterCPU(@ModelAttribute("cpuValues") BuildValues cpuValues, BindingResult error,
            RedirectAttributes redirect) {

        if (!cpuValues.getManufacturer().equals("all")) {
            isFilteringCPU = true;
            this.cpuValues.setManufacturer(cpuValues.getManufacturer());
        } else {
            isFilteringCPU = false;
        }

        // apply values to form
        this.cpuValues = cpuValues;
        return "redirect:/build/cpu";
    }

    @RequestMapping(value = "pickCpu", method = RequestMethod.POST)
    public String pickCPU(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get CPU from ID
        Product cpu = productFacade.find(id);
        // TODO go back to cpu page if there were errors/id isn't a cpu
        // Set CPU in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setCpu(cpu);
        buildService.setSessionPrebuilt(sessionPreBuilt);
        // redirect to build's home page
        return "redirect:/build";
    }

    @RequestMapping(value = "discardCpu", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove CPU from session Prebuilt
        buildService.getSessionPrebuilt().setCpu(null);
        return "redirect:/build";
    }

    // Socket list for form
    private List<String> cpuSockets() {
        List<String> sockets = new ArrayList<>();
        // TODO check if motherboard is picked. (needs testing)
        if (buildService.getSessionPrebuilt().getMotherboard() != null) {
            Product motherboard = buildService.getSessionPrebuilt().getMotherboard();
            String manufacturer = motherboard.getManufacturer();
            cpuValues.setManufacturer(manufacturer);
            // get specific socket

        }
        // filter manufacturer
        if (!cpuValues.getManufacturer().equals("all")) {
            // get all sockets from manufacturer
            sockets = buildProductList.stream()
                    .filter(p -> p.getCategory().getId() == 1
                            && p.getManufacturer().equals(cpuValues.getManufacturer()))
                    .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        } else {
            // get all sockets
            sockets = buildProductList.stream().filter(p -> p.getCategory().getId() == 1).map(s -> s.getSocket())
                    .distinct().collect(Collectors.toList());
        }
        return sockets;
    }

    private List<String> cpuManufacturers() {
        if (buildService.getPreBuilt() == null) {
            cpuValues.setManufacturer("all");
        }
        List<String> manufacturers = new ArrayList<>();
        // TODO check if motherboard is picked.

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
            if (cpus.stream().filter(m -> m.getManufacturer().equals(cpuValues.getManufacturer()))
                    .collect(Collectors.toList()).size() > 0) {
                cpus = cpus.stream().filter(m -> m.getManufacturer().equals(cpuValues.getManufacturer()))
                        .collect(Collectors.toList());
            }
        }
        // filter by socket
        if (!cpuValues.getSocket().equals("all")) {
            if (cpus.stream().filter(s -> s.getSocket().equals(cpuValues.getSocket())).collect(Collectors.toList())
                    .size() > 0) {
                cpus = cpus.stream().filter(s -> s.getSocket().equals(cpuValues.getSocket()))
                        .collect(Collectors.toList());
            }
        }
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
        cpuValues.setSocket("all");
        cpuValues.setPriceMax(2000);

        if (buildService.getSessionPrebuilt().getMotherboard() != null) {
            Product motherboard = buildService.getSessionPrebuilt().getMotherboard();
            cpuValues.setSocket(motherboard.getSocket());
        }
    }

    // public BuildValues getSessionCPUValues() {
    // if (session.getAttribute("cpuValues") == null) {
    // BuildValues testcpuValues = new BuildValues();
    // setSessionCPUValues(cpuValues);
    // }
    // return (BuildValues) session.getAttribute("cpuValues");
    // }

    // public void setSessionCPUValues(BuildValues cpuValues) {
    // session.setAttribute("cpuValues", cpuValues);
    // }
}
