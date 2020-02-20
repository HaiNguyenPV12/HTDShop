package vn.htdshop.entity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductView
 */
public class ProductView implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;

    private String name;

    private String manufacturer;

    private Double price;

    private Integer stock;

    private String status;

    private String image;

    private Integer imageMore;

    private String category;

    public ProductView() {

    }

    public ProductView(Product p) {
        this.id = p.getId();
        this.name = p.getName();
        this.manufacturer = p.getManufacturer();
        this.price = p.getPrice();
        this.stock = p.getStock();
        switch (p.getStatus()) {
            case 1:
                this.status = "Is selling";
                break;
            case 2:
                this.status = "Upcoming";
                break;
            default:
                this.status = "Unavailable";
                break;
        }

        if (p.getProductImageCollection().size() > 0) {
            int count = 0;
            for (ProductImage pi : p.getProductImageCollection()) {
                if (count == 0) {
                    this.image = pi.getImagePath();
                }
                if (pi.getMainImage()) {
                    this.image = pi.getImagePath();
                }
                count++;
            }
            this.imageMore = p.getProductImageCollection().size() - 1;
        } else {
            this.imageMore = 0;
            this.image = "";
        }
        this.category = p.getCategory().getName();
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
     * @return String return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return Double return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return Integer return the stock
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * @return String return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
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
     * @return the image
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
     * @return Integer return the imageMore
     */
    public Integer getImageMore() {
        return imageMore;
    }

    /**
     * @param imageMore the imageMore to set
     */
    public void setImageMore(Integer imageMore) {
        this.imageMore = imageMore;
    }

}