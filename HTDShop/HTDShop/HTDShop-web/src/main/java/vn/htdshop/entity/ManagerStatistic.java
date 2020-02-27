package vn.htdshop.entity;

/**
 * ManagerStatistic
 */
public class ManagerStatistic {

    private Integer orderToday = null;
    private Integer orderThisMonth = null;
    private Integer orderThisYear = null;
    private Double incomeToday = null;
    private Double incomeThisMonth = null;
    private Double incomeThisYear = null;
    private String productThisMonth = null;
    private String productImageThisMonth = null;
    private String manuThisMonth = null;
    private String categoryThisMonth = null;

    public ManagerStatistic() {
    }

    /**
     * @return Integer return the orderToday
     */
    public Integer getOrderToday() {
        return orderToday;
    }

    /**
     * @param orderToday the orderToday to set
     */
    public void setOrderToday(Integer orderToday) {
        this.orderToday = orderToday;
    }

    /**
     * @return Integer return the orderThisMonth
     */
    public Integer getOrderThisMonth() {
        return orderThisMonth;
    }

    /**
     * @param orderThisMonth the orderThisMonth to set
     */
    public void setOrderThisMonth(Integer orderThisMonth) {
        this.orderThisMonth = orderThisMonth;
    }

    /**
     * @return Integer return the orderThisYear
     */
    public Integer getOrderThisYear() {
        return orderThisYear;
    }

    /**
     * @param orderThisYear the orderThisYear to set
     */
    public void setOrderThisYear(Integer orderThisYear) {
        this.orderThisYear = orderThisYear;
    }

    /**
     * @return Double return the incomeToday
     */
    public Double getIncomeToday() {
        return incomeToday;
    }

    /**
     * @param incomeToday the incomeToday to set
     */
    public void setIncomeToday(Double incomeToday) {
        this.incomeToday = incomeToday;
    }

    /**
     * @return Double return the incomeThisMonth
     */
    public Double getIncomeThisMonth() {
        return incomeThisMonth;
    }

    /**
     * @param incomeThisMonth the incomeThisMonth to set
     */
    public void setIncomeThisMonth(Double incomeThisMonth) {
        this.incomeThisMonth = incomeThisMonth;
    }

    /**
     * @return Double return the incomeThisYear
     */
    public Double getIncomeThisYear() {
        return incomeThisYear;
    }

    /**
     * @param incomeThisYear the incomeThisYear to set
     */
    public void setIncomeThisYear(Double incomeThisYear) {
        this.incomeThisYear = incomeThisYear;
    }

    /**
     * @return String return the productThisMonth
     */
    public String getProductThisMonth() {
        return productThisMonth;
    }

    /**
     * @param productThisMonth the productThisMonth to set
     */
    public void setProductThisMonth(String productThisMonth) {
        this.productThisMonth = productThisMonth;
    }

    /**
     * @return String return the productImageThisMonth
     */
    public String getProductImageThisMonth() {
        return productImageThisMonth;
    }

    /**
     * @param productImageThisMonth the productImageThisMonth to set
     */
    public void setProductImageThisMonth(String productImageThisMonth) {
        this.productImageThisMonth = productImageThisMonth;
    }

    /**
     * @return String return the manuThisMonth
     */
    public String getManuThisMonth() {
        return manuThisMonth;
    }

    /**
     * @param manuThisMonth the manuThisMonth to set
     */
    public void setManuThisMonth(String manuThisMonth) {
        this.manuThisMonth = manuThisMonth;
    }

    /**
     * @return String return the categoryThisMonth
     */
    public String getCategoryThisMonth() {
        return categoryThisMonth;
    }

    /**
     * @param categoryThisMonth the categoryThisMonth to set
     */
    public void setCategoryThisMonth(String categoryThisMonth) {
        this.categoryThisMonth = categoryThisMonth;
    }

}