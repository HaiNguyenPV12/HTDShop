/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.ProductComment;

/**
 *
 * @author Hai
 */
@Local
public interface ProductCommentFacadeLocal {

    void create(ProductComment productComment);

    void edit(ProductComment productComment);

    void remove(ProductComment productComment);

    ProductComment find(Object id);

    List<ProductComment> findAll();

    List<ProductComment> findRange(int[] range);

    int count();
    
}
