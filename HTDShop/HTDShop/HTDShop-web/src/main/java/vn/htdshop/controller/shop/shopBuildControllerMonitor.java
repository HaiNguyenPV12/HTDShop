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

import vn.htdshop.utility.BuildService;
import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * shopBuildControllerMonitor
 */
@Controller
@RequestMapping("build/monitor")
public class shopBuildControllerMonitor {
    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getMonitorList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("monitorValues", getSessionMonitorValues());
        // -----FORM VALUES-----
        model.addAttribute("monitorManufacturerList", monitorManufacturers());
        model.addAttribute("monitorResolutionList", monitorResolutions());
        // -----FILTER RESULT---
        model.addAttribute("filteredMonitor", filterMonitor());
        return "HTDShop/pickMonitor";
    }

    @RequestMapping(value = "filterMotherboard", method = RequestMethod.POST)
    public String filterMonitor(@ModelAttribute("monitorValues") BuildValues monitorValues, BindingResult error,
            RedirectAttributes redirect) {
        // BuildValues prevSearch = getSessionMonitorValues();
        setSessionMonitorValues(monitorValues);
        return "redirect:/build/motherboard";
    }

    @RequestMapping(value = "pickMonitor", method = RequestMethod.POST)
    public String pickMotherboard(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get Monitor from ID
        Product monitor = productFacade.find(id);
        // Set Monitor in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setMonitor(monitor);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionMonitorValues(null);
        // redirect to build's home page
        return "redirect:/build";
    }

    // Reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionMonitorValues(initFilterValues());
        return "redirect:/build/monitor";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardMonitor(RedirectAttributes redirect) {
        // remove motherboard from session Prebuilt
        buildService.getSessionPrebuilt().setMonitor(null);
        setSessionMonitorValues(null);
        return "redirect:/build";
    }

    // Manufacturer list for form
    private List<String> monitorManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 9)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());
        return manufacturers;
    }

    // Resolution list for form
    private List<String> monitorResolutions() {
        List<String> resolutions = new ArrayList<>();
        resolutions = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 9)
                .map(m -> m.getResolution()).distinct().collect(Collectors.toList());
        return resolutions;
    }

    // Filtered List
    private List<Product> filterMonitor() {
        List<Product> monitors = new ArrayList<>();
        BuildValues monitorValues = getSessionMonitorValues();
        try {
            // all monitors
            monitors = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 9)
                    .collect(Collectors.toList());

            // filter by manufacturers
            String manufacturer = monitorValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                monitors = monitors.stream().filter(m -> m.getManufacturer().equals(manufacturer))
                        .collect(Collectors.toList());
            }

            // filter by size
            Integer size = monitorValues.getScreenSize();
            monitors = monitors.stream().filter(c -> c.getScreenSize() >= size).collect(Collectors.toList());

            // filter by resolution
            String resolution = monitorValues.getResolution();
            if (!resolution.equals("all")) {
                monitors = monitors.stream().filter(m -> m.getResolution().equals(resolution))
                        .collect(Collectors.toList());
            }

            // filter by price
            // --clamp prices and filter
            if (monitorValues.getPriceMin() == 0 && monitorValues.getPriceMax() == 0) {
                monitorValues.setPriceMax(2000);
                setSessionMonitorValues(monitorValues);
            } else if (monitorValues.getPriceMin() > monitorValues.getPriceMax()) {
                monitorValues.setPriceMax(monitorValues.getPriceMin());
                setSessionMonitorValues(monitorValues);
            }
            monitors = monitors.stream().filter(
                    p -> p.getPrice() >= monitorValues.getPriceMin() && p.getPrice() <= monitorValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < monitors.size(); i++) {
                if (monitors.get(i).getStatus() == 3) {
                    monitors.remove(i);
                    i--;
                }
            }
            if (monitors.size() == 0) {
                setSessionMonitorValues(initFilterValues());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return monitors;
    }

    // Filter values
    public BuildValues getSessionMonitorValues() {
        BuildValues result = (BuildValues) session.getAttribute("monitorValues");
        if (result == null) {
            BuildValues newMonitorValues = initFilterValues();
            setSessionMonitorValues(newMonitorValues);
        }
        return (BuildValues) session.getAttribute("monitorValues");
    }

    public void setSessionMonitorValues(BuildValues monitorValues) {
        session.setAttribute("monitorValues", monitorValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("monitor");
        result.setManufacturer("all");
        result.setResolution("all");
        result.setScreenSize(0);
        result.setPriceMin(0);
        result.setPriceMax(2000);
        return result;
    }
}