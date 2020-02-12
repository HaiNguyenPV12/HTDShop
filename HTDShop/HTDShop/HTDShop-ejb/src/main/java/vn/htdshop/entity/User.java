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
 * @author Thien
 */
@Entity
@Table(name = "[User]")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
    , @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id")
    , @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password")
    , @NamedQuery(name = "User.findByGender", query = "SELECT u FROM User u WHERE u.gender = :gender")
    , @NamedQuery(name = "User.findByBirthday", query = "SELECT u FROM User u WHERE u.birthday = :birthday")
    , @NamedQuery(name = "User.findByPoint", query = "SELECT u FROM User u WHERE u.point = :point")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Password")
    private String password;
    @Column(name = "Gender")
    private Boolean gender;
    @DateTimeFormat (pattern="dd/MM/yyyy")
    @Column(name = "Birthday")
    @Temporal(TemporalType.DATE)
    private Date birthday;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Point")
    private int point;
    @OneToMany(mappedBy = "user")
    private Collection<Customer> customerCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<ProductComment> productCommentCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<ProductCommentReply> productCommentReplyCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<PreBuilt> preBuiltCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<PreBuiltRating> preBuiltRatingCollection;

    public User() {
    }

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
    public Collection<PreBuilt> getPreBuiltCollection() {
        return preBuiltCollection;
    }

    public void setPreBuiltCollection(Collection<PreBuilt> preBuiltCollection) {
        this.preBuiltCollection = preBuiltCollection;
    }

    @XmlTransient
    public Collection<PreBuiltRating> getPreBuiltRatingCollection() {
        return preBuiltRatingCollection;
    }

    public void setPreBuiltRatingCollection(Collection<PreBuiltRating> preBuiltRatingCollection) {
        this.preBuiltRatingCollection = preBuiltRatingCollection;
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, String password, int point) {
        this.id = id;
        this.password = password;
        this.point = point;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    @XmlTransient
    public Collection<Customer> getCustomerCollection() {
        return customerCollection;
    }

    public void setCustomerCollection(Collection<Customer> customerCollection) {
        this.customerCollection = customerCollection;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.User[ id=" + id + " ]";
    }

}
