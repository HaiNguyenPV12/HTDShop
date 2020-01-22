/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Hai
 */
@Entity
@Table(name = "Product")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p")
    , @NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id")
    , @NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name")
    , @NamedQuery(name = "Product.findByManufacturer", query = "SELECT p FROM Product p WHERE p.manufacturer = :manufacturer")
    , @NamedQuery(name = "Product.findByColor", query = "SELECT p FROM Product p WHERE p.color = :color")
    , @NamedQuery(name = "Product.findByPrice", query = "SELECT p FROM Product p WHERE p.price = :price")
    , @NamedQuery(name = "Product.findByUnit", query = "SELECT p FROM Product p WHERE p.unit = :unit")
    , @NamedQuery(name = "Product.findByStock", query = "SELECT p FROM Product p WHERE p.stock = :stock")
    , @NamedQuery(name = "Product.findByWarrantyPeriod", query = "SELECT p FROM Product p WHERE p.warrantyPeriod = :warrantyPeriod")
    , @NamedQuery(name = "Product.findByStatus", query = "SELECT p FROM Product p WHERE p.status = :status")
    , @NamedQuery(name = "Product.findByChipset", query = "SELECT p FROM Product p WHERE p.chipset = :chipset")
    , @NamedQuery(name = "Product.findByMemoryType", query = "SELECT p FROM Product p WHERE p.memoryType = :memoryType")
    , @NamedQuery(name = "Product.findByFormFactor", query = "SELECT p FROM Product p WHERE p.formFactor = :formFactor")
    , @NamedQuery(name = "Product.findByTdp", query = "SELECT p FROM Product p WHERE p.tdp = :tdp")
    , @NamedQuery(name = "Product.findByInterface1", query = "SELECT p FROM Product p WHERE p.interface1 = :interface1")
    , @NamedQuery(name = "Product.findByMemory", query = "SELECT p FROM Product p WHERE p.memory = :memory")
    , @NamedQuery(name = "Product.findBySeries", query = "SELECT p FROM Product p WHERE p.series = :series")
    , @NamedQuery(name = "Product.findByThread", query = "SELECT p FROM Product p WHERE p.thread = :thread")
    , @NamedQuery(name = "Product.findByCore", query = "SELECT p FROM Product p WHERE p.core = :core")
    , @NamedQuery(name = "Product.findByMemorySlot", query = "SELECT p FROM Product p WHERE p.memorySlot = :memorySlot")
    , @NamedQuery(name = "Product.findByStorageType", query = "SELECT p FROM Product p WHERE p.storageType = :storageType")
    , @NamedQuery(name = "Product.findByPSUWattage", query = "SELECT p FROM Product p WHERE p.pSUWattage = :pSUWattage")
    , @NamedQuery(name = "Product.findByMemoryModules", query = "SELECT p FROM Product p WHERE p.memoryModules = :memoryModules")
    , @NamedQuery(name = "Product.findByPSUFormFactor", query = "SELECT p FROM Product p WHERE p.pSUFormFactor = :pSUFormFactor")
    , @NamedQuery(name = "Product.findByScreenSize", query = "SELECT p FROM Product p WHERE p.screenSize = :screenSize")
    , @NamedQuery(name = "Product.findByResolution", query = "SELECT p FROM Product p WHERE p.resolution = :resolution")})
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Manufacturer")
    private String manufacturer;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "Color")
    private String color;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Price")
    private double price;
    @Basic(optional = false)
    @Size(min = 1, max = 30)
    @Column(name = "Unit")
    private String unit;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Stock")
    private int stock;
    @Basic(optional = false)
    @NotNull
    @Column(name = "WarrantyPeriod")
    private int warrantyPeriod;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private int status;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Socket")
    private String socket;
    @Size(max = 100)
    @Column(name = "Chipset")
    private String chipset;
    @Size(max = 100)
    @Column(name = "MemoryType")
    private String memoryType;
    @Size(max = 100)
    @Column(name = "FormFactor")
    private String formFactor;
    @Column(name = "TDP")
    private Integer tdp;
    @Size(max = 100)
    @Column(name = "Interface")
    private String interface1;
    @Column(name = "Memory")
    private Integer memory;
    @Size(max = 100)
    @Column(name = "Series")
    private String series;
    @Column(name = "Thread")
    private Integer thread;
    @Column(name = "Core")
    private Integer core;
    @Column(name = "MemorySlot")
    private Integer memorySlot;
    @Size(max = 100)
    @Column(name = "StorageType")
    private String storageType;
    @Column(name = "PSUWattage")
    private Integer pSUWattage;
    @Column(name = "MemoryModules")
    private Integer memoryModules;
    @Size(max = 100)
    @Column(name = "PSUFormFactor")
    private String pSUFormFactor;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "ScreenSize")
    private Double screenSize;
    @Size(max = 50)
    @Column(name = "Resolution")
    private String resolution;
    @Basic(optional = false)
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private Collection<ProductComment> productCommentCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private Collection<ProductCommentReply> productCommentReplyCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private Collection<ProductImage> productImageCollection;
    @JoinColumn(name = "CateId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Category category;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private Collection<Promotion> promotionCollection;
    @OneToMany(mappedBy = "product")
    private Collection<PreBuilt> preBuiltCollection;
    @OneToMany(mappedBy = "product1")
    private Collection<PreBuilt> preBuiltCollection1;
    @OneToMany(mappedBy = "product2")
    private Collection<PreBuilt> preBuiltCollection2;
    @OneToMany(mappedBy = "product3")
    private Collection<PreBuilt> preBuiltCollection3;
    @OneToMany(mappedBy = "product4")
    private Collection<PreBuilt> preBuiltCollection4;
    @OneToMany(mappedBy = "product5")
    private Collection<PreBuilt> preBuiltCollection5;
    @OneToMany(mappedBy = "product6")
    private Collection<PreBuilt> preBuiltCollection6;
    @OneToMany(mappedBy = "product7")
    private Collection<PreBuilt> preBuiltCollection7;
    @OneToMany(mappedBy = "product8")
    private Collection<PreBuilt> preBuiltCollection8;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    private Collection<OrderDetail> orderDetailCollection;

    public Product() {
    }

    public Product(Integer id) {
        this.id = id;
    }

    public Product(Integer id, String name, String manufacturer, String color, double price, String unit, int stock, int warrantyPeriod, int status, String description) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.color = color;
        this.price = price;
        this.unit = unit;
        this.stock = stock;
        this.warrantyPeriod = warrantyPeriod;
        this.status = status;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public Integer getTdp() {
        return tdp;
    }

    public void setTdp(Integer tdp) {
        this.tdp = tdp;
    }

    public String getInterface1() {
        return interface1;
    }

    public void setInterface1(String interface1) {
        this.interface1 = interface1;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
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

    public Integer getMemorySlot() {
        return memorySlot;
    }

    public void setMemorySlot(Integer memorySlot) {
        this.memorySlot = memorySlot;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Integer getPSUWattage() {
        return pSUWattage;
    }

    public void setPSUWattage(Integer pSUWattage) {
        this.pSUWattage = pSUWattage;
    }

    public Integer getMemoryModules() {
        return memoryModules;
    }

    public void setMemoryModules(Integer memoryModules) {
        this.memoryModules = memoryModules;
    }

    public String getPSUFormFactor() {
        return pSUFormFactor;
    }

    public void setPSUFormFactor(String pSUFormFactor) {
        this.pSUFormFactor = pSUFormFactor;
    }

    public Double getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Double screenSize) {
        this.screenSize = screenSize;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Collection<ProductComment> getProductCommentCollection() {
        return productCommentCollection;
    }

    public void setProductCommentCollection(Collection<ProductComment> productCommentCollection) {
        this.productCommentCollection = productCommentCollection;
    }

    @XmlTransient
    public Collection<ProductCommentReply> getProductCommentReplyCollection() {
        return productCommentReplyCollection;
    }

    public void setProductCommentReplyCollection(Collection<ProductCommentReply> productCommentReplyCollection) {
        this.productCommentReplyCollection = productCommentReplyCollection;
    }

    @XmlTransient
    public Collection<ProductImage> getProductImageCollection() {
        return productImageCollection;
    }

    public void setProductImageCollection(Collection<ProductImage> productImageCollection) {
        this.productImageCollection = productImageCollection;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @XmlTransient
    public Collection<Promotion> getPromotionCollection() {
        return promotionCollection;
    }

    public void setPromotionCollection(Collection<Promotion> promotionCollection) {
        this.promotionCollection = promotionCollection;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection() {
        return preBuiltCollection;
    }

    public void setPreBuiltCollection(Collection<PreBuilt> preBuiltCollection) {
        this.preBuiltCollection = preBuiltCollection;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection1() {
        return preBuiltCollection1;
    }

    public void setPreBuiltCollection1(Collection<PreBuilt> preBuiltCollection1) {
        this.preBuiltCollection1 = preBuiltCollection1;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection2() {
        return preBuiltCollection2;
    }

    public void setPreBuiltCollection2(Collection<PreBuilt> preBuiltCollection2) {
        this.preBuiltCollection2 = preBuiltCollection2;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection3() {
        return preBuiltCollection3;
    }

    public void setPreBuiltCollection3(Collection<PreBuilt> preBuiltCollection3) {
        this.preBuiltCollection3 = preBuiltCollection3;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection4() {
        return preBuiltCollection4;
    }

    public void setPreBuiltCollection4(Collection<PreBuilt> preBuiltCollection4) {
        this.preBuiltCollection4 = preBuiltCollection4;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection5() {
        return preBuiltCollection5;
    }

    public void setPreBuiltCollection5(Collection<PreBuilt> preBuiltCollection5) {
        this.preBuiltCollection5 = preBuiltCollection5;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection6() {
        return preBuiltCollection6;
    }

    public void setPreBuiltCollection6(Collection<PreBuilt> preBuiltCollection6) {
        this.preBuiltCollection6 = preBuiltCollection6;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection7() {
        return preBuiltCollection7;
    }

    public void setPreBuiltCollection7(Collection<PreBuilt> preBuiltCollection7) {
        this.preBuiltCollection7 = preBuiltCollection7;
    }

    @XmlTransient
    public Collection<PreBuilt> getPreBuiltCollection8() {
        return preBuiltCollection8;
    }

    public void setPreBuiltCollection8(Collection<PreBuilt> preBuiltCollection8) {
        this.preBuiltCollection8 = preBuiltCollection8;
    }

    @XmlTransient
    public Collection<OrderDetail> getOrderDetailCollection() {
        return orderDetailCollection;
    }

    public void setOrderDetailCollection(Collection<OrderDetail> orderDetailCollection) {
        this.orderDetailCollection = orderDetailCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.Product[ id=" + id + " ]";
    }
    
}
