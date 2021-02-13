package cc.admin.modules.mnt.entity;

import cc.admin.modules.message.websocket.MsgType;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2021-02-09 12:10
 */
@Data
public class MntMessage {
	private String message;
	private MsgType msgType;

	public MntMessage(String message,MsgType msgType){
		this.message = message;
		this.msgType = msgType;
	}

	public String toString(){
		return JSONObject.toJSONString(this);
	}
}
