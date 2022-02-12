package cc.admin.modules.jp.entity;

import lombok.Data;
/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
@Data
public class Mate {
	private String name;

	public Mate(String wife) {
		this.name = wife;
	}
}
