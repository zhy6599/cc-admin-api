package cc.admin.modules.generate.model;

import lombok.Data;

import java.util.List;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-09 15:38
 */
@Data
public class GenerateContent {
	private List<ColumnSchema> columnSchemaList;
	private List<PageColumn> pageColumnList;
	private List<ColumnCheck> columnCheckList;
	private List<RelateData> relateDataList;
	private GenerateForm generateForm;
}
