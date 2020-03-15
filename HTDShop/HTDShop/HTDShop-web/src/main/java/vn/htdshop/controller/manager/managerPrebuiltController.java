package vn.htdshop.controller.manager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.htdshop.entity.*;
import vn.htdshop.sb.*;
import vn.htdshop.utility.BuildService;
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

    @EJB(mappedName = "PreBuiltImageFacade")
    PreBuiltImageFacadeLocal preBuiltImageFacade;

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @EJB(mappedName = "PreBuiltRatingFacade")
    PreBuiltRatingFacadeLocal preBuiltRatingFacade;

    @Autowired
    ServletContext context;

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ManagerService managerService;

    @Autowired
    BuildService buildService;

    @RequestMapping(value = { "", "index" }, method = RequestMethod.GET)
    public String getHome(Model model) {
        // Check login session with role
        if (!managerService.checkLoginWithRole("prebuilt_read")) {
            return redirectHome;
        }

        // Prebuilt list
        List<PreBuilt> prebuiltList = preBuiltFacade.findAll();
        prebuiltList = prebuiltList.stream().sorted(Comparator.comparing(PreBuilt::getStatus, Comparator.naturalOrder())
                .thenComparing(PreBuilt::getId, Comparator.naturalOrder())).collect(Collectors.toList());
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
        model.addAttribute("check", "add");
        model.asMap().put("menu", "prebuilt");

        if (model.asMap().containsKey("error")) {
            model.addAttribute("org.springframework.validation.BindingResult.preBuilt", model.asMap().get("error"));
            model.addAttribute("submited", "submited");
        }

        // form values
        model = sortedPartList(model);
        return "HTDManager/prebuilt_template";
    }

    @RequestMapping(value = "doAdd", method = RequestMethod.POST)
    public @ResponseBody Object doAdd(@ModelAttribute("prebuilt") PreBuilt prebuilt, Model model,
            @RequestParam(value = "image", required = false) MultipartFile[] image, BindingResult error) {

        if (!managerService.checkLoginWithRole("prebuilt_add")) {
            return redirectPrebuiltHome;
        }

        error = getError(prebuilt, error, false);
        if (image == null || image.length == 0) {
            error.reject("common", "Prebuilt must have preview images");
        }

        if (image.length == 1 && image[0].getSize() <= 0) {
            error.reject("common", "Prebuilt must have preview images");
        }

        if (!error.hasErrors()) {
            prebuilt.setStaff(managerService.getLoggedInStaff());
            prebuilt.setCreatedAt(new Date());
            prebuilt.setStatus(1);
            preBuiltFacade.create(prebuilt);
            if (image != null && image.length > 0) {
                if (!uploadImages(image, prebuilt, false)) {
                    error.reject("common", "Error uploading images.");
                }
            }
        }

        return error.getAllErrors();
        // return "redirect:/manager/prebuilt/add";
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
        model = sortedPartList(model);
        return "HTDManager/prebuilt_template";
    }

    @RequestMapping(value = "doEdit", method = RequestMethod.POST)
    public @ResponseBody Object doEdit(@ModelAttribute("prebuilt") PreBuilt prebuilt,
            @RequestParam(value = "image", required = false) MultipartFile[] image, Model model, BindingResult error) {

        if (!managerService.checkLoginWithRole("prebuilt_edit")) {
            return redirectPrebuiltHome;
        }

        error = getError(prebuilt, error, true);

        if (!error.hasErrors()) {
            prebuilt.setCreatedAt(new Date());
            preBuiltFacade.edit(prebuilt);
            if (prebuilt.getCustomer() == null) {
                if (image != null && image.length > 0) {
                    if (!uploadImages(image, prebuilt, true)) {
                        error.reject("common", "Error uploading images.");
                    }
                }
            }
        }

        return error.getAllErrors();
        // return "redirect:/manager/prebuilt/add";
    }

    @RequestMapping(value = "details", method = RequestMethod.GET)
    public String viewDetail(@RequestParam(value = "id") int id, Model model) {
        if (!managerService.checkLoginWithRole("prebuilt_read")) {
            return redirectPrebuiltHome;
        }
        model.asMap().put("menu", "prebuilt");
        PreBuilt prebuilt = preBuiltFacade.find(id);
        prebuilt = getPrebuiltValues(prebuilt);
        model.addAttribute("prebuilt", prebuilt);
        return "HTDManager/prebuilt_detail";
    }

    @RequestMapping(value = "rating", method = RequestMethod.GET)
    public String viewRating(@RequestParam(value = "id") Integer id, Model model) {
        if (!managerService.checkLoginWithRole("prebuilt_read")) {
            return redirectPrebuiltHome;
        }

        model.asMap().put("menu", "prebuilt");
        PreBuilt prebuilt = preBuiltFacade.find(id);

        List<PreBuiltRating> ratings = preBuiltRatingFacade.findAll();
        ratings = ratings.stream().filter(p -> p.getPreBuilt().getId() == id).collect(Collectors.toList());

        prebuilt = getPrebuiltValues(prebuilt);

        model.addAttribute("prebuilt", prebuilt);
        model.addAttribute("ratings", ratings);
        return "HTDManager/prebuilt_rating";
    }

    @RequestMapping(value = "rating/delete", method = RequestMethod.GET)
    public String deleteRating(@RequestParam(value = "id") Integer id, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("prebuilt_edit")) {
            return redirectPrebuiltHome;
        }
        PreBuiltRating rating = preBuiltRatingFacade.find(id);
        Integer prebuiltId = rating.getPreBuilt().getId();
        preBuiltRatingFacade.remove(rating);
        return "redirect:/manager/prebuilt/rating?id=" + prebuiltId;
    }

    @RequestMapping(value = "disable", method = RequestMethod.GET)
    public String delistPrebuilt(@RequestParam(value = "id") Integer id, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("prebuilt_edit")) {
            return redirectPrebuiltHome;
        }
        PreBuilt prebuilt = preBuiltFacade.find(id);

        if (prebuilt.getDetail() == null || prebuilt.getDetail().trim().isEmpty()) {
            prebuilt.setDetail("&nbsp;");
        }

        prebuilt.setStatus(3);
        preBuiltFacade.edit(prebuilt);
        return redirectPrebuiltHome;
    }

    @RequestMapping(value = "enable", method = RequestMethod.GET)
    public String enablePrebuilt(@RequestParam(value = "id") Integer id, RedirectAttributes redirect) {
        if (!managerService.checkLoginWithRole("prebuilt_edit")) {
            return redirectPrebuiltHome;
        }
        PreBuilt prebuilt = preBuiltFacade.find(id);

        if (prebuilt.getDetail() == null || prebuilt.getDetail().trim().isEmpty()) {
            prebuilt.setDetail("&nbsp;");
        }

        prebuilt.setStatus(1);
        preBuiltFacade.edit(prebuilt);
        return redirectPrebuiltHome;
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

    private Model sortedPartList(Model model) {
        Model result = model;
        result.addAttribute("cpuList", sellingProducts(1)); // CPUs
        result.addAttribute("cpucoolerList", sellingProducts(7)); // Coolers
        result.addAttribute("motherboardList", sellingProducts(2)); // Motherboards
        result.addAttribute("memoryList", sellingProducts(4)); // RAMs
        result.addAttribute("storageList", sellingProducts(6)); // Storage
        result.addAttribute("gpuList", sellingProducts(3)); // GPUs
        result.addAttribute("caseList", sellingProducts(8)); // cases
        result.addAttribute("psuList", sellingProducts(5)); // PSUs
        result.addAttribute("monitorList", sellingProducts(9)); // monitors
        return result;
    }

    private BindingResult getError(PreBuilt prebuilt, BindingResult error, boolean isUpdating) {
        Integer cpucoolerId = prebuilt.getCpucooler().getId();
        Integer gpuId = prebuilt.getVga().getId();
        Integer monitorId = prebuilt.getMonitor().getId();

        if (cpucoolerId == -1) {
            prebuilt.setCpucooler(null);
        }
        if (gpuId == -1) {
            prebuilt.setVga(null);
        }
        if (monitorId == -1) {
            prebuilt.setMonitor(null);
        }

        BindingResult errors = error;
        if (prebuilt.getName().trim().isEmpty()) {
            error.rejectValue("name", "prebuilt", "Build name is invalid");
        }

        if (prebuilt.getDetail().trim().isEmpty()) {
            error.rejectValue("detail", "prebuilt", "Details is invalid");
        }

        // check compatibility
        prebuilt = getPrebuiltValues(prebuilt);
        BuildCompatibility compatibility = checkCompatibility(prebuilt);
        if (!compatibility.isIsSocketCompatible()) {
            error.reject("common", "CPU Socket " + prebuilt.getCpu().getSocket()
                    + " incompatible with motherboard socket " + prebuilt.getMotherboard().getSocket());
        }
        if (!compatibility.isIsCoolerCompatible()) {
            error.reject("common", "CPU Socket " + prebuilt.getCpu().getSocket() + " incompatible with cooler socket "
                    + prebuilt.getCpucooler().getSocket());
        }
        if (!compatibility.isIsMemorySlotCompatible()) {
            error.reject("common", "Selected memory modules: " + prebuilt.getMemory().getMemoryModules()
                    + " incompatible with motherboard memory slots: " + prebuilt.getMotherboard().getMemoryModules());
        }
        if (!compatibility.isIsMemoryTypeCompatible()) {
            error.reject("common", "Selected memory type: " + prebuilt.getMemory().getMemoryType()
                    + " incompatible with motherboard memory type:  " + prebuilt.getMotherboard().getMemoryType());
        }
        if (!compatibility.isIsPSUFormFactorCompatible()) {
            error.reject("common", "Selected PSU size: " + prebuilt.getPsu().getPSUFormFactor()
                    + " incompatible with case with sizes:  " + prebuilt.getCases().getPSUFormFactor());
        }
        if (!compatibility.isIsFormFactorCompatible()) {
            error.reject("common", "Motherboard form factor: " + prebuilt.getMotherboard().getFormFactor()
                    + " incompatible with case with sizes:  " + prebuilt.getCases().getFormFactor());
        }
        if (!compatibility.isIsWattageCompatible()) {
            error.reject("common", "System wattage exceeds PSU max wattage: " + prebuilt.getPsu().getPSUWattage());
        }

        if (prebuilt.getCustomer() == null || !isUpdating) {
            Double price = prebuilt.getPrice();
            if (price == null || price < 0 || price > 100000) {
                error.rejectValue("price", "prebuilt", "Price is invalid");
            }
            Integer stock = prebuilt.getStock();
            if (stock == null || stock < 0 || stock > 1000) {
                error.rejectValue("stock", "prebuilt", "Stock is invalid");
            }
        }
        return errors;
    }

    private Boolean uploadImages(MultipartFile[] uploadimg, PreBuilt prebuilt, boolean deleteOldImages) {
        try {
            // Remove image
            if (deleteOldImages) {
                for (PreBuiltImage img : preBuiltFacade.find(prebuilt.getId()).getPreBuiltImageCollection()) {
                    // First, delete real file.
                    File deleteFile = new File(System.getProperty("catalina.home") + "/img/" + img.getPath());
                    if (deleteFile.delete()) {
                        System.out.println("Deleted image: " + deleteFile.getPath());
                    } else {
                        System.out.println("Cannot delete image: " + deleteFile.getPath());
                    }
                    // Then, delete record in database
                    preBuiltImageFacade.remove(img);
                }
            }

            // System.getProperty("catalina.base") : Path_to_glassfish/domains/domain_name/
            // Initialize folder path
            String folderPath = System.getProperty("catalina.base") + "/img/prebuilt/" + prebuilt.getId();
            File checkPath = new File(folderPath);
            // Check if path is not exists, create path to it
            if (!checkPath.exists()) {
                checkPath.mkdirs();
            }

            // With each of file, do following
            Integer count = 0;
            String[] extensions = { "jpg", "jpeg", "png" };
            for (MultipartFile multipartFile : uploadimg) {
                // File name: [count].[extension]
                String originName = new String(count.toString());
                String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                boolean isImageExtension = false;
                for (String extString : extensions) {
                    if (extString.equals(ext)) {
                        isImageExtension = true;
                        break;
                    }
                }
                if (!isImageExtension) {
                    continue;
                }
                String fileName = originName + "." + ext;
                // file path:
                // Path_to_glassfish/domains/domain_name/img/product/product_id/count.extension
                String filePath = folderPath + "/" + fileName;
                // Use Files to copy multipartFile's input stream to declared path
                Files.copy(multipartFile.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

                // Create image data in database
                PreBuiltImage pimg = new PreBuiltImage();
                pimg.setPath("prebuilt/" + prebuilt.getId() + "/" + fileName);
                pimg.setPreBuilt(prebuilt);
                preBuiltImageFacade.create(pimg);
                count++;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Prebuilt with full values
    private PreBuilt getPrebuiltValues(PreBuilt prebuilt) {
        prebuilt.setCpu(productFacade.find(prebuilt.getCpu().getId()));
        prebuilt.setMotherboard(productFacade.find(prebuilt.getMotherboard().getId()));
        prebuilt.setMemory(productFacade.find(prebuilt.getMemory().getId()));
        prebuilt.setStorage(productFacade.find(prebuilt.getStorage().getId()));
        prebuilt.setPsu(productFacade.find(prebuilt.getPsu().getId()));
        prebuilt.setCases(productFacade.find(prebuilt.getCases().getId()));
        if (prebuilt.getVga() != null) {
            prebuilt.setVga(productFacade.find(prebuilt.getVga().getId()));
        }
        if (prebuilt.getCpucooler() != null) {
            prebuilt.setCpucooler(productFacade.find(prebuilt.getCpucooler().getId()));
        }
        return prebuilt;
    }

    // checking parts compatibilities
    public BuildCompatibility checkCompatibility(PreBuilt prebuilt) {
        BuildCompatibility result = new BuildCompatibility();
        if (prebuilt != null) {
            result.setIsSocketCompatible(checkCPUSocket(prebuilt)); // check socket
            result.setIsMemorySlotCompatible(checkMemorySlot(prebuilt)); // check mem slots
            result.setIsMemoryTypeCompatible(checkMemoryType(prebuilt)); // check mem types
            result.setIsPSUFormFactorCompatible(checkPSUFormFactor(prebuilt));
            result.setIsWattageCompatible(checkWattage(prebuilt));
            result.setIsFormFactorCompatible(checkFormFactor(prebuilt));
            result.setIsCoolerCompatible(checkCoolerSocket(prebuilt));
        }
        return result;
    }

    // check socket
    private boolean checkCPUSocket(PreBuilt currentBuild) {
        if (currentBuild.getCpu() != null && currentBuild.getMotherboard() != null
                && currentBuild.getCpu().getId() != null && currentBuild.getMotherboard().getId() != null) {
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
        if (currentBuild.getMotherboard() != null && currentBuild.getMemory() != null
                && currentBuild.getMotherboard().getId() != null && currentBuild.getMemory().getId() != null) {
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
        if (currentBuild.getCases() != null && currentBuild.getPsu() != null && currentBuild.getCases().getId() != null
                && currentBuild.getPsu().getId() != null) {
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
        if (currentBuild.getCpu() != null && currentBuild.getVga() != null && currentBuild.getPsu() != null
                && currentBuild.getCpu().getId() != null && currentBuild.getVga().getId() != null
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
}
