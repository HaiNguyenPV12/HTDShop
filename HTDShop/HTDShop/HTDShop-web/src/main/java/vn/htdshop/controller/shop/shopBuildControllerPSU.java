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
import vn.htdshop.utility.ShopService;
import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * shopBuildControllerPSU
 */
@Controller
@RequestMapping("build/psu")
public class shopBuildControllerPSU {
    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCPUList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("psuValues", getSessionPSUValues());
        // -----FORM VALUES-----
        model.addAttribute("psuManufacturerList", psuManufacturers());
        model.addAttribute("psuFormFactorList", psuFormFactors());
        // -----FILTER RESULT---
        model.addAttribute("shopService", shopService);
        model.addAttribute("filteredPSU", filterPSU());
        return "HTDShop/pickPSU";
    }

    @RequestMapping(value = "filterPsu", method = RequestMethod.POST)
    public String filterPSU(@ModelAttribute("psuValues") BuildValues psuValues, BindingResult error,
            RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionPSUValues();
        String prevManufacturer = prevSearch.getManufacturer();
        String prevFormFactor = prevSearch.getPsuFormFactor();

        setSessionPSUValues(psuValues);
        return "redirect:/build/psu";
    }

    // pick and set to prebuilt values
    @RequestMapping(value = "pickPsu", method = RequestMethod.POST)
    public String pickCPU(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get PSU from ID
        Product psu = productFacade.find(id);

        // Set PSU in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setPsu(psu);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionPSUValues(null);
        // redirect to build's home page
        return "redirect:/build";
    }

    // Reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionPSUValues(initFilterValues());
        return "redirect:/build/psu";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove motherboard from session Prebuilt
        buildService.getSessionPrebuilt().setPsu(null);
        setSessionPSUValues(null);
        return "redirect:/build";
    }

    // Manufacturer list for form
    private List<String> psuManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 5)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());

        return manufacturers;
    }

    // PSU Form Factor list for form
    private List<String> psuFormFactors() {
        List<String> formFactors = new ArrayList<>();
        formFactors = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 5)
                .map(m -> m.getPSUFormFactor()).distinct().collect(Collectors.toList());

        return formFactors;
    }

    // filtered list
    private List<Product> filterPSU() {
        List<Product> psus = new ArrayList<>();
        BuildValues psuValues = getSessionPSUValues();
        try {
            // all PSUs
            psus = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 5)
                    .collect(Collectors.toList());

            // filter by manufacturers
            String manufacturer = psuValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                psus = psus.stream().filter(m -> m.getManufacturer().equals(manufacturer)).collect(Collectors.toList());
            }

            // filter by form factor
            String formFactor = psuValues.getPsuFormFactor();
            if (!formFactor.equals("all")) {
                psus = psus.stream().filter(m -> m.getPSUFormFactor().equals(formFactor)).collect(Collectors.toList());
            }

            // filter by wattage
            Integer wattage = psuValues.getPsuWattage();
            if (wattage <= 0) {
                psus = psus.stream().filter(m -> m.getPSUWattage() >= wattage).collect(Collectors.toList());
            } else {
                psus = psus.stream().filter(m -> m.getPSUWattage() <= wattage).collect(Collectors.toList());
            }

            // filter by price
            // --clamp prices and filter
            if (psuValues.getPriceMin() == 0 && psuValues.getPriceMax() == 0) {
                psuValues.setPriceMax(1000);
                setSessionPSUValues(psuValues);
            } else if (psuValues.getPriceMin() > psuValues.getPriceMax()) {
                psuValues.setPriceMax(psuValues.getPriceMin());
                setSessionPSUValues(psuValues);
            }
            psus = psus.stream()
                    .filter(p -> p.getPrice() >= psuValues.getPriceMin() && p.getPrice() <= psuValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < psus.size(); i++) {
                if (psus.get(i).getStatus() != 1) {
                    psus.remove(i);
                    i--;
                }
            }

            if (psus.size() == 0) {
                setSessionPSUValues(initFilterValues());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return psus;
    }

    public BuildValues getSessionPSUValues() {
        BuildValues result = (BuildValues) session.getAttribute("psuValues");
        if (result == null) {
            BuildValues newPSUValues = initFilterValues();
            setSessionPSUValues(newPSUValues);
        }
        return (BuildValues) session.getAttribute("psuValues");
    }

    public void setSessionPSUValues(BuildValues psuValues) {
        session.setAttribute("psuValues", psuValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("psu");
        result.setManufacturer("all");
        result.setPsuFormFactor("all");
        result.setPsuWattage(0);
        result.setPriceMin(0);
        result.setPriceMax(2000);
        return result;
    }
}