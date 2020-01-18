/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.RoleRights;

/**
 *
 * @author Hai
 */
@Local
public interface RoleRightsFacadeLocal {

    void create(RoleRights roleRights);

    void edit(RoleRights roleRights);

    void remove(RoleRights roleRights);

    RoleRights find(Object id);

    List<RoleRights> findAll();

    List<RoleRights> findRange(int[] range);

    int count();
    
}
