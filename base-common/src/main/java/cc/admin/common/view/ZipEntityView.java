package cc.admin.common.view;

import cn.hutool.core.util.ZipUtil;
import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
/**
 * @author: ZhangHouYing
 * @date: 2021-02-06 19:09
 */
public class ZipEntityView extends AbstractView {

	private static final String CONTENT_TYPE = "application/octet-stream";

	public ZipEntityView() {
		setContentType(CONTENT_TYPE);
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String srcPath = (String)map.get("srcPath");
		String zipPath = (String)map.get("zipPath");
		String fileName = (String)map.get("fileName");
		Charset charset = Charsets.UTF_8;

		File file = new File(zipPath);
		ZipUtil.zip(srcPath, zipPath, charset, false);
		byte[] fileData = FileUtils.readFileToByteArray(file);
		try {
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", URLEncoder.encode(fileName, "utf-8"));
			response.setHeader(headerKey, headerValue);
			response.setHeader("Content-Length", String.valueOf(fileData.length));
			response.getOutputStream().write(fileData);
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected boolean isIE(HttpServletRequest request) {
		return (request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 || request.getHeader("USER-AGENT").toLowerCase().indexOf("rv:11.0") > 0) ? true : false;
	}

}
