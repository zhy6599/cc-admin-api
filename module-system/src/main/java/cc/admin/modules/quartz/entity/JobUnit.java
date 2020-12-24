package cc.admin.modules.quartz.entity;

/**
 * @author: ZhangHouYing
 * @date: 2020-08-21 11:05
 */
public enum JobUnit {

	/** 分钟 */
	MINUTE("minute"),
	/** 小时 */
	HOUR("hour"),
	/** 天 */
	DAY("day"),
	/** 周 */
	WEEK("week"),
	/** 月 */
	MONTH("month"),
	/** 季度 */
	QUARTER("quarter"),
	/** 半年 */
	HALF_YEAR("half_year"),
	/** 年 */
	YEAR("YEAR");

	private String unit;

	JobUnit(String unit) {
		this.unit = unit;
	}

	public String getJobUnit(){
		return unit;
	}
}
