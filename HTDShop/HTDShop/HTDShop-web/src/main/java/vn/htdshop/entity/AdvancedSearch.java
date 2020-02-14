package vn.htdshop.entity;

import java.util.Map;

/**
 * AdvancedSearch
 */
public class AdvancedSearch {

    private Integer category;
    private String socket;
    private String memoryType;
    private String formFactor;
    private Integer tdpmin;
    private Integer tdpmax;
    private String interface1;
    private Integer memory;
    private String series;
    private Integer thread;
    private Integer core;
    private Integer memorySlot;
    private String storageType;
    private Integer pSUWattage;
    private Integer memoryModules;
    private String pSUFormFactor;
    private Double screenSize;
    private String resolution;

    public AdvancedSearch() {

    }

    public AdvancedSearch(Map<String, String> params) {
        // Category
        if (params.get("cateid") == null) {
            this.category = 0;
        } else {
            try {
                this.category = Integer.parseInt(params.get("cateid"));
            } catch (Exception e) {
                this.category = 0;
            }
        }

        // Socket
        if (params.get("sk") == null) {
            this.socket = "";
        } else {
            this.socket = params.get("sk");
        }

        // Memory Type
        if (params.get("mt") == null) {
            this.memoryType = "";
        } else {
            this.memoryType = params.get("mt");
        }

        // Form Factor
        if (params.get("ff") == null) {
            this.formFactor = "";
        } else {
            this.formFactor = params.get("ff");
        }

        // TDP
        if (params.get("tdpmax") == null) {
            this.tdpmax = 10000;
        } else {
            try {
                this.tdpmax = Integer.parseInt(params.get("tdpmax"));
            } catch (Exception e) {
                this.tdpmax = 10000;
            }
        }

        if (params.get("tdpmin") == null) {
            this.tdpmin = 0;
        } else {
            try {
                this.tdpmin = Integer.parseInt(params.get("tdpmin"));
            } catch (Exception e) {
                this.tdpmin = 0;
            }
        }

        // Interface
        if (params.get("itf") == null) {
            this.interface1 = "";
        } else {
            this.interface1 = params.get("itf");
        }

        // Memory
        if (params.get("mm") == null) {
            this.memory = 0;
        } else {
            try {
                this.memory = Integer.parseInt(params.get("mm"));
            } catch (Exception e) {
                this.memory = 0;
            }
        }

        // Series
        if (params.get("sr") == null) {
            this.series = "";
        } else {
            this.series = params.get("sr");
        }

        // Thread
        if (params.get("thr") == null) {
            this.thread = 0;
        } else {
            try {
                this.thread = Integer.parseInt(params.get("thr"));
            } catch (Exception e) {
                this.thread = 0;
            }
        }

        // Core
        if (params.get("co") == null) {
            this.core = 0;
        } else {
            try {
                this.core = Integer.parseInt(params.get("co"));
            } catch (Exception e) {
                //e.printStackTrace();
                this.core = 0;
            }
        }

        // Memory Slot
        if (params.get("mms") == null) {
            this.memorySlot = 0;
        } else {
            try {
                this.memorySlot = Integer.parseInt(params.get("mms"));
            } catch (Exception e) {
                this.memorySlot = 0;
            }
        }

        // Storage Type
        if (params.get("st") == null) {
            this.storageType = "";
        } else {
            this.storageType = params.get("st");
        }

        // PSU Wattage
        if (params.get("psuw") == null) {
            this.pSUWattage = 0;
        } else {
            try {
                this.pSUWattage = Integer.parseInt(params.get("psuw"));
            } catch (Exception e) {
                this.pSUWattage = 0;
            }
        }

        // Memory Modules
        if (params.get("mmm") == null) {
            this.memoryModules = 0;
        } else {
            try {
                this.memoryModules = Integer.parseInt(params.get("mmm"));
            } catch (Exception e) {
                this.memoryModules = 0;
            }
        }

        // PSU Form Factor
        if (params.get("psuf") == null) {
            this.pSUFormFactor = "";
        } else {
            this.pSUFormFactor = params.get("psuf");
        }

        // Screen Size
        if (params.get("ss") == null) {
            this.screenSize = 0d;
        } else {
            try {
                this.screenSize = Double.parseDouble(params.get("ss"));
            } catch (Exception e) {
                this.screenSize = 0d;
            }
        }

        // Resolution
        if (params.get("rs") == null) {
            this.resolution = "";
        } else {
            this.resolution = params.get("rs");
        }
    }

