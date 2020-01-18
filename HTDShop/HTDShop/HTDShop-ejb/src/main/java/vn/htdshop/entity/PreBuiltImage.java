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
 * @author Hai
 */
@Entity
@Table(name = "PreBuiltImage")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PreBuiltImage.findAll", query = "SELECT p FROM PreBuiltImage p")
    , @NamedQuery(name = "PreBuiltImage.findById", query = "SELECT p FROM PreBuiltImage p WHERE p.id = :id")
    , @NamedQuery(name = "PreBuiltImage.findByPath", query = "SELECT p FROM PreBuiltImage p WHERE p.path = :path")})
public class PreBuiltImage implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "Path")
    private String path;
    @JoinColumn(name = "PreBuiltId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private PreBuilt preBuilt;

    public PreBuiltImage() {
    }

    public PreBuiltImage(Integer id) {
        this.id = id;
    }

    public PreBuiltImage(Integer id, String path) {
        this.id = id;
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PreBuilt getPreBuilt() {
        return preBuilt;
    }

    public void setPreBuilt(PreBuilt preBuilt) {
        this.preBuilt = preBuilt;
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
        if (!(object instanceof PreBuiltImage)) {
            return false;
        }
        PreBuiltImage other = (PreBuiltImage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.PreBuiltImage[ id=" + id + " ]";
    }
    
}
