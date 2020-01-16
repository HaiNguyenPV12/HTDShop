/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.Delivery;

/**
 *
 * @author Hai
 */
@Local
public interface DeliveryFacadeLocal {

    void create(Delivery delivery);

    void edit(Delivery delivery);

    void remove(Delivery delivery);

    Delivery find(Object id);

    List<Delivery> findAll();

    List<Delivery> findRange(int[] range);

    int count();
    
}
