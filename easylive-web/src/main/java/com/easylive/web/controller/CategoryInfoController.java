package com.easylive.web.controller;

import com.easylive.entity.vo.ResponseVO;
import com.easylive.service.CategoryInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 分类信息表 Controller
 */
@RestController("categoryInfoController")
@RequestMapping("/categoryInfo")
public class CategoryInfoController extends ABaseController{

	@Resource
	private CategoryInfoService categoryInfoService;


	@RequestMapping("/loadAllCategoryInfo")
	public ResponseVO loadAllCategoryInfo() {
		return getSuccessResponseVO(categoryInfoService.getAllCategoryInfo());
	}
}