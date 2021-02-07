package cc.admin.modules.generate.service.impl;

import cc.admin.modules.bi.core.exception.ServerException;
import cc.admin.modules.generate.entity.Generate;
import cc.admin.modules.generate.mapper.GenerateMapper;
import cc.admin.modules.generate.model.*;
import cc.admin.modules.generate.service.IGenerateService;
import cc.admin.modules.generate.util.FreemarkerUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @Description: 代码生成
 * @Author: ZhangHouYing
 * @Date: 2020-09-05
 * @Version: V1.0.0
 */
@Service
@Slf4j
public class GenerateServiceImpl extends ServiceImpl<GenerateMapper, Generate> implements IGenerateService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public IPage<TableSchema> queryPageTableList(String filter, Integer pageNo, Integer pageSize) {
		List<TableSchema> tableSchemaList = Lists.newArrayList();
		String sql = "select * from v_table_schema order by id";
		if (StrUtil.isNotEmpty(filter)) {
			sql = String.format("select * from v_table_schema where id like '%%%s%%' or name like '%%%s%%'  order by id", filter, filter);
		}
		List<Map<String, Object>> tableMapList = jdbcTemplate.queryForList(sql);
		if (tableMapList == null) {
			throw new RuntimeException("表不存在");
		}
		for (Map<String, Object> tableMap : tableMapList) {
			TableSchema tableSchema = new TableSchema();
			String id = getStringValue(tableMap, "id", null);
			tableSchema.setId(id);
			tableSchema.setName(getStringValue(tableMap, "name", id));
			tableSchemaList.add(tableSchema);
		}
		Page<TableSchema> page = new Page<>(pageNo, pageSize);
		page.setTotal(tableSchemaList.size());
		page.setRecords(tableSchemaList);
		return page;
	}

	@Override
	public boolean syncTableByName(String tableName) {
		try {
			TableSchema tableSchema = getTableSchema(tableName);
			Generate generate = initByTableSchema(tableSchema);
			generate.setIsSync("1");
			this.baseMapper.insert(generate);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Get connection failed:" + e.getMessage());
			return false;
		} finally {
		}
		return true;
	}

	@Override
	public boolean syncTableById(String id) {
		try {
			Generate oldGenerate = this.baseMapper.selectById(id);
			Generate newGenerate = initByTableSchema(getTableSchema(oldGenerate.getName()));
			//将原来的配置信息保留下来
			String content = newGenerate.getContent();
			String old = oldGenerate.getContent();

			GenerateContent newObj = JSONObject.parseObject(content,GenerateContent.class);
			GenerateContent oldObj = JSONObject.parseObject(old,GenerateContent.class);
			List<PageColumn> newPageColumnList = newObj.getPageColumnList();
			List<PageColumn> oldPageColumnList = oldObj.getPageColumnList();
			List<ColumnCheck> newColumnCheckList = newObj.getColumnCheckList();
			List<ColumnCheck> oldColumnCheckList = oldObj.getColumnCheckList();
			List<RelateData> newRelateDataList = newObj.getRelateDataList();
			List<RelateData> oldRelateDataList = oldObj.getRelateDataList();

			GenerateContent contentObj = new GenerateContent();

			contentObj.setColumnSchemaList(newObj.getColumnSchemaList());
			contentObj.setPageColumnList(mergePageColumnSetting(newPageColumnList,oldPageColumnList));
			contentObj.setColumnCheckList(mergeColumnCheckSetting(newColumnCheckList,oldColumnCheckList));
			contentObj.setRelateDataList(mergeRelateDataSetting(newRelateDataList,oldRelateDataList));
			GenerateForm generateForm = oldObj.getGenerateForm();
			contentObj.setGenerateForm(generateForm);
			newGenerate.setContent(JSONObject.toJSONString(contentObj));
			newGenerate.setId(id);
			this.baseMapper.updateById(newGenerate);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Get connection failed:" + e.getMessage());
			return false;
		} finally {
		}
		return true;
	}

	private List<ColumnCheck> mergeColumnCheckSetting(List<ColumnCheck> newColumnCheckList, List<ColumnCheck> oldColumnCheckList) {
		List<ColumnCheck> resultList = Lists.newArrayList();
		for (ColumnCheck newObj : newColumnCheckList) {
			boolean notFind = true;
			for(ColumnCheck oldObj : oldColumnCheckList){
				if(newObj.getId().equals(oldObj.getId())){
					resultList.add(oldObj);
					notFind = false;
					break;
				}
			}
			if (notFind) {
				resultList.add(newObj);
			}
		}
		return resultList;
	}

	private List<PageColumn> mergePageColumnSetting(List<PageColumn> newPageColumnList, List<PageColumn> oldPageColumnList) {
		List<PageColumn> resultList = Lists.newArrayList();
		for (PageColumn newObj : newPageColumnList) {
			boolean notFind = true;
			for(PageColumn oldObj : oldPageColumnList){
				if(newObj.getId().equals(oldObj.getId())){
					resultList.add(oldObj);
					notFind = false;
					break;
				}
			}
			if (notFind) {
				resultList.add(newObj);
			}
		}
		return resultList;
	}

	private List<RelateData> mergeRelateDataSetting(List<RelateData> newList, List<RelateData>  oldList){
		List<RelateData> resultList = Lists.newArrayList();
		for (RelateData newObj : newList) {
			boolean notFind = true;
			for(RelateData oldObj : oldList){
				if(newObj.getId().equals(oldObj.getId())){
					resultList.add(oldObj);
					notFind = false;
					break;
				}
			}
			if (notFind) {
				resultList.add(newObj);
			}
		}
		return resultList;
	}

	@Override
	public String isSync(Generate generate){
		Generate oldGenerate = this.baseMapper.selectById(generate.getId());
		String content = generate.getContent();
		String old = oldGenerate.getContent();

		JSONObject contentObj = JSONObject.parseObject(content);
		JSONObject oldObj = JSONObject.parseObject(old);
		List<JSONObject> columnSchemaList = Lists.newArrayList();
		columnSchemaList = contentObj.getObject("columnSchemaList", columnSchemaList.getClass());

		List<JSONObject> oldColumnSchemaList = Lists.newArrayList();
		oldColumnSchemaList = oldObj.getObject("columnSchemaList", oldColumnSchemaList.getClass());
		if (CollectionUtil.isNotEmpty(columnSchemaList)) {
			for (JSONObject jsonObject : columnSchemaList) {
				ColumnSchema columnSchema = JSONObject.toJavaObject(jsonObject, ColumnSchema.class);
				if (columnSchemaList.size() != oldColumnSchemaList.size()) {
					return "0";
				}
				String columnSql = columnSchema.getColumnSql();
				boolean notEqual = true;
				for (JSONObject oldJsonObject : oldColumnSchemaList) {
					ColumnSchema oldColumnSchema = JSONObject.toJavaObject(oldJsonObject, ColumnSchema.class);
					if (columnSchema.getId().equalsIgnoreCase(oldColumnSchema.getId())) {
						if (columnSql.equalsIgnoreCase(oldColumnSchema.getColumnSql())) {
							notEqual = false;
						}
					}

				}
				if (notEqual) {
					return "0";
				}
			}
		}
		return "1";
	}

	@Override
	public List<Map<String, String>> queryColumns(String id) {
		List<Map<String, String>> resultList = Lists.newArrayList();
		Generate generate = this.baseMapper.selectById(id);
		String tableName = generate.getName();
		List<ColumnSchema> columnSchemaList = getColumns (tableName);
		columnSchemaList.forEach(columnSchema -> resultList.add(convertColumnSchema(columnSchema)));
		return resultList;
	}

	private Map<String, String> convertColumnSchema(ColumnSchema columnSchema){
		Map<String, String> map = Maps.newHashMap();
		map.put("label", columnSchema.getId());
		map.put("value", columnSchema.getId());
		return map;
	}

	@Override
	public boolean syncTableToDb(String id) {
		try {
			Generate generate = this.baseMapper.selectById(id);

			String dropSql = getDropSql( generate);
			log.info("drop table sql:{}",dropSql);
			jdbcTemplate.execute(dropSql);
			//这里写建表语句
			String sql = getCreateSql(generate);
			log.info("create table sql:{}",sql);
			jdbcTemplate.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Get connection failed:" + e.getMessage());
			return false;
		} finally {
		}
		return true;
	}

	@Override
	public String syncTableToConfig(String id) {
		try {
			Generate generate = this.baseMapper.selectById(id);
			String tableName = generate.getName();
			List<ColumnSchema> columnList = getColumns(tableName);
			//获取到内容，和数据库对比
			String content = generate.getContent();
			JSONObject contentObj = JSONObject.parseObject(content);
			List<ColumnSchema> columnSchemaListNew = Lists.newArrayList();
			List<PageColumn> pageColumnListNew = Lists.newArrayList();
			List<RelateData> relateDataListNew = Lists.newArrayList();
			System.out.println(content);

			List<JSONObject> columnSchemaList = Lists.newArrayList();
			List<JSONObject> pageColumnList = Lists.newArrayList();
			List<JSONObject> relateDataList = Lists.newArrayList();
			columnSchemaList = contentObj.getObject("columnSchemaList", columnSchemaList.getClass());
			pageColumnList = contentObj.getObject("pageColumnList", columnSchemaList.getClass());
			relateDataList = contentObj.getObject("relateDataList", columnSchemaList.getClass());

			//以数据库的为准
			for (ColumnSchema dbColumn : columnList){
				columnSchemaListNew.add(dbColumn);
				if (CollectionUtil.isNotEmpty(columnSchemaList)) {
					boolean have = false;
					for (JSONObject jsonObject : columnSchemaList) {
						ColumnSchema columnSchema = JSONObject.toJavaObject(jsonObject, ColumnSchema.class);
						if (dbColumn.getId().equals(columnSchema.getId())) {
							have = true;
						}
					}
					for (JSONObject jsonObject : pageColumnList) {
						PageColumn pageColumn = JSONObject.toJavaObject(jsonObject, PageColumn.class);
						if (dbColumn.getId().equals(pageColumn.getId())) {
							pageColumn.setName(dbColumn.getName());
							pageColumnListNew.add(pageColumn);
						}
					}
					for (JSONObject jsonObject : relateDataList) {
						RelateData relateData = JSONObject.toJavaObject(jsonObject, RelateData.class);
						if (dbColumn.getId().equals(relateData.getId())) {
							relateData.setName(dbColumn.getName());
							relateDataListNew.add(relateData);
						}
					}
					if (!have) {
						PageColumn pageColumn = new PageColumn(dbColumn);
						RelateData relateData = new RelateData(dbColumn);
						pageColumnListNew.add(pageColumn);
						relateDataListNew.add(relateData);
					}
				}
			}
			contentObj.put("columnSchemaList", columnSchemaListNew);
			contentObj.put("pageColumnList", pageColumnListNew);
			contentObj.put("relateDataList", relateDataListNew);
			generate.setContent(contentObj.toJSONString());
			this.baseMapper.updateById(generate);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Get connection failed:" + e.getMessage());
		} finally {
		}
		return "同步成功";
	}

	private String getDropSql(Generate generate) {
		String tableName = generate.getName();
		StringBuffer sql = new StringBuffer();
		sql.append("\n DROP TABLE IF EXISTS `" + tableName + "`; ");
		return sql.toString();
	}
	private String getCreateSql(Generate generate) {
		StringBuffer sql = new StringBuffer();
		JSONObject contentObj = JSONObject.parseObject(generate.getContent());
		List<JSONObject> columnSchemaList = Lists.newArrayList();
		columnSchemaList = contentObj.getObject("columnSchemaList", columnSchemaList.getClass());
		if (CollectionUtil.isNotEmpty(columnSchemaList)) {
			String tableName = generate.getName();
			String sqlPrimaryKey = "";
			StringBuffer cb = new StringBuffer();
			for (JSONObject jsonObject : columnSchemaList) {
				ColumnSchema columnSchema = JSONObject.toJavaObject(jsonObject, ColumnSchema.class);
				cb.append(columnSchema.getColumnSql());
				if ("1".equals(columnSchema.getIsPk())) {
					sqlPrimaryKey = columnSchema.getId();
				}
			}
			sql.append(" \n CREATE TABLE `" + tableName + "`  (");
			if (StrUtil.isNotEmpty(sqlPrimaryKey)) {
				sql.append(" \n " + cb.toString());
				sql.append(" \n PRIMARY KEY (`" + sqlPrimaryKey + "`)");
			} else {
				cb.deleteCharAt(cb.length() - 1);
				sql.append(cb.toString());
			}
			sql.append(" \n ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci");
			if (StrUtil.isNotEmpty(generate.getDescription())) {
				sql.append(" COMMENT = '").append(generate.getDescription()).append("';");
			}

		}
		return sql.toString();
	}

	private Generate initByTableSchema(TableSchema tableSchema) {
		Generate generate = new Generate();
		String id = IdUtil.objectId();
		generate.setId(id);
		generate.setName(tableSchema.getId());
		generate.setDescription(tableSchema.getName());
		JSONObject contentObj = new JSONObject();

		GenerateForm generateForm = new GenerateForm();
		generateForm.setId(id);
		generateForm.setModuleName(tableSchema.getName());
		generateForm.setClassName(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableSchema.getId()));
		generateForm.setPackageName(tableSchema.getId().split("_")[0]);
		generateForm.setJsPath(tableSchema.getId().replaceAll("_","/"));
		GenerateContent generateContent = new GenerateContent();
		generateContent.setGenerateForm(generateForm);
		generateContent.setRelateDataList(getRelateDataList(tableSchema.getColumnSchemaList()));
		generateContent.setColumnCheckList(getColumnCheckList(tableSchema.getColumnSchemaList()));
		generateContent.setColumnSchemaList(tableSchema.getColumnSchemaList());
		generateContent.setPageColumnList(getPageColumnList(tableSchema.getColumnSchemaList()));
		generate.setContent(JSONObject.toJSONString(generateContent));
		return generate;
	}

	private List<RelateData> getRelateDataList(List<ColumnSchema> columnSchemaList) {
		List<RelateData> relateDataList = Lists.newArrayList();
		for (ColumnSchema columnSchema : columnSchemaList) {
			relateDataList.add(new RelateData(columnSchema));
		}
		return relateDataList;
	}

	private List<PageColumn> getPageColumnList(List<ColumnSchema> columnSchemaList) {
		List<PageColumn> pageColumnList = Lists.newArrayList();
		for (ColumnSchema columnSchema : columnSchemaList) {
			pageColumnList.add(new PageColumn(columnSchema));
		}
		return pageColumnList;
	}

	private List<ColumnCheck> getColumnCheckList(List<ColumnSchema> columnSchemaList) {
		List<ColumnCheck> columnCheckList = Lists.newArrayList();
		for (ColumnSchema columnSchema : columnSchemaList) {
			columnCheckList.add(new ColumnCheck(columnSchema));
		}
		return columnCheckList;
	}

	/**
	 * 获取数据表主键
	 *
	 * @param tableName
	 * @return
	 * @throws ServerException
	 */
	@Override
	public TableSchema getTableSchema(String tableName) throws ServerException {
		Map<String, Object> tableMap = jdbcTemplate.queryForMap(String.format("select * from v_table_schema where id = '%s'", tableName));
		if (tableMap == null) {
			throw new RuntimeException("表不存在");
		}
		TableSchema tableSchema = new TableSchema();
		tableSchema.setId(tableName);
		if (tableMap.get("name") != null) {
			tableSchema.setName(String.valueOf(tableMap.get("name")));
		} else {
			tableSchema.setName(tableName);
		}
		List<ColumnSchema> columnSchemaList = getColumns(tableName);
		tableSchema.setColumnSchemaList(columnSchemaList);
		return tableSchema;
	}

	/**
	 * 获取数据表列
	 *
	 * @param tableName
	 * @return
	 * @throws ServerException
	 */
	private List<ColumnSchema> getColumns(String tableName) throws ServerException {
		List<ColumnSchema> columnSchemaList = Lists.newArrayList();
		List<Map<String, Object>> columnMapList = jdbcTemplate.queryForList(String.format("select * from v_column_schema where table_name = '%s'", tableName));
		if (columnMapList == null) {
			throw new RuntimeException("列不存在");
		}
		for (Map<String, Object> columnMap : columnMapList) {
			ColumnSchema columnSchema = new ColumnSchema();
			String id = getStringValue(columnMap, "id", null);
			columnSchema.setId(id);
			columnSchema.setName(getStringValue(columnMap, "name", id));
			String dataType = getStringValue(columnMap, "data_type", null);
			columnSchema.setDataType(dataType);
			Integer length = 0;
			Integer scale = 0;
			if (!"longtext".equals(dataType)) {
				length = getIntegerValue(columnMap, "length", 0);
				scale = getIntegerValue(columnMap, "scale", 0);
			}
			columnSchema.setLength(length);
			columnSchema.setScale(scale);
			columnSchema.setPosition(getIntegerValue(columnMap, "position", 0));
			columnSchema.setIsPk(getStringValue(columnMap, "is_pk", "0"));
			columnSchema.setDefaultValue(getStringValue(columnMap, "column_default", null));
			columnSchema.setNullAble(getStringValue(columnMap, "is_nullable", "YES"));
			columnSchemaList.add(columnSchema);
		}
		return columnSchemaList;
	}

	private String getStringValue(Map<String, Object> columnMap, String key, String defaultValue) {
		Object value = columnMap.get(key);
		if (value == null) {
			return defaultValue;
		} else {
			return (String) value;
		}
	}

	private Integer getIntegerValue(Map<String, Object> columnMap, String key, Integer defaultValue) {
		Object value = columnMap.get(key);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(String.valueOf(value));
		}
	}

	public static void closeResult(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Map<String, String> doGenerateCode(GenerateForm generateForm,Generate generate) {
		//保存生成配置信息
		GenerateContent contentObj = JSONObject.parseObject(generate.getContent(),GenerateContent.class);
		contentObj.setGenerateForm(generateForm);
		generate.setContent(JSONObject.toJSONString(contentObj));
		updateById(generate);
		return FreemarkerUtil.generateCode(generateForm, generate);
	}

}
