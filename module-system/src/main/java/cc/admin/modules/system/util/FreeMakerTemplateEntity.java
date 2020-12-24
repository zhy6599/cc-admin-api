package cc.admin.modules.system.util;

import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-10 13:23
 */
@Data
public class FreeMakerTemplateEntity {
	private String templateName;
	private String fileName;

	public FreeMakerTemplateEntity(String templateName, String fileName) {
		this.templateName = templateName;
		this.fileName = fileName;
	}
}
