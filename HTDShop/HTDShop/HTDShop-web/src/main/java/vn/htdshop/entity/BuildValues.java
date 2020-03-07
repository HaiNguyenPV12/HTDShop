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
    private Integer memoryModules;
    private String psuFormFactor;
    private Integer screenSize;
    private String resolution;

    public BuildValues(Integer id, String manufacturer) {
        this.id = id;
        this.manufacturer = manufacturer;
    }

    public Integer getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public String getResolution() {
        return resolution;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Integer screenSize) {
        this.screenSize = screenSize;
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

    public Integer getMemoryModules() {
        return memoryModules;
    }

    public void setMemoryModules(Integer memoryModules) {
        this.memoryModules = memoryModules;
    }

    public Integer getPsuWattage() {
        return psuWattage;
    }

    public void setPsuWattage(Integer psuWattage) {
        this.psuWattage = psuWattage;
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

    public Integer getMemorySlot() {
        return memorySlot;
    }

    public void setMemorySlot(Integer memorySlot) {
        this.memorySlot = memorySlot;
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

    public Integer getThread() {
        return thread;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public Integer getCore() {
        return core;
    }

    public void setCore(Integer core) {
        this.core = core;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
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

    public Integer getTdp() {
        return tdp;
    }

    public void setTdp(Integer tdp) {
        this.tdp = tdp;
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

    public Integer getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(Integer price) {
        this.priceMin = price;
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
}
