package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.BuildValues2;
import vn.htdshop.entity.Category;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.CategoryFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.BuildService;
import vn.htdshop.utility.ShopService;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build2")
public class shopBuild2Controller {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    PreBuilt preBuilt; // current build.

    BuildValues partValues; // current part filter values

    List<Product> buildProductList = null;

    @Autowired
    BuildService buildService;

    @Autowired
    ShopService shopService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuild(Model model) {
        model.asMap().put("userBuild", shopService.getUserSetting().getPrebuilt());
        return "HTDShop/build2";
    }

    @RequestMapping(value = "pick", method = RequestMethod.GET)
    public String getCpu(Model model, @RequestParam(value = "id", required = false) Integer id) {
        if (id == null) {
            return "redirect:/build";
        }
        Category category = categoryFacade.find(id);
        if (category == null) {
            return "redirect:/build";
        }
        shopService.checkLogin();
        BuildValues2 values = null;
        if (model.asMap().containsKey("filters")) {
            values = (BuildValues2) model.asMap().get("filters");
            if (!values.getId().equals(category.getId())) {
                values = new BuildValues2(category.getId(), category.getName());
            }
            model.addAttribute("filters", values);
        } else {
            values = new BuildValues2(category.getId(), category.getName());
            model.addAttribute("filters", values);
        }
        if (model.asMap().containsKey("filterList")) {
            model.addAttribute("filterList", model.asMap().get("filterList"));
        } else {
            List<Product> pList = productFacade.findAll().stream().filter(
                    p -> p.getCategory().getId() == category.getId() && (p.getStatus() == 1 || p.getStatus() == 2))
                    .collect(Collectors.toList());
            model.addAttribute("filterList", pList);
            session.setAttribute("pList", pList);
        }
        model.asMap().put("buildSv", buildService);
        model.asMap().put("category", category);
        return "HTDShop/pick";
    }

    @RequestMapping(value = "filter", method = RequestMethod.POST)
    public String getCpuFilter(Model model, @ModelAttribute("filters") BuildValues2 filters,
            RedirectAttributes redirect) {
        List<Product> pList = (List<Product>) session.getAttribute("pList");
        Stream<Product> filterList = pList.stream();
        if (filters.getManufacturer() != null && !filters.getManufacturer().equals("all")
                && !filters.getManufacturer().isEmpty()) {
            filterList = filterList.filter(p -> p.getManufacturer().equals(filters.getManufacturer()));
        }
        if (filters.getPriceMin() != null && filters.getPriceMin() > 0) {
            filterList = filterList.filter(p -> p.getPrice() >= filters.getPriceMin());
        }
        if (filters.getPriceMax() != null && filters.getPriceMax() < 2000) {
            filterList = filterList.filter(p -> p.getPrice() <= filters.getPriceMax());
        }
        if (filters.getSocket() != null && !filters.getSocket().equals("all") && !filters.getSocket().isEmpty()) {
            filterList = filterList.filter(p -> p.getSocket().equals(filters.getSocket()));
        }
        if (filters.getCore() != null && filters.getCore() > 0) {
            filterList = filterList.filter(p -> p.getCore() == filters.getCore());
        }
        if (filters.getThread() != null && filters.getThread() > 0) {
            filterList = filterList.filter(p -> p.getThread() == filters.getThread());
        }
        if (filters.getTdp() != null && filters.getTdp() > 0) {
            filterList = filterList.filter(p -> p.getTdp() <= filters.getTdp());
        }
        if (filters.getSeries() != null && !filters.getSeries().equals("all") && !filters.getSeries().isEmpty()) {
            filterList = filterList.filter(p -> p.getSeries().equals(filters.getSeries()));
        }
        List<Product> result = filterList.collect(Collectors.toList());
        if (result == null || result.size() <= 0) {
            result = new ArrayList<>();
        }
        redirect.addFlashAttribute("filters", filters);
        redirect.addFlashAttribute("filterList", result);
        return "redirect:/build2/pick?id=" + filters.getId();
    }
}
