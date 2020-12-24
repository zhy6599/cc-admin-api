package cc.admin.modules.bi.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Aggregator {

	@NotBlank(message = "Invalid aggregator column")
	private String column;

	private String func;

	private String axisIndex;

	private String alias;

	public Aggregator() {
	}

	public Aggregator(String column, String func) {
		this.column = column;
		this.func = func;
	}

	public Aggregator(String column, String func, String axisIndex) {
		this.column = column;
		this.func = func;
		this.axisIndex = axisIndex;
	}

	public String getDisplayName() {
		if (StrUtil.isNotEmpty(alias)) {
			return alias;
		}
		return column;
	}

	public String getName() {
		if (StrUtil.isEmpty(func)) {
			return column;
		} else {
			return String.format("%s(%s)", func, column);
		}
	}
}
