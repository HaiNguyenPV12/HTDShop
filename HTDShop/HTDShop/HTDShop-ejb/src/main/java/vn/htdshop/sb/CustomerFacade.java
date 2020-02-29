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

import vn.htdshop.entity.Customer;

/**
 *
 * @author Thien
 */
@Stateless
public class CustomerFacade extends AbstractFacade<Customer> implements CustomerFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CustomerFacade() {
        super(Customer.class);
    }

    @Override
    public Customer checkLogin(String e, String p) {
        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email AND c.password = :password", Customer.class);
        query.setParameter("email", e);
        query.setParameter("password", p);

        if (query.getResultList().size() == 0) {
            return null;
        }
        return query.getSingleResult();
    }

    @Override
    public Customer findByEmail(String email) {
        Query q = em.createNamedQuery("Customer.findByEmail");
        q.setParameter("email", email);
        if (q.getResultList().size() <= 0) {
            return null;
        }
        return (Customer) q.getSingleResult();
    }

}
