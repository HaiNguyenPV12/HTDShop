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

import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.PreBuiltSearch;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.Search;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.PreBuiltFacade;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;

/**
 *
 * @author Hai
 */
@Controller
@RequestMapping("prebuilt")
public class shopPreBuiltController {
    final private String redirectHome = "redirect:";

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String getSearch(Model model, @RequestParam(required = false) Map<String, String> params) {
        shopService.checkLogin();
        PreBuiltSearch search = new PreBuiltSearch(params);

        model.asMap().put("products", productFacade.findAll().stream()
                .filter(p -> p.getStatus() != 3 && p.getStock() > 0).collect(Collectors.toList()));
        return "HTDShop/prebuilt_search";

    }

    @RequestMapping(value = "result", method = RequestMethod.POST)
    public String getSearchResult(Model model, @RequestParam(required = false) Map<String, String> params) {
        PreBuiltSearch search = new PreBuiltSearch(params);
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
        List<PreBuilt> result = preBuiltFacade.search(search.getKeyword());
        // Filter parts
        if (search.getCpu() > 0) {
            result = result.stream().filter(p->p.getCpu().getId() == search.getCpu()).collect(Collectors.toList());
        }
        if (search.getMotherboard() > 0) {
            result = result.stream().filter(p->p.getMotherboard().getId() == search.getMotherboard()).collect(Collectors.toList());
        }
        if (search.getGpu() > 0) {
            result = result.stream().filter(p->p.getVga().getId() == search.getGpu()).collect(Collectors.toList());
        }
        if (search.getMemory() > 0) {
            result = result.stream().filter(p->p.getMemory().getId() == search.getMemory()).collect(Collectors.toList());
        }
        if (search.getPsu() > 0) {
            result = result.stream().filter(p->p.getPsu().getId() == search.getPsu()).collect(Collectors.toList());
        }
        if (search.getStorage() > 0) {
            result = result.stream().filter(p->p.getStorage().getId() == search.getStorage()).collect(Collectors.toList());
        }
        if (search.getCpucooler() > 0) {
            result = result.stream().filter(p->p.getCpucooler().getId() == search.getCpucooler()).collect(Collectors.toList());
        }
        if (search.getCase1() > 0) {
            result = result.stream().filter(p->p.getCases().getId() == search.getCase1()).collect(Collectors.toList());
        }
        if (search.getMonitor() > 0) {
            result = result.stream().filter(p->p.getMonitor().getId() == search.getMonitor()).collect(Collectors.toList());
        }
        // Price
        if (search.getFrom() != null && search.getTo() != null) {
            result = result.stream().filter(p -> shopService.getPreBuiltPrice(p) >= search.getFrom()
                    && shopService.getPreBuiltPrice(p) <= search.getTo()).collect(Collectors.toList());
        }

        totalResult = result.size();

        // Paging
        result = result.stream().skip(pageDivide * (pageNumber - 1)).limit(pageDivide).collect(Collectors.toList());
        // Sort
        if (search.getSort() != null && search.getSort().equals("priceasc")) {
            sortString = search.getSort();
            result = result.stream().sorted(
                    Comparator.comparing(p -> shopService.getPreBuiltDiscountPrice(p), Comparator.naturalOrder()))
                    .collect(Collectors.toList());
        } else if (search.getSort() != null && search.getSort().equals("pricedesc")) {
            sortString = search.getSort();
            result = result.stream().sorted(
                    Comparator.comparing(p -> shopService.getPreBuiltDiscountPrice(p), Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            result = result.stream().sorted(Comparator.comparing(PreBuilt::getId, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
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
        return "HTDShop/prebuilt_search_result";
    }

}
