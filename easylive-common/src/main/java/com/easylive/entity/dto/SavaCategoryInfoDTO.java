package com.easylive.entity.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * 分类信息传输对象 (DTO)
 * 用于新增/修改/查询分类时的数据传递
 */
@Data
public class SavaCategoryInfoDTO {

    /**
     * 父级分类ID，不能为空
     * 0 表示根分类
     */
    @NotNull(message = "父级分类ID不能为空")
    private Integer pCategoryId;

    /**
     * 分类ID
     * 新增时可以不传，更新时必传
     */
    private Integer categoryId;

    /**
     * 分类编码，不能为空
     */
    @NotEmpty(message = "分类编码不能为空")
    private String categoryCode;

    /**
     * 分类名称，不能为空
     */
    @NotEmpty(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 图标路径，可为空
     */
    private String icon;

    /**
     * 背景图路径，可为空
     */
    private String background;

    public Integer getpCategoryId() {
        return pCategoryId;
    }

    public void setpCategoryId(Integer pCategoryId) {
        this.pCategoryId = pCategoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}