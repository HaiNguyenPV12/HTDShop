package vn.htdshop.entity;

import java.util.StringTokenizer;

/**
 * OtherAttribute
 */
public class OtherAttribute {
    private String attribute;
    private String value;

    public OtherAttribute() {
    }

    public OtherAttribute(String others) {
        if (others.isEmpty() || !others.contains(":")) {
            this.attribute = null;
            this.value = null;
        } else {
            this.attribute = others.substring(0, others.indexOf(":"));
            this.value = others.substring(others.indexOf(":") + 1, others.length());
        }
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}