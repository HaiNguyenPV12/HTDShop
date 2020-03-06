package vn.htdshop.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

/**
 * BuildValues
 */
@Entity
public class BuildValues implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String partCategory;
    private String manufacturer;
    private String color;
    private Integer priceMin;
    private Integer priceMax;
    private String socket;
    private String chipset;
    private String memoryType;
    private String formFactor;
    private Integer tdp;
    private String partInterface;
    private Integer memory;
    private Integer core;
    private Integer thread;
    private String series;
    private Integer memorySlot;
    private String storageType;
    private Integer psuWattage;
    private int memoryModules;
    private String psuFormFactor;
    private Integer screenSize;
    private String resolution;

    public BuildValues() {
    }

    public BuildValues(int id, String manufacturer) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.priceMin = 0;
        this.priceMax = 2000;
        switch (id) {
            case 1:
                this.tdp = 0;
                this.core = 0;
                this.thread = 0;
                break;
            case 2:
            
                break;
            case 3:
                
                break;
            case 4:
                
                break;
            case 5:
               
                break;
            case 6:
                
                break;
            case 7:
                
                break;
            case 8:
                
                break;
            case 9:
                
                break;
            default:
                break;
        }
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the partCategory
     */
    public String getPartCategory() {
        return partCategory;
    }

    /**
     * @param partCategory the partCategory to set
     */
    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the priceMin
     */
    public Integer getPriceMin() {
        return priceMin;
    }

    /**
     * @param priceMin the priceMin to set
     */
    public void setPriceMin(Integer priceMin) {
        this.priceMin = priceMin;
    }

    /**
     * @return the priceMax
     */
    public Integer getPriceMax() {
        return priceMax;
    }

    /**
     * @param priceMax the priceMax to set
     */
    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    /**
     * @return the socket
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
     * @return the chipset
     */
    public String getChipset() {
        return chipset;
    }

    /**
     * @param chipset the chipset to set
     */
    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    /**
     * @return the memoryType
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
     * @return the formFactor
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
     * @return the tdp
     */
    public Integer getTdp() {
        return tdp;
    }

    /**
     * @param tdp the tdp to set
     */
    public void setTdp(Integer tdp) {
        this.tdp = tdp;
    }

    /**
     * @return the partInterface
     */
    public String getPartInterface() {
        return partInterface;
    }

    /**
     * @param partInterface the partInterface to set
     */
    public void setPartInterface(String partInterface) {
        this.partInterface = partInterface;
    }

    /**
     * @return the memory
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
     * @return the core
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
     * @return the thread
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
     * @return the series
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
     * @return the memorySlot
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
     * @return the storageType
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
     * @return the psuWattage
     */
    public Integer getPsuWattage() {
        return psuWattage;
    }

    /**
     * @param psuWattage the psuWattage to set
     */
    public void setPsuWattage(Integer psuWattage) {
        this.psuWattage = psuWattage;
    }

    /**
     * @return the memoryModules
     */
    public int getMemoryModules() {
        return memoryModules;
    }

    /**
     * @param memoryModules the memoryModules to set
     */
    public void setMemoryModules(int memoryModules) {
        this.memoryModules = memoryModules;
    }

    /**
     * @return the psuFormFactor
     */
    public String getPsuFormFactor() {
        return psuFormFactor;
    }

    /**
     * @param psuFormFactor the psuFormFactor to set
     */
    public void setPsuFormFactor(String psuFormFactor) {
        this.psuFormFactor = psuFormFactor;
    }

    /**
     * @return the screenSize
     */
    public Integer getScreenSize() {
        return screenSize;
    }

    /**
     * @param screenSize the screenSize to set
     */
    public void setScreenSize(Integer screenSize) {
        this.screenSize = screenSize;
    }

    /**
     * @return the resolution
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

}