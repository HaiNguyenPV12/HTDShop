package vn.htdshop.controller.shop;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.PreBuiltImage;
import vn.htdshop.entity.Product;
import vn.htdshop.entity.ProductImage;
import vn.htdshop.sb.PreBuiltFacade;
import vn.htdshop.sb.PreBuiltFacadeLocal;
import vn.htdshop.sb.PreBuiltImageFacade;
import vn.htdshop.sb.PreBuiltImageFacadeLocal;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.BuildService;
import vn.htdshop.utility.ShopService;

/**
 * shopBuildAdd
 */
@Controller
@RequestMapping("build/add")
public class shopBuildAdd {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltFacade")
    PreBuiltFacadeLocal preBuiltFacade;

    @EJB(mappedName = "PreBuiltImageFacade")
    PreBuiltImageFacadeLocal prebuiltImageFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @Autowired
    ShopService shopService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuildForm(Model model, RedirectAttributes redirect) {
        if (!shopService.checkLogin()) {
            return "redirect:/build";
        }

        buildService.initBuildApp();

        if (model.asMap().containsKey("errorMessage")) {
            model.addAttribute("errorMessage", model.asMap().get("errorMessage"));
        }

        // -----Build IN SESSION---
        model.addAttribute("prebuilt", buildService.getSessionPrebuilt());
        model.addAttribute("shopService", shopService);
        return "HTDShop/prebuiltAdd";
    }

    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public String addBuild(@ModelAttribute("prebuilt") PreBuilt preBuilt, BindingResult error,
            RedirectAttributes redirect) {

        Integer redirectId = 0;

        if (preBuilt.getName().trim().isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Name is required");
            return "redirect:/build/add";
        } else if (preBuilt.getName().trim().length() <= 10) {
            redirect.addFlashAttribute("errorMessage", "Name must be at least 10 characters long");
            return "redirect:/build/add";
        }

        if (preBuilt.getDetail().trim().isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Please describe your build.");
            return "redirect:/build/add";
        } else if (preBuilt.getDetail().length() < 15) {
            redirect.addFlashAttribute("errorMessage",
                    "Build description is too short. Must be at least 15 characters.");
            return "redirect:/build/add";
        }

        // check for minimum required parts
        if (!isPartsValid(preBuilt)) {
            redirect.addFlashAttribute("errorMessage", "Build is missing required parts");
            return "redirect:/build/add";
        }

        try {
            preBuilt.setCustomer(shopService.getLoggedInCustomer());
            preBuilt.setCreatedAt(new Date());
            preBuilt.setStatus(2);
            preBuiltFacade.create(preBuilt);
            PreBuiltImage preBuiltImage = new PreBuiltImage();
            preBuiltImage.setPreBuilt(preBuilt);
            preBuiltImage.setPath("noimage.png"); // default image
            prebuiltImageFacade.create(preBuiltImage);
            Product myCase = productFacade.find(preBuilt.getCases().getId());
            if (myCase.getProductImageCollection() != null && myCase.getProductImageCollection().size() > 0) {
                // Copy Image
                String folderPath = System.getProperty("catalina.base") + "/img/prebuilt/" + preBuilt.getId();
                File folderCheck = new File(folderPath);
                if (!folderCheck.exists()) {
                    folderCheck.mkdirs();
                }
                // get case img
                ProductImage caseImage = (ProductImage) myCase.getProductImageCollection().toArray()[0];
                String ext = FilenameUtils.getExtension(caseImage.getImagePath());
                String fileName = "0." + ext;
                String originPath = System.getProperty("catalina.base") + "/img/" + caseImage.getImagePath();
                String filePath = folderPath + "/" + fileName;
                Files.copy(Paths.get(originPath), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                preBuiltImage.setPath("prebuilt/" + preBuilt.getId() + "/" + fileName);
                prebuiltImageFacade.edit(preBuiltImage);
            }
            redirectId = preBuilt.getId();
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("errorMessage", "Sorry, we couldn't process that request at the moment.");
            return "redirect:/build/add";
        }
        return "redirect:/prebuilt?id=" + redirectId;
    }

    private boolean isPartsValid(PreBuilt preBuilt) {
        if (preBuilt.getCpu() == null || preBuilt.getMotherboard() == null || preBuilt.getMemory() == null
                || preBuilt.getPsu() == null || preBuilt.getStorage() == null || preBuilt.getCases() == null) {
            return false;
        }
        return true;
    }
}
