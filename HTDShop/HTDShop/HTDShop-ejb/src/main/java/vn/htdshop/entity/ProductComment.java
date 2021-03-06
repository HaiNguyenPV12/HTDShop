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
@Table(name = "ProductComment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductComment.findAll", query = "SELECT p FROM ProductComment p")
    , @NamedQuery(name = "ProductComment.findById", query = "SELECT p FROM ProductComment p WHERE p.id = :id")
    , @NamedQuery(name = "ProductComment.findByCreatedAt", query = "SELECT p FROM ProductComment p WHERE p.createdAt = :createdAt")})
public class ProductComment implements Serializable {

    @JoinColumn(name = "CustomerId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Customer customer;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Content")
    private String content;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "ProductId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Product product;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "productComment")
    private Collection<ProductCommentReply> productCommentReplyCollection;

    @XmlTransient
    public Collection<ProductCommentReply> getProductCommentReplyCollection() {
        return productCommentReplyCollection;
    }

    public void setProductCommentReplyCollection(Collection<ProductCommentReply> productCommentReplyCollection) {
        this.productCommentReplyCollection = productCommentReplyCollection;
    }

    public ProductComment() {
    }

    public ProductComment(Integer id) {
        this.id = id;
    }

    public ProductComment(Integer id, String content, Date createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
        if (!(object instanceof ProductComment)) {
            return false;
        }
        ProductComment other = (ProductComment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.ProductComment[ id=" + id + " ]";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
