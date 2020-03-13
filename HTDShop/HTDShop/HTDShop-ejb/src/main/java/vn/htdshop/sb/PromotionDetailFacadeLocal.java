/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.PromotionDetail;

/**
 *
 * @author Hai
 */
@Local
public interface PromotionDetailFacadeLocal {

    void create(PromotionDetail promotionDetail);

    void edit(PromotionDetail promotionDetail);

    void remove(PromotionDetail promotionDetail);

    PromotionDetail find(Object id);

    List<PromotionDetail> findAll();

    List<PromotionDetail> findAllValidPromotion();
    
    List<PromotionDetail> findAllValidPromotion(String type);

    List<PromotionDetail> findRange(int[] range);

    int count();
    
}
