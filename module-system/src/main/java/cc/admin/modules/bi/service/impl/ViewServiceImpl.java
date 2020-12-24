package cc.admin.modules.bi.service.impl;

import cc.admin.modules.bi.core.common.Constants;
import cc.admin.modules.bi.core.enums.SqlVariableTypeEnum;
import cc.admin.modules.bi.core.enums.SqlVariableValueTypeEnum;
import cc.admin.modules.bi.core.exception.ServerException;
import cc.admin.modules.bi.core.model.SqlEntity;
import cc.admin.modules.bi.core.model.SqlFilter;
import cc.admin.modules.bi.core.model.SqlVariable;
import cc.admin.modules.bi.core.utils.SqlParseUtils;
import cc.admin.modules.bi.entity.PieData;
import cc.admin.modules.bi.entity.SeriesData;
import cc.admin.modules.bi.entity.View;
import cc.admin.modules.bi.mapper.ViewMapper;
import cc.admin.modules.bi.model.*;
import cc.admin.modules.bi.service.IViewService;
import cc.admin.modules.bi.util.SqlUtils;
import cc.admin.modules.system.entity.SysDataSource;
import cc.admin.modules.system.mapper.SysDataSourceMapper;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.*;
import java.util.stream.Collectors;

import static cc.admin.modules.bi.core.common.Consts.COMMA;
import static cc.admin.modules.bi.core.common.Consts.MINUS;
import static cc.admin.modules.bi.core.enums.SqlVariableTypeEnum.QUERYVAR;

/**
 * @Description: bi_view
 * @Author: ZhangHouYing
 * @Date: 2020-07-04
 * @Version: V1.0
 */
@Service
public class ViewServiceImpl extends ServiceImpl<ViewMapper, View> implements IViewService {

	@Autowired
	ViewMapper viewMapper;

	@Autowired
	private SysDataSourceMapper sysDataSourceMapper;

	@Value("${sql_template_delimiter:$}")
	private String sqlTempDelimiter;

	@Autowired
	private SqlParseUtils sqlParseUtils;

	private String dbVersion = null;

