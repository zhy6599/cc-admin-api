package cc.admin.modules.jp.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.Map;
/**
 * @author: ZhangHouYing
 * @date: 2021-04-18 15:35
 */
@Slf4j
public class LevelFreeMaker {
	private static Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

	public static String renderContent(String name, Map<String, Object> map) {
		try {
			StringWriter stringWriter = new StringWriter();
			configuration.setClassForTemplateLoading(LevelFreeMaker.class, "level");
			Template template = configuration.getTemplate(name, "utf-8");
			template.process(map, stringWriter);
			return stringWriter.toString();
		} catch (Exception exception) {
			log.error(exception.getMessage(), (Throwable) exception);
			return exception.toString();
		}
	}
}
