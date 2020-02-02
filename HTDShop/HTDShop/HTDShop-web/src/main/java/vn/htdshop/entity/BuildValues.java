package vn.htdshop.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * BuildValues
 */
@Entity
public class BuildValues {

    @Id
    private Integer id;

    private String manufacturer;

    public BuildValues(int id, String manufacturer) {
        this.id = id;
        this.manufacturer = manufacturer;
    }

    public BuildValues() {
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}