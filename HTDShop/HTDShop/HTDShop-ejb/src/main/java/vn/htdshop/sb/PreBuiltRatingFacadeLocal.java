/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.PreBuiltRating;

/**
 *
 * @author Hai
 */
@Local
public interface PreBuiltRatingFacadeLocal {

    void create(PreBuiltRating preBuiltRating);

    void edit(PreBuiltRating preBuiltRating);

    void remove(PreBuiltRating preBuiltRating);

    PreBuiltRating find(Object id);

    List<PreBuiltRating> findAll();

    List<PreBuiltRating> findRange(int[] range);

    int count();
    
}
