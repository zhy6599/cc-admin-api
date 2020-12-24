package cc.admin.modules.generate.util;

import cc.admin.core.util.ApplicationContextUtil;
import cc.admin.modules.generate.entity.FreeMakerTemplateEntity;
import cc.admin.modules.generate.entity.Generate;
import cc.admin.modules.generate.model.*;
import cc.admin.modules.system.entity.SysDictItem;
import cc.admin.modules.system.service.impl.SysDictItemServiceImpl;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * @author: ZhangHouYing
 * @date: 2020/9/9
 */
@Slf4j
public class FreemarkerUtil {

	private static Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
	private static Converter<String, String> converterUpperCamel = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.UPPER_CAMEL);
	public static final String PATH = "\\opt\\result\\";
	private static String ONE = "1";

	public static List<FreeMakerTemplateEntity> getTemplateEntityList(String packageName, String className, String tableType) {
		List<FreeMakerTemplateEntity> templateEntityList = Lists.newArrayList();
		//主表 检测是否有从表，有的话那么就应该是主从
		if ("main".equals(tableType)) {
			templateEntityList.add(new FreeMakerTemplateEntity("controller.ftl", String.format("cc\\admin\\modules\\%s\\controller\\%sController.java", packageName, className)));
			templateEntityList.add(new FreeMakerTemplateEntity("mainIndex.ftl", "index.vue"));
		}else if ("slave".equals(tableType)) {
			templateEntityList.add(new FreeMakerTemplateEntity("mainItemController.ftl", String.format("cc\\admin\\modules\\%s\\controller\\%sController.java", packageName, className)));
			templateEntityList.add(new FreeMakerTemplateEntity("mainItem.ftl", "item.vue"));
		} else if ("middle".equals(tableType)) {
			templateEntityList.add(new FreeMakerTemplateEntity("index.ftl", "index.vue"));
		} else {
			templateEntityList.add(new FreeMakerTemplateEntity("controller.ftl", String.format("cc\\admin\\modules\\%s\\controller\\%sController.java", packageName, className)));
			templateEntityList.add(new FreeMakerTemplateEntity("index.ftl", "index.vue"));
		}
		templateEntityList.add(new FreeMakerTemplateEntity("list.ftl", String.format("%sList.js",className)));
		templateEntityList.add(new FreeMakerTemplateEntity("entity.ftl", String.format("cc\\admin\\modules\\%s\\entity\\%s.java", packageName, className)));
		templateEntityList.add(new FreeMakerTemplateEntity("impl.ftl", String.format("cc\\admin\\modules\\%s\\service\\impl\\%sServiceImpl.java", packageName, className)));
		templateEntityList.add(new FreeMakerTemplateEntity("mapper.ftl", String.format("cc\\admin\\modules\\%s\\mapper\\%sMapper.java", packageName, className)));
		templateEntityList.add(new FreeMakerTemplateEntity("service.ftl", String.format("cc\\admin\\modules\\%s\\service\\I%sService.java", packageName, className)));
		templateEntityList.add(new FreeMakerTemplateEntity("xml.ftl", String.format("cc\\admin\\modules\\%s\\mapper\\xml\\%sMapper.xml", packageName, className)));
		return templateEntityList;
	}

	public static String renderContent(String name, Map<String, Object> map) {
		try {
			StringWriter stringWriter = new StringWriter();
			configuration.setClassForTemplateLoading(FreemarkerUtil.class, "template");
			Template template = configuration.getTemplate(name, "utf-8");
			template.process(map, stringWriter);
			return stringWriter.toString();
		} catch (Exception exception) {
			log.error(exception.getMessage(), (Throwable) exception);
			return exception.toString();
		}
	}

	private static void initMap(String tableName, String content, Map<String, Object> map) {
		GenerateContent generateContent = JSONObject.parseObject(content, GenerateContent.class);
		List<ColumnCheck> columnCheckList = generateContent.getColumnCheckList();
		List<ColumnSchema> columnSchemaList = generateContent.getColumnSchemaList();
		List<PageColumn> pageColumnList = generateContent.getPageColumnList();
		List<RelateData> relateDataList = generateContent.getRelateDataList();
		List<FmColumn> fmColumnList = Lists.newArrayList();
		List<FmColumn> fmColumnSearchList = Lists.newArrayList();
		List<String> fmRuleList = Lists.newArrayList();
		pageColumnList.forEach(pageColumn -> {
			FmColumn fmColumn = new FmColumn(pageColumn);
			fmColumnList.add(fmColumn);
			if (ONE.equals(pageColumn.getIsQuery())) {
				fmColumnSearchList.add(fmColumn);
			}
			//字段检查设置
			columnCheckList.forEach(columnCheck -> {
				if (fmColumn.getId().equals(columnCheck.getId())) {
					// 这里做必输项控制
					initRule(fmColumn, columnCheck, fmRuleList);
					// 动态字典
					fmColumn.setDynamicDic(columnCheck.getDynamicDic());
					if (StrUtil.isNotEmpty(columnCheck.getSysDicCode())) {
						String sysDicCode = columnCheck.getSysDicCode();
						fmColumn.setSysDicCode(sysDicCode);
						String optionsName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, sysDicCode);
						fmColumn.setOptionsName(optionsName);
						if (ONE.equals(columnCheck.getDynamicDic())) {
							fmColumn.setOptionsContent(String.format("%s:[]", optionsName));
						} else {
							fmColumn.setOptionsContent(getOptionsContent(sysDicCode, optionsName));
						}
					} else if (StrUtil.isNotEmpty(columnCheck.getDicTable())) {
						fmColumn.setDicTable(columnCheck.getDicTable());
						fmColumn.setDicCode(columnCheck.getDicCode());
						fmColumn.setDicText(columnCheck.getDicText());
						String dicTable = columnCheck.getDicTable();
						String optionsName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, dicTable);
						fmColumn.setOptionsName(optionsName);
						if (ONE.equals(columnCheck.getDynamicDic())) {
							// 这里只能靠后台服务获取对应数据
							// TODO
							fmColumn.setOptionsContent(String.format("%s:[]", optionsName));
						} else {
							fmColumn.setOptionsContent(getOptionsContent(dicTable, optionsName, columnCheck.getDicCode(), columnCheck.getDicText()));
						}
					}
				}
			});
			columnSchemaList.forEach(columnSchema -> {
				if (fmColumn.getId().equals(columnSchema.getId())) {
					fmColumn.setDataType(columnSchema.getDataType());
					fmColumn.setJavaType(ColumnTypeEnum.toJavaType(columnSchema.getDataType()));
					fmColumn.setIsPk(columnSchema.getIsPk());
				}
			});
		});
		map.put("fmColumnList", fmColumnList);
		map.put("fmColumnSearchList", fmColumnSearchList);
		map.put("fmRuleList", fmRuleList);
		map.put("tableName", tableName);
		map.put("requestMapping", getRequestMapping(tableName));
		map.put("simpleQueryContent", getSimpleQueryContent(fmColumnList));

	}

	private static String getRequestMapping(String tableName) {
		tableName = tableName.toLowerCase();
		StringBuilder sb = new StringBuilder();
		String[] nameArr = StrUtil.split(tableName, "_");
		sb.append(nameArr[0]);
		if (nameArr.length > 1) {
			sb.append("/");
			for (int i = 1; i < nameArr.length; i++) {
				if (i == 1) {
					sb.append(nameArr[i]);
				} else {
					sb.append(converterUpperCamel.convert(nameArr[i]));
				}
			}
		}
		return sb.toString();
	}

	private static String getSimpleQueryContent(List<FmColumn> fmColumnList) {
		StringBuilder sb = new StringBuilder();
		fmColumnList.forEach(fmColumn -> {
			if ("1".equalsIgnoreCase(fmColumn.getIsSimple())) {
				if (StrUtil.isNotEmpty(sb.toString())) {
					sb.append(".or().");
				}
				sb.append(String.format("like(\"%s\", key)", fmColumn.getId()));
			}
		});
		if (StrUtil.isNotEmpty(sb.toString())) {
			return String.format("queryWrapper.and(i->i.%s);", sb.toString());
		}
		return "";
	}

	private static void initRule(FmColumn fmColumn, ColumnCheck columnCheck, List<String> fmRuleList) {
		StringBuilder ruleBuilder = new StringBuilder();
		String req = "requiredTest";
		if (ONE.equals(columnCheck.getMustInput())) {
			if (!fmRuleList.contains(req)) {
				fmRuleList.add(req);
			}
			ruleBuilder.append(req);
			fmColumn.setMastInput(ONE);
			if (StrUtil.isNotEmpty(columnCheck.getRule())) {
				ruleBuilder.append(", ").append(columnCheck.getRule());
			}
			fmColumn.setMastInput(ONE);
		} else {
			if (columnCheck.getRule() != null) {
				ruleBuilder.append(columnCheck.getRule());
			}
		}
		if (StrUtil.isNotEmpty(columnCheck.getRule())) {
			if (!fmRuleList.contains(columnCheck.getRule())) {
				fmRuleList.add(columnCheck.getRule());
			}
		}
		if (StrUtil.isNotEmpty(ruleBuilder.toString())) {
			fmColumn.setRule(String.format(":rules=\"[%s]\"", ruleBuilder.toString()));
		}
	}

	private static String getOptionsContent(String dicTable, String optionsName, String dicCode, String dicText) {
		SysDictItemServiceImpl sysDictItemService = null;
		try {
			sysDictItemService = ApplicationContextUtil.getContext().getBean(SysDictItemServiceImpl.class);
		} catch (Exception e) {
		}
		if (sysDictItemService != null) {
			List<SysDictItem> sysDictItemList = sysDictItemService.selectItemsByTable(dicTable, dicCode, dicText);
			return getOptionsContent(optionsName, sysDictItemList);
		} else {
			throw new RuntimeException(String.format("字典表[%s]不存在", dicTable));
		}
	}

	/**
	 * 获取字典配置信息
	 *
	 * @param sysDicCode
	 * @param optionsName
	 * @return
	 */
	private static String getOptionsContent(String sysDicCode, String optionsName) {
		SysDictItemServiceImpl sysDictItemServiceImpl = null;
		try {
			sysDictItemServiceImpl = ApplicationContextUtil.getContext().getBean(SysDictItemServiceImpl.class);
		} catch (Exception e) {
		}
		List<SysDictItem> sysDictItemList = sysDictItemServiceImpl.selectItemsByDictCode(sysDicCode);
		return getOptionsContent(optionsName, sysDictItemList);
	}

	private static String getOptionsContent(String optionsName, List<SysDictItem> sysDictItemList) {
		StringBuilder sb = new StringBuilder();
		sb.append(optionsName).append(":[");
		for (int i = 0; i < sysDictItemList.size(); i++) {
			SysDictItem sysDictItem = sysDictItemList.get(i);
			if (i > 0) {
				sb.append(",");
			}
			sb.append("{value:'").append(sysDictItem.getItemValue()).append("',label: '").append(sysDictItem.getItemText()).append("'}");
		}
		sb.append("]");
		return sb.toString();
	}

	public static Map<String, String> generateCode(GenerateForm generateForm, Generate sysGenerate) {
		Map<String, String> result = Maps.newHashMap();
		Map<String, Object> map = Maps.newHashMap();
		String content = sysGenerate.getContent();
		map.put("geForm", generateForm);
		map.put("generate", sysGenerate);

		map.put("mainColumn", sysGenerate.getMainColumn());
		map.put("mainColumnCode", sysGenerate.getMainColumnCode());
		map.put("slaveColumn", sysGenerate.getSlaveColumn());
		map.put("slaveColumnCode", sysGenerate.getSlaveColumnCode());

		initMap(sysGenerate.getName(), content, map);
		//这里设置下分类表相关参数
		if ("catalog".equalsIgnoreCase(sysGenerate.getTableType())) {
			map.put("pageClass", "row");
			map.put("viewCatalog", String.format("<viewcatalog class=\"q-mt-sm q-mb-sm q-ml-sm\" type=\"%s\" @select=\"selectCatalog\" />", generateForm.getClassName()));
			map.put("catalogInput", String.format("<catalogselect :form.sync=\"form\" type=\"%s\" />", generateForm.getClassName()));
		} else {
			map.put("pageClass", "column");
		}
		map.put("bussiPackage", "cc.admin.modules");
		map.put("entityPackage", generateForm.getPackageName());
		map.put("entityName", generateForm.getClassName());
		List<FreeMakerTemplateEntity> freeMakerTemplateEntityList = getTemplateEntityList(generateForm.getPackageName(), generateForm.getClassName(), sysGenerate.getTableType());
		for (FreeMakerTemplateEntity freeMakerTemplateEntity : freeMakerTemplateEntityList) {
			String renderContent = FreemarkerUtil.renderContent(freeMakerTemplateEntity.getTemplateName(), map);
			//采用linux换行符
			String filePath = PATH + freeMakerTemplateEntity.getFileName();
			//创建文件
			FileUtil.mkParentDirs(filePath);
			FileUtil.writeBytes(renderContent.replaceAll("\r\n", "\n").getBytes(Charsets.UTF_8), filePath);
			result.put(freeMakerTemplateEntity.getTemplateName(), renderContent);
		}
		return result;
	}

	public static void main(String[] args) {
		String form = "{\"className\":\"SysUserRole\",\"id\":\"1303523871885942785\",\"jsPath\":\"sys/user/role\",\"moduleName\":\"用户角色表\",\"packageName\":\"sys\"}";
		String ge = "{\"content\":\"{\\\"pageColumnList\\\":[{\\\"cmpLength\\\":32,\\\"cmpType\\\":\\\"text\\\",\\\"disForm\\\":\\\"1\\\",\\\"disList\\\":\\\"1\\\",\\\"id\\\":\\\"id\\\",\\\"isQuery\\\":\\\"0\\\",\\\"isReadonly\\\":\\\"0\\\",\\\"name\\\":\\\"主键id\\\",\\\"orderBy\\\":\\\"0\\\",\\\"queryDefault\\\":\\\"\\\",\\\"queryType\\\":\\\"0\\\"},{\\\"cmpLength\\\":32,\\\"cmpType\\\":\\\"text\\\",\\\"disForm\\\":\\\"1\\\",\\\"disList\\\":\\\"1\\\",\\\"id\\\":\\\"user_id\\\",\\\"isQuery\\\":\\\"0\\\",\\\"isReadonly\\\":\\\"0\\\",\\\"name\\\":\\\"用户id\\\",\\\"orderBy\\\":\\\"0\\\",\\\"queryDefault\\\":\\\"\\\",\\\"queryType\\\":\\\"0\\\"},{\\\"cmpLength\\\":32,\\\"cmpType\\\":\\\"text\\\",\\\"disForm\\\":\\\"1\\\",\\\"disList\\\":\\\"1\\\",\\\"id\\\":\\\"role_id\\\",\\\"isQuery\\\":\\\"0\\\",\\\"isReadonly\\\":\\\"0\\\",\\\"name\\\":\\\"角色id\\\",\\\"orderBy\\\":\\\"0\\\",\\\"queryDefault\\\":\\\"\\\",\\\"queryType\\\":\\\"0\\\"}],\\\"columnCheckList\\\":[{\\\"colLink\\\":\\\"\\\",\\\"dictCode\\\":\\\"\\\",\\\"dictTable\\\":\\\"\\\",\\\"dictValue\\\":\\\"\\\",\\\"id\\\":\\\"id\\\",\\\"mustInput\\\":\\\"0\\\",\\\"name\\\":\\\"主键id\\\",\\\"rule\\\":\\\"\\\",\\\"sysDict\\\":\\\"\\\"},{\\\"colLink\\\":\\\"\\\",\\\"dictCode\\\":\\\"\\\",\\\"dictTable\\\":\\\"\\\",\\\"dictValue\\\":\\\"\\\",\\\"id\\\":\\\"user_id\\\",\\\"mustInput\\\":\\\"0\\\",\\\"name\\\":\\\"用户id\\\",\\\"rule\\\":\\\"\\\",\\\"sysDict\\\":\\\"\\\"},{\\\"colLink\\\":\\\"\\\",\\\"dictCode\\\":\\\"\\\",\\\"dictTable\\\":\\\"\\\",\\\"dictValue\\\":\\\"\\\",\\\"id\\\":\\\"role_id\\\",\\\"mustInput\\\":\\\"0\\\",\\\"name\\\":\\\"角色id\\\",\\\"rule\\\":\\\"\\\",\\\"sysDict\\\":\\\"\\\"}],\\\"columnSchemaList\\\":[{\\\"dataType\\\":\\\"varchar\\\",\\\"id\\\":\\\"id\\\",\\\"isPk\\\":\\\"1\\\",\\\"length\\\":32,\\\"name\\\":\\\"主键id\\\",\\\"nullAble\\\":\\\"NO\\\",\\\"position\\\":1,\\\"scale\\\":0},{\\\"dataType\\\":\\\"varchar\\\",\\\"id\\\":\\\"user_id\\\",\\\"isPk\\\":\\\"0\\\",\\\"length\\\":32,\\\"name\\\":\\\"用户id\\\",\\\"nullAble\\\":\\\"YES\\\",\\\"position\\\":2,\\\"scale\\\":0},{\\\"dataType\\\":\\\"varchar\\\",\\\"id\\\":\\\"role_id\\\",\\\"isPk\\\":\\\"0\\\",\\\"length\\\":32,\\\"name\\\":\\\"角色id\\\",\\\"nullAble\\\":\\\"YES\\\",\\\"position\\\":3,\\\"scale\\\":0}]}\",\"createBy\":\"jianli01\",\"createTime\":1599619284000,\"description\":\"用户角色表\",\"id\":\"1303523871885942785\",\"idType\":\"0\",\"isPage\":\"1\",\"isSync\":\"1\",\"isTree\":\"0\",\"name\":\"sys_user_role\",\"relationType\":\"0\",\"sysOrgCode\":\"A02A01A10\",\"tableType\":\"0\"}";
		GenerateForm generateForm = JSONObject.parseObject(form, GenerateForm.class);
		Generate sysGenerate = JSONObject.parseObject(ge, Generate.class);
		generateCode(generateForm, sysGenerate);
	}

}

