package cc.admin.modules.cas.constant;

/**
 * 常量类
 *
 * @author zhuhuix
 * @date 2020-04-07
 */
public class Constant {

	/**
	 * 定义"openid"的微信常量
	 */
	public static final String OPEN_ID = "openid";
	/**
	 * 定义"session_key"的微信常量
	 */
	public static final String SESSION_KEY = "session_key";
	/**
	 * 定义"unionid"的微信常量
	 */
	public static final String UNION_ID = "unionid";
	/**
	 * 定义"errcode"的微信常量
	 */
	public static final String ERR_CODE = "errcode";
	/**
	 * 定义"errmsg"的微信常量
	 */
	public static final String ERR_MSG = "errmsg";

	/**
	 * 定义"0"的微信常量
	 */
	public static final String CHAR_ZERO = "0";

	/**
	 * 定义"saveUserInfo"的微信常量
	 */
	public static final String SAVE_USER_INFO = "saveUserInfo";

	/**
	 * 定义"saveUserInfo"的微信常量
	 */
	public static final String SAVE_CUSTOMER_INFO = "saveCustomerInfo";

	/**
	 * 定义GB的计算常量
	 */
	public static final int GB = 1024 * 1024 * 1024;
	/**
	 * 定义MB的计算常量
	 */
	public static final int MB = 1024 * 1024;
	/**
	 * 定义KB的计算常量
	 */
	public static final int KB = 1024;

	/**
	 * 定义Redis缓存默认过期时间
	 */
	public static final int CACHE_TIMEOUT_HOUR = 2;

	/**
	 * 定义上传信息缓存队列的名称
	 */
	public static final String REDIS_UPLOAD_QUEUE_NAME = "upload_queue";

	/**
	 * 定义上传信息缓存队列中元素的数量
	 */
	public static final int REDIS_UPLOAD_QUEUE_COUNT = 10;

	/**
	 * 定义请求登录限制计数的时间段区间，需与REQUEST_LOGIN_LIMIT_COUNT同时使用
	 */
	public static final int REQUEST_LOGIN_LIMIT_TIME = 60;

	/**
	 * 定义请求登录在时间段区间的限制次数，需与REQUEST_LOGIN_LIMIT_TIME同时使用
	 */
	public static final int REQUEST_LOGIN_LIMIT_COUNT = 5;

	/**
	 * 定义unknown字串串的常量
	 */
	public static final String UNKNOWN = "unknown";
}
