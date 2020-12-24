package cc.admin.modules.generate.model;

import com.google.common.collect.ImmutableList;

import java.util.List;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-08 9:46
 */
public class PageColumnConstant {
	public static final List<String> NUMBER_LIST = ImmutableList.of("int","float","double","tiny int","medium int","big int","decimal");
	public static final List<String> DATE_LIST = ImmutableList.of("date","datetime");
	public static final List<String> SYS_COLUMN_LIST = ImmutableList.of("create_time","create_by","update_time","update_by","sys_org_code");

}
