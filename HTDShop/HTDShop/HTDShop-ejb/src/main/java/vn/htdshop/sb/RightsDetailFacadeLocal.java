/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.RightsDetail;

/**
 *
 * @author Hai
 */
@Local
public interface RightsDetailFacadeLocal {

    void create(RightsDetail rightsDetail);

    void edit(RightsDetail rightsDetail);

    void remove(RightsDetail rightsDetail);

    RightsDetail find(Object id);

    List<RightsDetail> findAll();

    List<RightsDetail> findRange(int[] range);

    int count();
    
}
