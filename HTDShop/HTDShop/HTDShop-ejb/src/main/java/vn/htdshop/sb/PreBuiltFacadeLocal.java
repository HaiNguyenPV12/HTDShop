/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.PreBuilt;

/**
 *
 * @author Hai
 */
@Local
public interface PreBuiltFacadeLocal {

    void create(PreBuilt preBuilt);

    void edit(PreBuilt preBuilt);

    void remove(PreBuilt preBuilt);

    PreBuilt find(Object id);

    List<PreBuilt> findAll();

    List<PreBuilt> findRange(int[] range);

    int count();
    
}
