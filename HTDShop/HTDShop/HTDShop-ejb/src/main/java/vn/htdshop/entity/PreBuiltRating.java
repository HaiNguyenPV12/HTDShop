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
@Table(name = "PreBuiltRating")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PreBuiltRating.findAll", query = "SELECT p FROM PreBuiltRating p")
    , @NamedQuery(name = "PreBuiltRating.findById", query = "SELECT p FROM PreBuiltRating p WHERE p.id = :id")
    , @NamedQuery(name = "PreBuiltRating.findByRating", query = "SELECT p FROM PreBuiltRating p WHERE p.rating = :rating")
    , @NamedQuery(name = "PreBuiltRating.findByCreatedAt", query = "SELECT p FROM PreBuiltRating p WHERE p.createdAt = :createdAt")})
public class PreBuiltRating implements Serializable {

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
    @Column(name = "Comment")
    private String comment;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Rating")
    private double rating;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @JoinColumn(name = "PreBuiltId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private PreBuilt preBuilt;
    @JoinColumn(name = "UserId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private User user;

    public PreBuiltRating() {
    }

    public PreBuiltRating(Integer id) {
        this.id = id;
    }

    public PreBuiltRating(Integer id, String comment, double rating, Date createdAt) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public PreBuilt getPreBuilt() {
        return preBuilt;
    }

    public void setPreBuilt(PreBuilt preBuilt) {
        this.preBuilt = preBuilt;
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
        if (!(object instanceof PreBuiltRating)) {
            return false;
        }
        PreBuiltRating other = (PreBuiltRating) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.PreBuiltRating[ id=" + id + " ]";
    }
    
}
