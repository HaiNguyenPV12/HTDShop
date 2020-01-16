/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.ProductCommentReply;

/**
 *
 * @author Hai
 */
@Local
public interface ProductCommentReplyFacadeLocal {

    void create(ProductCommentReply productCommentReply);

    void edit(ProductCommentReply productCommentReply);

    void remove(ProductCommentReply productCommentReply);

    ProductCommentReply find(Object id);

    List<ProductCommentReply> findAll();

    List<ProductCommentReply> findRange(int[] range);

    int count();
    
}
