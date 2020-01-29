/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.htdshop.sb;

import java.util.List;
import javax.ejb.Local;
import vn.htdshop.entity.ImageSlide;

/**
 *
 * @author Thien
 */
@Local
public interface ImageSlideFacadeLocal {

    void create(ImageSlide imageSlide);

    void edit(ImageSlide imageSlide);

    void remove(ImageSlide imageSlide);

    ImageSlide find(Object id);

    List<ImageSlide> findAll();

    List<ImageSlide> findRange(int[] range);

    int count();
    
}
