package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Arrays;
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
import vn.htdshop.utility.ShopService;
import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * shopBuildControllerCPUCooler
 */
@Controller
@RequestMapping("build/cpucooler")
public class shopBuildControllerCPUCooler {
    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    ShopService shopService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCoolerList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("coolerValues", getSessionCoolerValues());
        // -----FORM VALUES-----
        model.addAttribute("coolerManufacturerList", coolerManufacturers());
        model.addAttribute("coolerSocketList", coolerSockets());
        // -----FILTER RESULT---
        model.addAttribute("shopService", shopService);
        model.addAttribute("filteredCooler", filterCooler());
        return "HTDShop/pickCPUCooler";
    }

    @RequestMapping(value = "filtercooler", method = RequestMethod.POST)
    public String filterCooler(@ModelAttribute("coolerValues") BuildValues coolerValues, BindingResult error,
            RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionCoolerValues();
        String prevManufacturer = prevSearch.getManufacturer();
        String prevSocket = prevSearch.getSocket();

        setSessionCoolerValues(coolerValues);
        return "redirect:/build/cpucooler";
    }

    // pick and set to prebuilt values
    @RequestMapping(value = "pickcooler", method = RequestMethod.POST)
    public String pickCPU(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get CPU from ID
        Product cooler = productFacade.find(id);
        // TODO go back to cpu page if there were errors/id isn't a cpu
        // Set CPU in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setCpucooler(cooler);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionCoolerValues(initFilterValues());
        // redirect to build's home page
        return "redirect:/build";
    }

    // reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionCoolerValues(initFilterValues());
        return "redirect:/build/cpucooler";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove CPU from session Prebuilt
        buildService.getSessionPrebuilt().setCpucooler(null);
        return "redirect:/build";
    }

    public BuildValues getSessionCoolerValues() {
        BuildValues result = (BuildValues) session.getAttribute("coolerValues");
        if (result == null) {
            BuildValues newCoolerValues = initFilterValues();
            setSessionCoolerValues(newCoolerValues);
        }
        return (BuildValues) session.getAttribute("coolerValues");
    }

    public void setSessionCoolerValues(BuildValues coolerValues) {
        session.setAttribute("coolerValues", coolerValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("cpuCooler");
        result.setManufacturer("all");
        result.setSocket("all");
        result.setPriceMin(0);
        result.setPriceMax(10000);
        return result;
    }

    // Socket list for form
    private List<String> coolerSockets() {
        List<String> dbSockets = new ArrayList<>();

        // all socksets with commas
        dbSockets = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 7)
                .map(s -> s.getSocket()).distinct().collect(Collectors.toList());

        // split socket strings into individual socket
        List<String> sockets = new ArrayList<>();
        for (int i = 0; i < dbSockets.size(); i++) {
            List<String> items = Arrays.asList(dbSockets.get(i).split("\\s*,\\s*"));
            for (String item : items) {
                sockets.add(item);
            }
        }

        sockets = sockets.stream().distinct().collect(Collectors.toList());
        return sockets;
    }

    // Manufacturer list for form
    private List<String> coolerManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 7)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());

        return manufacturers;
    }

    // Filter from form values
    private List<Product> filterCooler() {
        List<Product> coolers = new ArrayList<>();
        BuildValues coolerValues = getSessionCoolerValues();
        try {
            // all coolers
            coolers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 7)
                    .collect(Collectors.toList());

            // filter by manufacturers
            String manufacturer = coolerValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                coolers = coolers.stream().filter(c -> c.getManufacturer().equals(manufacturer))
                        .collect(Collectors.toList());
            }

            // filter by sockets
            String socket = getSessionCoolerValues().getSocket();
            if (!socket.equals("all")) {
                for (int i = 0; i < coolers.size(); i++) {
                    // remove unsupported sockets
                    if (!coolers.get(i).getSocket().contains(socket)) {
                        coolers.remove(i);
                        i--;
                    }
                }
            }

            // filter by price
            // --clamp prices and filter
            if (coolerValues.getPriceMin() == 0 && coolerValues.getPriceMax() == 0) {
                coolerValues.setPriceMax(10000);
                setSessionCoolerValues(coolerValues);
            } else if (coolerValues.getPriceMin() > coolerValues.getPriceMax()) {
                coolerValues.setPriceMax(coolerValues.getPriceMin());
                setSessionCoolerValues(coolerValues);
            }
            coolers = coolers.stream().filter(
                    p -> p.getPrice() >= coolerValues.getPriceMin() && p.getPrice() <= coolerValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < coolers.size(); i++) {
                if (coolers.get(i).getStatus() != 1) {
                    coolers.remove(i);
                    i--;
                }
            }

            if (coolers.size() == 0) {
                setSessionCoolerValues(initFilterValues());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return coolers;
    }
}