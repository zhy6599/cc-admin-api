package cc.admin.modules.generate.model;

import lombok.Data;

import java.util.List;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-03 16:42
 */
@Data
public class TableSchema {
	private String id;
	private String name;
	private List<ColumnSchema> columnSchemaList;
}
