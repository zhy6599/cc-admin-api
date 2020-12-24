package cc.admin.modules.bi.entity;

import lombok.Data;
/**
 * @author: ZhangHouYing
 * @date: 2020-11-21 20:03
 */
@Data
public class PieData {
	String name;
	Object value;

	public PieData(String name, Object value) {
		this.name = name;
		this.value = value;
	}
}
