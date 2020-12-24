/**
 * Copyright 2013-2015 JEECG (jeecgos@163.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.admin.poi.excel.view;

import cc.admin.poi.excel.def.TemplateWordConstants;
import cc.admin.poi.word.WordExportUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Word模板导出
 *
 * @author JEECG
 * @date 2014年6月30日 下午9:15:49
 */
@SuppressWarnings("unchecked")
@Controller(TemplateWordConstants.DSTC_TEMPLATE_WORD_VIEW)
public class PoiTemplateWordView extends AbstractView {

	private static final String CONTENT_TYPE = "application/msword";

	public PoiTemplateWordView() {
		setContentType(CONTENT_TYPE);
	}

	public boolean isIE(HttpServletRequest request) {
		return (request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 || request.getHeader("USER-AGENT").toLowerCase().indexOf("rv:11.0") > 0) ? true : false;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String codedFileName = "临时文件.docx";
		if (model.containsKey(TemplateWordConstants.FILE_NAME)) {
			codedFileName = (String) model.get(TemplateWordConstants.FILE_NAME) + ".docx";
		}
		if (isIE(request)) {
			codedFileName = java.net.URLEncoder.encode(codedFileName, "UTF8");
		} else {
			codedFileName = new String(codedFileName.getBytes("UTF-8"), "ISO-8859-1");
		}
		response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
		XWPFDocument document = WordExportUtil.exportWord07((String) model.get(TemplateWordConstants.URL), (Map<String, Object>) model.get(TemplateWordConstants.MAP_DATA));
		ServletOutputStream out = response.getOutputStream();
		document.write(out);
		out.flush();
	}
}
