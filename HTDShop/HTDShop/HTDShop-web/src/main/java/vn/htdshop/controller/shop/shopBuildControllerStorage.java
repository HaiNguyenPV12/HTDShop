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
 * shopBuildControllerStorage
 */
@Controller
@RequestMapping("build/storage")
public class shopBuildControllerStorage {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getStorageList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("storageValues", getSessionStorageValues());
        // -----FORM VALUES-----
        model.addAttribute("storageManufacturerList", storageManufacturers());
        model.addAttribute("storageFormFactorList", storageFormFactors());
        model.addAttribute("storageInterfaceList", storageInterfaces());
        model.addAttribute("storageTypeList", storageTypes());
        // -----FILTER RESULT---
        model.addAttribute("shopService", shopService);
        model.addAttribute("filteredStorage", filterStorage());
        return "HTDShop/pickStorage";
    }

    @RequestMapping(value = "filterStorage", method = RequestMethod.POST)
    public String filterStorage(@ModelAttribute("storageValues") BuildValues storageValues, BindingResult error,
            RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionStorageValues();

        setSessionStorageValues(storageValues);
        return "redirect:/build/storage";
    }

    @RequestMapping(value = "pickStorage", method = RequestMethod.POST)
    public String pickStorage(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get Storage from ID
        Product sotrage = productFacade.find(id);
        // Set storage in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setStorage(sotrage);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionStorageValues(null);
        // redirect to build's home page
        return "redirect:/build";
    }

    // Reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionStorageValues(initFilterValues());
        return "redirect:/build/storage";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardStorage(RedirectAttributes redirect) {
        // remove Storage from session Prebuilt
        buildService.getSessionPrebuilt().setStorage(null);
        return "redirect:/build";
    }

    // Manufacturer list for form
    private List<String> storageManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 6)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());
        return manufacturers;
    }

    // Form Factor list for form
    private List<String> storageFormFactors() {
        List<String> formFactors = new ArrayList<>();
        formFactors = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 6)
                .map(m -> m.getFormFactor()).distinct().collect(Collectors.toList());
        return formFactors;
    }

    // Interface list for form
    private List<String> storageInterfaces() {
        List<String> interfaces = new ArrayList<>();
        interfaces = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 6)
                .map(m -> m.getInterface1()).distinct().collect(Collectors.toList());
        return interfaces;
    }

    // Memory Type list for form
    private List<String> storageTypes() {
        List<String> storageTypes = new ArrayList<>();
        storageTypes = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 6)
                .map(m -> m.getStorageType()).distinct().collect(Collectors.toList());
        return storageTypes;
    }

    // Filter from form values
    private List<Product> filterStorage() {
        List<Product> storages = new ArrayList<>();
        BuildValues storageValues = getSessionStorageValues();
        try {
            // all Storages
            storages = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 6)
                    .collect(Collectors.toList());

            // filter by manufaturers
            String manufacturer = storageValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                storages = storages.stream().filter(m -> m.getManufacturer().equals(manufacturer))
                        .collect(Collectors.toList());
            }

            // filter by interface
            String partInterface = storageValues.getPartInterface();
            if (!partInterface.equals("all")) {
                storages = storages.stream().filter(m -> m.getInterface1().equals(partInterface))
                        .collect(Collectors.toList());
            }

            // filter by form factor
            String formFactor = storageValues.getFormFactor();
            if (!formFactor.equals("all")) {
                storages = storages.stream().filter(m -> m.getFormFactor().equals(formFactor))
                        .collect(Collectors.toList());
            }

            // filter by storage type
            String storageType = storageValues.getStorageType();
            if (!storageType.equals("all")) {
                storages = storages.stream().filter(m -> m.getStorageType().equals(storageType))
                        .collect(Collectors.toList());
            }

            // filter by capacity
            Integer size = storageValues.getMemory();
            storages = storages.stream().filter(c -> c.getMemory() >= size).collect(Collectors.toList());

            // filter by price
            // --clamp prices and filter
            if (storageValues.getPriceMin() == 0 && storageValues.getPriceMax() == 0) {
                storageValues.setPriceMax(2000);
                setSessionStorageValues(storageValues);
            } else if (storageValues.getPriceMin() > storageValues.getPriceMax()) {
                storageValues.setPriceMax(storageValues.getPriceMin());
                setSessionStorageValues(storageValues);
            }
            storages = storages.stream().filter(
                    s -> s.getPrice() >= storageValues.getPriceMin() && s.getPrice() <= storageValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < storages.size(); i++) {
                if (storages.get(i).getStatus() != 1) {
                    storages.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return storages;
    }

    // Filter values
    public BuildValues getSessionStorageValues() {
        BuildValues result = (BuildValues) session.getAttribute("storageValues");
        if (result == null) {
            BuildValues newStorageValues = initFilterValues();
            setSessionStorageValues(newStorageValues);
        }
        return (BuildValues) session.getAttribute("storageValues");
    }

    public void setSessionStorageValues(BuildValues storageValues) {
        session.setAttribute("storageValues", storageValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("storage");
        result.setManufacturer("all");
        result.setStorageType("all");
        result.setFormFactor("all");
        result.setPartInterface("all");
        result.setMemory(0);
        result.setPriceMin(0);
        result.setPriceMax(2000);
        return result;
    }
}
