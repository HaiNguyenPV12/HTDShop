package vn.htdshop.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * UserSetting
 */
public class UserSetting {

    private Integer customerId;
    private String sessionId;
    private List<String> recentItems;
    private List<CartItem> cart;

    public UserSetting() {
    }

    public UserSetting(Integer id) {
        this.setCustomerId(id);
        this.setCart(new ArrayList<CartItem>());
        this.setRecentItems(new ArrayList<String>());
    }

    public UserSetting(String id) {
        this.setSessionId(id);
        this.setCart(new ArrayList<CartItem>());
        this.setRecentItems(new ArrayList<String>());
    }

    /**
     * @return List<CartItem> return the cart
     */
    public List<CartItem> getCart() {
        return cart;
    }

    /**
     * @param cart the cart to set
     */
    public void setCart(List<CartItem> cart) {
        this.cart = cart;
    }

    /**
     * @return Integer return the customerId
     */
    public Integer getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }


    /**
     * @return String return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    /**
     * @return List<String> return the recentItems
     */
    public List<String> getRecentItems() {
        return recentItems;
    }

    /**
     * @param recentItems the recentItems to set
     */
    public void setRecentItems(List<String> recentItems) {
        this.recentItems = recentItems;
    }

}