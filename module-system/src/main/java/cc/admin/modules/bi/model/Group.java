package cc.admin.modules.bi.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class Group {
	private Field field;
	private String name;
	private String type;

	public String getDisplayName() {
		if (StrUtil.isEmpty(field.getAlias())) {
			return name;
		} else {
			return field.getAlias();
		}
	}
}
