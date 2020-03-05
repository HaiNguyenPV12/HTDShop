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

@Controller
@RequestMapping("build/motherboard")
public class shopBuildControllerMotherboard {
    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    boolean isFilteringCPU = false;

    @Autowired
    BuildService buildService;

    BuildValues motherboardValues = null;

    List<Product> buildProductList = null;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getMotherboardList(HttpSession session, Model model) {

        buildService.initBuildApp();
        buildProductList = buildService.getProductList();

        if (motherboardValues == null || !motherboardValues.getPartCategory().equals("motherboard")) {
            motherboardValues = new BuildValues();
            initFilterValues();
        }
        model.addAttribute("motherboardSocketList", motherboardSockets());
        model.addAttribute("filteredMotherboard", filterMotherboard());
        return "HTDShop/pickMotherboard";
    }

    @RequestMapping(value = "pickMotherboard", method = RequestMethod.POST)
    public String pickMotherboard(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get Motherboard from ID
        Product motherboard = productFacade.find(id);
        // TODO go back to cpu page if there were errors/id isn't a motherboard
        // Set Motherboard in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setMotherboard(motherboard);
        buildService.setSessionPrebuilt(sessionPreBuilt);
        // redirect to build's home page
        return "redirect:/build";
    }

    // For filter form
    private List<String> motherboardSockets() {
        List<String> sockets = new ArrayList<>();
        // TODO check if CPU is picked. (needs testing)
        if (buildService.getSessionPrebuilt().getCpu() != null) {
            Product cpu = buildService.getSessionPrebuilt().getCpu();

            // get specific sockets
            sockets = buildProductList.stream()
                    .filter(p -> p.getCategory().getId() == 2 && p.getSocket().equals(cpu.getSocket()))
                    .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        }
        // filter manufacturer
        if (!motherboardValues.getManufacturer().equals("all")) {
            // get all sockets from manufacturer
            sockets = buildProductList.stream()
                    .filter(p -> p.getCategory().getId() == 2
                            && p.getManufacturer().equals(motherboardValues.getManufacturer()))
                    .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        } else {
            // get all sockets
            sockets = buildProductList.stream().filter(p -> p.getCategory().getId() == 2).map(s -> s.getSocket())
                    .distinct().collect(Collectors.toList());
        }
        return sockets;
    }

    private List<Product> filterMotherboard() {
        List<Product> motherboards = new ArrayList<>();
        if (motherboardValues == null) {
            return buildProductList.stream().filter(p -> p.getCategory().getId() == 2).collect(Collectors.toList());
        }
        motherboards = buildProductList.stream().filter(p -> p.getCategory().getId() == 2).collect(Collectors.toList());
        // filter by manufacturers
        if (!motherboardValues.getManufacturer().equals("all")) {
            motherboards = motherboards.stream()
                    .filter(m -> m.getManufacturer().equals(motherboardValues.getManufacturer()))
                    .collect(Collectors.toList());
        }
        // filter by socket

        // filter by series

        // filter by core&threads

        // filter by price
        if (motherboardValues.getPriceMin() == 0 && motherboardValues.getPriceMax() == 0) {
            motherboardValues.setPriceMax(2000);
        } else if (motherboardValues.getPriceMin() > motherboardValues.getPriceMax()) {
            motherboardValues.setPriceMax(motherboardValues.getPriceMin());
        }
        motherboards = motherboards.stream().filter(
                p -> p.getPrice() >= motherboardValues.getPriceMin() && p.getPrice() <= motherboardValues.getPriceMax())
                .collect(Collectors.toList());

        return motherboards;
    }

    private void initFilterValues() {
        motherboardValues.setPartCategory("motherboard");
        motherboardValues.setManufacturer("all");
        motherboardValues.setPriceMax(2000);
    }
}