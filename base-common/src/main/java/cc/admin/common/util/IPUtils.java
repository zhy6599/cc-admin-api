package cc.admin.common.util;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址
 *
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2019年01月14日
 */
@Slf4j
public class IPUtils {

	/**
	 * 用于IP定位转换
	 */
	public static final String REGION = "内网IP|内网IP";
	/**
	 * win 系统
	 */
	public static final String WIN = "win";

	/**
	 * mac 系统
	 */
	public static final String MAC = "mac";

	private static File file = null;
	private static DbConfig config;

	private static boolean ipLocal = false;

	public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir") + File.separator;

	static {
		/*
		 * 此文件为独享 ，不必关闭
		 */
		String path = "ip2region/ip2region.db";
		String name = "ip2region.db";
		try {
			config = new DbConfig();
			file = inputStreamToFile(new ClassPathResource(path).getInputStream(), name);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * inputStream 转 File
	 */
	static File inputStreamToFile(InputStream ins, String name) throws Exception {
		File file = new File(SYS_TEM_DIR + name);
		if (file.exists()) {
			return file;
		}
		OutputStream os = new FileOutputStream(file);
		int bytesRead;
		int len = 8192;
		byte[] buffer = new byte[len];
		while ((bytesRead = ins.read(buffer, 0, len)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		ins.close();
		return file;
	}

	/**
	 * 常用接口
	 */
	public static class Url {
		// IP归属地查询
		public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";
	}

	/**
	 * 获取IP地址
	 * <p>
	 * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = null;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
			log.error("IPUtils ERROR ", e);
		}
		String comma = ",";
		String localhost = "127.0.0.1";
		if (ip.contains(comma)) {
			ip = ip.split(",")[0];
		}
		if (localhost.equals(ip)) {
			// 获取本机真正的ip地址
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				log.error(e.getMessage(), e);
			}
		}
		return ip;
	}

	public static String getBrowser(HttpServletRequest request) {
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		Browser browser = userAgent.getBrowser();
		return browser.getName();
	}

	public static String getOs(HttpServletRequest request) {
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		OperatingSystem os = userAgent.getOperatingSystem();
		return os.getName();
	}

	/**
	 * 根据ip获取详细地址
	 */
	public static String getHttpCityInfo(String ip) {
		String api = String.format(Url.IP_URL, ip);
		JSONObject object = JSONUtil.parseObj(HttpUtil.get(api));
		return object.get("addr", String.class);
	}

	/**
	 * 根据ip获取详细地址
	 */
	public static String getLocalCityInfo(String ip) {
		try {
			DataBlock dataBlock = new DbSearcher(config, file.getPath())
					.binarySearch(ip);
			String region = dataBlock.getRegion();
			String address = region.replace("0|", "");
			char symbol = '|';
			if (address.charAt(address.length() - 1) == symbol) {
				address = address.substring(0, address.length() - 1);
			}
			return address.equals(REGION) ? "内网IP" : address;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}

}
