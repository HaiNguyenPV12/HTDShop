/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import vn.htdshop.entity.RightsDetail;

/**
 *
 * @author Hai
 */
@Stateless
public class RightsDetailFacade extends AbstractFacade<RightsDetail> implements RightsDetailFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RightsDetailFacade() {
        super(RightsDetail.class);
    }

    @Override
    public RightsDetail findByTag(String tag) {
        TypedQuery q = em.createNamedQuery("RightsDetail.findByTag", RightsDetail.class);
        q.setParameter("tag", tag);
        if (q.getSingleResult()==null) {
            return null;
        }
        return (RightsDetail) q.getSingleResult();
    }
    
}
