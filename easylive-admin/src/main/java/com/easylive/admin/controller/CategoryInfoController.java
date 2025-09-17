package com.easylive.admin.controller;

import com.easylive.entity.po.CategoryInfo;
import com.easylive.entity.query.CategoryInfoQuery;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.CategoryInfoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description: category_info Controller
 * @date: 2025-03-03
 */
@RestController
@RequestMapping("/categoryInfo")
@Validated
public class CategoryInfoController extends ABaseController {

	@Resource
	private CategoryInfoService categoryInfoService;

	@RequestMapping("/loadCategoryInfo")
	public ResponseVO loadCategoryInfo(CategoryInfoQuery query) {
		query.setOrderBy("sort asc");
		query.setConvert2Tree(true);
		List<CategoryInfo> categoryInfoList = categoryInfoService.findListByParam(query);
		return getSuccessResponseVO(categoryInfoList);
	}

	@RequestMapping("/saveCategoryInfo")
	public ResponseVO saveCategoryInfo(@NotNull Integer pCategoryId,
									   Integer categoryId,
									   @NotEmpty String categoryCode,
									   @NotEmpty String categoryName,
									   String icon,
									   String background) {
		CategoryInfo categoryInfo = new CategoryInfo();
		categoryInfo.setCategoryId(categoryId);
		categoryInfo.setpCategoryId(pCategoryId);
		categoryInfo.setCategoryCode(categoryCode);
		categoryInfo.setCategoryName(categoryName);
		categoryInfo.setIcon(icon);
		categoryInfo.setBackground(background);

		categoryInfoService.saveCategoryInfo(categoryInfo);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/delCategoryInfo")
	public ResponseVO delCategoryInfo(@NotNull Integer categoryId) {
		categoryInfoService.deleteCategoryInfoByCategoryId(categoryId);
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/changeSort")
	public ResponseVO changeSort(@NotNull Integer pCategoryId, @NotEmpty String categoryIds) {
		categoryInfoService.changeSort(pCategoryId, categoryIds);
		return getSuccessResponseVO(null);
	}
}