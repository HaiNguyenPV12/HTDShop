/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hai
 */
@Entity
@Table(name = "Promotion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Promotion.findAll", query = "SELECT p FROM Promotion p")
    , @NamedQuery(name = "Promotion.findById", query = "SELECT p FROM Promotion p WHERE p.id = :id")
    , @NamedQuery(name = "Promotion.findByLimitedQuantity", query = "SELECT p FROM Promotion p WHERE p.limitedQuantity = :limitedQuantity")
    , @NamedQuery(name = "Promotion.findByMinQuantity", query = "SELECT p FROM Promotion p WHERE p.minQuantity = :minQuantity")
    , @NamedQuery(name = "Promotion.findByMaxQuantity", query = "SELECT p FROM Promotion p WHERE p.maxQuantity = :maxQuantity")
    , @NamedQuery(name = "Promotion.findByQuantityLeft", query = "SELECT p FROM Promotion p WHERE p.quantityLeft = :quantityLeft")
    , @NamedQuery(name = "Promotion.findByPercentage", query = "SELECT p FROM Promotion p WHERE p.percentage = :percentage")
    , @NamedQuery(name = "Promotion.findByExactSaleOff", query = "SELECT p FROM Promotion p WHERE p.exactSaleOff = :exactSaleOff")
    , @NamedQuery(name = "Promotion.findByMaxSaleOff", query = "SELECT p FROM Promotion p WHERE p.maxSaleOff = :maxSaleOff")})
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Column(name = "LimitedQuantity")
    private Integer limitedQuantity;
    @Column(name = "MinQuantity")
    private Integer minQuantity;
    @Column(name = "MaxQuantity")
    private Integer maxQuantity;
    @Column(name = "QuantityLeft")
    private Integer quantityLeft;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "Percentage")
    private Double percentage;
    @Column(name = "ExactSaleOff")
    private Double exactSaleOff;
    @Column(name = "MaxSaleOff")
    private Double maxSaleOff;
    @JoinColumn(name = "CategoryId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Category category;
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Product product;
    @JoinColumn(name = "PromotionId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private PromotionDetail promotionDetail;

    public Promotion() {
    }

    public Promotion(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLimitedQuantity() {
        return limitedQuantity;
    }

    public void setLimitedQuantity(Integer limitedQuantity) {
        this.limitedQuantity = limitedQuantity;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Integer getQuantityLeft() {
        return quantityLeft;
    }

    public void setQuantityLeft(Integer quantityLeft) {
        this.quantityLeft = quantityLeft;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getExactSaleOff() {
        return exactSaleOff;
    }

    public void setExactSaleOff(Double exactSaleOff) {
        this.exactSaleOff = exactSaleOff;
    }

    public Double getMaxSaleOff() {
        return maxSaleOff;
    }

    public void setMaxSaleOff(Double maxSaleOff) {
        this.maxSaleOff = maxSaleOff;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public PromotionDetail getPromotionDetail() {
        return promotionDetail;
    }

    public void setPromotionDetail(PromotionDetail promotionDetail) {
        this.promotionDetail = promotionDetail;
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
        if (!(object instanceof Promotion)) {
            return false;
        }
        Promotion other = (Promotion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.Promotion[ id=" + id + " ]";
    }
    
}
