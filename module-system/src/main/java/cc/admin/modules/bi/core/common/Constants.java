/*
 * <<
 *  Davinci
 *  ==
 *  Copyright (C) 2016 - 2019 EDP
 *  ==
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain getHardWareId copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  >>
 *
 */

package cc.admin.modules.bi.core.common;

import java.util.regex.Pattern;

/**
 * 常量
 */
public class Constants extends Consts {

	/**
	 * 用户头像上传地址
	 */
	public static final String USER_AVATAR_PATH = "/image/user/";

	/**
	 * 组织头像上传地址
	 */
	public static final String ORG_AVATAR_PATH = "/image/organization/";

	/**
	 * display封面图地址
	 */
	public static final String DISPLAY_AVATAR_PATH = "/image/display/";

	/**
	 * CSV地址
	 */
	public static final String SOURCE_CSV_PATH = "/source/csv/";

	/**
	 * 分割符号
	 */
	public static final String SPLIT_CHAR_STRING = ":-:";

	/**
	 * sql ST模板
	 */
	public static final String SQL_TEMPLATE = "templates/sql/sqlTemplate.stg";

	/**
	 * excel 表头，数据格式化js
	 */
	public static final String TABLE_FORMAT_JS = "templates/js/formatCellValue.js";

	/**
	 * 格式化全局参数js
	 */
	public static final String EXECUTE_PARAM_FORMAT_JS = "templates/js/executeParam.js";

	/**
	 * select 表达式
	 */
	public static final String SELECT_EXEPRESSION = "SELECT * FROM TABLE WHERE %s";

	public static final String REG_USER_PASSWORD = ".{6,20}";

	public static final String EXCEL_FORMAT_KEY = "format";

	public static final String EXCEL_FORMAT_TYPE_KEY = "formatType";

	public static final String REG_SQL_PLACEHOLDER = "%s.+%s";

	public static final String REG_AUTHVAR = "\\([getHardWareId-zA-Z0-9_.-[\\u4e00-\\u9fa5]*]+\\s*[\\s\\w<>!=]*\\s*[getHardWareId-zA-Z0-9_.-]*((\\(%s[getHardWareId-zA-Z0-9_]+%s\\))|(%s[getHardWareId-zA-Z0-9_]+%s))+\\s*\\)";

	public static final String REG_CHINESE = "[\\u4e00-\\u9fa5]+";

	public static final Pattern REG_CHINESE_PATTERN = Pattern.compile(REG_CHINESE);

	public static final String LDAP_USER_PASSWORD = "LDAP";

	public static final String NO_AUTH_PERMISSION = "@DAVINCI_DATA_ACCESS_DENIED@";

	public static final String DAVINCI_TOPIC_CHANNEL = "DAVINCI_TOPIC_CHANNEL";

	public static char getSqlTempDelimiter(String sqlTempDelimiter) {
		return sqlTempDelimiter.charAt(sqlTempDelimiter.length() - 1);
	}

	public static String getReg(String express, char delimiter, boolean isAuthPress) {
		String arg = String.valueOf(delimiter);
		if (delimiter == DOLLAR_DELIMITER) {
			arg = "\\" + arg;
		}
		if (isAuthPress) {
			return String.format(express, arg, arg, arg, arg);
		} else {
			return String.format(express, arg, arg);
		}
	}
}
