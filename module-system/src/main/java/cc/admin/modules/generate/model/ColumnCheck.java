package cc.admin.modules.generate.model;

import lombok.Data;
/**
 * 字段校验
 * @author: ZhangHouYing
 * @date: 2020-09-08 9:20
 */
@Data
public class ColumnCheck {

	private String id;
	private String name;
	private String colLink ="";
	private String rule ="";
	private String mustInput ="0";
	private String dynamicDic ="0";
	private String sysDicCode ="";
	private String sysDicName ="";
	private String dicTable ="";
	private String dicCode ="";
	private String dicText ="";

	public ColumnCheck() {
	}

	public ColumnCheck(ColumnSchema columnSchema) {
		this.id = columnSchema.getId();
		this.name = columnSchema.getName();
	}
}
