package vn.htdshop.entity;

import java.util.Map;

/**
 * Search
 */
public class Search {

    private Integer category;
    private String keyword;
    private Integer page;
    private String style;
    private Double from;
    private Double to;
    private String sort;

    public Search() {
    }

    public Search(Map<String, String> params) {
        // Keyword
        this.keyword = params.get("keyword");
        if (this.keyword == null) {
            this.keyword = "";
        }
        // Style
        this.style = params.get("style");
        if (this.style == null) {
            this.style = "grid";
        }
        // Sort
        this.sort = params.get("sort");
        if (this.sort == null) {
            this.sort = "default";
        }
        // Category
        if (params.get("category") == null || params.get("category").isEmpty()) {
            this.category = 0;
        } else {
            try {
                this.category = Integer.parseInt(params.get("category"));
            } catch (Exception e) {
                this.category = 0;
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
            this.to = 1000000d;
        } else {
            try {
                this.to = Double.parseDouble(params.get("to"));
            } catch (Exception e) {
                this.to = 0d;
            }
        }

    }

    public Search(Integer category, String keyword, Integer page, String style, Double from, Double to, String sort) {
        this.category = category;
        this.keyword = keyword;
        this.page = page;
        this.style = style;
        this.from = from;
        this.to = to;
        this.sort = sort;
    }

    public Integer getCategory() {
        return this.category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Double getFrom() {
        return this.from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return this.to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Search category(Integer category) {
        this.category = category;
        return this;
    }

    public Search keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public Search page(Integer page) {
        this.page = page;
        return this;
    }

    public Search style(String style) {
        this.style = style;
        return this;
    }

    public Search from(Double from) {
        this.from = from;
        return this;
    }

    public Search to(Double to) {
        this.to = to;
        return this;
    }

    public Search sort(String sort) {
        this.sort = sort;
        return this;
    }
}