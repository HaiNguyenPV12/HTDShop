/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.CategoryOther;

/**
 *
 * @author Thien
 */
@Local
public interface CategoryOtherFacadeLocal {

    void create(CategoryOther categoryOther);

    void edit(CategoryOther categoryOther);

    void remove(CategoryOther categoryOther);

    CategoryOther find(Object id);

    List<CategoryOther> findAll();

    List<CategoryOther> findRange(int[] range);

    int count();
    
}