    /**
     * @return Integer return the category
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Integer category) {
        this.category = category;
    }

    /**
     * @return String return the socket
     */
    public String getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(String socket) {
        this.socket = socket;
    }

    /**
     * @return String return the memoryType
     */
    public String getMemoryType() {
        return memoryType;
    }

    /**
     * @param memoryType the memoryType to set
     */
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    /**
     * @return String return the formFactor
     */
    public String getFormFactor() {
        return formFactor;
    }

    /**
     * @param formFactor the formFactor to set
     */
    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    /**
     * @return String return the interface1
     */
    public String getInterface1() {
        return interface1;
    }

    /**
     * @param interface1 the interface1 to set
     */
    public void setInterface1(String interface1) {
        this.interface1 = interface1;
    }

    /**
     * @return Integer return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    /**
     * @return String return the series
     */
    public String getSeries() {
        return series;
    }

    /**
     * @param series the series to set
     */
    public void setSeries(String series) {
        this.series = series;
    }

    /**
     * @return Integer return the thread
     */
    public Integer getThread() {
        return thread;
    }

    /**
     * @param thread the thread to set
     */
    public void setThread(Integer thread) {
        this.thread = thread;
    }

    /**
     * @return Integer return the core
     */
    public Integer getCore() {
        return core;
    }

    /**
     * @param core the core to set
     */
    public void setCore(Integer core) {
        this.core = core;
    }

    /**
     * @return Integer return the memorySlot
     */
    public Integer getMemorySlot() {
        return memorySlot;
    }

    /**
     * @param memorySlot the memorySlot to set
     */
    public void setMemorySlot(Integer memorySlot) {
        this.memorySlot = memorySlot;
    }

    /**
     * @return String return the storageType
     */
    public String getStorageType() {
        return storageType;
    }

    /**
     * @param storageType the storageType to set
     */
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    /**
     * @return Integer return the pSUWattage
     */
    public Integer getPSUWattage() {
        return pSUWattage;
    }

    /**
     * @param pSUWattage the pSUWattage to set
     */
    public void setPSUWattage(Integer pSUWattage) {
        this.pSUWattage = pSUWattage;
    }

    /**
     * @return Integer return the memoryModules
     */
    public Integer getMemoryModules() {
        return memoryModules;
    }

    /**
     * @param memoryModules the memoryModules to set
     */
    public void setMemoryModules(Integer memoryModules) {
        this.memoryModules = memoryModules;
    }

    /**
     * @return String return the pSUFormFactor
     */
    public String getPSUFormFactor() {
        return pSUFormFactor;
    }

    /**
     * @param pSUFormFactor the pSUFormFactor to set
     */
    public void setPSUFormFactor(String pSUFormFactor) {
        this.pSUFormFactor = pSUFormFactor;
    }

    /**
     * @return Double return the screenSize
     */
    public Double getScreenSize() {
        return screenSize;
    }

    /**
     * @param screenSize the screenSize to set
     */
    public void setScreenSize(Double screenSize) {
        this.screenSize = screenSize;
    }

    /**
     * @return String return the resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }


    /**
     * @return Integer return the tdpmin
     */
    public Integer getTdpmin() {
        return tdpmin;
    }

    /**
     * @param tdpmin the tdpmin to set
     */
    public void setTdpmin(Integer tdpmin) {
        this.tdpmin = tdpmin;
    }

    /**
     * @return Integer return the tdpmax
     */
    public Integer getTdpmax() {
        return tdpmax;
    }

    /**
     * @param tdpmax the tdpmax to set
     */
    public void setTdpmax(Integer tdpmax) {
        this.tdpmax = tdpmax;
    }

}