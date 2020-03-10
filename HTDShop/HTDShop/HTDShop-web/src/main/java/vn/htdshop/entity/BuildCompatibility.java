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
    private boolean isCoolerCompatible; // CPU vs CPU Cooler
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
        this.isCoolerCompatible = true;
    }

    /**
     * @return the isSocketCompatible
     */
    public boolean isIsSocketCompatible() {
        return isSocketCompatible;
    }

    /**
     * @param isSocketCompatible the isSocketCompatible to set
     */
    public void setIsSocketCompatible(boolean isSocketCompatible) {
        this.isSocketCompatible = isSocketCompatible;
    }

    /**
     * @return the isMemoryTypeCompatible
     */
    public boolean isIsMemoryTypeCompatible() {
        return isMemoryTypeCompatible;
    }

    /**
     * @param isMemoryTypeCompatible the isMemoryTypeCompatible to set
     */
    public void setIsMemoryTypeCompatible(boolean isMemoryTypeCompatible) {
        this.isMemoryTypeCompatible = isMemoryTypeCompatible;
    }

    /**
     * @return the isFormFactorCompatible
     */
    public boolean isIsFormFactorCompatible() {
        return isFormFactorCompatible;
    }

    /**
     * @param isFormFactorCompatible the isFormFactorCompatible to set
     */
    public void setIsFormFactorCompatible(boolean isFormFactorCompatible) {
        this.isFormFactorCompatible = isFormFactorCompatible;
    }

    /**
     * @return the isPSUFormFactorCompatible
     */
    public boolean isIsPSUFormFactorCompatible() {
        return isPSUFormFactorCompatible;
    }

    /**
     * @param isPSUFormFactorCompatible the isPSUFormFactorCompatible to set
     */
    public void setIsPSUFormFactorCompatible(boolean isPSUFormFactorCompatible) {
        this.isPSUFormFactorCompatible = isPSUFormFactorCompatible;
    }

    /**
     * @return the isMemorySlotCompatible
     */
    public boolean isIsMemorySlotCompatible() {
        return isMemorySlotCompatible;
    }

    /**
     * @param isMemorySlotCompatible the isMemorySlotCompatible to set
     */
    public void setIsMemorySlotCompatible(boolean isMemorySlotCompatible) {
        this.isMemorySlotCompatible = isMemorySlotCompatible;
    }

    /**
     * @return the isWattageCompatible
     */
    public boolean isIsWattageCompatible() {
        return isWattageCompatible;
    }

    /**
     * @param isWattageCompatible the isWattageCompatible to set
     */
    public void setIsWattageCompatible(boolean isWattageCompatible) {
        this.isWattageCompatible = isWattageCompatible;
    }

    /**
     * @return the isCoolerCompatible
     */
    public boolean isIsCoolerCompatible() {
        return isCoolerCompatible;
    }

    /**
     * @param isCoolerCompatible the isCoolerCompatible to set
     */
    public void setIsCoolerCompatible(boolean isCoolerCompatible) {
        this.isCoolerCompatible = isCoolerCompatible;
    }
}