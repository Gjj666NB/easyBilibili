package com.easylive.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.easylive.entity.dto.CountInfoDto;
import com.easylive.entity.dto.SysSettingDto;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.dto.UserCountInfoDto;
import com.easylive.entity.po.UserFocus;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserFocusQuery;
import com.easylive.entity.query.VideoInfoQuery;
import com.easylive.enums.ResponseEnum;
import com.easylive.enums.UserStatusEnum;
import com.easylive.component.RedisComponent;
import com.easylive.constants.Constants;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserFocusMapper;
import com.easylive.mappers.VideoInfoMapper;
import com.easylive.utils.CopyTools;
import org.springframework.stereotype.Service;

import com.easylive.enums.PageSize;
import com.easylive.entity.query.UserInfoQuery;
import com.easylive.entity.po.UserInfo;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.query.SimplePage;
import com.easylive.mappers.UserInfoMapper;
import com.easylive.service.UserInfoService;
import com.easylive.utils.StringTools;


/**
 * 用户信息 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private UserFocusMapper<UserFocus, UserFocusQuery> userFocusMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
	 * 根据NickName获取对象
	 */
	@Override
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName修改
	 */
	@Override
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return this.userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

    @Override
    public void register(String email, String nickName, String password) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		//校验邮箱是否重复
		if (userInfo!= null){
			throw new RuntimeException("邮箱已存在");
		}
		userInfo = userInfoMapper.selectByNickName(nickName);
		//校验昵称是否重复
		if (userInfo!= null){
			throw new RuntimeException("昵称已存在");
		}
		//传入用户的数据
		userInfo = new UserInfo();
		String userId = StringTools.getRandomNumbers(Constants.LENGTH_10);
		userInfo.setUserId(userId);
		userInfo.setEmail(email);
		userInfo.setNickName(nickName);
		userInfo.setPassword(StringTools.passwordMD5(password));
		userInfo.setJoinTime(new Date());
		userInfo.setStatus(UserStatusEnum.ENABLE.getCode());
		userInfo.setTheme(Constants.ONE);

		SysSettingDto sysSetting = redisComponent.getSysSetting();
		userInfo.setTotalCoinCount(sysSetting.getRegisterCoinCount());
		userInfo.setCurrentCoinCount(sysSetting.getRegisterCoinCount());
		userInfoMapper.insert(userInfo);
    }

	@Override
	public TokenUserInfoDto login(String email, String password, String ip) {
		//校验账号密码
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo == null || !password.equals(userInfo.getPassword())) {
			throw new BusinessException("用户名或密码错误");
		}
		if (UserStatusEnum.DISABLE.getCode().equals(userInfo.getStatus())){
			throw new BusinessException("用户被禁用");
		}

		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setLastLoginTime(new Date());
		updateUserInfo.setLastLoginIp(ip);
		userInfoMapper.updateByUserId(updateUserInfo, userInfo.getUserId());
		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
		redisComponent.saveTokenInfo4Web(tokenUserInfoDto);
		return tokenUserInfoDto;
	}

    @Override
    public UserInfo getUserInfoDetail(String currentUserId, String userId) {
		UserInfo userInfo = userInfoMapper.selectByUserId(userId);
		if (userInfo == null){
			throw new BusinessException(ResponseEnum.CODE_600);
		}
		CountInfoDto countInfo = videoInfoMapper.selectSumCountInfo(userId);
		userInfo.setPlayCount(countInfo.getPlayCount());
		userInfo.setLikeCount(countInfo.getLikeCount());
		Integer fansCount = userFocusMapper.selectFansCount(userId);
		Integer focusCount = userFocusMapper.selectFocusCount(userId);
		userInfo.setFansCount(fansCount);
		userInfo.setFocusCount(focusCount);

		if (countInfo == null){
			userInfo.setHaveFocus(false);
		}else {
			UserFocus userFocus = userFocusMapper.selectByUserIdAndFocusUserId(currentUserId, userId);
			userInfo.setHaveFocus(userFocus != null);
		}


		return userInfo;
    }

	@Override
	public void updateUserInfo(UserInfo userInfo, TokenUserInfoDto userInfoDto) {
		//校验硬币够不够改名
		UserInfo dbInfo = userInfoMapper.selectByUserId(userInfoDto.getUserId());
		if (!dbInfo.getNickName().equals(userInfo.getNickName()) &&dbInfo.getCurrentCoinCount()<Constants.UPDATE_USER_NICK_NAME_COIN) {
			throw new BusinessException("硬币不足，无法修改昵称");
		}

		if (!dbInfo.getNickName().equals(userInfo.getNickName())){
			Integer count = userInfoMapper.updateCoinCount(dbInfo.getUserId(), -Constants.UPDATE_USER_NICK_NAME_COIN);
			if (count == 0){
				throw new BusinessException("硬币不足，无法修改昵称");
			}
		}
			userInfoMapper.updateByUserId(userInfo, userInfoDto.getUserId());

			boolean updateTokenInfo = false;

			if (!userInfo.getNickName().equals(userInfoDto.getNickName())){
				userInfoDto.setNickName(userInfo.getNickName());
				updateTokenInfo = true;
			}

			if (!userInfo.getAvatar().equals(userInfoDto.getAvatar())){
				userInfoDto.setAvatar(userInfo.getAvatar());
				updateTokenInfo = true;
			}

			if (updateTokenInfo) {
				redisComponent.updateTokenInfo(userInfoDto);
			}
	}

	@Override
	public UserCountInfoDto getCountInfo(String userId) {
		UserCountInfoDto userCountInfoDto = new UserCountInfoDto();
		UserInfo userInfo = userInfoMapper.selectByUserId(userId);
		Integer focusCount = userFocusMapper.selectFocusCount(userId);
		Integer fansCount = userFocusMapper.selectFansCount(userId);
		userCountInfoDto.setFocusCount(focusCount);
		userCountInfoDto.setFansCount(fansCount);
		userCountInfoDto.setCurrentCoinCount(userInfo.getCurrentCoinCount());
		return userCountInfoDto;
	}
}