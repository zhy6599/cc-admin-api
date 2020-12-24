package cc.admin.modules.generate.model;

import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-09 14:14
 */
@Data
public class GenerateForm {
	private String id;
	private String moduleName;
	private String className;
	private String packageName;
	private String jsPath;
}
