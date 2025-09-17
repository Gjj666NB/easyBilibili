package com.easylive.entity.query;

import java.util.Date;


/**
 * 分类信息表参数
 */
public class CategoryInfoQuery extends BaseParam {


	/**
	 * 分类ID
	 */
	private Integer categoryId;

	/**
	 * 分类编码
	 */
	private String categoryCode;

	private String categoryCodeFuzzy;

	/**
	 * 分类名称
	 */
	private String categoryName;

	private String categoryNameFuzzy;

	/**
	 * 父级分类ID，0表示根分类
	 */
	private Integer pCategoryId;

	/**
	 * 图标路径
	 */
	private String icon;

	private String iconFuzzy;

	/**
	 * 背景图路径
	 */
	private String background;

	private String backgroundFuzzy;

	/**
	 * 排序字段，数值越小越靠前
	 */
	private Integer sort;

	/**
	 * 创建时间
	 */
	private String createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 更新时间
	 */
	private String updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;


	private Boolean convert2Tree;

	private Integer pCategoryIdOrCategoryId;

	public Integer getpCategoryIdOrCategoryId() {
		return pCategoryIdOrCategoryId;
	}

	public void setpCategoryIdOrCategoryId(Integer pCategoryIdOrCategoryId) {
		this.pCategoryIdOrCategoryId = pCategoryIdOrCategoryId;
	}

	public Boolean getConvert2Tree() {
		return convert2Tree;
	}

	public void setConvert2Tree(Boolean convert2Tree) {
		this.convert2Tree = convert2Tree;
	}


	public void setCategoryId(Integer categoryId){
		this.categoryId = categoryId;
	}

	public Integer getCategoryId(){
		return this.categoryId;
	}

	public void setCategoryCode(String categoryCode){
		this.categoryCode = categoryCode;
	}

	public String getCategoryCode(){
		return this.categoryCode;
	}

	public void setCategoryCodeFuzzy(String categoryCodeFuzzy){
		this.categoryCodeFuzzy = categoryCodeFuzzy;
	}

	public String getCategoryCodeFuzzy(){
		return this.categoryCodeFuzzy;
	}

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return this.categoryName;
	}

	public void setCategoryNameFuzzy(String categoryNameFuzzy){
		this.categoryNameFuzzy = categoryNameFuzzy;
	}

	public String getCategoryNameFuzzy(){
		return this.categoryNameFuzzy;
	}

	public void setpCategoryId(Integer pCategoryId){
		this.pCategoryId = pCategoryId;
	}

	public Integer getpCategoryId(){
		return this.pCategoryId;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}

	public String getIcon(){
		return this.icon;
	}

	public void setIconFuzzy(String iconFuzzy){
		this.iconFuzzy = iconFuzzy;
	}

	public String getIconFuzzy(){
		return this.iconFuzzy;
	}

	public void setBackground(String background){
		this.background = background;
	}

	public String getBackground(){
		return this.background;
	}

	public void setBackgroundFuzzy(String backgroundFuzzy){
		this.backgroundFuzzy = backgroundFuzzy;
	}

	public String getBackgroundFuzzy(){
		return this.backgroundFuzzy;
	}

	public void setSort(Integer sort){
		this.sort = sort;
	}

	public Integer getSort(){
		return this.sort;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}

	public String getCreateTime(){
		return this.createTime;
	}

	public void setCreateTimeStart(String createTimeStart){
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart(){
		return this.createTimeStart;
	}
	public void setCreateTimeEnd(String createTimeEnd){
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd(){
		return this.createTimeEnd;
	}

	public void setUpdateTime(String updateTime){
		this.updateTime = updateTime;
	}

	public String getUpdateTime(){
		return this.updateTime;
	}

	public void setUpdateTimeStart(String updateTimeStart){
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeStart(){
		return this.updateTimeStart;
	}
	public void setUpdateTimeEnd(String updateTimeEnd){
		this.updateTimeEnd = updateTimeEnd;
	}

	public String getUpdateTimeEnd(){
		return this.updateTimeEnd;
	}

}
