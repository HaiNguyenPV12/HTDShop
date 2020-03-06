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
    private int priceMin;
    private int priceMax;
    private String socket;
    private String chipset;
    private String memoryType;
    private String formFactor;
    private int tdp;
    private String partInterface;
    private int memory;
    private int core;
    private int thread;
    private String series;
    private int memorySlot;
    private String storageType;
    private int psuWattage;
    private int memoryModules;
    private String psuFormFactor;
    private int screenSize;
    private String resolution;

    public BuildValues(int id, String manufacturer) {
        this.id = id;
        this.manufacturer = manufacturer;
    }

    public int getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(int priceMax) {
        this.priceMax = priceMax;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public int getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(int screenSize) {
        this.screenSize = screenSize;
    }

    public String getPsuFormFactor() {
        return psuFormFactor;
    }

    public void setPsuFormFactor(String psuFormFactor) {
        this.psuFormFactor = psuFormFactor;
    }

    public int getMemoryModules() {
        return memoryModules;
    }

    public void setMemoryModules(int memoryModules) {
        this.memoryModules = memoryModules;
    }

    public int getPsuWattage() {
        return psuWattage;
    }

    public void setPsuWattage(int psuWattage) {
        this.psuWattage = psuWattage;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public int getMemorySlot() {
        return memorySlot;
    }

    public void setMemorySlot(int memorySlot) {
        this.memorySlot = memorySlot;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getCore() {
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public String getPartInterface() {
        return partInterface;
    }

    public void setPartInterface(String partInterface) {
        this.partInterface = partInterface;
    }

    public int getTdp() {
        return tdp;
    }

    public void setTdp(int tdp) {
        this.tdp = tdp;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public int getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(int price) {
        this.priceMin = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BuildValues() {
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartCategory() {
        return partCategory;
    }

    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }
}
