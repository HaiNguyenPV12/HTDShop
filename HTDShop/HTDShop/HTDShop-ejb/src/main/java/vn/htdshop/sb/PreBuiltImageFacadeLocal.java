/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.PreBuiltImage;

/**
 *
 * @author Hai
 */
@Local
public interface PreBuiltImageFacadeLocal {

    void create(PreBuiltImage preBuiltImage);

    void edit(PreBuiltImage preBuiltImage);

    void remove(PreBuiltImage preBuiltImage);

    PreBuiltImage find(Object id);

    List<PreBuiltImage> findAll();

    List<PreBuiltImage> findRange(int[] range);

    int count();
    
}
