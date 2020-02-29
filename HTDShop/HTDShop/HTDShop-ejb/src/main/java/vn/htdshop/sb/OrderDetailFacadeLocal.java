/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import vn.htdshop.entity.OrderDetail;

/**
 *
 * @author Hai
 */
@Local
public interface OrderDetailFacadeLocal {

    void create(OrderDetail orderDetail);

    void edit(OrderDetail orderDetail);

    void remove(OrderDetail orderDetail);

    OrderDetail find(Object id);

    List<OrderDetail> findAll();

    List<OrderDetail> findRange(int[] range);

    int count();

    Map<Integer, Integer> getTopProduct(String datepart, Integer top);

    Map<Integer, Integer> getTopCategory(String datepart, Integer top);

    Map<String, Integer> getTopManufacturer(String datepart, Integer top);
    
}
