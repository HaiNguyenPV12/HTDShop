/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import vn.htdshop.entity.RoleRights;

/**
 *
 * @author Hai
 */
@Stateless
public class RoleRightsFacade extends AbstractFacade<RoleRights> implements RoleRightsFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RoleRightsFacade() {
        super(RoleRights.class);
    }
    
}
