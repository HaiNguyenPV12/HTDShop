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
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "ImageSlide")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ImageSlide.findAll", query = "SELECT i FROM ImageSlide i")
    , @NamedQuery(name = "ImageSlide.findById", query = "SELECT i FROM ImageSlide i WHERE i.id = :id")
    , @NamedQuery(name = "ImageSlide.findByOrder", query = "SELECT i FROM ImageSlide i WHERE i.order = :order")
    , @NamedQuery(name = "ImageSlide.findByStatus", query = "SELECT i FROM ImageSlide i WHERE i.status = :status")})
public class ImageSlide implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "Image")
    private String image;
    @Column(name = "[Order]")
    private Integer order;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Title")
    private String title;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "[Description]")
    private String description;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "Link")
    private String link;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Status")
    private boolean status;

    public ImageSlide() {
    }

    public ImageSlide(Integer id) {
        this.id = id;
    }

    public ImageSlide(Integer id, String image, boolean status) {
        this.id = id;
        this.image = image;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
        if (!(object instanceof ImageSlide)) {
            return false;
        }
        ImageSlide other = (ImageSlide) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.ImageSlide[ id=" + id + " ]";
    }
    
}
