/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.htdshop.entity.AdvancedSearch;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Search;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.ShopService;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("search")
public class shopSearchController {
    final private String redirectHome = "redirect:";

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getSearch(Model model, @RequestParam(required = false) Map<String, String> params) {
        Search search = new Search(params);
        shopService.checkLogin();
        if (search.getCategory() == -1) {
            return "redirect:/prebuilt/search?keyword=" + search.getKeyword();
        }
        if (search.getCategory() == null
                || (search.getCategory() != 0 && categoryFacade.find(search.getCategory()) == null)) {
            return redirectHome;
        }
        return "HTDShop/search";

    }

    @RequestMapping(value = "result", method = RequestMethod.POST)
    public String getSearchResult(Model model, @RequestParam(required = false) Map<String, String> params) {
        Search search = new Search(params);
        AdvancedSearch asearch = new AdvancedSearch(params);
        Integer pageDivide = 12;
        Integer pageNumber = 1;
        Integer totalResult = 0;
        Integer totalPage = 0;
        String sortString = "default";
        if (search.getStyle() != null && search.getStyle().equals("list")) {
            pageDivide = 6;
        }
        if (search.getPage() != null && search.getPage() > 1) {
            pageNumber = search.getPage();
        }

        // Create list
        List<Product> result = productFacade.search(search.getCategory(), search.getKeyword()).stream()
                .filter(p -> p.getStatus() != 3).collect(Collectors.toList());

        // Price
        if (search.getFrom() != null && search.getTo() != null) {
            result = result.stream().filter(p -> p.getPrice() >= search.getFrom() && p.getPrice() <= search.getTo())
                    .collect(Collectors.toList());
        }
        // More filters
        if (!asearch.getManufacturer().isEmpty()) {
            result = result.stream().filter(p -> p.getManufacturer().equals(asearch.getManufacturer()))
                    .collect(Collectors.toList());
        }
        if (!asearch.getSocket().isEmpty()) {
            result = result.stream().filter(p -> p.getSocket().equals(asearch.getSocket()))
                    .collect(Collectors.toList());
        }
        if (!asearch.getSeries().isEmpty()) {
            result = result.stream().filter(p -> p.getSeries().equals(asearch.getSeries()))
                    .collect(Collectors.toList());
        }
        if (asearch.getCore() > 0) {
            result = result.stream().filter(p -> p.getCore() == asearch.getCore()).collect(Collectors.toList());
        }
        if (!asearch.getChipset().isEmpty()) {
            result = result.stream().filter(p -> p.getChipset().equals(asearch.getChipset()))
                    .collect(Collectors.toList());
        }
        if (!asearch.getMemoryType().isEmpty()) {
            result = result.stream().filter(p -> p.getMemoryType().equals(asearch.getMemoryType()))
                    .collect(Collectors.toList());
        }
        if (!asearch.getFormFactor().isEmpty()) {
            result = result.stream().filter(p -> p.getFormFactor().equals(asearch.getFormFactor()))
                    .collect(Collectors.toList());
        }
        if (asearch.getMemorySlot() > 0) {
            result = result.stream().filter(p -> p.getMemorySlot() >= asearch.getMemorySlot())
                    .collect(Collectors.toList());
        }
        if (!asearch.getInterface1().isEmpty()) {
            result = result.stream().filter(p -> p.getInterface1().equals(asearch.getInterface1()))
                    .collect(Collectors.toList());
        }
        if (asearch.getMemory() > 0) {
            result = result.stream().filter(p -> p.getMemory() >= asearch.getMemory()).collect(Collectors.toList());
        }
        if (asearch.getMemoryModules() > 0) {
            result = result.stream().filter(p -> p.getMemoryModules() == asearch.getMemoryModules())
                    .collect(Collectors.toList());
        }
        if (!asearch.getPSUFormFactor().isEmpty()) {
            result = result.stream().filter(p -> p.getPSUFormFactor().equals(asearch.getPSUFormFactor()))
                    .collect(Collectors.toList());
        }
        if (asearch.getPSUWattage() > 0) {
            result = result.stream().filter(p -> p.getPSUWattage() >= asearch.getPSUWattage())
                    .collect(Collectors.toList());
        }
        if (!asearch.getStorageType().isEmpty()) {
            result = result.stream().filter(p -> p.getStorageType().equals(asearch.getStorageType()))
                    .collect(Collectors.toList());
        }
        if (!asearch.getResolution().isEmpty()) {
            result = result.stream().filter(p -> p.getResolution().equals(asearch.getResolution()))
                    .collect(Collectors.toList());
        }
        if (asearch.getScreenSize() > 0) {
            result = result.stream().filter(p -> p.getScreenSize() >= asearch.getScreenSize())
                    .collect(Collectors.toList());
        }
        if (asearch.getTdpmin() > 0 || asearch.getTdpmax() < 2000) {
            result = result.stream().filter(p -> p.getTdp() >= asearch.getTdpmin() && p.getTdp() <= asearch.getTdpmax())
                    .collect(Collectors.toList());
        }
        
        totalResult = result.size();

        // Sort
        if (search.getSort() != null && search.getSort().equals("priceasc")) {
            sortString = search.getSort();
            result = result.stream()
                    .sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p), Comparator.naturalOrder()))
                    .collect(Collectors.toList());
        } else if (search.getSort() != null && search.getSort().equals("pricedesc")) {
            sortString = search.getSort();
            result = result.stream()
                    .sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p), Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            result = result.stream().sorted(Comparator.comparing(Product::getId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        // Paging
        result = result.stream().skip(pageDivide * (pageNumber - 1)).limit(pageDivide).collect(Collectors.toList());

        // Calculate page's related data
        totalPage = Math.floorDiv(totalResult, pageDivide);
        if (Double.parseDouble(totalResult.toString()) % Double.parseDouble(pageDivide.toString()) > 0) {
            totalPage++;
        }

        model.addAttribute("shopSv", shopService);
        model.asMap().put("pageNumber", pageNumber);
        model.asMap().put("totalResult", totalResult);
        model.asMap().put("pageDivide", pageDivide);
        model.asMap().put("sortString", sortString);
        model.asMap().put("totalPage", totalPage);
        model.asMap().put("result", result);
        return "HTDShop/search_result";
    }

    @RequestMapping(value = "getAdvancedSearch", method = RequestMethod.POST)
    public String getAdvancedSearch(Model model, @RequestParam(required = false) Map<String, String> params) {
        AdvancedSearch search = new AdvancedSearch(params);
        switch (search.getCategory()) {
        case 1:
            model.asMap().put("manuList", productFacade.getStringList("manuCpu"));
            model.asMap().put("skList", productFacade.getStringList("socketCpu"));
            model.asMap().put("srList", productFacade.getStringList("series"));
            model.asMap().put("coList", productFacade.getIntegerList("core"));
            break;
        case 2:
            model.asMap().put("manuList", productFacade.getStringList("manuMotherboard"));
            model.asMap().put("skList", productFacade.getStringList("socketMotherboard"));
            model.asMap().put("csList", productFacade.getStringList("chipsetMotherboard"));
            model.asMap().put("mtList", productFacade.getStringList("memoryTypeMotherboard"));
            model.asMap().put("ffList", productFacade.getStringList("formFactorMotherboard"));
            break;
        case 3:
            model.asMap().put("manuList", productFacade.getStringList("manuGpu"));
            model.asMap().put("csList", productFacade.getStringList("chipsetGpu"));
            model.asMap().put("mtList", productFacade.getStringList("memoryTypeGpu"));
            model.asMap().put("itfList", productFacade.getStringList("interfaceGpu"));
            break;
        case 4:
            model.asMap().put("manuList", productFacade.getStringList("manuMemory"));
            model.asMap().put("mtList", productFacade.getStringList("memoryTypeMemory"));
            model.asMap().put("mmmList", productFacade.getIntegerList("memoryModuleMemory"));
            break;
        case 5:
            model.asMap().put("manuList", productFacade.getStringList("manuPsu"));
            model.asMap().put("psufList", productFacade.getStringList("psuFormFactorPsu"));
            break;
        case 6:
            model.asMap().put("manuList", productFacade.getStringList("manuStorage"));
            model.asMap().put("ffList", productFacade.getStringList("formFactorStorage"));
            model.asMap().put("stList", productFacade.getStringList("storageTypeStorage"));
            model.asMap().put("itfList", productFacade.getStringList("interfaceStorage"));
            break;
        case 7:
            model.asMap().put("manuList", productFacade.getStringList("manuCpuCooler"));
            break;
        case 8:
            model.asMap().put("manuList", productFacade.getStringList("manuCase"));
            model.asMap().put("ffList", productFacade.getStringList("formFactorCase"));
            model.asMap().put("psufList", productFacade.getStringList("psuFormFactorCase"));
            break;
        case 9:
            model.asMap().put("manuList", productFacade.getStringList("manuMonitor"));
            model.asMap().put("ssList", productFacade.getDoubleList("screenSizeMonitor"));
            model.asMap().put("rsList", productFacade.getStringList("resolutionMonitor"));
            break;
        default:
            break;
        }

        model.asMap().put("data", search);
        model.asMap().put("cateid", search.getCategory());
        return "HTDShop/search_advanced";
    }

}
