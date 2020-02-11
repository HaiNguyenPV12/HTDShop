/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.ejb.EJB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import vn.htdshop.entity.Product;
import vn.htdshop.entity.ProductComment;
import vn.htdshop.entity.ProductCommentReply;
import vn.htdshop.entity.Search;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.ProductCommentFacadeLocal;
import vn.htdshop.sb.ProductCommentReplyFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;

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

        totalResult = result.size();

        // Paging
        result = result.stream().skip(pageDivide * (pageNumber - 1)).limit(pageDivide).collect(Collectors.toList());
        // Sort
        if (search.getSort() != null && search.getSort().equals("priceasc")) {
            sortString = search.getSort();
            result = result.stream().sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p), Comparator.naturalOrder()))
                    .collect(Collectors.toList());
        } else if (search.getSort() != null && search.getSort().equals("pricedesc")) {
            sortString = search.getSort();
            result = result.stream().sorted(Comparator.comparing(p -> shopService.getDiscountPrice(p), Comparator.reverseOrder()))
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
        return "HTDShop/search_result";
    }

}
