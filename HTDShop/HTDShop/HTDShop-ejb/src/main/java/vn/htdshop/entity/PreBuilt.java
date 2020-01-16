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
    private Product product;
    @JoinColumn(name = "CaseId", referencedColumnName = "Id")
    @ManyToOne
    private Product product1;
    @JoinColumn(name = "MonitorId", referencedColumnName = "Id")
    @ManyToOne
    private Product product2;
    @JoinColumn(name = "PSUId", referencedColumnName = "Id")
    @ManyToOne
    private Product product3;
    @JoinColumn(name = "StorageId", referencedColumnName = "Id")
    @ManyToOne
    private Product product4;
    @JoinColumn(name = "MemoryId", referencedColumnName = "Id")
    @ManyToOne
    private Product product5;
    @JoinColumn(name = "CPUCoolerId", referencedColumnName = "Id")
    @ManyToOne
    private Product product6;
    @JoinColumn(name = "MotherBoardId", referencedColumnName = "Id")
    @ManyToOne
    private Product product7;
    @JoinColumn(name = "CPUId", referencedColumnName = "Id")
    @ManyToOne
    private Product product8;
    @JoinColumn(name = "StaffUserName", referencedColumnName = "UserName")
    @ManyToOne(optional = false)
    private Staff staff;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private User user;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct1() {
        return product1;
    }

    public void setProduct1(Product product1) {
        this.product1 = product1;
    }

    public Product getProduct2() {
        return product2;
    }

    public void setProduct2(Product product2) {
        this.product2 = product2;
    }

    public Product getProduct3() {
        return product3;
    }

    public void setProduct3(Product product3) {
        this.product3 = product3;
    }

    public Product getProduct4() {
        return product4;
    }

    public void setProduct4(Product product4) {
        this.product4 = product4;
    }

    public Product getProduct5() {
        return product5;
    }

    public void setProduct5(Product product5) {
        this.product5 = product5;
    }

    public Product getProduct6() {
        return product6;
    }

    public void setProduct6(Product product6) {
        this.product6 = product6;
    }

    public Product getProduct7() {
        return product7;
    }

    public void setProduct7(Product product7) {
        this.product7 = product7;
    }

    public Product getProduct8() {
        return product8;
    }

    public void setProduct8(Product product8) {
        this.product8 = product8;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
    
}
