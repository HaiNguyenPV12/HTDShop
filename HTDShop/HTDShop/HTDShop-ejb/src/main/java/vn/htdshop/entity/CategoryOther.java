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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thien
 */
@Entity
@Table(name = "CategoryOther")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CategoryOther.findAll", query = "SELECT c FROM CategoryOther c")
    , @NamedQuery(name = "CategoryOther.findById", query = "SELECT c FROM CategoryOther c WHERE c.id = :id")
    , @NamedQuery(name = "CategoryOther.findByName", query = "SELECT c FROM CategoryOther c WHERE c.name = :name")})
public class CategoryOther implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Name")
    private String name;
    @JoinColumn(name = "CateId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Category category;

    public CategoryOther() {
    }

    public CategoryOther(Integer id) {
        this.id = id;
    }

    public CategoryOther(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
        if (!(object instanceof CategoryOther)) {
            return false;
        }
        CategoryOther other = (CategoryOther) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.CategoryOther[ id=" + id + " ]";
    }
    
}
