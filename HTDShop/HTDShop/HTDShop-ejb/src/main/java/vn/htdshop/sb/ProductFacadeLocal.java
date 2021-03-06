/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.Product;

/**
 *
 * @author Hai
 */
@Local
public interface ProductFacadeLocal {

    void create(Product product);

    void edit(Product product);

    void remove(Product product);

    Product find(Integer id);

    List<Product> findAll();

    List<Product> findRange(int[] range);

    int count();

    List<Product> search(Integer cateId, String search);

    List<Product> search(Integer cateId, String search, Integer promoId);

    List<String> getStringList(String attr);

    List<String> getManuOtherList(String cateId);

    List<String> getStringList(String attr, String options);

    List<Integer> getIntegerList(String attr);

    List<Double> getDoubleList(String attr);
}
