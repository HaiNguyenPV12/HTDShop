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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hai
 */
@Entity
@Table(name = "Delivery")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Delivery.findAll", query = "SELECT d FROM Delivery d")
    , @NamedQuery(name = "Delivery.findById", query = "SELECT d FROM Delivery d WHERE d.id = :id")
    , @NamedQuery(name = "Delivery.findByDeliveryStatus", query = "SELECT d FROM Delivery d WHERE d.deliveryStatus = :deliveryStatus")
    , @NamedQuery(name = "Delivery.findByAssignedDate", query = "SELECT d FROM Delivery d WHERE d.assignedDate = :assignedDate")
    , @NamedQuery(name = "Delivery.findByDelayedDate", query = "SELECT d FROM Delivery d WHERE d.delayedDate = :delayedDate")
    , @NamedQuery(name = "Delivery.findByDeliveredDate", query = "SELECT d FROM Delivery d WHERE d.deliveredDate = :deliveredDate")
    , @NamedQuery(name = "Delivery.findByCancelledDate", query = "SELECT d FROM Delivery d WHERE d.cancelledDate = :cancelledDate")})
public class Delivery implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DeliveryStatus")
    private int deliveryStatus;
    @Basic(optional = false)
    @NotNull
    @Column(name = "AssignedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedDate;
    @Column(name = "DelayedDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date delayedDate;
    @Column(name = "DeliveredDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredDate;
    @Column(name = "CancelledDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelledDate;
    @JoinColumn(name = "OrderId", referencedColumnName = "Id")
    @ManyToOne(optional = false)
    private Order1 order1;
    @JoinColumn(name = "DeliveryStaff", referencedColumnName = "UserName")
    @ManyToOne(optional = false)
    private Staff staff;

    public Delivery() {
    }

    public Delivery(Integer id) {
        this.id = id;
    }

    public Delivery(Integer id, int deliveryStatus, Date assignedDate) {
        this.id = id;
        this.deliveryStatus = deliveryStatus;
        this.assignedDate = assignedDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Date getDelayedDate() {
        return delayedDate;
    }

    public void setDelayedDate(Date delayedDate) {
        this.delayedDate = delayedDate;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public Order1 getOrder1() {
        return order1;
    }

    public void setOrder1(Order1 order1) {
        this.order1 = order1;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
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
        if (!(object instanceof Delivery)) {
            return false;
        }
        Delivery other = (Delivery) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.htdshop.entity.Delivery[ id=" + id + " ]";
    }
    
}
