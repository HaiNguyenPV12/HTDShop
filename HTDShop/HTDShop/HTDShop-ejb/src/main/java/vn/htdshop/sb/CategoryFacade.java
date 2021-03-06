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

import vn.htdshop.entity.Category;

/**
 *
 * @author Hai
 */
@Stateless
public class CategoryFacade extends AbstractFacade<Category> implements CategoryFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoryFacade() {
        super(Category.class);
    }

    @Override
    public Category findByName(String name) {
        Query q = em.createQuery("SELECT c FROM Category c WHERE c.name = :name");
        q.setParameter("name", name);
        if (q.getResultList().size() <= 0) {
            return null;
        }
        return (Category) q.getSingleResult();
    }

}
