package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.BuildCompatibility;
import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.BuildService;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build")
public class shopBuildController {

    boolean isFilteringCPU = false;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    PreBuilt preBuilt; // current build.

    BuildValues partValues; // current part filter values

    List<Product> buildProductList = null;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuild(Model model) {
        buildService.initBuildApp();

        setSessionBuildCompatibility(checkCompatibility());

        return "HTDShop/build";
    }

    // reset build link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset build values
        buildService.setSessionPrebuilt(new PreBuilt());
        return "redirect:/build";
    }

    private boolean isBuildStarted() {
        return session.getAttribute("isBuilding") == null;
    }

    // checking parts compatibilities
    public BuildCompatibility checkCompatibility() {
        BuildCompatibility result = getSessionBuildCompatibility();
        if (buildService.getSessionPrebuilt() != null) {
            PreBuilt currentBuild = buildService.getSessionPrebuilt();

            result.setSocketCompatible(checkCPUSocket(currentBuild)); // check socket
            result.setMemorySlotCompatible(checkMemorySlot(currentBuild)); // check mem slots
            result.setMemoryTypeCompatible(checkMemoryType(currentBuild)); // check mem types
            result.setPSUFormFactorCompatible(checkPSUFormFactor(currentBuild));
            result.setWattageCompatible(checkWattage(currentBuild));
            result.setFormFactorCompatible(checkFormFactor(currentBuild));

            setSessionBuildCompatibility(result);
        }
        return getSessionBuildCompatibility();
    }

    // check socket
    private boolean checkCPUSocket(PreBuilt currentBuild) {
        if (currentBuild.getCpu() != null && currentBuild.getMotherboard() != null) {
            Product cpu = currentBuild.getCpu();
            Product motherboard = currentBuild.getMotherboard();
            if (!cpu.getSocket().equals(motherboard.getSocket())) {
                return false;
            }
        }
        return true;
    }

    // check memory slot
    private boolean checkMemorySlot(PreBuilt currentBuild) {
        if (currentBuild.getMotherboard() != null && currentBuild.getMemory() != null) {
            Product motherboard = currentBuild.getMotherboard();
            Product memory = currentBuild.getMemory();
            if (motherboard.getMemorySlot() != memory.getMemoryModules()) {
                return false;
            }
        }
        return true;
    }

    // check memory type
    private boolean checkMemoryType(PreBuilt currentBuild) {
        if (currentBuild.getMotherboard() != null && currentBuild.getMemory() != null) {
            Product motherboard = currentBuild.getMotherboard();
            Product memory = currentBuild.getMemory();
            if (motherboard.getMemoryType() != memory.getMemoryType()) {
                return false;
            }
        }
        return true;
    }

    // check PSU vs Case
    private boolean checkPSUFormFactor(PreBuilt currentBuild) {
        if (currentBuild.getCases() != null && currentBuild.getPsu() != null) {
            Product cases = currentBuild.getCases();
            Product psu = currentBuild.getPsu();
            if (cases.getPSUFormFactor() != psu.getPSUFormFactor()) {
                return false;
            }
        }
        return true;
    }

    // check motherboard vs Case
    private boolean checkFormFactor(PreBuilt currentBuild) {
        if (currentBuild.getCases() != null && currentBuild.getMotherboard() != null) {
            Product cases = currentBuild.getCases();
            Product motherboard = currentBuild.getMotherboard();
            if (cases.getFormFactor() != motherboard.getFormFactor()) {
                return false;
            }
        }
        return true;
    }

    // check wattage
    private boolean checkWattage(PreBuilt currentBuild) {
        if (currentBuild.getCpu() != null && currentBuild.getVga() != null && currentBuild.getPsu() != null) {
            Product cpu = currentBuild.getCpu();
            Product gpu = currentBuild.getVga();
            Product psu = currentBuild.getPsu();
            if ((cpu.getTdp() + gpu.getTdp()) < psu.getPSUWattage()) {
                return false;
            }
        }
        return true;
    }

    public BuildCompatibility getSessionBuildCompatibility() {
        BuildCompatibility result = (BuildCompatibility) session.getAttribute("buildCompatibility");
        if (result == null) {
            BuildCompatibility newBuildCompatibility = initCompatibilityValues();
            setSessionBuildCompatibility(newBuildCompatibility);
        }
        return (BuildCompatibility) session.getAttribute("buildCompatibility");
    }

    public void setSessionBuildCompatibility(BuildCompatibility buildCompatibility) {
        session.setAttribute("buildCompatibility", buildCompatibility);
    }

    private BuildCompatibility initCompatibilityValues() {
        BuildCompatibility result = new BuildCompatibility();
        return result;
    }
}
