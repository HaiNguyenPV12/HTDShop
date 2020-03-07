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

import vn.htdshop.entity.ImageSlide;

/**
 *
 * @author Thien
 */
@Stateless
public class ImageSlideFacade extends AbstractFacade<ImageSlide> implements ImageSlideFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ImageSlideFacade() {
        super(ImageSlide.class);
    }

    @Override
    public boolean promoExist(int promoId) {
        Query query = em.createNativeQuery(
                "SELECT Link FROM ImageSlide WHERE CAST(Link AS VARCHAR(MAX)) = 'promotion?id=" + promoId + "'");
        if (query.getResultList().size() > 0) {
            return true;
        }
        return false;
    }

}
