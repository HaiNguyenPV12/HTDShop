/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;

import vn.htdshop.entity.Order1;

/**
 *
 * @author Hai
 */
@Stateless
public class Order1Facade extends AbstractFacade<Order1> implements Order1FacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Order1Facade() {
        super(Order1.class);
    }
    
    public List<Order1> findByOrderStatus(){
        TypedQuery<Order1> query = em.createQuery("Select o from Order1 o where o.orderStatus = 4",Order1.class);
        return query.getResultList();
    }

    @Override
    public Order1 findByPhoneAndId(Integer id, String phone) {
        TypedQuery<Order1> query = em.createQuery(
                "SELECT o FROM Order1 o WHERE o.id = :id AND o.customer.phone = :phone", Order1.class);
        query.setParameter("id", id);
        query.setParameter("phone", phone);
        if (query.getResultList().size()<=0) {
            return null;
        }
        return query.getSingleResult();
    }
}
