package cc.admin.common.util;

import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 腾讯短信平台
 *
 * @author: ZhangHouYing
 */
@Slf4j
public class SendSmsUtil {

	private final static int APP_ID = 1400497788;
	private final static String APP_KEY = "dddddddddddddddddddddddddddddd";
	private final static String SMS_SIGN = "短信工程";

	private static SmsMultiSenderResult SendSms(String[] phone, int modelId, String[] params) {
		try {
			SmsMultiSender msender = new SmsMultiSender(APP_ID, APP_KEY);
			SmsMultiSenderResult result = msender.sendWithParam("86", phone, modelId, params, SMS_SIGN, "", "");
			return result;
		} catch (HTTPException e) {
			// HTTP 响应码错误
			e.printStackTrace();
		} catch (JSONException e) {
			// JSON 解析错误
			e.printStackTrace();
		} catch (IOException e) {
			// 网络 IO 错误
			e.printStackTrace();
		}
		return null;
	}

	public static boolean sendMsg(String phone, String code) {
		boolean result = false;
		//{"result":0,"errmsg":"OK","ext":"","detail":[{"result":0,"errmsg":"OK","mobile":"xxxxxxxxx","nationcode":"86","isocode":"CN","sid":"2019:7578492220075326171","fee":1}]}
		String[] phoneArray = {phone};
		String[] params = {code};
		SmsMultiSenderResult smsMultiSenderResult = SendSms(phoneArray, 111111, params);
		log.info("短信验证码:{}接口返回的数据:{}", code, smsMultiSenderResult);
		SmsMultiSenderResult.Detail detail = smsMultiSenderResult.details.get(0);
		if (smsMultiSenderResult != null && "OK".equals(detail.errmsg)) {
			result = true;
		}
		return result;
	}
}