	@Override
	public Page<Map<String, Object>> getData(String id, ViewExecuteParam executeParam) {
		if (null == executeParam || (CollUtil.isEmpty(executeParam.getGroups()) && CollUtil.isEmpty(executeParam.getAggregators()))) {
			return null;
		}
		View view = viewMapper.selectById(id);
		Page<Map<String, Object>> paginate = new Page<>();
		if (null == executeParam || (CollUtil.isEmpty(executeParam.getGroups()) && CollUtil.isEmpty(executeParam.getAggregators()))) {
			return null;
		}
		SysDataSource sysDataSource = sysDataSourceMapper.selectById(view.getSourceId());
		if (null == sysDataSource) {
			throw new RuntimeException("source is not found");
		}
		try {
			if (StringUtils.isEmpty(view.getViewSql())) {
				return paginate;
			}
			// 解析变量
			List<SqlVariable> variables = view.getVariables();
			// 解析sql
			SqlEntity sqlEntity = sqlParseUtils.parseSql(view.getViewSql(), variables, sqlTempDelimiter);
			// 列权限（只记录被限制访问的字段）
			Set<String> excludeColumns = new HashSet<>();
			packageParams(view.getId(), sqlEntity, variables, executeParam.getParams(),
					excludeColumns);
			String srcSql = sqlParseUtils.replaceParams(sqlEntity.getSql(), sqlEntity.getQuaryParams(),
					sqlEntity.getAuthParams(), sqlTempDelimiter);
			List<String> executeSqlList = sqlParseUtils.getSqls(srcSql, false);
			if (!CollUtil.isEmpty(executeSqlList)) {
				executeSqlList.forEach(sql -> SqlUtils.execute(sysDataSource.getDbUrl(),
						sysDataSource.getDbUsername(),
						sysDataSource.getDbPassword(), sql));
			}
			List<String> querySqlList = sqlParseUtils.getSqls(srcSql, true);
			if (!CollUtil.isEmpty(querySqlList)) {
				buildQuerySql(querySqlList, sysDataSource, executeParam);
				executeParam.addExcludeColumn(excludeColumns, sysDataSource.getDbUrl(), dbVersion);
				if (null != executeParam && null != executeParam.getCache() && executeParam.getCache()
						&& executeParam.getExpired() > 0L) {
					StringBuilder slatBuilder = new StringBuilder();
					slatBuilder.append(executeParam.getPageNo());
					slatBuilder.append(MINUS);
					slatBuilder.append(executeParam.getLimit());
					slatBuilder.append(MINUS);
					slatBuilder.append(executeParam.getPageSize());
					excludeColumns.forEach(slatBuilder::append);
				}
				for (String sql : querySqlList) {
					paginate = SqlUtils.syncQuery4Paginate(sysDataSource.getDbUrl(),
							sysDataSource.getDbUsername(),
							sysDataSource.getDbPassword(), SqlParseUtils.rebuildSqlWithFragment(sql),
							executeParam.getPageNo(), executeParam.getPageSize(), executeParam.getTotalCount(),
							executeParam.getLimit(), excludeColumns);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServerException(e.getMessage());
		}
		return paginate;
	}

	public void buildQuerySql(List<String> querySqlList, SysDataSource sysDataSource, ViewExecuteParam executeParam) {
		if (null == executeParam) {
			return;
		}
		// 构造参数， 原有的被传入的替换
		STGroup stg = new STGroupFile(Constants.SQL_TEMPLATE);
		ST st = stg.getInstanceOf("querySql");
		st.add("nativeQuery", executeParam.isNativeQuery());
		st.add("groups", executeParam.getGroups());
		if (executeParam.isNativeQuery()) {
			st.add("aggregators", executeParam.getAggregators());
		} else {
			st.add("aggregators", executeParam.getAggregators(sysDataSource.getDbUrl(), dbVersion));
		}
		st.add("orders", executeParam.getOrders(sysDataSource.getDbUrl(), dbVersion));
		st.add("filters", convertFilters(executeParam.getFilters(), sysDataSource));
		st.add("keywordPrefix", "");
		st.add("keywordSuffix", "");
		for (int i = 0; i < querySqlList.size(); i++) {
			st.add("sql", querySqlList.get(i));
			querySqlList.set(i, st.render());
		}
	}

	public List<String> convertFilters(List<String> filterStrs, SysDataSource sysDataSource) {
		List<String> whereClauses = new ArrayList<>();
		List<SqlFilter> filters = new ArrayList<>();
		try {
			if (null == filterStrs || filterStrs.isEmpty()) {
				return null;
			}
			for (String str : filterStrs) {
				SqlFilter obj = JSON.parseObject(str, SqlFilter.class);
				if (!StringUtils.isEmpty(obj.getName())) {
					obj.setName(ViewExecuteParam.getField(obj.getName(), sysDataSource.getDbUrl(), dbVersion));
				}
				filters.add(obj);
			}
			filters.forEach(filter -> whereClauses.add(SqlFilter.dealFilter(filter)));

		} catch (Exception e) {
			throw e;
		}
		return whereClauses;
	}

	private List<SqlVariable> getQueryVariables(List<SqlVariable> variables) {
		if (!CollUtil.isEmpty(variables)) {
			return variables.stream().filter(v -> QUERYVAR == SqlVariableTypeEnum.typeOf(v.getType())).collect(Collectors.toList());
		}
		return null;
	}

	private void packageParams(String viewId, SqlEntity sqlEntity, List<SqlVariable> variables, List<Param> paramList, Set<String> excludeColumns) {
		List<SqlVariable> queryVariables = getQueryVariables(variables);
		List<SqlVariable> authVariables = null;
		//查询参数
		if (!CollUtil.isEmpty(queryVariables) && !CollUtil.isEmpty(sqlEntity.getQuaryParams())) {
			if (!CollUtil.isEmpty(paramList)) {
				Map<String, List<SqlVariable>> map = queryVariables.stream().collect(Collectors.groupingBy(SqlVariable::getName));
				paramList.forEach(p -> {
					if (map.containsKey(p.getName())) {
						List<SqlVariable> list = map.get(p.getName());
						if (!CollUtil.isEmpty(list)) {
							SqlVariable v = list.get(list.size() - 1);
							if (null == sqlEntity.getQuaryParams()) {
								sqlEntity.setQuaryParams(new HashMap<>());
							}
							sqlEntity.getQuaryParams().put(p.getName().trim(), SqlVariableValueTypeEnum.getValue(v.getValueType(), p.getValue(), v.isUdf()));
						}
					}
				});
			}
			sqlEntity.getQuaryParams().forEach((k, v) -> {
				if (v instanceof List && ((List) v).size() > 0) {
					v = ((List) v).stream().collect(Collectors.joining(COMMA)).toString();
				}
				sqlEntity.getQuaryParams().put(k, v);
			});
		}
		sqlEntity.setAuthParams(null);
	}

	@Override
	public PaginateWithQueryColumns executeSql(ViewExecuteSql executeSql) {
		SysDataSource sysDataSource = sysDataSourceMapper.selectById(executeSql.getSourceId());
		if (null == sysDataSource) {
			throw new RuntimeException("source is not found");
		}
		//结构化Sql
		PaginateWithQueryColumns paginateWithQueryColumns = null;
		try {
			SqlEntity sqlEntity = sqlParseUtils.parseSql(executeSql.getSql(), executeSql.getVariables(), sqlTempDelimiter);
			if (null == sqlEntity || StringUtils.isEmpty(sqlEntity.getSql())) {
				return paginateWithQueryColumns;
			}
			if (!CollUtil.isEmpty(sqlEntity.getQuaryParams())) {
				sqlEntity.getQuaryParams().forEach((k, v) -> {
					if (v instanceof List && ((List) v).size() > 0) {
						v = ((List) v).stream().collect(Collectors.joining(COMMA)).toString();
					}
					sqlEntity.getQuaryParams().put(k, v);
				});
			}
			String srcSql = sqlParseUtils.replaceParams(sqlEntity.getSql(), sqlEntity.getQuaryParams(),
					sqlEntity.getAuthParams(), sqlTempDelimiter);
			List<String> executeSqlList = sqlParseUtils.getSqls(srcSql, false);
			List<String> querySqlList = sqlParseUtils.getSqls(srcSql, true);
			if (!CollUtil.isEmpty(executeSqlList)) {
				executeSqlList.forEach(sql -> SqlUtils.execute(sysDataSource.getDbUrl(),
						sysDataSource.getDbUsername(),
						sysDataSource.getDbPassword(), sql));
			}
			if (!CollUtil.isEmpty(querySqlList)) {
				for (String sql : querySqlList) {
					sql = SqlParseUtils.rebuildSqlWithFragment(sql);
					sql = sql + " LIMIT " + executeSql.getLimit();
					paginateWithQueryColumns = SqlUtils.syncQuery4Paginate(sysDataSource.getDbUrl(),
							sysDataSource.getDbUsername(),
							sysDataSource.getDbPassword(), sql, null, null, null, executeSql.getLimit(),
							null);
				}
			}

		} catch (Exception e) {
			throw new ServerException(e.getMessage());
		}
		return paginateWithQueryColumns;
	}

	@Override
	public TableInfo columns(String id, String dbName, String tableName) {
		SysDataSource sysDataSource = sysDataSourceMapper.selectById(id);
		if (null == sysDataSource) {
			throw new RuntimeException("source is not found");
		}
		return SqlUtils.getTableInfo(sysDataSource.getDbUrl(),
				sysDataSource.getDbUsername(),
				sysDataSource.getDbPassword(), dbName, tableName);
	}

	@Override
	public Map<String, Object> getChartData(String id, ViewExecuteParam executeParam) {
		Map<String, Object> resultMap = Maps.newHashMap();
		if ("".equals(executeParam.getType())) {
		} else if ("pie".equals(executeParam.getType())) {
			resultMap = getPieData(id, executeParam);
		} else if ("radar".equals(executeParam.getType())) {
			resultMap = getRadarData(id, executeParam);
		} else if ("funnel".equals(executeParam.getType())) {
			resultMap = getFunnelData(id, executeParam);
		} else if ("wordCloud".equals(executeParam.getType())) {
			resultMap = getWordCloudData(id, executeParam);
		} else if ("gauge".equals(executeParam.getType())) {
			resultMap = getGaugeData(id, executeParam);
		} else {
			resultMap = getLineData(id, executeParam);
		}
		return resultMap;
	}

	private Map<String, Object> getGaugeData(String id, ViewExecuteParam executeParam) {
		Page<Map<String, Object>> resultPage = getData(id, executeParam);
		List<Map<String, Object>> records = resultPage.getRecords();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> groups = executeParam.getGroups();
		List<Aggregator> aggregators = executeParam.getAggregators();
		List<PieData> dataList = Lists.newArrayList();
		double min = 0;
		double max = 100;
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			for (Aggregator aggregator : aggregators) {
				double value = Double.parseDouble(String.valueOf(record.get(aggregator.getName())));
				dataList.add(new PieData(String.valueOf(record.get(groups.get(0))), record.get(aggregator.getName())));
				if (min > value) {
					min = value;
				}
				if (value > max) {
					max = value;
				}
			}

		}
		if (max > 100) {
			max = ((int)((max+100)/100))*100;
		}
		resultMap.put("min", min);
		resultMap.put("max", max);
		resultMap.put("dataList", dataList);
		return resultMap;
	}

	private Map<String, Object> getWordCloudData(String id, ViewExecuteParam executeParam) {
		Page<Map<String, Object>> resultPage = getData(id, executeParam);
		List<Map<String, Object>> records = resultPage.getRecords();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> groups = executeParam.getGroups();
		List<Aggregator> aggregators = executeParam.getAggregators();
		List<PieData> dataList = Lists.newArrayList();
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			for (Aggregator aggregator : aggregators) {
				dataList.add(new PieData(String.valueOf(record.get(groups.get(0))), record.get(aggregator.getName())));
			}

		}
		resultMap.put("dataList", dataList);
		return resultMap;
	}

