/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.controller.manager;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

/**
 *
 * @author Thien
 */
@Controller
@RequestMapping("manager")
public class managerIndexController {

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "CategoryFacade")
    CategoryFacadeLocal categoryFacade;

    @EJB(mappedName = "OrderDetailFacade")
    OrderDetailFacadeLocal orderDetailFacade;

    @EJB(mappedName = "Order1Facade")
    Order1FacadeLocal orderFacade;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model, HttpServletRequest request) {
        // Check if logged in session is exists
        if (!managerService.checkLogin()) {
            // If not, return to login page
            return "redirect:/manager/login";
        }
        // Check for any alert
        if (model.asMap().containsKey("goodAlert")) {
            System.out.println(model.asMap().get("goodAlert"));
            model.addAttribute("goodAlert", model.asMap().get("goodAlert"));
        }

        // Statistic
        ManagerStatistic statistic = new ManagerStatistic();
        if (managerService.checkLoginWithRole("revenue_read")) {
            // Orders and Income
            int oToday = 0;
            int oThisMonth = 0;
            int oThisYear = 0;
            double incomeToday = 0d;
            double incomeThisMonth = 0d;
            double incomeThisYear = 0d;
            Date today = new Date();
            int thisMonth = new LocalDate().getMonthOfYear();
            int thisYear = new LocalDate().getYear();
            DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
            for (Order1 order : orderFacade.findAll().stream().filter(o -> o.getOrderStatus() != 5)
                    .collect(Collectors.toList())) {
                double income = 0d;
                if (new LocalDate(order.getOrderDate()).getYear() == thisYear) {
                    oThisYear++;
                    if (order.getOrderStatus() == 4) {
                        for (OrderDetail od : order.getOrderDetailCollection()) {
                            income += od.getPrice();
                        }
                        incomeThisYear += income;
                    }
                }

                if (new LocalDate(order.getOrderDate()).getMonthOfYear() == thisMonth) {
                    oThisMonth++;
                    if (order.getOrderStatus() == 4) {
                        incomeThisMonth += income;
                    }
                }

                if (comparator.compare(order.getOrderDate(), today) == 0) {
                    oToday++;
                    if (order.getOrderStatus() == 4) {
                        incomeThisMonth += income;
                    }
                }
            }
            statistic.setIncomeToday(incomeToday);
            statistic.setIncomeThisMonth(incomeThisMonth);
            statistic.setIncomeThisYear(incomeThisYear);
            statistic.setOrderToday(oToday);
            statistic.setOrderThisMonth(oThisMonth);
            statistic.setOrderThisYear(oThisYear);
        }

        if (managerService.checkLoginWithRole("product_read")) {
            // Best seller product

            // Comparator<Product> cByMonth = new Comparator<Product>() {
            // @Override
            // public int compare(Product p1, Product p2) {
            // return Integer.compare(
            // p1.getOrderDetailCollection().stream()
            // .filter(o -> o.getOrder1().getOrderStatus() == 4
            // && new LocalDate(o.getOrder1().getPaidDate())
            // .getMonthOfYear() == new LocalDate().getMonthOfYear())
            // .collect(Collectors.toList()).size(),
            // p2.getOrderDetailCollection().stream()
            // .filter(o -> o.getOrder1().getOrderStatus() == 4
            // && new LocalDate(o.getOrder1().getPaidDate())
            // .getMonthOfYear() == new LocalDate().getMonthOfYear())
            // .collect(Collectors.toList()).size());
            // }
            // };
            // Product p =
            // productFacade.findAll().stream().sorted(cByMonth).findFirst().orElse(null);
            // if (p != null && p.getOrderDetailCollection().stream().filter(o ->
            // o.getOrder1().getOrderStatus() == 4
            // && new LocalDate(o.getOrder1().getPaidDate()).getMonthOfYear() == new
            // LocalDate().getMonthOfYear())
            // .collect(Collectors.toList()).size() > 0) {
            // statistic.setProductThisMonth(p.getName());
            // if (p.getProductImageCollection().size() > 0) {
            // ProductImage pi = p.getProductImageCollection().stream().filter(img ->
            // img.getMainImage())
            // .findFirst().orElse(null);
            // if (pi == null) {
            // pi = (ProductImage) p.getProductImageCollection().toArray()[0];
            // }

            // if (pi.getThumbnailPath() != null && !pi.getThumbnailPath().isEmpty()) {
            // statistic.setProductImageThisMonth(pi.getThumbnailPath());
            // } else {
            // statistic.setProductImageThisMonth(pi.getImagePath());
            // }
            // } else {
            // statistic.setProductImageThisMonth("images/noimage.png");
            // }
            // } else {
            // statistic.setProductThisMonth("No data");
            // statistic.setProductImageThisMonth("");
            // }

            Map<Integer, Integer> productMonth = orderDetailFacade.getTopProduct("month", 1);
            if (!productMonth.isEmpty()) {
                Product p = productFacade.find(productMonth.entrySet().iterator().next().getKey());
                statistic.setProductThisMonth(p.getName());
                if (p.getProductImageCollection().size() > 0) {
                    ProductImage pi = p.getProductImageCollection().stream().filter(img -> img.getMainImage())
                            .findFirst().orElse(null);
                    if (pi == null) {
                        pi = (ProductImage) p.getProductImageCollection().toArray()[0];
                    }
                    if (pi.getThumbnailPath() != null && !pi.getThumbnailPath().isEmpty()) {
                        statistic.setProductImageThisMonth(pi.getThumbnailPath());
                    } else {
                        statistic.setProductImageThisMonth(pi.getImagePath());
                    }
                } else {
                    statistic.setProductImageThisMonth("images/noimage.png");
                }
            } else {
                statistic.setProductThisMonth("No data");
                statistic.setProductImageThisMonth("");
            }

            // Best seller category
            Map<Integer, Integer> categoryMonth = orderDetailFacade.getTopCategory("month", 1);
            if (!categoryMonth.isEmpty()) {
                Category c = categoryFacade.find(categoryMonth.entrySet().iterator().next().getKey());
                statistic.setCategoryThisMonth(c.getName());
            } else {
                statistic.setCategoryThisMonth("No data");
            }

            // Best seller manufacturer
            Map<String, Integer> manuMonth = orderDetailFacade.getTopManufacturer("month", 1);
            if (!manuMonth.isEmpty()) {
                statistic.setManuThisMonth(manuMonth.entrySet().iterator().next().getKey());
            } else {
                statistic.setManuThisMonth("No data");
            }

        }

        model.asMap().put("statistic", statistic);

        // Continue to index
        return "HTDManager/index";

    }

    @RequestMapping(value = "topProduct", method = RequestMethod.GET)
    public @ResponseBody Object getTopProduct(HttpServletResponse response, @RequestParam String datepart,
            @RequestParam Integer top) {
        response.setContentType("application/json");
        return orderDetailFacade.getTopProduct(datepart, top);
    }

    @RequestMapping(value = "topCategory", method = RequestMethod.GET)
    public @ResponseBody Object getTopCategory(HttpServletResponse response, @RequestParam String datepart,
            @RequestParam Integer top) {
        response.setContentType("application/json");
        return orderDetailFacade.getTopCategory(datepart, top);
    }

    @RequestMapping(value = "topManu", method = RequestMethod.GET)
    public @ResponseBody Object getTopManu(HttpServletResponse response, @RequestParam String datepart,
            @RequestParam Integer top) {
        response.setContentType("application/json");
        return orderDetailFacade.getTopManufacturer(datepart, top);
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String getLogin(@ModelAttribute("staff") Staff staff, Model model, ModelMap modelMap, HttpSession session) {
        // Check if logged in session is exists
        if (managerService.checkLogin()) {
            // If yes, redirect to index
            return "redirect:/manager";
        }
        // Prepare staff model if not exists (for postLogin process)
        model.addAttribute("staff", new Staff());
        // Show error (if exists) after redirect to this page again
        if (model.asMap().containsKey("error")) {
            // Here is using for staff attribute that is declared, for other attribute,
            // please change
            // "org.springframework.validation.BindingResult.staff" to
            // "org.springframework.validation.BindingResult.your_attribute's_name_here"
            model.addAttribute("org.springframework.validation.BindingResult.staff", model.asMap().get("error"));
        }
        // Continue to login page
        return "HTDManager/login";
    }

    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    // Add @Valid before @ModelAttribute to validate base on entity annotation
    // For example: public String postLogin(@Valid @ModelAttribute("staff") Staff
    // staff...){}
    // Here we just have to check username and password, not all so we check
    // manually
    public String postLogin(@ModelAttribute("staff") Staff staff,
            @RequestParam(value = "remember", required = false) String remember, Model model, BindingResult error,
            RedirectAttributes redirect, HttpSession session, HttpServletResponse response) {
        // Mannually check blank username
        if (staff.getUsername() == null || staff.getUsername().isEmpty()) {
            error.rejectValue("username", "staff", "Username cannot be blank.");
        }
        // Mannually check blank password
        if (staff.getPassword().isEmpty()) {
            error.rejectValue("password", "staff", "Password cannot be blank.");
        }

        // Check if error exists
        if (!error.hasErrors()) {
            // If not, start to check login
            Staff result = staffFacade.checkLogin(staff.getUsername(), staff.getPassword());
            if (result != null) {
                // If ok, save staff's session
                session.setAttribute("loggedInStaff", result);
                if (remember != null) {
                    Cookie cookie = new Cookie("loggedInStaff", staff.getUsername());
                    cookie.setMaxAge(60 * 60 * 24 * 7 * 4);
                    response.addCookie(cookie);
                }

                redirect.addFlashAttribute("goodAlert", "Successfully logged in as \"" + result.getFirstName() + "\".");
                // Then redirect to index
                return "redirect:/manager/index";
            }
            // If checking is false, manually add error
            error.rejectValue("username", "staff", "Invalid Login.");
        }

        // Add error and staff's input info to redirect session
        redirect.addFlashAttribute("error", error);
        redirect.addFlashAttribute("staff", staff);
        // redirect to login page
        return "redirect:/manager/login";
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String getLogout(HttpSession session, HttpServletResponse response) {
        // remove session
        session.removeAttribute("loggedInStaff");
        // remove cookie
        Cookie cookie = new Cookie("loggedInStaff", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        // redirect to login
        return "redirect:/manager/login";
    }
}
