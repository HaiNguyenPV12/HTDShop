package vn.htdshop.entity;

import java.io.Serializable;

/**
 * ProductView
 */
public class ProductImageView implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String imagePath;

    private String thumbnailPath;

    private Boolean mainImage;

    public ProductImageView() {

    }

    public ProductImageView(ProductImage pi) {
        this.id = pi.getId();
        this.imagePath = pi.getImagePath();
        if (pi.getThumbnailPath() != null && !pi.getThumbnailPath().isEmpty()) {
            this.thumbnailPath = pi.getThumbnailPath();
        } else {
            this.thumbnailPath = pi.getImagePath();
        }

        this.mainImage = pi.getMainImage();
    }

    public ProductImageView(Integer id, String imagePath, String thumbnailPath, Boolean mainImage) {
        this.id = id;
        this.imagePath = imagePath;
        this.thumbnailPath = thumbnailPath;
        this.mainImage = mainImage;
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

    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return Boolean return the mainImage
     */
    public Boolean getMainImage() {
        return mainImage;
    }

    /**
     * @param mainImage the mainImage to set
     */
    public void setMainImage(Boolean mainImage) {
        this.mainImage = mainImage;
    }

    /**
     * @return the thumbnailPath
     */
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    /**
     * @param thumbnailPath the thumbnailPath to set
     */
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

}