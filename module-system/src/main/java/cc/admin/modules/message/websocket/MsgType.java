package cc.admin.modules.message.websocket;

/**
 * @author: ZhangHouYing
 * @date: 2021-02-09 12:02
 */
public enum MsgType {
	/** 连接 */
	CONNECT,
	/** 关闭 */
	CLOSE,
	/** 信息 */
	INFO,
	/** 错误 */
	ERROR
}
