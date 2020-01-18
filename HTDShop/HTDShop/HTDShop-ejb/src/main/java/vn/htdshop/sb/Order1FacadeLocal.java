/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.Order1;

/**
 *
 * @author Hai
 */
@Local
public interface Order1FacadeLocal {

    void create(Order1 order1);

    void edit(Order1 order1);

    void remove(Order1 order1);

    Order1 find(Object id);

    List<Order1> findAll();

    List<Order1> findRange(int[] range);

    int count();
    
}
