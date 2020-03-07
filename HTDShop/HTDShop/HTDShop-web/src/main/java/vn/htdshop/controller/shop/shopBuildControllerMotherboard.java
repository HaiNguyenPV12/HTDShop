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

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getMotherboardList(HttpSession session, Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("motherboardValues", getSessionMotherboardValues());
        // -----FORM VALUES-----
        model.addAttribute("motherboardManufacturerList", motherboardManufacturers());
        model.addAttribute("motherboardSocketList", motherboardSockets());
        model.addAttribute("motherboardChipsetList", motherboardChipsets());
        // -----FILTER RESULT---
        model.addAttribute("filteredMotherboard", filterMotherboard());
        return "HTDShop/pickMotherboard";
    }

    @RequestMapping(value = "filterMotherboard", method = RequestMethod.POST)
    public String filterMotherboard(@ModelAttribute("motherboardValues") BuildValues motherboardValues,
            BindingResult error, RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionMotherboardValues();
        String prevManufacturer = prevSearch.getManufacturer();
        String prevSocket = prevSearch.getSocket();
        String prevSeries = prevSearch.getSeries();

        return "redirect:/build/motherboard";
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

    // Manufacturer list for form
    private List<String> motherboardManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 2)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());
        // TODO check if CPU/RAM is picked and filter accordingly.
        return manufacturers;
    }

    // Socket list for form
    private List<String> motherboardSockets() {
        List<String> sockets = new ArrayList<>();

        // all sockets
        sockets = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 2)
                .map(s -> s.getSocket()).distinct().collect(Collectors.toList());

        // filter by manufacturer
        String manufacturer = getSessionMotherboardValues().getManufacturer();
        if (!manufacturer.equals("all")) {
            // get all sockets from manufacturer
            sockets = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 2 && p.getManufacturer().equals(manufacturer))
                    .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        }
        return sockets;
    }

    // Chipset list for form
    private List<String> motherboardChipsets() {
        List<String> chipsets = new ArrayList<>();

        // all sockets
        chipsets = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 2)
                .map(s -> s.getChipset()).distinct().collect(Collectors.toList());

        // filter by manufacturer
        String manufacturer = getSessionMotherboardValues().getManufacturer();
        if (!manufacturer.equals("all")) {
            // get all chipsets from manufacturer
            chipsets = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 2 && p.getManufacturer().equals(manufacturer))
                    .map(s -> s.getChipset()).distinct().collect(Collectors.toList());
        }
        return chipsets;
    }

    // Filter values
    public BuildValues getSessionMotherboardValues() {
        BuildValues result = (BuildValues) session.getAttribute("motherboardValues");
        if (result == null) {
            BuildValues newNotherboardValues = initFilterValues();
            setSessionMotherboardValues(newNotherboardValues);
        }
        return (BuildValues) session.getAttribute("motherboardValues");
    }

    public void setSessionMotherboardValues(BuildValues motherboardValues) {
        session.setAttribute("motherboardValues", motherboardValues);
    }

    // Filtered List
    private List<Product> filterMotherboard() {
        List<Product> motherboards = new ArrayList<>();
        BuildValues motherboardValues = getSessionMotherboardValues();
        try {
            // all motherboards
            motherboards = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 2)
                    .collect(Collectors.toList());

            // filter by manufacturers
            String manufacturer = motherboardValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                motherboards = motherboards.stream().filter(m -> m.getManufacturer().equals(manufacturer))
                        .collect(Collectors.toList());
            }

            // filter by sockets
            String socket = motherboardValues.getSocket();
            if (!socket.equals("all")) {
                motherboards = motherboards.stream().filter(s -> s.getSocket().equals(socket))
                        .collect(Collectors.toList());
            }

            // filter by chipsets
            String chipset = motherboardValues.getSocket();
            if (!socket.equals("all")) {
                motherboards = motherboards.stream().filter(s -> s.getSocket().equals(chipset))
                        .collect(Collectors.toList());
            }

            // filter by price
            // --clamp prices and filter
            if (motherboardValues.getPriceMin() == 0 && motherboardValues.getPriceMax() == 0) {
                motherboardValues.setPriceMax(10000);
                setSessionMotherboardValues(motherboardValues);
            } else if (motherboardValues.getPriceMin() > motherboardValues.getPriceMax()) {
                motherboardValues.setPriceMax(motherboardValues.getPriceMin());
                setSessionMotherboardValues(motherboardValues);
            }
            motherboards = motherboards.stream().filter(p -> p.getPrice() >= motherboardValues.getPriceMin()
                    && p.getPrice() <= motherboardValues.getPriceMax()).collect(Collectors.toList());

        } catch (Exception e) {
            return motherboards;
        }
        return motherboards;
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("motherboard");
        result.setManufacturer("all");
        result.setSocket("all");
        result.setChipset("all");
        result.setPriceMin(0);
        result.setPriceMax(10000);
        return result;
    }
}