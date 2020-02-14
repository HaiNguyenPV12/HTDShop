package vn.htdshop.entity;

import java.util.Map;

/**
 * PreBuiltSearch
 */
public class PreBuiltSearch {

    private String keyword;
    private Integer page;
    private Integer cpu;
    private Integer motherboard;
    private Integer gpu;
    private Integer memory;
    private Integer psu;
    private Integer storage;
    private Integer cpucooler;
    private Integer case1;
    private Integer monitor;
    private String style;
    private Double from;
    private Double to;
    private String sort;

    public PreBuiltSearch() {

    }

    public PreBuiltSearch (Map<String, String> params) {
        // Keyword
        this.keyword = params.get("keyword");
        // Style
        this.style = params.get("style");
        // Sort
        this.sort = params.get("sort");
        // CPU
        if (params.get("1") == null) {
            this.cpu = 0;
        } else {
            try {
                this.cpu = Integer.parseInt(params.get("1"));
            } catch (Exception e) {
                this.cpu = 0;
            }
        }
        // Motherboard
        if (params.get("2") == null) {
            this.motherboard = 0;
        } else {
            try {
                this.motherboard = Integer.parseInt(params.get("2"));
            } catch (Exception e) {
                this.motherboard = 0;
            }
        }
        // GPU
        if (params.get("3") == null) {
            this.gpu = 0;
        } else {
            try {
                this.gpu = Integer.parseInt(params.get("3"));
            } catch (Exception e) {
                this.gpu = 0;
            }
        }
        // Memory
        if (params.get("4") == null) {
            this.memory = 0;
        } else {
            try {
                this.memory = Integer.parseInt(params.get("4"));
            } catch (Exception e) {
                this.memory = 0;
            }
        }
        // PSU
        if (params.get("5") == null) {
            this.psu = 0;
        } else {
            try {
                this.psu = Integer.parseInt(params.get("5"));
            } catch (Exception e) {
                this.psu = 0;
            }
        }
        // Storage
        if (params.get("6") == null) {
            this.storage = 0;
        } else {
            try {
                this.storage = Integer.parseInt(params.get("6"));
            } catch (Exception e) {
                this.storage = 0;
            }
        }
        // CPU Cooler
        if (params.get("7") == null) {
            this.cpucooler = 0;
        } else {
            try {
                this.cpucooler = Integer.parseInt(params.get("7"));
            } catch (Exception e) {
                this.cpucooler = 0;
            }
        }
        // Case
        if (params.get("8") == null) {
            this.case1 = 0;
        } else {
            try {
                this.case1 = Integer.parseInt(params.get("8"));
            } catch (Exception e) {
                this.case1 = 0;
            }
        }
        // Monitor
        if (params.get("9") == null) {
            this.monitor = 0;
        } else {
            try {
                this.monitor = Integer.parseInt(params.get("9"));
            } catch (Exception e) {
                this.monitor = 0;
            }
        }

        // Page
        if (params.get("page") == null) {
            this.page = 1;
        } else {
            try {
                this.page = Integer.parseInt(params.get("page"));
            } catch (Exception e) {
                this.page = 1;
            }
        }
        // From and To
        if (params.get("from") == null) {
            this.from = 0d;
        } else {
            try {
                this.from = Double.parseDouble(params.get("from"));
            } catch (Exception e) {
                this.from = 0d;
            }
        }
        if (params.get("to") == null) {
            this.to = 10000d;
        } else {
            try {
                this.to = Double.parseDouble(params.get("to"));
            } catch (Exception e) {
                this.to = 0d;
            }
        }

    }

    /**
     * @return String return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return Integer return the page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return Integer return the cpu
     */
    public Integer getCpu() {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    /**
     * @return Integer return the motherboard
     */
    public Integer getMotherboard() {
        return motherboard;
    }

    /**
     * @param motherboard the motherboard to set
     */
    public void setMotherboard(Integer motherboard) {
        this.motherboard = motherboard;
    }

    /**
     * @return Integer return the gpu
     */
    public Integer getGpu() {
        return gpu;
    }

    /**
     * @param gpu the gpu to set
     */
    public void setGpu(Integer gpu) {
        this.gpu = gpu;
    }

    /**
     * @return Integer return the memory
     */
    public Integer getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    /**
     * @return Integer return the psu
     */
    public Integer getPsu() {
        return psu;
    }

    /**
     * @param psu the psu to set
     */
    public void setPsu(Integer psu) {
        this.psu = psu;
    }

    /**
     * @return Integer return the storage
     */
    public Integer getStorage() {
        return storage;
    }

    /**
     * @param storage the storage to set
     */
    public void setStorage(Integer storage) {
        this.storage = storage;
    }

    /**
     * @return Integer return the cpucooler
     */
    public Integer getCpucooler() {
        return cpucooler;
    }

    /**
     * @param cpucooler the cpucooler to set
     */
    public void setCpucooler(Integer cpucooler) {
        this.cpucooler = cpucooler;
    }

    /**
     * @return Integer return the case1
     */
    public Integer getCase1() {
        return case1;
    }

    /**
     * @param case1 the case1 to set
     */
    public void setCase1(Integer case1) {
        this.case1 = case1;
    }

    /**
     * @return Integer return the monitor
     */
    public Integer getMonitor() {
        return monitor;
    }

    /**
     * @param monitor the monitor to set
     */
    public void setMonitor(Integer monitor) {
        this.monitor = monitor;
    }

    /**
     * @return String return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @return Double return the from
     */
    public Double getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Double from) {
        this.from = from;
    }

    /**
     * @return Double return the to
     */
    public Double getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Double to) {
        this.to = to;
    }

    /**
     * @return String return the sort
     */
    public String getSort() {
        return sort;
    }

    /**
     * @param sort the sort to set
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

}