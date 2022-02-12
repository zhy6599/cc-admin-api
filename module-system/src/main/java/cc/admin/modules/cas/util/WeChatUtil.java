package cc.admin.modules.cas.util;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;
/**
 * 微信小程序工具类
 *
 * @author zhuhuix
 * @date 2019-12-25
 */
@Slf4j
public class WeChatUtil {

	public static String httpRequest(String requestUrl, String requestMethod, String output) {
		try {
			URL url = new URL(requestUrl);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod(requestMethod);
			if (null != output) {
				OutputStream outputStream = connection.getOutputStream();
				outputStream.write(output.getBytes(StandardCharsets.UTF_8));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str;
			StringBuilder buffer = new StringBuilder();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			connection.disconnect();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String decryptData(String encryptDataB64, String sessionKeyB64, String ivB64) {
		log.info("encryptDataB64:" + encryptDataB64);
		log.info("sessionKeyB64:" + sessionKeyB64);
		log.info("ivB64:" + ivB64);
		return new String(
				decryptOfDiyIv(
						Base64.decode(encryptDataB64),
						Base64.decode(sessionKeyB64),
						Base64.decode(ivB64)
				)
		);
	}

	private static final String KEY_ALGORITHM = "AES";
	private static final String ALGORITHM_STR = "AES/CBC/PKCS7Padding";
	private static Key key;
	private static Cipher cipher;

	private static void init(byte[] keyBytes) {
		// 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
		int base = 16;
		if (keyBytes.length % base != 0) {
			int groups = keyBytes.length / base + 1;
			byte[] temp = new byte[groups * base];
			Arrays.fill(temp, (byte) 0);
			System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
			keyBytes = temp;
		}
		// 初始化
		Security.addProvider(new BouncyCastleProvider());
		// 转化成JAVA的密钥格式
		key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
		try {
			// 初始化cipher
			cipher = Cipher.getInstance(ALGORITHM_STR, "BC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解密方法
	 *
	 * @param encryptedData 要解密的字符串
	 * @param keyBytes      解密密钥
	 * @param ivs           自定义对称解密算法初始向量 iv
	 * @return 解密后的字节数组
	 */
	private static byte[] decryptOfDiyIv(byte[] encryptedData, byte[] keyBytes, byte[] ivs) {
		byte[] encryptedText = null;
		init(keyBytes);
		try {
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivs));
			encryptedText = cipher.doFinal(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedText;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url  发送请求的 URL
	 * @param json 请求参数，请求参数应该是 json 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String httpPost(String url, JSONObject json) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(json);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result = result.concat(line);
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		//使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
