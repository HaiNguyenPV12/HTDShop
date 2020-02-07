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
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import vn.htdshop.entity.Role;

/**
 *
 * @author Hai
 */
@Stateless
public class RoleFacade extends AbstractFacade<Role> implements RoleFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RoleFacade() {
        super(Role.class);
    }

    @Override
    public Role findByName(String name) {
        Query q = em.createNamedQuery("Role.findByName");
        q.setParameter("name", name);
        if (q.getResultList().size() <= 0) {
            return null;
        }
        return (Role) q.getSingleResult();
    }

}
