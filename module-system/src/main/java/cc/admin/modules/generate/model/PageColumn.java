package cc.admin.modules.generate.model;

import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-08 9:20
 */
@Data
public class PageColumn {

	private String id;
	private String name;
	private String disForm ="1";
	private String disList ="1";
	private String orderBy ="0";
	private String isReadonly = "0";
	private String cmpType = "text";
	private Integer cmpLength = 255;
	private String isQuery = "0";
	private String isSimple = "0";
	private String queryType = "0";
	private String queryDefault ="";
	private String mainTable ="";
	private String mainColumn ="";

	public PageColumn() {
	}

	public PageColumn(ColumnSchema columnSchema) {
		this.id = columnSchema.getId();
		this.name = columnSchema.getName();
		if (PageColumnConstant.NUMBER_LIST.contains(columnSchema.getDataType())) {
			cmpType = "number";
		}else if(PageColumnConstant.DATE_LIST.contains(columnSchema.getDataType())) {
			cmpType = columnSchema.getDataType();
		}
		if (columnSchema.getLength() > 0) {
			cmpLength = columnSchema.getLength();
		}
		if(PageColumnConstant.SYS_COLUMN_LIST.contains(columnSchema.getId()) || "1".equals(columnSchema.getIsPk())) {
			disForm ="0";
			disList ="0";
			isReadonly = "1";
		}


	}
}
