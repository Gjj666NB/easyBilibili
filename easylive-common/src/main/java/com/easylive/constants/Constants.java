package com.easylive.constants;

/**
 * @projectName: easylive
 * @author: Li
 * @description:
 */

public class Constants {

    public static final String VIDEO_CODE_HEVC = "hevc";

    public static final String VIDEO_CODE_TEMP_FILE_SUFFIX = "_temp";

    public static final String TS_NAME = "index.ts";

    public static final String M3U8_NAME = "index.m3u8";

    public static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,18}$";

    public static final String REDIS_KEY_PREFIX = "easylive:";

    public static final String FILE_FOLDER = "file/";

    public static final String FILE_FOLDER_TEMP = "temp/";

    public static final String TEMP_VIDEO_NAME = "/temp.mp4";

    public static final String FILE_COVER = "cover/";

    public static final String FILE_VIDEO = "video/";

    public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";

    public static final String REDIS_KEY_UPLOADING_FILE = REDIS_KEY_PREFIX + "uploading:";

    public static final String REDIS_KEY_SYS_SETTING = REDIS_KEY_PREFIX + "sysSetting:";

    public static final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkCode:";

    public static final String REDIS_KEY_TOKEN_WEB = REDIS_KEY_PREFIX + "token:web:";

    public static final String REDIS_KEY_TOKEN_ADMIN = REDIS_KEY_PREFIX + "token:admin:";

    public static final String REDIS_KEY_CATEGORY_LIST = REDIS_KEY_PREFIX + "category:list";
    // 删除文件的结合
    public static final String REDIS_KEY_FILE_DEL = REDIS_KEY_PREFIX + "file:list:del:";

    public static final String REDIS_KEY_QUEUE_TRANSFER = REDIS_KEY_PREFIX + "queue:transfer:";

    public static final String REDIS_KEY_QUEUE_VIDEO_PLAY = REDIS_KEY_PREFIX + "queue:video:play:";

    public static final String REDIS_KEY_VIDEO_SEARCH_COUNT = REDIS_KEY_PREFIX + "video:search:";

    public static final String REDIS_KEY_VIDEO_PLAY_COUNT = REDIS_KEY_PREFIX + "video:playcount:";


    public static final String WEB_TOKEN = "webToken";

    public static final String ADMIN_TOKEN = "adminToken";

    public static final Integer REDIS_KEY_EXPIRE_ONE_MINUTE = 60000;

    public static final Integer REDIS_KEY_EXPIRE_ONE_SECOND = 1000;

    public static final Integer REDIS_KEY_EXPIRE_ONE_DAY = REDIS_KEY_EXPIRE_ONE_MINUTE * 60 * 24;

    public static final Integer TIME_SECONDS_DAY = REDIS_KEY_EXPIRE_ONE_DAY / 1000;

    public static final Long MB_SIZE = 1024 * 1024L;

    public static final Integer HOUR_24 = 24;

    public static final Integer UPDATE_USER_NICK_NAME_COIN = 5;

    public static final Integer LENGTH_20 = 20;

    public static final Integer LENGTH_30 = 30;

    public static final Integer LENGTH_10 = 10;

    public static final Integer ZERO = 0;

    public static final Integer ONE = 1;

    public static final Integer TWO = 2;

    // 视频在线
    //"easylive:video:play:online:"
    public static final String REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREFIX = REDIS_KEY_PREFIX + "video:play:online:";
    //"easylive:video:play:online:count:"
    public static final String REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE = REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREFIX + "count:%s";

    public static final String REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX = "user:";
    //"easylive:video:play:online:user:"
    public static final String REDIS_KEY_VIDEO_PLAY_COUNT_USER = REDIS_KEY_VIDEO_PLAY_COUNT_ONLINE_PREFIX + REDIS_KEY_VIDEO_PLAY_COUNT_USER_PREFIX + "%s:%s";


}
