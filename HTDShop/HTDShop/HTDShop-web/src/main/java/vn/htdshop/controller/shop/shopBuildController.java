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
import vn.htdshop.utility.ShopService;

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
    ShopService shopService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getBuild(Model model) {
        buildService.initBuildApp();

        // total price
        PreBuilt sessionPrebuilt = buildService.getSessionPrebuilt();
        buildService.setSessionPrebuilt(sessionPrebuilt);
        setSessionBuildCompatibility(checkCompatibility());
        sessionPrebuilt.setPrice(buildPrice(sessionPrebuilt));

        model.addAttribute("shopService", shopService);
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

            result.setIsSocketCompatible(checkCPUSocket(currentBuild)); // check socket
            result.setIsMemorySlotCompatible(checkMemorySlot(currentBuild)); // check mem slots
            result.setIsMemoryTypeCompatible(checkMemoryType(currentBuild)); // check mem types
            result.setIsPSUFormFactorCompatible(checkPSUFormFactor(currentBuild));
            result.setIsWattageCompatible(checkWattage(currentBuild));
            result.setIsFormFactorCompatible(checkFormFactor(currentBuild));
            result.setIsCoolerCompatible(checkCoolerSocket(currentBuild));
            setSessionBuildCompatibility(result);
        }
        return getSessionBuildCompatibility();
    }

    // check socket
    private boolean checkCPUSocket(PreBuilt currentBuild) {
        if (currentBuild.getCpu() != null && currentBuild.getMotherboard() != null && currentBuild.getCpu().getId() != null && currentBuild.getMotherboard().getId() != null) {
            Product cpu = currentBuild.getCpu();
            Product motherboard = currentBuild.getMotherboard();
            if (!cpu.getSocket().equals(motherboard.getSocket())) {
                return false;
            }
        }
        return true;
    }

    // check cooler
    private boolean checkCoolerSocket(PreBuilt currentBuild) {
        if (currentBuild.getCpu() != null && currentBuild.getCpucooler() != null
                && currentBuild.getCpu().getId() != null && currentBuild.getCpucooler().getId() != null) {
            Product cpu = currentBuild.getCpu();
            Product cooler = currentBuild.getCpucooler();
            if (!cooler.getSocket().contains(cpu.getSocket())) {
                return false;
            }
        }
        return true;
    }

    // check memory slot
    private boolean checkMemorySlot(PreBuilt currentBuild) {
        if (currentBuild.getMotherboard() != null && currentBuild.getMemory() != null
                && currentBuild.getMotherboard().getId() != null && currentBuild.getMemory().getId() != null) {
            Product motherboard = currentBuild.getMotherboard();
            Product memory = currentBuild.getMemory();
            if (motherboard.getMemorySlot() < memory.getMemoryModules()) {
                return false;
            }
        }
        return true;
    }

    // check memory type
    private boolean checkMemoryType(PreBuilt currentBuild) {
        if (currentBuild.getMotherboard() != null && currentBuild.getMemory() != null && currentBuild.getMotherboard().getId() != null && currentBuild.getMemory().getId() != null) {
            Product motherboard = currentBuild.getMotherboard();
            Product memory = currentBuild.getMemory();
            if (!motherboard.getMemoryType().equals(memory.getMemoryType())) {
                return false;
            }
        }
        return true;
    }

    // check PSU vs Case
    private boolean checkPSUFormFactor(PreBuilt currentBuild) {
        if (currentBuild.getCases() != null && currentBuild.getPsu() != null
                && currentBuild.getCases().getId() != null && currentBuild.getPsu().getId() != null) {
            Product cases = currentBuild.getCases();
            Product psu = currentBuild.getPsu();
            if (!cases.getPSUFormFactor().equals(psu.getPSUFormFactor())) {
                Integer caseSize = buildService.psuFormFactorSizes().indexOf(cases.getFormFactor());
                Integer psuSize = buildService.psuFormFactorSizes().indexOf(psu.getFormFactor());
                if (caseSize < psuSize) {
                    return false;
                }
            }
        }
        return true;
    }

    // check motherboard vs Case
    private boolean checkFormFactor(PreBuilt currentBuild) {
        if (currentBuild.getCases() != null && currentBuild.getMotherboard() != null
                && currentBuild.getCases().getId() != null && currentBuild.getMotherboard().getId() != null) {
            Product cases = currentBuild.getCases();
            Product motherboard = currentBuild.getMotherboard();
            if (!cases.getFormFactor().equals(motherboard.getFormFactor())) {
                Integer caseSize = buildService.motherboardFormFactorSizes().indexOf(cases.getFormFactor());
                Integer motherboardSize = buildService.motherboardFormFactorSizes()
                        .indexOf(motherboard.getFormFactor());
                if (caseSize < motherboardSize) {
                    return false;
                }
            }
        }
        return true;
    }

    // check wattage
    private boolean checkWattage(PreBuilt currentBuild) {
        if (currentBuild.getCpu() != null && currentBuild.getVga() != null && currentBuild.getPsu() != null && currentBuild.getCpu().getId() != null && currentBuild.getVga().getId() != null
                && currentBuild.getPsu().getId() != null) {
            Product cpu = currentBuild.getCpu();
            Product gpu = currentBuild.getVga();
            Product psu = currentBuild.getPsu();
            if ((cpu.getTdp() + gpu.getTdp()) > psu.getPSUWattage()) {
                return false;
            }
        }
        return true;
    }

    public BuildCompatibility getSessionBuildCompatibility() {
        if (session.getAttribute("buildCompatibility") == null) {
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

    // Total Price
    public Double buildPrice(PreBuilt currentBuild) {
        Double result = new Double(0);
        if (currentBuild.getCpu() != null && currentBuild.getCpu().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getCpu().getId());
        }
        if (currentBuild.getCpucooler() != null && currentBuild.getCpucooler().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getCpucooler().getId());
        }
        if (currentBuild.getMotherboard() != null && currentBuild.getMotherboard().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getMotherboard().getId());
        }
        if (currentBuild.getMemory() != null && currentBuild.getMemory().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getMemory().getId());
        }
        if (currentBuild.getStorage() != null && currentBuild.getStorage().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getStorage().getId());
        }
        if (currentBuild.getVga() != null && currentBuild.getVga().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getVga().getId());
        }
        if (currentBuild.getCases() != null && currentBuild.getCases().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getCases().getId());
        }
        if (currentBuild.getPsu() != null && currentBuild.getPsu().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getPsu().getId());
        }
        if (currentBuild.getMonitor() != null && currentBuild.getMonitor().getId() != null) {
            result = result + shopService.getDiscountPrice(currentBuild.getMonitor().getId());
        }
        return result;
    }
}
