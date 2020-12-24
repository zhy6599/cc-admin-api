package cc.admin.modules.generate.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
/**
 * @author: ZhangHouYing
 * @date: 2020-09-10 10:03
 */
public class T {
	public static void main(String[] args) {
		// 驼峰转下划线, userName -> user_name
		Converter<String, String> converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
		// 输出: user_name
		System.out.println(converter.convert("userName"));
		System.out.println();

		// 驼峰转连接符, userName -> user-name
		converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN);
		// 输出: user-name
		System.out.println(converter.convert("userName"));
		System.out.println();

		// 驼峰转首字符大写驼峰, userName -> UserName
		converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);
		// 输出: UserName
		System.out.println(converter.convert("userName"));
		System.out.println();

		// 驼峰转大写下划线, userName -> USER_NAME
		converter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_UNDERSCORE);
		// 输出: USER_NAME
		System.out.println(converter.convert("userName"));
		System.out.println();


		System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, "test-data"));
		System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "test_data"));
		System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "test_data"));

		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "testdata"));
		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "TestData"));
		System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "testData"));

	}
}
