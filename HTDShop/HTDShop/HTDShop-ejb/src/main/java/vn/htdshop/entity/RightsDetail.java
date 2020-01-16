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
import javax.persistence.Lob;
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
@Table(name = "RightsDetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RightsDetail.findAll", query = "SELECT r FROM RightsDetail r")
    , @NamedQuery(name = "RightsDetail.findById", query = "SELECT r FROM RightsDetail r WHERE r.id = :id")
    , @NamedQuery(name = "RightsDetail.findByTag", query = "SELECT r FROM RightsDetail r WHERE r.tag = :tag")})
public class RightsDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "Tag")
    private String tag;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "rightsDetail")
    private Collection<RoleRights> roleRightsCollection;

    public RightsDetail() {
    }

    public RightsDetail(Integer id) {
        this.id = id;
    }

    public RightsDetail(Integer id, String tag, String description) {
        this.id = id;
        this.tag = tag;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Collection<RoleRights> getRoleRightsCollection() {
        return roleRightsCollection;
    }

    public void setRoleRightsCollection(Collection<RoleRights> roleRightsCollection) {
        this.roleRightsCollection = roleRightsCollection;
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
        if (!(object instanceof RightsDetail)) {
            return false;
        }
        RightsDetail other = (RightsDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.RightsDetail[ id=" + id + " ]";
    }
    
}
