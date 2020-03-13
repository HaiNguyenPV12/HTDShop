/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import vn.htdshop.entity.Promotion;

import vn.htdshop.entity.PromotionDetail;
import vn.htdshop.entity.PromotionDetail_;
import vn.htdshop.entity.Promotion_;

/**
 *
 * @author Hai
 */
@Stateless
public class PromotionDetailFacade extends AbstractFacade<PromotionDetail> implements PromotionDetailFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PromotionDetailFacade() {
        super(PromotionDetail.class);
    }

    @Override
    public List<PromotionDetail> findAllValidPromotion() {
        TypedQuery<PromotionDetail> query = em.createQuery("SELECT p FROM PromotionDetail p WHERE p.isDisabled = 0 AND (p.isAlways = 1 OR p.endDate >= :date)", PromotionDetail.class);
        query.setParameter("date", new Date(), TemporalType.DATE);
        return query.getResultList();
    }

    @Override
    public List<PromotionDetail> findAllValidPromotion(String type) {
        
        TypedQuery<PromotionDetail> query = null;
        if ("product".equals(type)) {
            query = em.createQuery("SELECT pd FROM PromotionDetail pd, Promotion p WHERE pd.promotionCollection = p AND pd.isDisabled = 0 AND (pd.isAlways = 1 OR pd.endDate >= :date) AND p.preBuiltTarget = null", PromotionDetail.class);
//            query = em.createQuery("SELECT pd FROM PromotionDetail pd LEFT JOIN pd.promotionCollection p WHERE pd.isDisabled = 0 AND (pd.isAlways = 1 OR pd.endDate >= :date) AND p.preBuiltTarget = null", PromotionDetail.class);
        } else if ("prebuilt".equals(type)) {
            query = em.createQuery("SELECT p FROM PromotionDetail p WHERE p.isDisabled = 0 AND (p.isAlways = 1 OR p.endDate >= :date) AND p.promotionCollection.preBuiltTarget != null", PromotionDetail.class);
        } else {
            query = em.createQuery("SELECT p FROM PromotionDetail p WHERE p.isDisabled = 0 AND (p.isAlways = 1 OR p.endDate >= :date)", PromotionDetail.class);
        }
        query.setParameter("date", new Date(), TemporalType.DATE);
        return query.getResultList();
//        CriteriaBuilder cb = em.getCriteriaBuilder(); 
//        CriteriaQuery<PromotionDetail> query = cb.createQuery(PromotionDetail.class);
//        Root<PromotionDetail> promoDetail = query.from(PromotionDetail.class);
//        Join<PromotionDetail, Promotion> promo = promoDetail.join(PromotionDetail_.promotionCollection);
//
//        Predicate notDisable = cb.equal(promoDetail.get("isDisabled"), 0);
//        Predicate validDate = cb.or(cb.equal(promoDetail.get("isAlways"), 1), cb.greaterThanOrEqualTo(promoDetail.<Date>get("endDate"), new Date()));
//
//        if ("product".equals(type)) {
//            query.select(promoDetail).distinct(true).where(cb.and(cb.isNull(promo.get(Promotion_.preBuiltTarget))), notDisable, validDate);
//        } else if ("prebuilt".equals(type)) {
//            query.select(promoDetail).distinct(true).where(cb.and(cb.isNotNull(promo.get(Promotion_.preBuiltTarget))), notDisable, validDate);
//        } else {
//            query.select(promoDetail).distinct(true).where(cb.and(notDisable, validDate));
//        }
//        return em.createQuery(query).getResultList();
    }

}
