package cc.admin.modules.generate.model;

import com.google.common.base.CaseFormat;
import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-09 15:34
 */
@Data
public class FmColumn {
	private String id;
	private String code;
	private String name;
	private String mastInput = "0";
	private String rule = "";
	private String isPk;
	private String dataType;
	private String javaType;
	private String disForm = "1";
	private String disList = "1";
	private String orderBy = "0";
	private String isReadonly = "0";
	private String dynamicDic = "0";
	private String cmpType = "text";
	private String sysDicCode ="";
	private String sysDicName ="";
	private String dicTable ="";
	private String dicCode ="";
	private String dicText ="";
	private String mainColumn ="";
	private String optionsName;
	private String optionsContent ="";
	private String isQuery = "0";
	private String isSimple = "0";
	private String defaultValue;

	public FmColumn(PageColumn pageColumn) {
		this.id = pageColumn.getId();
		this.code = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.id);
		this.name = pageColumn.getName();
		this.disForm = pageColumn.getDisForm();
		this.disList = pageColumn.getDisList();
		this.orderBy = pageColumn.getOrderBy();
		this.isReadonly = pageColumn.getIsReadonly();
		this.cmpType = pageColumn.getCmpType();
		this.isQuery = pageColumn.getIsQuery();
		this.isSimple = pageColumn.getIsSimple();
	}
}
