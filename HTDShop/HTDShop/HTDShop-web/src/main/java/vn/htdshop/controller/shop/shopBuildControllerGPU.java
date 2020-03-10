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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * shopBuildControllerGPU
 */
@Controller
@RequestMapping("build/gpu")
public class shopBuildControllerGPU {
    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getGPUList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("gpuValues", getSessionGPUValues());
        // -----FORM VALUES-----
        model.addAttribute("gpuManufacturerList", gpuManufacturers());
        model.addAttribute("gpuChipsetList", gpuChipsets());
        model.addAttribute("gpuMemoryTypeList", gpuMemoryTypes());
        // -----FILTER RESULT---
        model.addAttribute("filteredGPU", filterGPU());
        return "HTDShop/pickGPU";
    }

    @RequestMapping(value = "filterGpu", method = RequestMethod.POST)
    public String filterGPU(@ModelAttribute("gpuValues") BuildValues gpuValues, BindingResult error,
            RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionGPUValues();
        String prevChipset = prevSearch.getChipset();
        String currentChipset = gpuValues.getChipset();

        if (!currentChipset.equals(prevChipset)) {
            gpuValues.setMemoryType("all");
        }

        setSessionGPUValues(gpuValues);
        return "redirect:/build/gpu";
    }

    // pick and set to prebuilt values
    @RequestMapping(value = "pickGpu", method = RequestMethod.POST)
    public String pickGPU(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        Product gpu = productFacade.find(id);
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setVga(gpu);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        setSessionGPUValues(null);
        return "redirect:/build";
    }

    // Reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionGPUValues(null);
        return "redirect:/build/gpu";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove from session Prebuilt
        buildService.getSessionPrebuilt().setVga(null);
        return "redirect:/build";
    }

    // Manufacturer list for form
    private List<String> gpuManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 3)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());

        return manufacturers;
    }

    // Chipset list for form
    private List<String> gpuChipsets() {
        List<String> chipsets = new ArrayList<>();
        BuildValues gpuValues = getSessionGPUValues();
        chipsets = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 3)
                .map(m -> m.getChipset()).distinct().collect(Collectors.toList());

        // filter by manufacturer
        String manufacturer = gpuValues.getManufacturer();
        if (!manufacturer.equals("all")) {

            chipsets = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 3 && p.getManufacturer().equals(manufacturer))
                    .map(c -> c.getChipset()).distinct().collect(Collectors.toList());
        }

        return chipsets;
    }

    // Memory type list for form
    private List<String> gpuMemoryTypes() {
        List<String> memoryTypes = new ArrayList<>();

        // all memory types
        memoryTypes = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 3)
                .map(s -> s.getMemoryType()).distinct().collect(Collectors.toList());

        return memoryTypes;
    }

    // Filter from form values
    private List<Product> filterGPU() {
        List<Product> gpus = new ArrayList<>();
        BuildValues gpuValues = getSessionGPUValues();
        try {
            // all GPUs
            gpus = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 3)
                    .collect(Collectors.toList());

            // filter by manufacturer
            String manufacturer = gpuValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                gpus = gpus.stream().filter(m -> m.getManufacturer().equals(manufacturer)).collect(Collectors.toList());
            }

            // filter by chipset
            String chipset = gpuValues.getChipset();
            if (!chipset.equals("all")) {
                gpus = gpus.stream().filter(c -> c.getChipset().equals(chipset)).collect(Collectors.toList());
            }

            // filter by memory type
            String memoryType = gpuValues.getMemoryType();
            if (!memoryType.equals("all")) {
                gpus = gpus.stream().filter(m -> m.getMemoryType().equals(memoryType)).collect(Collectors.toList());
            }

            // filter by TDP
            Integer tdp = gpuValues.getTdp();
            if (tdp <= 0) {
                gpus = gpus.stream().filter(t -> t.getTdp() >= tdp).collect(Collectors.toList());
            } else {
                gpus = gpus.stream().filter(t -> t.getTdp() <= tdp).collect(Collectors.toList());
            }

            // filter by RAM
            Integer memory = gpuValues.getMemory();
            gpus = gpus.stream().filter(m -> m.getMemory() >= memory).collect(Collectors.toList());

            // filter by price
            // --clamp prices and filter
            if (gpuValues.getPriceMin() == 0 && gpuValues.getPriceMax() == 0) {
                gpuValues.setPriceMax(10000);
                setSessionGPUValues(gpuValues);
            } else if (gpuValues.getPriceMin() > gpuValues.getPriceMax()) {
                gpuValues.setPriceMax(gpuValues.getPriceMin());
                setSessionGPUValues(gpuValues);
            }
            gpus = gpus.stream()
                    .filter(p -> p.getPrice() >= gpuValues.getPriceMin() && p.getPrice() <= gpuValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < gpus.size(); i++) {
                if (gpus.get(i).getStatus() == 3) {
                    gpus.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return gpus;
    }

    public BuildValues getSessionGPUValues() {
        BuildValues result = (BuildValues) session.getAttribute("gpuValues");
        if (result == null) {
            BuildValues newGPUValues = initFilterValues();
            setSessionGPUValues(newGPUValues);
        }
        return (BuildValues) session.getAttribute("gpuValues");
    }

    public void setSessionGPUValues(BuildValues gpuValues) {
        session.setAttribute("gpuValues", gpuValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("gpu");
        result.setManufacturer("all");
        result.setChipset("all");
        result.setMemoryType("all");
        result.setMemory(0);
        result.setTdp(0);
        result.setPriceMin(0);
        result.setPriceMax(10000);
        return result;
    }
}