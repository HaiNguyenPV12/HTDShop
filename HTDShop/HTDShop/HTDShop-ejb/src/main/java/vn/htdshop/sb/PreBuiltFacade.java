/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;

import javax.ejb.Stateless;
import javax.management.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import vn.htdshop.entity.PreBuilt;

/**
 *
 * @author Hai
 */
@Stateless
public class PreBuiltFacade extends AbstractFacade<PreBuilt> implements PreBuiltFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PreBuiltFacade() {
        super(PreBuilt.class);
    }

    @Override
    public List<PreBuilt> search(String keyword) {
        TypedQuery<PreBuilt> query = null;
        query = em.createQuery("SELECT p FROM PreBuilt p WHERE p.name LIKE :keyword", PreBuilt.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    @Override
    public PreBuilt find(Integer id) {
        // TODO Auto-generated method stub
        return em.find(PreBuilt.class, id);
    }

    @Override
    public List<PreBuilt> findByCustomerID(Integer id) {
        TypedQuery<PreBuilt> query = em.createQuery("SELECT p FROM PreBuilt p WHERE p.customer.id = :id",PreBuilt.class);
        query.setParameter("id", id);
        return query.getResultList();
    }


}
