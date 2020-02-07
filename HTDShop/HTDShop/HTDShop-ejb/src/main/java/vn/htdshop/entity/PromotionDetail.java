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
import javax.persistence.Lob;
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
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Hai
 */
@Entity
@Table(name = "PromotionDetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PromotionDetail.findAll", query = "SELECT p FROM PromotionDetail p")
    , @NamedQuery(name = "PromotionDetail.findById", query = "SELECT p FROM PromotionDetail p WHERE p.id = :id")
    , @NamedQuery(name = "PromotionDetail.findByName", query = "SELECT p FROM PromotionDetail p WHERE p.name = :name")
    , @NamedQuery(name = "PromotionDetail.findByImage", query = "SELECT p FROM PromotionDetail p WHERE p.image = :image")
    , @NamedQuery(name = "PromotionDetail.findByTarget", query = "SELECT p FROM PromotionDetail p WHERE p.target = :target")
    , @NamedQuery(name = "PromotionDetail.findByStartDate", query = "SELECT p FROM PromotionDetail p WHERE p.startDate = :startDate")
    , @NamedQuery(name = "PromotionDetail.findByEndDate", query = "SELECT p FROM PromotionDetail p WHERE p.endDate = :endDate")
    , @NamedQuery(name = "PromotionDetail.findByIsAlways", query = "SELECT p FROM PromotionDetail p WHERE p.isAlways = :isAlways")
    , @NamedQuery(name = "PromotionDetail.findByIsDisabled", query = "SELECT p FROM PromotionDetail p WHERE p.isDisabled = :isDisabled")})
public class PromotionDetail implements Serializable {

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
    @Size(min = 1, max = 255)
    @Column(name = "Image")
    private String image;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Target")
    private int target;
    @Column(name = "StartDate")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat (pattern="dd/MM/yyyy")
    private Date startDate;
    @Column(name = "EndDate")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat (pattern="dd/MM/yyyy")
    private Date endDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IsAlways")
    private boolean isAlways;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IsDisabled")
    private boolean isDisabled;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "promotionDetail")
    private Collection<Promotion> promotionCollection;

    public PromotionDetail() {
    }

    public PromotionDetail(Integer id) {
        this.id = id;
    }

    public PromotionDetail(Integer id, String name, String detail, String image, int target, boolean isAlways, boolean isDisabled) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.image = image;
        this.target = target;
        this.isAlways = isAlways;
        this.isDisabled = isDisabled;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date starDate) {
        this.startDate = starDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean getIsAlways() {
        return isAlways;
    }

    public void setIsAlways(boolean isAlways) {
        this.isAlways = isAlways;
    }

    public boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    @XmlTransient
    public Collection<Promotion> getPromotionCollection() {
        return promotionCollection;
    }

    public void setPromotionCollection(Collection<Promotion> promotionCollection) {
        this.promotionCollection = promotionCollection;
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
        if (!(object instanceof PromotionDetail)) {
            return false;
        }
        PromotionDetail other = (PromotionDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.PromotionDetail[ id=" + id + " ]";
    }
    
}
