package vn.htdshop.entity;

/**
 * CartItem
 */
public class CartItem {
    private String id;
    private Integer quan;

    public CartItem() {
    }

    // public CartItem(Integer id, Integer quan) {
    //     this.id = id;
    //     this.quan = quan;
    //     this.isPrebuilt = false;
    // }

    public CartItem(String id, Integer quan) {
        this.id = id;
        this.quan = quan;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the quan
     */
    public Integer getQuan() {
        return quan;
    }

    /**
     * @param quan the quan to set
     */
    public void setQuan(Integer quan) {
        this.quan = quan;
    }

}