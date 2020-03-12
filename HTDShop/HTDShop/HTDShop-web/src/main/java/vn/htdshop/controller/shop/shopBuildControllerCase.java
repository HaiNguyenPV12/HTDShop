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
 * shopBuildControllerCase
 */
@Controller
@RequestMapping("build/case")
public class shopBuildControllerCase {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    boolean isFilteringCPU = false;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCaseList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("caseValues", getSessionCaseValues());
        // -----FORM VALUES-----
        model.addAttribute("caseManufacturerList", caseManufacturers());
        model.addAttribute("caseFormFactorList", caseFormFactors());
        model.addAttribute("casePSUFormFactorList", casePSUFormFactors());
        // -----FILTER RESULT---
        model.addAttribute("shopService", shopService);
        model.addAttribute("filteredCase", filterCase());
        return "HTDShop/pickCase";
    }

    @RequestMapping(value = "filterCase", method = RequestMethod.POST)
    public String filterMotherboard(@ModelAttribute("caseValues") BuildValues caseValues, BindingResult error,
            RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionCaseValues();
        String prevManufacturer = prevSearch.getManufacturer();
        String prevSocket = prevSearch.getSocket();
        String prevSeries = prevSearch.getSeries();
        String prevChipset = prevSearch.getChipset();

        setSessionCaseValues(caseValues);
        return "redirect:/build/case";
    }

    @RequestMapping(value = "pickCase", method = RequestMethod.POST)
    public String pickMotherboard(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get Motherboard from ID
        Product cases = productFacade.find(id);
        // Set Motherboard in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setCases(cases);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionCaseValues(null);
        // redirect to build's home page
        return "redirect:/build";
    }

    // Reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionCaseValues(initFilterValues());
        return "redirect:/build/case";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove motherboard from session Prebuilt
        buildService.getSessionPrebuilt().setCases(null);
        setSessionCaseValues(null);
        return "redirect:/build";
    }

    // Manufacturer list for form
    private List<String> caseManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 8)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());
        return manufacturers;
    }

    // Form Factor list for form
    private List<String> caseFormFactors() {
        List<String> formFactors = new ArrayList<>();
        formFactors = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 8)
                .map(m -> m.getFormFactor()).distinct().collect(Collectors.toList());
        return formFactors;
    }

    // Form Factor list for form
    private List<String> casePSUFormFactors() {
        List<String> psuFormFactors = new ArrayList<>();
        psuFormFactors = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 8)
                .map(m -> m.getPSUFormFactor()).distinct().collect(Collectors.toList());
        return psuFormFactors;
    }

    // Filter values
    public BuildValues getSessionCaseValues() {
        BuildValues result = (BuildValues) session.getAttribute("caseValues");
        if (result == null) {
            BuildValues newCaseValues = initFilterValues();
            setSessionCaseValues(newCaseValues);
            if (buildService.getSessionPrebuilt().getPsu() != null) {
                String psuFormFactor = buildService.getSessionPrebuilt().getPsu().getPSUFormFactor();
                getSessionCaseValues().setPsuFormFactor(psuFormFactor);
            }
        }
        return (BuildValues) session.getAttribute("caseValues");
    }

    private List<Product> filterCase() {
        List<Product> cases = new ArrayList<>();
        BuildValues caseValues = getSessionCaseValues();
        try {
            // all cases
            cases = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 8)
                    .collect(Collectors.toList());

            // filter by manufaturers
            String manufacturer = caseValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                cases = cases.stream().filter(m -> m.getManufacturer().equals(manufacturer))
                        .collect(Collectors.toList());
            }

            // filter by form factor
            String formFactor = caseValues.getFormFactor();
            if (!formFactor.equals("all")) {
                cases = cases.stream().filter(m -> m.getFormFactor().equals(formFactor)).collect(Collectors.toList());
            }

            // filter by PSU form factor
            String psuFormFactor = caseValues.getFormFactor();
            if (!psuFormFactor.equals("all")) {
                cases = cases.stream().filter(m -> m.getPSUFormFactor().equals(psuFormFactor))
                        .collect(Collectors.toList());
            }

            // filter by price
            // --clamp prices and filter
            if (caseValues.getPriceMin() == 0 && caseValues.getPriceMax() == 0) {
                caseValues.setPriceMax(10000);
                setSessionCaseValues(caseValues);
            } else if (caseValues.getPriceMin() > caseValues.getPriceMax()) {
                caseValues.setPriceMax(caseValues.getPriceMin());
                setSessionCaseValues(caseValues);
            }
            cases = cases.stream()
                    .filter(p -> p.getPrice() >= caseValues.getPriceMin() && p.getPrice() <= caseValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < cases.size(); i++) {
                if (cases.get(i).getStatus() != 1) {
                    cases.remove(i);
                    i--;
                }
            }

            if (cases.size() == 0) {
                setSessionCaseValues(initFilterValues());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return cases;
    }

    public void setSessionCaseValues(BuildValues caseValues) {
        session.setAttribute("caseValues", caseValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("case");
        result.setManufacturer("all");
        result.setFormFactor("all");
        result.setPsuFormFactor("all");
        result.setPriceMin(0);
        result.setPriceMax(2000);
        return result;
    }
}