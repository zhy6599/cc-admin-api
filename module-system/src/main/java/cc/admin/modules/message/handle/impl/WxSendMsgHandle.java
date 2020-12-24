package cc.admin.modules.message.handle.impl;

import cc.admin.modules.message.handle.ISendMsgHandle;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class WxSendMsgHandle implements ISendMsgHandle {

	@Override
	public void SendMsg(String es_receiver, String es_title, String es_content) {
		log.info("发微信消息模板");
	}

}
