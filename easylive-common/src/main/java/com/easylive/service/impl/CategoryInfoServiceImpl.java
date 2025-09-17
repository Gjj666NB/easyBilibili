package com.easylive.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import com.easylive.component.RedisComponent;
import com.easylive.constants.Constants;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.enums.PageSize;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.VideoInfoMapper;
import org.springframework.stereotype.Service;


import com.easylive.entity.query.CategoryInfoQuery;
import com.easylive.entity.po.CategoryInfo;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.query.SimplePage;
import com.easylive.mappers.CategoryInfoMapper;
import com.easylive.service.CategoryInfoService;
import com.easylive.utils.StringTools;


/**
 * 分类信息表 业务接口实现
 */
@Service("categoryInfoService")
public class CategoryInfoServiceImpl implements CategoryInfoService {

	@Resource
	private CategoryInfoMapper<CategoryInfo, CategoryInfoQuery> categoryInfoMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<CategoryInfo> findListByParam(CategoryInfoQuery param) {

		List<CategoryInfo> categoryInfoList = categoryInfoMapper.selectList(param);

		if (param.getConvert2Tree() != null && param.getConvert2Tree()){
			categoryInfoList = convert2Tree(categoryInfoList, Constants.ZERO);
		}
		return categoryInfoList;
	}

	private List<CategoryInfo> convert2Tree(List<CategoryInfo> dataList, Integer pCategoryId) {
		List<CategoryInfo> children = new ArrayList<>();
		for (CategoryInfo data : dataList){
			if(data.getCategoryId()!=null && data.getpCategoryId()!=null && data.getpCategoryId().equals(pCategoryId)){
				data.setChildren(convert2Tree(dataList, data.getCategoryId()));
				children.add(data);
			}
		}
		return children;
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(CategoryInfoQuery param) {
		return this.categoryInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<CategoryInfo> list = this.findListByParam(param);
		PaginationResultVO<CategoryInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(CategoryInfo bean) {
		return this.categoryInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<CategoryInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.categoryInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<CategoryInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.categoryInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(CategoryInfo bean, CategoryInfoQuery param) {
		StringTools.checkParam(param);
		return this.categoryInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(CategoryInfoQuery param) {
		StringTools.checkParam(param);
		return this.categoryInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据CategoryId获取对象
	 */
	@Override
	public CategoryInfo getCategoryInfoByCategoryId(Integer categoryId) {
		return this.categoryInfoMapper.selectByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryId修改
	 */
	@Override
	public Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId) {
		return this.categoryInfoMapper.updateByCategoryId(bean, categoryId);
	}

	/**
	 * 根据CategoryId删除
	 */
	@Override
	public void deleteCategoryInfoByCategoryId(Integer categoryId) {
		//判断分类下面有没有视频
		VideoInfoQuery videoInfoQuery = new VideoInfoQuery();
		videoInfoQuery.setCategoryIdOrpCategoryId(categoryId);
		Integer count = videoInfoMapper.selectCount(videoInfoQuery);
		if (count > 0) {
			throw new BusinessException("分类下存在视频，不能删除");
		}

		CategoryInfoQuery categoryInfoQuery = new CategoryInfoQuery();
		categoryInfoQuery.setpCategoryIdOrCategoryId(categoryId);
		categoryInfoMapper.deleteByParam(categoryInfoQuery);

		//刷新缓存
		saveCategoryInfo2Redis();
	}

	/**
	 * 根据CategoryCode获取对象
	 */
	@Override
	public CategoryInfo getCategoryInfoByCategoryCode(String categoryCode) {
		return this.categoryInfoMapper.selectByCategoryCode(categoryCode);
	}

	/**
	 * 根据CategoryCode修改
	 */
	@Override
	public Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode) {
		return this.categoryInfoMapper.updateByCategoryCode(bean, categoryCode);
	}

	/**
	 * 根据CategoryCode删除
	 */
	@Override
	public Integer deleteCategoryInfoByCategoryCode(String categoryCode) {
		return this.categoryInfoMapper.deleteByCategoryCode(categoryCode);
	}

	@Override
	public void saveCategoryInfo(CategoryInfo categoryInfo) {
		//判断新增还是修改
		CategoryInfo dbInfo = categoryInfoMapper.selectByCategoryCode(categoryInfo.getCategoryCode());
		if(categoryInfo.getpCategoryId() == null && dbInfo != null &&
		   categoryInfo.getpCategoryId() != null && dbInfo != null && !dbInfo.getCategoryId().equals(categoryInfo.getCategoryId())){
			throw new RuntimeException("父级分类编码已存在");
		}

		if (categoryInfo.getCategoryId() == null){
			Integer maxSort = categoryInfoMapper.selectMaxSort(categoryInfo.getpCategoryId());
			categoryInfo.setSort(maxSort+1);
			categoryInfoMapper.insert(categoryInfo);
		}else {
			categoryInfoMapper.updateByCategoryId(categoryInfo, categoryInfo.getCategoryId());
		}
		//刷新缓存
		saveCategoryInfo2Redis();
	}

	@Override
	public void changeSort(Integer pCategoryId, String categoryIds) {
		String[] ids = categoryIds.split(",");
		ArrayList<CategoryInfo> categoryInfoList = new ArrayList<>();
		Integer sort = 0;
		for (String id : ids){
			CategoryInfo categoryInfo = new CategoryInfo();
			categoryInfo.setCategoryId(Integer.parseInt(id));
			categoryInfo.setpCategoryId(pCategoryId);
			categoryInfo.setSort(++sort);
			categoryInfoList.add(categoryInfo);
		}
		categoryInfoMapper.updateSortBatch(categoryInfoList);
		//刷新缓存
		saveCategoryInfo2Redis();
	}

	@Override
	public List<CategoryInfo> getAllCategoryInfo() {
		List<CategoryInfo> categoryInfoList = redisComponent.getCategoryInfo();
		if (categoryInfoList == null || categoryInfoList.isEmpty()) { //缓存中没有数据，则从数据库中查询
			saveCategoryInfo2Redis();
		}
		return categoryInfoList;
	}

	private void saveCategoryInfo2Redis() {
		CategoryInfoQuery query = new CategoryInfoQuery();
		query.setOrderBy("sort asc");
		query.setConvert2Tree( true);
		List<CategoryInfo> categoryInfoList = findListByParam(query);
		redisComponent.saveCategoryInfo(categoryInfoList);
	}
}