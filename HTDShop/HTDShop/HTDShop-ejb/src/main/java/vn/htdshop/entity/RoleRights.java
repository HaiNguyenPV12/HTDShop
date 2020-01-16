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
@Table(name = "RoleRights")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RoleRights.findAll", query = "SELECT r FROM RoleRights r")
    , @NamedQuery(name = "RoleRights.findById", query = "SELECT r FROM RoleRights r WHERE r.id = :id")})
public class RoleRights implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @JoinColumn(name = "RightsId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private RightsDetail rightsDetail;
    @JoinColumn(name = "RoleId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Role role;

    public RoleRights() {
    }

    public RoleRights(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RightsDetail getRightsDetail() {
        return rightsDetail;
    }

    public void setRightsDetail(RightsDetail rightsDetail) {
        this.rightsDetail = rightsDetail;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        if (!(object instanceof RoleRights)) {
            return false;
        }
        RoleRights other = (RoleRights) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.RoleRights[ id=" + id + " ]";
    }
    
}
