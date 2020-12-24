package cc.admin.modules.generate.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-03 16:42
 */
@Data
public class ColumnSchema {

	private String id;
	private String name;
	private String dataType;
	private Integer length;
	private Integer scale;
	private Integer position;
	private String isPk;
	private String nullAble;
	private String defaultValue;

	public String getColumnSql() {
		StringBuffer sb = new StringBuffer();
		//  `templete_id` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板id',
		sb.append(id).append(" ");
		if ("varchar".equalsIgnoreCase(dataType) || "int".equalsIgnoreCase(dataType) ||
				"tinyint".equalsIgnoreCase(dataType) || "char".equalsIgnoreCase(dataType) ||
				"bigint".equalsIgnoreCase(dataType) || "integer".equalsIgnoreCase(dataType) ||
				"timestamp".equalsIgnoreCase(dataType) || "time".equalsIgnoreCase(dataType) ||
				"date".equalsIgnoreCase(dataType) || "datetime".equalsIgnoreCase(dataType)) {
			sb.append(dataType).append("(").append(length).append(")");
		} else if ("double".equalsIgnoreCase(dataType) || "float".equalsIgnoreCase(dataType)
				|| "decimal".equalsIgnoreCase(dataType)|| "numeric".equalsIgnoreCase(dataType)) {
			sb.append(dataType).append("(").append(length).append(",").append(scale).append(")");
		} else if ("text".equalsIgnoreCase(dataType) || "tinytext".equalsIgnoreCase(dataType)
				|| "longtext".equalsIgnoreCase(dataType)|| "polygon".equalsIgnoreCase(dataType)
				|| "point".equalsIgnoreCase(dataType)|| "polygon".equalsIgnoreCase(dataType)) {
			sb.append(dataType);
		}
		if ("NO".equalsIgnoreCase(nullAble)) {
			sb.append(" NOT NULL ");
		}
		if (StrUtil.isNotEmpty(defaultValue)) {
			sb.append(" default ").append(defaultValue).append(" ");
		}
		if (StrUtil.isNotEmpty(name)) {
			sb.append(" comment '").append(name).append("', ");
		}
		return sb.toString();
	}

}
