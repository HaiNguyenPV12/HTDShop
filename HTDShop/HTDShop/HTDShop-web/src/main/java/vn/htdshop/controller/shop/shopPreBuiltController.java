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

import vn.htdshop.entity.Product;

import vn.htdshop.sb.CategoryFacadeLocal;
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
    public String getSearch(Model model, @RequestParam(required = false) Map<String, String> param) {
        shopService.checkLogin();

        return "HTDShop/prebuilt";
    }

    @RequestMapping(value = "result", method = RequestMethod.POST)
    public String getSearchResult(Model model, @RequestParam(value = "c", required = false) Integer category,
            @RequestParam(value = "k", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "style", required = false) String style,
            @RequestParam(value = "from", required = false) Double from,
            @RequestParam(value = "to", required = false) Double to,
            @RequestParam(value = "sort", required = false) String sort) {

        Integer pageDivide = 12;
        Integer pageNumber = 1;
        Integer totalResult = 0;
        Integer totalPage = 0;
        String sortString = "default";
        if (style != null && style.equals("list")) {
            pageDivide = 6;
        }
        if (page != null && page > 1) {
            pageNumber = page;
        }

        // Create list
        List<Product> result = productFacade.search(category, keyword);

        // Price
        if (from != null && to != null) {
            result = result.stream().filter(p -> p.getPrice() >= from && p.getPrice() <= to)
                    .collect(Collectors.toList());
        }

        totalResult = result.size();

        // Paging
        result = result.stream().filter(p -> p.getStatus() != 3).skip(pageDivide * (pageNumber - 1)).limit(pageDivide)
                .collect(Collectors.toList());
        // Sort
        if (sort != null && sort.equals("priceasc")) {
            sortString = sort;
            result = result.stream().sorted(Comparator.comparing(Product::getPrice, Comparator.naturalOrder()))
                    .collect(Collectors.toList());
        } else if (sort != null && sort.equals("pricedesc")) {
            sortString = sort;
            result = result.stream().sorted(Comparator.comparing(Product::getPrice, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        } else {
            result = result.stream().sorted(Comparator.comparing(Product::getId, Comparator.reverseOrder()))
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
        return "HTDShop/searchResult";
    }

    @RequestMapping(value = "productdetail", method = RequestMethod.POST)
    public String getProductDetail(Model model, @RequestParam(value = "id", required = false) Integer id) {
        model.asMap().put("product", productFacade.find(id));
        return "HTDShop/searchProductDetail";
    }

}
