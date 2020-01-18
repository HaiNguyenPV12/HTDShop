/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.Staff;

/**
 *
 * @author Hai
 */
@Local
public interface StaffFacadeLocal {

    void create(Staff staff);

    void edit(Staff staff);

    void remove(Staff staff);

    Staff find(Object id);

    List<Staff> findAll();

    List<Staff> findRange(int[] range);

    int count();

    Staff checkLogin(String u, String p);
    
}
