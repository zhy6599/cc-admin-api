package cc.admin.modules.generate.model;

import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-22 11:24
 */
@Data
public class DictTable {

	private String dicTable = "";
	private String dicCode = "";
	private String dicText = "";

	public DictTable(String dicTable, String dicCode, String dicText) {
		this.dicTable = dicTable;
		this.dicCode = dicCode;
		this.dicText = dicText;
	}
}
