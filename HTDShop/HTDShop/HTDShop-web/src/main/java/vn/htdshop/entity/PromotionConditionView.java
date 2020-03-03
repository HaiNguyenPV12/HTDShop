package vn.htdshop.entity;

import java.io.Serializable;

/**
 * PromotionConditionView
 */
public class PromotionConditionView implements Serializable {

    private static final long serialVersionUID = 1L;
    private String category;
    private String product;
    private String prebuilt;
    private Integer limitedQuantity;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Integer quantityLeft;
    private Double percentage;
    private Double exactSaleOff;
    private Double maxSaleOff;

    public PromotionConditionView() {
        
    }

    public PromotionConditionView(Promotion promotion) {
        
    }

    /**
     * @return String return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return String return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return String return the prebuilt
     */
    public String getPrebuilt() {
        return prebuilt;
    }

    /**
     * @param prebuilt the prebuilt to set
     */
    public void setPrebuilt(String prebuilt) {
        this.prebuilt = prebuilt;
    }

    /**
     * @return Integer return the limitedQuantity
     */
    public Integer getLimitedQuantity() {
        return limitedQuantity;
    }

    /**
     * @param limitedQuantity the limitedQuantity to set
     */
    public void setLimitedQuantity(Integer limitedQuantity) {
        this.limitedQuantity = limitedQuantity;
    }

    /**
     * @return Integer return the minQuantity
     */
    public Integer getMinQuantity() {
        return minQuantity;
    }

    /**
     * @param minQuantity the minQuantity to set
     */
    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    /**
     * @return Integer return the maxQuantity
     */
    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    /**
     * @param maxQuantity the maxQuantity to set
     */
    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    /**
     * @return Integer return the quantityLeft
     */
    public Integer getQuantityLeft() {
        return quantityLeft;
    }

    /**
     * @param quantityLeft the quantityLeft to set
     */
    public void setQuantityLeft(Integer quantityLeft) {
        this.quantityLeft = quantityLeft;
    }

    /**
     * @return Double return the percentage
     */
    public Double getPercentage() {
        return percentage;
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    /**
     * @return Double return the exactSaleOff
     */
    public Double getExactSaleOff() {
        return exactSaleOff;
    }

    /**
     * @param exactSaleOff the exactSaleOff to set
     */
    public void setExactSaleOff(Double exactSaleOff) {
        this.exactSaleOff = exactSaleOff;
    }

    /**
     * @return Double return the maxSaleOff
     */
    public Double getMaxSaleOff() {
        return maxSaleOff;
    }

    /**
     * @param maxSaleOff the maxSaleOff to set
     */
    public void setMaxSaleOff(Double maxSaleOff) {
        this.maxSaleOff = maxSaleOff;
    }

}