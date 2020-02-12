/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hai
 */
@Entity
@Table(name = "ProductCommentReply")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductCommentReply.findAll", query = "SELECT p FROM ProductCommentReply p")
    , @NamedQuery(name = "ProductCommentReply.findById", query = "SELECT p FROM ProductCommentReply p WHERE p.id = :id")
    , @NamedQuery(name = "ProductCommentReply.findByCreatedAt", query = "SELECT p FROM ProductCommentReply p WHERE p.createdAt = :createdAt")})
public class ProductCommentReply implements Serializable {

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
    @JoinColumn(name = "ProductCommentId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private ProductComment productComment;
    @JoinColumn(name = "StaffUserName", referencedColumnName = "UserName")
    @ManyToOne(optional = false)
    private Staff staff;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private User user;

    public ProductCommentReply() {
    }

    public ProductCommentReply(Integer id) {
        this.id = id;
    }

    public ProductCommentReply(Integer id, String content, Date createdAt) {
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

    public ProductComment getProductComment() {
        return productComment;
    }

    public void setProductComment(ProductComment productComment) {
        this.productComment = productComment;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductCommentReply)) {
            return false;
        }
        ProductCommentReply other = (ProductCommentReply) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.ProductCommentReply[ id=" + id + " ]";
    }
    
}
