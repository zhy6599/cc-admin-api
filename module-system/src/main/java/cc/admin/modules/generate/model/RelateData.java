package cc.admin.modules.generate.model;

import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-11-08 20:24
 */
@Data
public class RelateData {

	private String id;
	private String name;
	private String mainTable ="";
	private String mainColumn ="";
	private String slaveTable ="";
	private String slaveColumn ="";

	public RelateData() {
	}

	public RelateData(ColumnSchema columnSchema) {
		this.id = columnSchema.getId();
		this.name = columnSchema.getName();
	}
}
