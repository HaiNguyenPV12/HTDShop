/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.htdshop.entity.AdvancedSearch;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Promotion;
import vn.htdshop.entity.PromotionDetail;
import vn.htdshop.entity.Search;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.sb.PromotionDetailFacadeLocal;
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

    @EJB(mappedName = "PromotionDetailFacade")
    PromotionDetailFacadeLocal promotionDetailFacade;

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
        Collection<PromotionDetail> promocollection = promotionDetailFacade.findAllValidPromotion();
        // List<PromotionDetail> promolist = new ArrayList<PromotionDetail>();
        // for (PromotionDetail promo : promocollection) {
        // if (promo.getPromotionCollection() != null &&
        // promo.getPromotionCollection().stream()
        // .filter(p -> null != p.getPreBuiltTarget() || p.getPreBuiltTarget() <=
        // 0).count() > 0) {
        // promolist.add(promo);
        // }
        // }
        // promocollection.stream().filter(p -> p.getPromotionCollection().stream()
        // .filter(p2 -> p2.getPreBuiltTarget() ==
        // null).collect(Collectors.toList()).size() > 0)
        // .collect(Collectors.toList());
        model.addAttribute("promolist", promocollection);
        return "HTDShop/search";

    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public @ResponseBody Object getTest(@RequestParam(value = "id", required = false) Integer id) {
        Integer ID = 1;
        if (id != null) {
            ID = id;
        }
        List<String> result = new ArrayList<>();
        try {
            for (Product product : productFacade.findAll()) {
                boolean found = false;
                Collection<Promotion> promolist = product.getPromotionCollection();
                for (Promotion promo : promolist) {
                    if (promo.getPromotionDetail().getId() == ID) {
                        result.add(product.getName());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    promolist = product.getCategory().getPromotionCollection();
                    for (Promotion promo : promolist) {
                        if (promo.getPromotionDetail().getId() == ID) {
                            result.add(product.getName());
                            found = true;
                            break;
                        }
                    }
                }
                // if (product.getPromotionCollection().stream().filter(promo ->
                // promo.getPromotionDetail().getId() == 4)
                // .count() > 0L
                // || product.getCategory().getPromotionCollection().stream()
                // .filter(promo -> promo.getPromotionDetail().getId() == 4).count() > 0L) {
                // result.add(product.getName());
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
        // List<Product> found = productFacade.search(search.getCategory(),
        // search.getKeyword());
        List<Product> result = new ArrayList<>();
        if (search.getPromo() != null && search.getPromo() > 0) {
            for (Product product : productFacade.search(search.getCategory(), search.getKeyword())) {
                boolean found = false;
                Collection<Promotion> promolist = product.getPromotionCollection();
                for (Promotion promo : promolist) {
                    if (promo.getPromotionDetail().getId() == search.getPromo()) {
                        result.add(product);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    promolist = product.getCategory().getPromotionCollection();
                    for (Promotion promo : promolist) {
                        if (promo.getPromotionDetail().getId() == search.getPromo()) {
                            result.add(product);
                            found = true;
                            break;
                        }
                    }
                }
            }
        } else {
            result = productFacade.search(search.getCategory(), search.getKeyword());
        }
        Stream<Product> resultStream = result.stream();
        // Price
        if (search.getFrom() != null && search.getTo() != null) {
            // result = result.stream().filter(p -> p.getPrice() >= search.getFrom() &&
            // p.getPrice() <= search.getTo())
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getPrice() >= search.getFrom() && p.getPrice() <= search.getTo());
        }
        // More filters
        if (!asearch.getManufacturer().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getManufacturer().equals(asearch.getManufacturer()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getManufacturer().equals(asearch.getManufacturer()));
        }
        if (!asearch.getSocket().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getSocket().equals(asearch.getSocket()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getSocket().equals(asearch.getSocket()));
        }
        if (!asearch.getSeries().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getSeries().equals(asearch.getSeries()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getSeries().equals(asearch.getSeries()));
        }
        if (asearch.getCore() > 0) {
            // result = result.stream().filter(p -> p.getCore() ==
            // asearch.getCore()).collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getCore() == asearch.getCore());
        }
        if (!asearch.getChipset().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getChipset().equals(asearch.getChipset()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getChipset().equals(asearch.getChipset()));
        }
        if (!asearch.getMemoryType().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getMemoryType().equals(asearch.getMemoryType()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getMemoryType().equals(asearch.getMemoryType()));
        }
        if (!asearch.getFormFactor().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getFormFactor().equals(asearch.getFormFactor()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getFormFactor().equals(asearch.getFormFactor()));
        }
        if (asearch.getMemorySlot() > 0) {
            // result = result.stream().filter(p -> p.getMemorySlot() >=
            // asearch.getMemorySlot())
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getMemorySlot() >= asearch.getMemorySlot());
        }
        if (!asearch.getInterface1().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getInterface1().equals(asearch.getInterface1()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getInterface1().equals(asearch.getInterface1()));
        }
        if (asearch.getMemory() > 0) {
            // result = result.stream().filter(p -> p.getMemory() >=
            // asearch.getMemory()).collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getMemory() >= asearch.getMemory());
        }
        if (asearch.getMemoryModules() > 0) {
            // result = result.stream().filter(p -> p.getMemoryModules() ==
            // asearch.getMemoryModules())
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getMemoryModules() == asearch.getMemoryModules());
        }
        if (!asearch.getPSUFormFactor().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getPSUFormFactor().equals(asearch.getPSUFormFactor()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getPSUFormFactor().equals(asearch.getPSUFormFactor()));
        }
        if (asearch.getPSUWattage() > 0) {
            // result = result.stream().filter(p -> p.getPSUWattage() >=
            // asearch.getPSUWattage())
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getPSUWattage() >= asearch.getPSUWattage());
        }
        if (!asearch.getStorageType().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getStorageType().equals(asearch.getStorageType()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getStorageType().equals(asearch.getStorageType()));
        }
        if (!asearch.getResolution().isEmpty()) {
            // result = result.stream().filter(p ->
            // p.getResolution().equals(asearch.getResolution()))
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getResolution().equals(asearch.getResolution()));
        }
        if (asearch.getScreenSize() > 0) {
            // result = result.stream().filter(p -> p.getScreenSize() >=
            // asearch.getScreenSize())
            // .collect(Collectors.toList());
            resultStream = resultStream.filter(p -> p.getScreenSize() >= asearch.getScreenSize());
        }
        if (asearch.getTdpmin() > 0 || asearch.getTdpmax() < 2000) {
            // result = result.stream().filter(p -> p.getTdp() >= asearch.getTdpmin() &&
            // p.getTdp() <= asearch.getTdpmax())
            // .collect(Collectors.toList());
            resultStream = resultStream
                    .filter(p -> p.getTdp() >= asearch.getTdpmin() && p.getTdp() <= asearch.getTdpmax());
        }

        // Sort
        if (search.getSort() != null && search.getSort().equals("priceasc")) {
            sortString = search.getSort();
            // result = result.stream()
            // .sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p),
            // Comparator.naturalOrder()))
            // .collect(Collectors.toList());
            resultStream = resultStream
                    .sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p), Comparator.naturalOrder()));
        } else if (search.getSort() != null && search.getSort().equals("pricedesc")) {
            sortString = search.getSort();
            // result = result.stream()
            // .sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p),
            // Comparator.reverseOrder()))
            // .collect(Collectors.toList());
            resultStream = resultStream
                    .sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p), Comparator.reverseOrder()));
        } else {
            // result = result.stream().sorted(Comparator.comparing(Product::getId,
            // Comparator.reverseOrder()))
            // .collect(Collectors.toList());
            resultStream = resultStream.sorted(Comparator.comparing(Product::getId, Comparator.reverseOrder()));
        }

        result = resultStream.collect(Collectors.toList());
        totalResult = result.size();

        // Paging
        result = result.stream().skip(pageDivide * (pageNumber - 1)).limit(pageDivide).collect(Collectors.toList());
        // result = result.stream().skip(pageDivide * (pageNumber -
        // 1)).limit(pageDivide).collect(Collectors.toList());

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
