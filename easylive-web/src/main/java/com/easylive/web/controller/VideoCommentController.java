package com.easylive.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.easylive.constants.Constants;
import com.easylive.entity.dto.TokenUserInfoDto;
import com.easylive.entity.po.UserAction;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.UserActionQuery;
import com.easylive.entity.query.VideoCommentQuery;
import com.easylive.entity.po.VideoComment;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.entity.vo.ResponseVO;
import com.easylive.entity.vo.VideoCommentResultVO;
import com.easylive.enums.CommentTopTypeEnum;
import com.easylive.enums.PageSize;
import com.easylive.enums.ResponseEnum;
import com.easylive.enums.UserActionTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.service.UserActionService;
import com.easylive.service.VideoCommentService;
import com.easylive.service.VideoInfoService;
import com.easylive.web.annotation.GlobalInterceptor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 评论 Controller
 */
@RestController("videoCommentController")
@RequestMapping("/interact/comment")
public class VideoCommentController extends ABaseController{

	@Resource
	private VideoCommentService videoCommentService;

    @Resource
    private VideoInfoService videoInfoService ;

    @Resource
    private UserActionService userActionService;

	@RequestMapping("postComment")
	@GlobalInterceptor(checkLogin = true)
//	@MessageInterceptor(messageType = MessageTypeEnum.COMMENT)
	public ResponseVO postComment(@NotEmpty String videoId,
								  @NotEmpty @Size(max = 500) String content,
								  Integer replyCommentId,
								  @Size(max = 500) String imgPath) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		VideoComment videoComment = new VideoComment();
		videoComment.setVideoId(videoId);
		videoComment.setContent(content);
		videoComment.setImgPath(imgPath);
		videoComment.setUserId(tokenUserInfo.getUserId());
		videoComment.setAvatar(tokenUserInfo.getAvatar());
		videoComment.setNickName(tokenUserInfo.getNickName());

		videoCommentService.postComment(videoComment, replyCommentId);
		return getSuccessResponseVO(videoComment);

	}

    @RequestMapping("loadComment")
    public ResponseVO loadComment(@NotEmpty String videoId,
                                  Integer pageNo,
                                  Integer orderType) {
        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        if (videoInfo == null){
            throw new BusinessException(ResponseEnum.CODE_600);
        }

        if (videoInfo.getInteraction() != null && videoInfo.getInteraction().contains(Constants.ZERO.toString())) {
            return getSuccessResponseVO(new ArrayList<>());
        }

        VideoCommentQuery commentQuery = new VideoCommentQuery();
        commentQuery.setVideoId(videoId);
        commentQuery.setPageNo(pageNo);
        commentQuery.setPageSize(PageSize.SIZE15.getSize());
        commentQuery.setpCommentId(Constants.ZERO);
        commentQuery.setLoadChildren(true);
        String orderBy = orderType == null || orderType == 0 ? "v.like_count desc,v.comment_id desc" : "v.comment_id desc";
        commentQuery.setOrderBy(orderBy);

        PaginationResultVO<VideoComment> commentData = videoCommentService.findListByPage(commentQuery);

        if (pageNo == null) {
            List<VideoComment> topCommentList = topComment(videoId);
            if (!topCommentList.isEmpty()) {
                List<VideoComment> commentList = commentData.getList().stream().filter(item -> !item.getCommentId().equals(topCommentList.get(0).getCommentId())).collect(Collectors.toList());
                commentList.addAll(0, topCommentList);
                commentData.setList(commentList);
            }
        }

        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        List<UserAction> userActionList = new ArrayList<>();
        if (tokenUserInfoDto != null) {
            UserActionQuery userActionQuery = new UserActionQuery();
            userActionQuery.setVideoId(videoId);
            userActionQuery.setUserId(tokenUserInfoDto.getUserId());
            userActionQuery.setActionTypeArray(new Integer[]{UserActionTypeEnum.COMMENT_LIKE.getType(), UserActionTypeEnum.COMMENT_HATE.getType()});
            userActionList = userActionService.findListByParam(userActionQuery);
        }
        VideoCommentResultVO videoCommentResultVO = new VideoCommentResultVO();
        videoCommentResultVO.setCommentData(commentData);
        videoCommentResultVO.setUserActionList(userActionList);
        return getSuccessResponseVO(videoCommentResultVO);

    }

    //查询被置顶的评论
    private List<VideoComment> topComment( String videoId) {
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoId(videoId);
        videoCommentQuery.setTopType(CommentTopTypeEnum.TOP.getType());
        videoCommentQuery.setLoadChildren(true);
        return videoCommentService.findListByParam(videoCommentQuery);
    }

    @RequestMapping("top")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO top(@NotNull Integer commentId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        videoCommentService.top(commentId, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("cancelTop")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO cancelTop(@NotNull Integer commentId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        videoCommentService.cancelTop(commentId, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }

    @RequestMapping("deleteComment")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO userDelComment(@NotNull Integer commentId){
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        videoCommentService.delComment(commentId, tokenUserInfoDto.getUserId());
        return getSuccessResponseVO(null);
    }



}