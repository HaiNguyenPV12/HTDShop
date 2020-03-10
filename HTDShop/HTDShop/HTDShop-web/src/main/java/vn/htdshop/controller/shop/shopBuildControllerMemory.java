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
import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * shopBuildControllerMemory
 */
@Controller
@RequestMapping("build/memory")
public class shopBuildControllerMemory {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getMemoryList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("memoryValues", getSessionMemoryValues());
        // -----FORM VALUES-----
        model.addAttribute("memoryManufacturerList", memoryManufacturers());
        model.addAttribute("memoryTypeList", memoryTypes());
        // -----FILTER RESULT---
        model.addAttribute("filteredMemory", filterMemory());
        return "HTDShop/pickRAM";
    }

    @RequestMapping(value = "filterMemory", method = RequestMethod.POST)
    public String filterMemory(@ModelAttribute("memoryValues") BuildValues memoryValues, BindingResult error,
            RedirectAttributes redirect) {
        // BuildValues prevSearch = getSessionMemoryValues();

        setSessionMemoryValues(memoryValues);
        return "redirect:/build/memory";
    }

    // pick and set to prebuilt values
    @RequestMapping(value = "pickMemory", method = RequestMethod.POST)
    public String pickCPU(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get CPU from ID
        Product memory = productFacade.find(id);
        // TODO go back to cpu page if there were errors/id isn't a cpu

        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setMemory(memory);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionMemoryValues(initFilterValues());
        // redirect to build's home page
        return "redirect:/build";
    }

    // reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionMemoryValues(initFilterValues());
        return "redirect:/build/memory";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove CPU from session Prebuilt
        buildService.getSessionPrebuilt().setMemory(null);
        return "redirect:/build";
    }

    public BuildValues getSessionMemoryValues() {
        BuildValues result = (BuildValues) session.getAttribute("memoryValues");
        if (result == null) {
            BuildValues newMemoryValues = initFilterValues();
            setSessionMemoryValues(newMemoryValues);
        }
        return (BuildValues) session.getAttribute("memoryValues");
    }

    public void setSessionMemoryValues(BuildValues memoryValues) {
        session.setAttribute("memoryValues", memoryValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("memory");
        result.setManufacturer("all");
        result.setMemoryType("all");
        result.setPriceMin(0);
        result.setPriceMax(10000);
        result.setMemory(0);
        result.setMemoryModules(0);
        return result;
    }

    // Manufacturer list for form
    private List<String> memoryManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 4)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());

        return manufacturers;
    }

    // RAM Type list for form
    private List<String> memoryTypes() {
        List<String> memoryTypes = new ArrayList<>();
        memoryTypes = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 4)
                .map(m -> m.getMemoryType()).distinct().collect(Collectors.toList());

        return memoryTypes;
    }

    // Filter from form values
    private List<Product> filterMemory() {
        List<Product> memories = new ArrayList<>();
        BuildValues memoryValues = getSessionMemoryValues();
        try {
            // all RAMs
            memories = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 4)
                    .collect(Collectors.toList());

            // filter by manufacturers
            String manufactuer = memoryValues.getManufacturer();
            if (!manufactuer.equals("all")) {
                memories = memories.stream().filter(m -> m.getManufacturer().equals(manufactuer))
                        .collect(Collectors.toList());
            }

            // filter by type
            String memoryType = memoryValues.getMemoryType();
            if (!memoryType.equals("all")) {
                memories = memories.stream().filter(m -> m.getMemoryType().equals(memoryType))
                        .collect(Collectors.toList());
            }

            // filter by size
            Integer size = memoryValues.getMemory();
            memories = memories.stream().filter(m -> m.getMemory() >= size).collect(Collectors.toList());

            // filter by number of modules
            Integer modules = memoryValues.getMemoryModules();
            memories = memories.stream().filter(m -> m.getMemoryModules() >= modules).collect(Collectors.toList());

            // filter by price
            // --clamp prices and filter
            if (memoryValues.getPriceMin() == 0 && memoryValues.getPriceMax() == 0) {
                memoryValues.setPriceMax(10000);
                setSessionMemoryValues(memoryValues);
            } else if (memoryValues.getPriceMin() > memoryValues.getPriceMax()) {
                memoryValues.setPriceMax(memoryValues.getPriceMin());
                setSessionMemoryValues(memoryValues);
            }
            memories = memories.stream().filter(
                    p -> p.getPrice() >= memoryValues.getPriceMin() && p.getPrice() <= memoryValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < memories.size(); i++) {
                if (memories.get(i).getStatus() == 3) {
                    memories.remove(i);
                    i--;
                }
            }

            if (memories.size() == 0) {
                setSessionMemoryValues(initFilterValues());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return memories;
    }
}
