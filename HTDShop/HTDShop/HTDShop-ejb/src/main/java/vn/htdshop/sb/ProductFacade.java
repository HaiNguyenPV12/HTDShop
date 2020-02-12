/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import vn.htdshop.entity.Product;

/**
 *
 * @author Hai
 */
@Stateless
public class ProductFacade extends AbstractFacade<Product> implements ProductFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductFacade() {
        super(Product.class);
    }

    @Override
    public List<Product> search(Integer cateId, String search) {
        TypedQuery<Product> query = null;
        if (cateId == 0) {
            query = em.createQuery("SELECT p FROM Product p WHERE p.name LIKE :search",
                    Product.class);
        } else {
            query = em.createQuery("SELECT p FROM Product p WHERE p.category.id = :cateId AND p.name LIKE :search",
                    Product.class);
            query.setParameter("cateId", cateId);
        }
        query.setParameter("search", "%" + search + "%");
        return query.getResultList();
    }

}
