package cc.admin.modules.bi.model;

import lombok.Data;

@Data
public class Field {
	private String alias;
	private String desc;
	private Boolean useExpression;
}
