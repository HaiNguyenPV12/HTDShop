package vn.htdshop.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * PromotionView
 */
public class PromotionView implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String detail;
    private String image;
    private String target;
    private Date startDate;
    private Date endDate;
    private boolean isAlways;
    private boolean isDisabled;

    public PromotionView() {
    }

    public PromotionView(PromotionDetail promo) {
        this.id = promo.getId();
        this.name = promo.getName();
        this.image = promo.getImage();
        this.detail = promo.getDetail();
        this.startDate = promo.getStartDate();
        this.endDate = promo.getEndDate();
        this.isAlways = promo.getIsAlways();
        this.isDisabled = promo.getIsDisabled();
        if (promo.getTarget() == 0) {
            this.target = "All";
        } else {
            this.target = "Member";
        }
    }

    /**
     * @return Integer return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return String return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return String return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return Date return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return Date return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return boolean return the isAlways
     */
    public boolean getIsAlways() {
        return isAlways;
    }

    /**
     * @param isAlways the isAlways to set
     */
    public void setIsAlways(boolean isAlways) {
        this.isAlways = isAlways;
    }

    /**
     * @return boolean return the isDisabled
     */
    public boolean getIsDisabled() {
        return isDisabled;
    }

    /**
     * @param isDisabled the isDisabled to set
     */
    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

}