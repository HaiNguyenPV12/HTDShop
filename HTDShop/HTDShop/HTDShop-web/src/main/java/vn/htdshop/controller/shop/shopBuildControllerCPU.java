package vn.htdshop.controller.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.htdshop.entity.BuildValues;
import vn.htdshop.entity.PreBuilt;
import vn.htdshop.entity.Product;
import vn.htdshop.sb.ProductFacadeLocal;
import vn.htdshop.utility.BuildService;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * shopBuildController
 */
@Controller
@RequestMapping("build/cpu")
public class shopBuildControllerCPU {

    @EJB(mappedName = "ProductFacade")
    ProductFacadeLocal productFacade;

    @Autowired
    BuildService buildService;

    @Autowired
    HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCPUList(Model model) {
        buildService.initBuildApp();

        // -----SEARCH VALUES---
        model.addAttribute("cpuValues", getSessionCPUValues());
        // -----FORM VALUES-----
        model.addAttribute("cpuManufacturerList", cpuManufacturers());
        model.addAttribute("cpuSocketList", cpuSockets());
        model.addAttribute("cpuSeriesList", cpuSeries());
        // -----FILTER RESULT---
        model.addAttribute("filteredCPU", filterCPU());
        return "HTDShop/pickCPU";
    }

    @RequestMapping(value = "filterCpu", method = RequestMethod.POST)
    public String filterCPU(@ModelAttribute("cpuValues") BuildValues cpuValues, BindingResult error,
            RedirectAttributes redirect) {
        BuildValues prevSearch = getSessionCPUValues();
        String prevManufacturer = prevSearch.getManufacturer();
        String prevSocket = prevSearch.getSocket();
        String prevSeries = prevSearch.getSeries();

        // if mnufacturer/series is different, revert sockets to all
        if (!cpuValues.getManufacturer().equals(prevManufacturer) || !cpuValues.getSeries().equals(prevSeries)) {
            cpuValues.setSocket("all");
        }

        // if socket is different, revert series to all
        if (!cpuValues.getSocket().equals(prevSocket)) {
            cpuValues.setSeries("all");
        }

        // if manufacturer is different AND series was not from said manufacturer
        // before.
        if (!cpuValues.getManufacturer().equals(prevManufacturer)) {
            String newManufacturer = cpuValues.getManufacturer();
            String newSeries = cpuValues.getSeries();
            boolean seriesAndManuMatch = false;
            List<Product> cpus = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 1 && p.getSeries().equals(newSeries))
                    .collect(Collectors.toList());
            for (Product cpu : cpus) {
                if (cpu.getManufacturer().equals(newManufacturer)) {
                    seriesAndManuMatch = true;
                    break;
                }
            }
            if (!seriesAndManuMatch) {
                cpuValues.setSeries("all");
            }
        }

