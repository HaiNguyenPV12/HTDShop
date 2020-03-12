/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultType;

import vn.htdshop.entity.OrderDetail;

/**
 *
 * @author Hai
 */
@Stateless
public class OrderDetailFacade extends AbstractFacade<OrderDetail> implements OrderDetailFacadeLocal {

    @PersistenceContext(unitName = "vn.htdshop_HTDShop-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrderDetailFacade() {
        super(OrderDetail.class);
    }

    @Override
    public Map<Integer, Integer> getTopProduct(String datepart, Integer top) {
        Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        String str = "SELECT";
        if (top > 0) {
            str += " TOP " + top;
        }
        str += " ProductId, SUM(Quantity) AS Quan FROM OrderDetail, [Order]"
                + " WHERE OrderDetail.OrderId = [Order].Id AND [Order].OrderStatus = 4";
        if (!datepart.equals("")) {
            str += " AND DATEDIFF(" + datepart + ", [Order].PaidDate, GETDATE()) = 0";
        }
        str += " GROUP BY ProductId ORDER BY Quan DESC";
        Query q = em.createNativeQuery(str);
        if (q.getResultList().size() > 0) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                result.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Integer> getTopProductInCategory(String datepart, Integer top, Integer cateId) {
        Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        String str = "SELECT";
        if (top > 0) {
            str += " TOP " + top;
        }
        str += " ProductId, SUM(Quantity) AS Quan FROM OrderDetail, [Order], Product"
                + " WHERE OrderDetail.OrderId = [Order].Id AND Product.Id = OrderDetail.ProductId AND [Order].OrderStatus = 4";
        str += " AND Product.CateId = " + cateId;
        if (!datepart.equals("")) {
            str += " AND DATEDIFF(" + datepart + ", [Order].OrderDate, GETDATE()) = 0";
        }
        str += " GROUP BY ProductId ORDER BY Quan DESC";
        Query q = em.createNativeQuery(str);
        if (q.getResultList().size() > 0) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                result.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Integer> getTopCategory(String datepart, Integer top) {
        Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        String str = "SELECT";
        if (top > 0) {
            str += " TOP " + top;
        }
        str += " Product.CateId, SUM(Quantity) AS Quan FROM OrderDetail, [Order], Product"
                + " WHERE OrderDetail.OrderId = [Order].Id AND OrderDetail.ProductId = Product.Id AND [Order].OrderStatus = 4";
        if (!datepart.equals("")) {
            str += " AND DATEDIFF(" + datepart + ", [Order].OrderDate, GETDATE()) = 0";
        }
        str += " GROUP BY Product.CateId ORDER BY Quan DESC";
        Query q = em.createNativeQuery(str);
        if (q.getResultList().size() > 0) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                result.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
            }
        }
        return result;
    }

    @Override
    public Map<String, Integer> getTopManufacturer(String datepart, Integer top) {
        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        String str = "SELECT";
        if (top > 0) {
            str += " TOP " + top;
        }
        str += " Product.Manufacturer, SUM(Quantity) AS Quan FROM OrderDetail, [Order], Product"
                + " WHERE OrderDetail.OrderId = [Order].Id AND OrderDetail.ProductId = Product.Id AND [Order].OrderStatus = 4";
        if (!datepart.equals("")) {
            str += " AND DATEDIFF(" + datepart + ", [Order].OrderDate, GETDATE()) = 0";
        }
        str += " GROUP BY Product.Manufacturer ORDER BY Quan DESC";
        Query q = em.createNativeQuery(str);

        if (q.getResultList().size() > 0) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                result.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
            }
        }
        return result;
    }

}
