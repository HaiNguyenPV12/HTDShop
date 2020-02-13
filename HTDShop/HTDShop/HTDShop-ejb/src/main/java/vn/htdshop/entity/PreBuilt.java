/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Hai
 */
@Entity
@Table(name = "PreBuilt")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PreBuilt.findAll", query = "SELECT p FROM PreBuilt p")
    , @NamedQuery(name = "PreBuilt.findById", query = "SELECT p FROM PreBuilt p WHERE p.id = :id")
    , @NamedQuery(name = "PreBuilt.findByName", query = "SELECT p FROM PreBuilt p WHERE p.name = :name")
    , @NamedQuery(name = "PreBuilt.findByCreatedAt", query = "SELECT p FROM PreBuilt p WHERE p.createdAt = :createdAt")})
public class PreBuilt implements Serializable {

    @JoinColumn(name = "CustomerId", referencedColumnName = "Id")
    @ManyToOne
    private Customer customer;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Detail")
    private String detail;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "preBuilt")
    private Collection<PreBuiltImage> preBuiltImageCollection;
    @JoinColumn(name = "VGAId", referencedColumnName = "Id")
    @ManyToOne
    private Product vga;
    @JoinColumn(name = "CaseId", referencedColumnName = "Id")
    @ManyToOne
    private Product cases;
    @JoinColumn(name = "MonitorId", referencedColumnName = "Id")
    @ManyToOne
    private Product monitor;
    @JoinColumn(name = "PSUId", referencedColumnName = "Id")
    @ManyToOne
    private Product psu;
    @JoinColumn(name = "StorageId", referencedColumnName = "Id")
    @ManyToOne
    private Product storage;
    @JoinColumn(name = "MemoryId", referencedColumnName = "Id")
    @ManyToOne
    private Product memory;
    @JoinColumn(name = "CPUCoolerId", referencedColumnName = "Id")
    @ManyToOne
    private Product cpucooler;
    @JoinColumn(name = "MotherBoardId", referencedColumnName = "Id")
    @ManyToOne
    private Product motherboard;
    @JoinColumn(name = "CPUId", referencedColumnName = "Id")
    @ManyToOne
    private Product cpu;
    @JoinColumn(name = "StaffUserName", referencedColumnName = "UserName")
    @ManyToOne(optional = false)
    private Staff staff;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "preBuilt")
    private Collection<PreBuiltRating> preBuiltRatingCollection;

    public PreBuilt() {
    }

    public PreBuilt(Integer id) {
        this.id = id;
    }

    public PreBuilt(Integer id, String name, String detail, Date createdAt) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.createdAt = createdAt;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @XmlTransient
    public Collection<PreBuiltImage> getPreBuiltImageCollection() {
        return preBuiltImageCollection;
    }

    public void setPreBuiltImageCollection(Collection<PreBuiltImage> preBuiltImageCollection) {
        this.preBuiltImageCollection = preBuiltImageCollection;
    }

    

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @XmlTransient
    public Collection<PreBuiltRating> getPreBuiltRatingCollection() {
        return preBuiltRatingCollection;
    }

    public void setPreBuiltRatingCollection(Collection<PreBuiltRating> preBuiltRatingCollection) {
        this.preBuiltRatingCollection = preBuiltRatingCollection;
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
        if (!(object instanceof PreBuilt)) {
            return false;
        }
        PreBuilt other = (PreBuilt) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.PreBuilt[ id=" + id + " ]";
    }

    /**
     * @return the vga
     */
    public Product getVga() {
        return vga;
    }

    /**
     * @param vga the vga to set
     */
    public void setVga(Product vga) {
        this.vga = vga;
    }

    /**
     * @return the cases
     */
    public Product getCases() {
        return cases;
    }

    /**
     * @param cases the cases to set
     */
    public void setCases(Product cases) {
        this.cases = cases;
    }

    /**
     * @return the monitor
     */
    public Product getMonitor() {
        return monitor;
    }

    /**
     * @param monitor the monitor to set
     */
    public void setMonitor(Product monitor) {
        this.monitor = monitor;
    }

    /**
     * @return the psu
     */
    public Product getPsu() {
        return psu;
    }

    /**
     * @param psu the psu to set
     */
    public void setPsu(Product psu) {
        this.psu = psu;
    }

    /**
     * @return the storage
     */
    public Product getStorage() {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(Product storage) {
        this.storage = storage;
    }

    /**
     * @return the memory
     */
    public Product getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(Product memory) {
        this.memory = memory;
    }

    /**
     * @return the cpucooler
     */
    public Product getCpucooler() {
        return cpucooler;
    }

    /**
     * @param cpucooler the cpucooler to set
     */
    public void setCpucooler(Product cpucooler) {
        this.cpucooler = cpucooler;
    }

    /**
     * @return the motherboard
     */
    public Product getMotherboard() {
        return motherboard;
    }

    /**
     * @param motherboard the motherboard to set
     */
    public void setMotherboard(Product motherboard) {
        this.motherboard = motherboard;
    }

    /**
     * @return the cpu
     */
    public Product getCpu() {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(Product cpu) {
        this.cpu = cpu;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
}
