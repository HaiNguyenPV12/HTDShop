/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

import org.springframework.format.annotation.NumberFormat;

/**
 *
 * @author Thien
 */
@Entity
public class CPU {
    @Id
    private Integer id;
    
    @NotEmpty(message = "Name cannot be empty.")
    @Size(min = 3, max = 255, message = "Name must have from 3 - 255 characters.")
    private String name;
    
    @NotEmpty(message = "Manufacturer cannot be empty.")
    @Size(min = 1, max = 100, message = "Manufacturer must have from 1 - 100 characters.")
    private String manufacturer;
    
    @NotEmpty(message = "Color cannot be empty.")
    private String color;

    @NotNull(message = "Price cannot be empty.")
    @Digits(fraction = 2, integer = 10, message = "Must be number.")
    @DecimalMin(value = "0.0", message = "Price's minimum is 0.")
    @DecimalMax(value = "100000000.0", message = "Price's maximum is 100,000,000.")
    private Double price;
    
    @Size(max = 30, message = "Unit must have from 0 - 30 characters.")
    private String unit;
    
    @NotNull(message = "Stock cannot be empty.")
    @Min(value = 0, message = "Stock's minimum is 0.")
    @Max(value = 10000, message = "Stock's maximum is 10,000.")
    private Integer stock;

    @NotNull(message = "Warranty period cannot be empty.")
    @Min(value = 0, message = "Warranty preriod's minimum is 0.")
    @Max(value = 120, message = "Warranty preriod's maximum is 120.")
    private Integer warrantyPeriod;

    @NotEmpty(message = "Socket cannot be empty.")
    private String socket;

    @NotEmpty(message = "Chipset cannot be empty.")
    private String chipset;

    @NotNull(message = "TDP cannot be empty.")
    @Min(value = 0, message = "TDP's minimum is 0.")
    @Max(value = 10000, message = "TDP's maximum is 10000.")
    private Integer tdp;

    @NotNull(message = "Core cannot be empty.")
    @Min(value = 0, message = "Core's minimum is 1.")
    @Max(value = 1000, message = "Core's maximum is 1000.")
    private Integer core;

    @NotNull(message = "Thread cannot be empty.")
    @Min(value = 0, message = "Thread's minimum is 1.")
    @Max(value = 1000, message = "Thread's maximum is 1000.")
    private Integer thread;

    public Product toNewProduct(){
        Product p = new Product();
        Category c = new Category();
        c.setId(2);
        
        p.setId(null);
        p.setName(name);
        p.setCategory(c);
        p.setManufacturer(manufacturer);
        p.setColor(color);
        p.setPrice(price);
        p.setStock(stock);
        p.setWarrantyPeriod(warrantyPeriod);
        p.setSocket(socket);
        p.setChipset(chipset);
        p.setTdp(tdp);
        p.setCore(core);
        p.setThread(thread);
        p.setStatus(1);
        return p;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the stock
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * @return the warrantyPeriod
     */
    public Integer getWarrantyPeriod() {
        return warrantyPeriod;
    }

    /**
     * @param warrantyPeriod the warrantyPeriod to set
     */
    public void setWarrantyPeriod(Integer warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
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

    
    
}
