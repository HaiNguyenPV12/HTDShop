package vn.htdshop.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * BuildCompatibility
 */
@Entity
public class BuildCompatibility implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private boolean isSocketCompatible; // CPU vs motherboard
    private boolean isMemoryTypeCompatible; // RAM type vs motherboard
    private boolean isFormFactorCompatible; // motherboard vs case
    private boolean isPSUFormFactorCompatible; // PSU vs case
    private boolean isMemorySlotCompatible; // modules vs motherboard slots
    private boolean isWattageCompatible; // CPU + GPU vs PSU

    public BuildCompatibility() {
        this.isSocketCompatible = true;
        this.isMemorySlotCompatible = true;
        this.isMemoryTypeCompatible = true;
        this.isPSUFormFactorCompatible = true;
        this.isWattageCompatible = true;
        this.isFormFactorCompatible = true;
    }

    public boolean isSocketCompatible() {
        return isSocketCompatible;
    }

    public boolean isWattageCompatible() {
        return isWattageCompatible;
    }

    public void setWattageCompatible(boolean isWattageCompatible) {
        this.isWattageCompatible = isWattageCompatible;
    }

    public boolean isMemorySlotCompatible() {
        return isMemorySlotCompatible;
    }

    public void setMemorySlotCompatible(boolean isMemorySlotCompatible) {
        this.isMemorySlotCompatible = isMemorySlotCompatible;
    }

    public boolean isPSUFormFactorCompatible() {
        return isPSUFormFactorCompatible;
    }

    public void setPSUFormFactorCompatible(boolean isPSUFormFactorCompatible) {
        this.isPSUFormFactorCompatible = isPSUFormFactorCompatible;
    }

    public boolean isFormFactorCompatible() {
        return isFormFactorCompatible;
    }

    public void setFormFactorCompatible(boolean isFormFactorCompatible) {
        this.isFormFactorCompatible = isFormFactorCompatible;
    }

    public boolean isMemoryTypeCompatible() {
        return isMemoryTypeCompatible;
    }

    public void setMemoryTypeCompatible(boolean isMemoryTypeCompatible) {
        this.isMemoryTypeCompatible = isMemoryTypeCompatible;
    }

    public void setSocketCompatible(boolean isSocketCompatible) {
        this.isSocketCompatible = isSocketCompatible;
    }

}