package com.easylive.component;
import com.easylive.config.AppConfig;
import com.easylive.entity.dto.VideoInfoDto;
import com.easylive.entity.po.UserInfo;
import com.easylive.entity.po.VideoInfo;
import com.easylive.entity.query.SimplePage;
import com.easylive.entity.query.UserInfoQuery;
import com.easylive.entity.vo.PaginationResultVO;
import com.easylive.enums.PageSize;
import com.easylive.enums.SearchOrderTypeEnum;
import com.easylive.exception.BusinessException;
import com.easylive.mappers.UserInfoMapper;
import com.easylive.utils.CopyTools;
import com.easylive.utils.JsonUtils;
import com.easylive.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@Component("esSearchComponent")
public class EsSearchComponent {
    @Resource
    private AppConfig appConfig;

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private UserInfoMapper userInfoMapper;

    private Boolean isExistIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(appConfig.getEsIndexVideoName());
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public void createIndex() {
        try {
            if (isExistIndex()) {
                return;
            }
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(appConfig.getEsIndexVideoName());
            createIndexRequest.settings("{\"analysis\": {\n" +
                    "    \"analyzer\": {\n" +
                    "      \"comma\": {\n" +
                    "        \"type\": \"pattern\",\n" +
                    "        \"pattern\": \",\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "}}", XContentType.JSON);

            createIndexRequest.mapping("{\"properties\": {\n" +
                    "    \"videoId\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"userId\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"videoCover\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"videoName\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"ik_max_word\"\n" +
                    "    },\n" +
                    "    \"tags\": {\n" +
                    "      \"type\": \"text\",\n" +
                    "      \"analyzer\": \"comma\"\n" +
                    "    },\n" +
                    "    \"playCount\": {\n" +
                    "      \"type\": \"integer\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"danmuCount\": {\n" +
                    "      \"type\": \"integer\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"collectCount\": {\n" +
                    "      \"type\": \"integer\",\n" +
                    "      \"index\": false\n" +
                    "    },\n" +
                    "    \"createTime\": {\n" +
                    "      \"type\": \"date\",\n" +
                    "      \"format\": \"yyyy-MM-dd HH:mm:ss\",\n" +
                    "      \"index\": false\n" +
                    "    }\n" +
                    "}}", XContentType.JSON);
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            Boolean acknowledged = createIndexResponse.isAcknowledged();
            if (!acknowledged) {
                throw new BusinessException("初始化es索引失败");
            }
        } catch (Exception e) {
            log.error("初始化es索引失败", e);
            throw new BusinessException("初始化es索引失败");
        }
    }

    public void saveDoc(VideoInfo videoInfo){
        try {
            if (isExistDoc(videoInfo.getVideoId())){
                UpdateDoc(videoInfo);
            }else {
                VideoInfoDto videoInfoDto = CopyTools.copy(videoInfo, VideoInfoDto.class);
                videoInfoDto.setCollectCount(0);
                videoInfoDto.setDanmuCount(0);
                videoInfoDto.setPlayCount(0);
                IndexRequest indexRequest = new IndexRequest(appConfig.getEsIndexVideoName());
                indexRequest.id(videoInfo.getVideoId()).source(JsonUtils.convertObj2Json(videoInfoDto), XContentType.JSON);
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            }
        } catch (Exception e) {
            log.error("保持视频信息到ES失败", e);
            throw new BusinessException("保持视频信息到ES失败");
        }
    }

    private void UpdateDoc(VideoInfo videoInfo) {
     try {
        videoInfo.setCreateTime(null);
        videoInfo.setLastUpdateTime(null);
        VideoInfoDto videoInfoDto = CopyTools.copy(videoInfo, VideoInfoDto.class);
        Map<String, Object> dataMap = new HashMap<>();
        Field[] fields = videoInfoDto.getClass().getDeclaredFields();
        for (Field field : fields) {
            String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
            Method method = videoInfoDto.getClass().getMethod(methodName);
            Object value = method.invoke(videoInfoDto);
            if (value != null && value instanceof String && !StringTools.isEmpty((String) value) || value != null && !(value instanceof String)) {
                dataMap.put(field.getName(), value);
            }
        }
        if (dataMap.isEmpty()) {
            return;
        }
        UpdateRequest updateRequest = new UpdateRequest(appConfig.getEsIndexVideoName(), videoInfoDto.getVideoId());
        updateRequest.doc(dataMap);
        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    } catch (Exception e) {
        log.error("更新视频信息到es失败", e);
        throw new BusinessException("更新视频信息到es失败");
    }
}

    private boolean isExistDoc(String videoId) throws IOException{
        GetRequest getRequest = new GetRequest(appConfig.getEsIndexVideoName(), videoId);
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.isExists();
    }


    public void deleteDoc(String videoId) {
        DeleteRequest deleteRequest = new DeleteRequest(appConfig.getEsIndexVideoName(), videoId);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es删除视频信息失败", e);
            throw new BusinessException("es删除视频信息失败");
        }
    }


    public PaginationResultVO<VideoInfo> search(Boolean highLight, String keyword, Integer orderType, Integer pageNo, Integer pageSize) {
        try {
            SearchOrderTypeEnum searchOrderTypeEnum = SearchOrderTypeEnum.getByType(orderType);

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "videoName", "tags"));
            //高亮
            if (highLight) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field("videoName");
                highlightBuilder.preTags("<span class='highlight'>");
                highlightBuilder.postTags("</span>");
                searchSourceBuilder.highlighter(highlightBuilder);
            }
            //排序
            searchSourceBuilder.sort("_score", SortOrder.ASC);
            if (searchOrderTypeEnum != null) {
                searchSourceBuilder.sort(searchOrderTypeEnum.getField(), SortOrder.DESC);
            }
            pageNo = pageNo == null ? 1 : pageNo;
            pageSize = pageSize == null ? PageSize.SIZE20.getSize() : pageSize;
            searchSourceBuilder.size(pageSize);
            searchSourceBuilder.from((pageNo - 1) * pageSize);

            SearchRequest searchRequest = new SearchRequest(appConfig.getEsIndexVideoName());
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            Integer totalCount = (int) hits.getTotalHits().value;

            List<VideoInfo> videoInfoList = new ArrayList<>();
            List<String> userIdList = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                VideoInfo videoInfo = JsonUtils.convertJson2Obj(hit.getSourceAsString(), VideoInfo.class);
                if (hit.getHighlightFields().get("videoName") != null) {
                    videoInfo.setVideoName(hit.getHighlightFields().get("videoName").getFragments()[0].toString());
                }
                videoInfoList.add(videoInfo);
                userIdList.add(videoInfo.getUserId());
            }

            UserInfoQuery userInfoQuery = new UserInfoQuery();
            userInfoQuery.setUserIdList(userIdList);
            List<UserInfo> userInfoList = userInfoMapper.selectList(userInfoQuery);
            Map<String, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(item -> item.getUserId(), Function.identity(), (data1, data2) -> data2));

            for (VideoInfo videoInfo : videoInfoList) {
                UserInfo userInfo = userInfoMap.get(videoInfo.getUserId());
                videoInfo.setNickName(userInfo == null ? "" : userInfo.getNickName());
            }

            SimplePage page = new SimplePage(pageNo, totalCount, pageSize);
            PaginationResultVO<VideoInfo> paginationResultVo = new PaginationResultVO<>(totalCount, page.getPageSize(), page.getPageNo(),  page.getPageTotal(),videoInfoList);
            return paginationResultVo;
        } catch (Exception e) {
            log.error("es搜索视频信息失败", e);
            throw new BusinessException("es搜索视频信息失败");
        }
    }

}
