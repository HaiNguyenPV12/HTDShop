/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.ProductImage;

/**
 *
 * @author Hai
 */
@Local
public interface ProductImageFacadeLocal {

    void create(ProductImage productImage);

    void edit(ProductImage productImage);

    void remove(ProductImage productImage);

    ProductImage find(Object id);

    List<ProductImage> findAll();

    List<ProductImage> findRange(int[] range);

    int count();
    
}