        // apply values to form
        setSessionCPUValues(cpuValues);
        return "redirect:/build/cpu";
    }

    // pick and set to prebuilt values
    @RequestMapping(value = "pickCpu", method = RequestMethod.POST)
    public String pickCPU(@ModelAttribute("id") Integer id, RedirectAttributes redirect) {
        // get CPU from ID
        Product cpu = productFacade.find(id);
        // TODO go back to cpu page if there were errors/id isn't a cpu
        // Set CPU in session's build
        PreBuilt sessionPreBuilt = buildService.getSessionPrebuilt();
        sessionPreBuilt.setCpu(cpu);
        buildService.setSessionPrebuilt(sessionPreBuilt);

        // reset filter values
        setSessionCPUValues(null);
        // redirect to build's home page
        return "redirect:/build";
    }

    // Reset search form link
    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public String filterReset(RedirectAttributes redirect) {
        // reset search values
        setSessionCPUValues(initFilterValues());
        return "redirect:/build/cpu";
    }

    // Remove from current build
    @RequestMapping(value = "discard", method = RequestMethod.GET)
    public String discardCPU(RedirectAttributes redirect) {
        // remove CPU from session Prebuilt
        buildService.getSessionPrebuilt().setCpu(null);
        return "redirect:/build";
    }

    public BuildValues getSessionCPUValues() {
        BuildValues result = (BuildValues) session.getAttribute("cpuValues");
        if (result == null) {
            BuildValues newCPUValues = initFilterValues();
            setSessionCPUValues(newCPUValues);
        }
        return (BuildValues) session.getAttribute("cpuValues");
    }

    public void setSessionCPUValues(BuildValues cpuValues) {
        session.setAttribute("cpuValues", cpuValues);
    }

    private BuildValues initFilterValues() {
        BuildValues result = new BuildValues();
        result.setPartCategory("cpu");
        result.setManufacturer("all");
        result.setSocket("all");
        result.setSeries("all");
        result.setTdp(0);
        result.setCore(0);
        result.setThread(0);
        result.setPriceMin(0);
        result.setPriceMax(10000);
        return result;
    }

    // Socket list for form
    private List<String> cpuSockets() {
        List<String> sockets = new ArrayList<>();
        sockets = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 1)
                .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        // TODO check if motherboard is picked

        // filter by manufacturer
        String manufacturer = getSessionCPUValues().getManufacturer();
        if (!manufacturer.equals("all")) {
            // get all sockets from manufacturer
            sockets = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 1 && p.getManufacturer().equals(manufacturer))
                    .map(s -> s.getSocket()).distinct().collect(Collectors.toList());
        }
        return sockets;
    }

    // Manufacturer list for form
    private List<String> cpuManufacturers() {
        List<String> manufacturers = new ArrayList<>();
        manufacturers = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 1)
                .map(m -> m.getManufacturer()).distinct().collect(Collectors.toList());
        // TODO check if motherboard is picked and filter accordingly.

        return manufacturers;
    }

    // Series list for form
    private List<String> cpuSeries() {
        List<String> series = new ArrayList<>();
        BuildValues cpuValues = getSessionCPUValues();
        series = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 1)
                .map(s -> s.getSeries()).distinct().collect(Collectors.toList());

        // TODO check if motherboard is picked
        // filter by manufacturer (AMD/Intel)
        String manufacturer = cpuValues.getManufacturer();
        if (!manufacturer.equals("all")) {
            series = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 1 && p.getManufacturer().equals(manufacturer))
                    .map(s -> s.getSeries()).distinct().collect(Collectors.toList());
        }
        // filter by soket
        String socket = cpuValues.getSocket();
        if (!socket.equals("all")) {
            series = buildService.getSessionProductList().stream()
                    .filter(p -> p.getCategory().getId() == 1 && p.getSocket().equals(socket)).map(s -> s.getSeries())
                    .distinct().collect(Collectors.toList());
        }

        return series;
    }

    // Filter from form values
    private List<Product> filterCPU() {
        List<Product> cpus = new ArrayList<>();
        BuildValues cpuValues = getSessionCPUValues();

        try {
            // all CPUs
            cpus = buildService.getSessionProductList().stream().filter(p -> p.getCategory().getId() == 1)
                    .collect(Collectors.toList());

            // filter by manufaturers
            String manufacturer = cpuValues.getManufacturer();
            if (!manufacturer.equals("all")) {
                cpus = cpus.stream().filter(m -> m.getManufacturer().equals(manufacturer)).collect(Collectors.toList());
            }

            // filter by sockets
            String socket = cpuValues.getSocket();
            if (!socket.equals("all")) {
                cpus = cpus.stream().filter(s -> s.getSocket().equals(socket)).collect(Collectors.toList());
            }

            // filter by series
            String series = cpuValues.getSeries();
            if (!series.equals("all")) {
                cpus = cpus.stream().filter(s -> s.getSeries().equals(series)).collect(Collectors.toList());
            }

            // filter by core & threads
            // --cores
            Integer cores = cpuValues.getCore();
            cpus = cpus.stream().filter(c -> c.getCore() >= cores).collect(Collectors.toList());
            // --threads
            Integer threads = cpuValues.getThread();
            cpus = cpus.stream().filter(t -> t.getThread() >= threads).collect(Collectors.toList());

            // filter by TDP
            Integer tdp = cpuValues.getTdp();
            if (tdp <= 0) {
                cpus = cpus.stream().filter(c -> c.getTdp() >= tdp).collect(Collectors.toList());
            } else {
                cpus = cpus.stream().filter(c -> c.getTdp() <= tdp).collect(Collectors.toList());
            }

            // filter by price
            // --clamp prices and filter
            if (cpuValues.getPriceMin() == 0 && cpuValues.getPriceMax() == 0) {
                cpuValues.setPriceMax(10000);
                setSessionCPUValues(cpuValues);
            } else if (cpuValues.getPriceMin() > cpuValues.getPriceMax()) {
                cpuValues.setPriceMax(cpuValues.getPriceMin());
                setSessionCPUValues(cpuValues);
            }
            cpus = cpus.stream()
                    .filter(p -> p.getPrice() >= cpuValues.getPriceMin() && p.getPrice() <= cpuValues.getPriceMax())
                    .collect(Collectors.toList());

            // return only selling items
            for (int i = 0; i < cpus.size(); i++) {
                if (cpus.get(i).getStatus() == 3) {
                    cpus.remove(i);
                    i--;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return cpus;
    }
}