	private Map<String, Object> getFunnelData(String id, ViewExecuteParam executeParam) {
		Page<Map<String, Object>> resultPage = getData(id, executeParam);
		List<Map<String, Object>> records = resultPage.getRecords();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> legend = Lists.newArrayList();
		List<String> xAxisData = Lists.newArrayList();
		List<String> yAxisData = Lists.newArrayList();
		List<String> groups = executeParam.getGroups();
		List<Aggregator> aggregators = executeParam.getAggregators();
		//一个指标对应一个Data
		Map<String, SeriesData> seriesDataMap = Maps.newHashMap();
		List<SeriesData> seriesDataList = Lists.newArrayList();
		for (Aggregator aggregator : aggregators) {
			SeriesData seriesData = new SeriesData();
			seriesData.setName(aggregator.getDisplayName());
			seriesData.setDataList(Lists.newArrayList());
			seriesDataMap.put(aggregator.getName(), seriesData);
			seriesDataList.add(seriesData);
			legend.add(aggregator.getDisplayName());
		}
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			for (Aggregator aggregator : aggregators) {
				seriesDataMap.get(aggregator.getName()).getDataList().add(new PieData(String.valueOf(record.get(groups.get(0))), record.get(aggregator.getName())));
			}
			legend.add(String.valueOf(record.get(groups.get(0))));

		}
		resultMap.put("legend", legend);
		resultMap.put("seriesDataList", seriesDataList);
		return resultMap;
	}

	private Map<String, Object> getRadarData(String id, ViewExecuteParam executeParam) {
		Page<Map<String, Object>> resultPage = getData(id, executeParam);
		List<Map<String, Object>> records = resultPage.getRecords();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> legend = Lists.newArrayList();
		List<String> groups = executeParam.getGroups();
		List<Aggregator> aggregators = executeParam.getAggregators();
		//一个指标对应一个Data
		Map<String, SeriesData> seriesDataMap = Maps.newHashMap();
		List<SeriesData> seriesDataList = Lists.newArrayList();
		List<Map<String, Object>> indicator = Lists.newArrayList();
		for (Aggregator aggregator : aggregators) {
			SeriesData seriesData = new SeriesData();
			seriesData.setName(aggregator.getDisplayName());
			seriesData.setDataList(Lists.newArrayList());
			seriesDataMap.put(aggregator.getName(), seriesData);
			seriesDataList.add(seriesData);
			legend.add(aggregator.getDisplayName());
		}
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			for (Aggregator aggregator : aggregators) {
				seriesDataMap.get(aggregator.getName()).getDataList().add(record.get(aggregator.getName()));
			}
			indicator.add(ImmutableMap.of("name", record.get(groups.get(0))));

		}
		resultMap.put("legend", legend);
		resultMap.put("indicator", indicator);
		resultMap.put("seriesDataList", seriesDataList);
		return resultMap;
	}

	private Map<String, Object> getPieData(String id, ViewExecuteParam executeParam) {
		Page<Map<String, Object>> resultPage = getData(id, executeParam);
		List<Map<String, Object>> records = resultPage.getRecords();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> legend = Lists.newArrayList();
		List<String> groups = executeParam.getGroups();
		List<Aggregator> aggregators = executeParam.getAggregators();
		//一个指标对应一个Data
		Map<String, SeriesData> seriesDataMap = Maps.newHashMap();
		List<SeriesData> seriesDataList = Lists.newArrayList();
		for (Aggregator aggregator : aggregators) {
			SeriesData seriesData = new SeriesData();
			seriesData.setName(aggregator.getDisplayName());
			seriesData.setDataList(Lists.newArrayList());
			seriesDataMap.put(aggregator.getName(), seriesData);
			seriesDataList.add(seriesData);
			legend.add(aggregator.getDisplayName());
		}
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			for (Aggregator aggregator : aggregators) {
				seriesDataMap.get(aggregator.getName()).getDataList().add(new PieData(String.valueOf(record.get(groups.get(0))), record.get(aggregator.getName())));
			}

		}
		resultMap.put("legend", legend);
		resultMap.put("seriesDataList", seriesDataList);
		return resultMap;
	}

	private Map<String, Object> getLineData(String id, ViewExecuteParam executeParam) {
		Page<Map<String, Object>> resultPage = getData(id, executeParam);
		List<Map<String, Object>> records = resultPage.getRecords();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> legend = Lists.newArrayList();
		List<String> xAxisData = Lists.newArrayList();
		List<String> yAxisData = Lists.newArrayList();
		List<String> groups = executeParam.getGroups();
		List<Aggregator> aggregators = executeParam.getAggregators();
		//一个指标对应一个Data
		Map<String, SeriesData> seriesDataMap = Maps.newHashMap();
		List<SeriesData> seriesDataList = Lists.newArrayList();
		for (Aggregator aggregator : aggregators) {
			SeriesData seriesData = new SeriesData();
			seriesData.setName(aggregator.getDisplayName());
			seriesData.setAxisIndex(aggregator.getAxisIndex());
			seriesData.setDataList(Lists.newArrayList());
			seriesDataMap.put(aggregator.getName(), seriesData);
			seriesDataList.add(seriesData);
			legend.add(aggregator.getDisplayName());
		}
		for (int i = 0; i < records.size(); i++) {
			Map<String, Object> record = records.get(i);
			for (Aggregator aggregator : aggregators) {
				seriesDataMap.get(aggregator.getName()).getDataList().add(record.get(aggregator.getName()));
			}
			xAxisData.add(String.valueOf(record.get(groups.get(0))));

		}
		resultMap.put("legend", legend);
		resultMap.put("xAxisData", xAxisData);
		resultMap.put("seriesDataList", seriesDataList);
		return resultMap;
	}
}
