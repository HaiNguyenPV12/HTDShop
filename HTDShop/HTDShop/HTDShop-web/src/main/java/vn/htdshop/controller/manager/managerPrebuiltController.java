package vn.htdshop.controller.manager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.ManagerService;

/**
 * managerPrebuiltController
 */
@Controller
@RequestMapping("manager/prebuilt")
public class managerPrebuiltController {
    private final String redirectPrebuiltHome = "redirect:/manager/prebuilt";
    private final String redirectHome = "redirect:/manager";

    @EJB(mappedName = "StaffFacade")
    StaffFacadeLocal staffFacade;

    @EJB(mappedName = "RoleFacade")
    RoleFacadeLocal roleFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("prebuilt_read")) {
            return redirectHome;
        }

        // Prebuilt list
        List<PreBuilt> prebuiltList = preBuiltFacade.findAll();
        model.asMap().put("prebuilts", prebuiltList);

        model.asMap().put("menu", "prebuilt");
        return "HTDManager/prebuilt";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String viewAdd(Model model) {

        if (!managerService.checkLoginWithRole("prebuilt_add")) {
            return redirectHome;
        }

        // prepare model
        PreBuilt preBuilt = new PreBuilt();
        model.addAttribute("prebuilt", preBuilt);
        model.addAttribute("formUrl", "doAdd");
        model.asMap().put("menu", "prebuilt");
        // form values
        model.addAttribute("caseList", sellingProducts(8));
        return "HTDManager/prebuilt_template";
    }

    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String viewEdit(Model model, @RequestParam(value = "id") int id) {

        if (!managerService.checkLoginWithRole("prebuilt_edit")) {
            return redirectHome;
        }

        // prepare model
        PreBuilt preBuilt = preBuiltFacade.find(id);
        model.addAttribute("prebuilt", preBuilt);
        model.addAttribute("formUrl", "doEdit");
        model.asMap().put("menu", "prebuilt");
        model.asMap().put("update", "update");
        // form values
        model.addAttribute("caseList", sellingProducts(8));
        return "HTDManager/prebuilt_template";
    }

    private List<Product> sellingProducts(Integer category) {
        List<Product> result = new ArrayList<>();
        try {
            result = productFacade.findAll();
            result = result.stream().filter(p -> p.getCategory().getId() == category).collect(Collectors.toList());
            // return only selling items
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getStatus() != 1) {
                    result.remove(i);
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}