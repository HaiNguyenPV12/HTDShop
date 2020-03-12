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
    public String getBuildForm(Model model) {
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

        // if (!isBuildMatch(preBuilt)) {
        // redirect.addFlashAttribute("errorMessage", "Error processing request.");
        // return "redirect:/build/add";
        // }

        if (preBuilt.getName().trim().isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Name is required");
            return "redirect:/build/add";
        }

        if (preBuilt.getDetail().trim().isEmpty()) {
            redirect.addFlashAttribute("errorMessage", "Please describe your build.");
            return "redirect:/build/add";
        }

        // check for minimum required parts
        if (!isPartsValid(preBuilt)) {
            redirect.addFlashAttribute("errorMessage", "Build is missing required parts");
            return "redirect:/build/add";
        }

        try {
            preBuilt.setCustomer(shopService.getLoggedInCustomer());
            // preBuilt.setCpu(preBuilt.getCpu() != null ? new
            // Product(preBuilt.getCpu().getId()) : null);
            // preBuilt.setCpucooler(
            // preBuilt.getCpucooler() != null ? new
            // Product(preBuilt.getCpucooler().getId()) : null);
            // preBuilt.setMotherboard(
            // preBuilt.getMotherboard() != null ? new
            // Product(preBuilt.getMotherboard().getId()) : null);
            // preBuilt.setMemory(preBuilt.getMemory() != null ? new
            // Product(preBuilt.getMemory().getId()) : null);
            // preBuilt.setStorage(preBuilt.getStorage() != null ? new
            // Product(preBuilt.getStorage().getId()) : null);
            // preBuilt.setVga(preBuilt.getVga() != null ? new
            // Product(preBuilt.getVga().getId()) : null);
            // preBuilt.setPsu(preBuilt.getPsu() != null ? new
            // Product(preBuilt.getPsu().getId()) : null);
            // preBuilt.setCases(preBuilt.getCases() != null ? new
            // Product(preBuilt.getCases().getId()) : null);
            // preBuilt.setMonitor(preBuilt.getMonitor() != null ? new
            // Product(preBuilt.getMonitor().getId()) : null);
            preBuilt.setCreatedAt(new Date());
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
        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("errorMessage", "Sorry, we couldn't process that request at the moment.");
            return "redirect:/build/add";
        }
        return "redirect:/build/";
    }

    private boolean isPartsValid(PreBuilt preBuilt) {
        if (preBuilt.getCpu() == null || preBuilt.getMotherboard() == null || preBuilt.getMemory() == null
                || preBuilt.getPsu() == null || preBuilt.getStorage() == null || preBuilt.getCases() == null) {
            return false;
        }
        return true;
    }

    private boolean isBuildMatch(PreBuilt preBuilt) {
        PreBuilt sessionPrebuilt = buildService.getSessionPrebuilt();
        if (preBuilt.getCpu().getId() != sessionPrebuilt.getCpu().getId()
                || preBuilt.getMotherboard().getId() != sessionPrebuilt.getMotherboard().getId()
                || preBuilt.getMemory().getId() != sessionPrebuilt.getMemory().getId()
                || preBuilt.getStorage().getId() != sessionPrebuilt.getStorage().getId()
                || preBuilt.getCases().getId() != sessionPrebuilt.getCases().getId()
                || preBuilt.getPsu().getId() != sessionPrebuilt.getPsu().getId()) {
            return false;
        }
        if (preBuilt.getCpucooler() != null) {
            if (preBuilt.getCpucooler().getId() != sessionPrebuilt.getCpucooler().getId()) {
                return false;
            }
        }
        if (preBuilt.getVga() != null) {
            if (preBuilt.getVga().getId() != sessionPrebuilt.getVga().getId()) {
                return false;
            }
        }
        if (preBuilt.getMonitor() != null) {
            if (preBuilt.getMonitor().getId() != sessionPrebuilt.getMonitor().getId()) {
                return false;
            }
        }
        return true;
    }
}
