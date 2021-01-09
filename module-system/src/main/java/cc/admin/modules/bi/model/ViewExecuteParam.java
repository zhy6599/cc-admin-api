package cc.admin.modules.bi.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cc.admin.modules.bi.core.common.Consts.*;

@Data
public class ViewExecuteParam {
	private List<Group> groups;
    private List<Aggregator> aggregators;
    private List<Order> orders;
    private List<String> filters;
    private List<Param> params;
    private Boolean cache;
    private Long expired;
	/**
	 * 图形类型
	 */
	private String type;
	/**
	 * 水平表格
	 */
	private Boolean horizontal = false;
    private Boolean flush = false;
    private int limit = 0;
    private int pageNo = -1;
    private int pageSize = -1;
    private int totalCount = 0;

    private boolean nativeQuery = false;

    public ViewExecuteParam() {
	}

	public ViewExecuteParam(List<Group> groupList,
							List<Aggregator> aggregators,
							List<Order> orders,
							List<String> filterList,
							List<Param> params,
							Boolean cache,
							Long expired,
							Boolean nativeQuery) {
        this.groups = groupList;
        this.aggregators = aggregators;
        this.orders = orders;
        this.filters = filterList;
        this.params = params;
        this.cache = cache;
        this.expired = expired;
        this.nativeQuery = nativeQuery;
    }

    public List<String> getFilters() {
        if (CollUtil.isNotEmpty(this.filters)) {
            this.filters = filters.stream().filter(f -> !StringUtils.isEmpty(f)).collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(this.filters)) {
            return null;
        }

        return this.filters;
    }

    public List<Order> getOrders(String jdbcUrl, String dbVersion) {
        List<Order> list = null;
        if (CollUtil.isNotEmpty(orders)) {
            list = new ArrayList<>();

            for (Order order : this.orders) {
                String column = order.getColumn().trim();
                StringBuilder columnBuilder = new StringBuilder();
                columnBuilder.append(column);
                order.setColumn(columnBuilder.toString());
                list.add(order);
            }
        }
        return list;
    }

    public void addExcludeColumn(Set<String> excludeColumns, String jdbcUrl, String dbVersion) {
        if (CollUtil.isNotEmpty(excludeColumns) && CollUtil.isNotEmpty(aggregators)) {
            excludeColumns.addAll(this.aggregators.stream()
                    .filter(a -> CollUtil.isNotEmpty(excludeColumns) && excludeColumns.contains(a.getColumn()))
                    .map(a -> formatColumn(a.getColumn(), a.getFunc(), jdbcUrl, dbVersion, true))
                    .collect(Collectors.toSet())
            );
        }
    }

    public List<String> getAggregators(String jdbcUrl, String dbVersion) {
        if (CollUtil.isNotEmpty(aggregators)) {
            return this.aggregators.stream().map(a -> formatColumn(a.getColumn(), a.getFunc(), jdbcUrl, dbVersion, false)).collect(Collectors.toList());
        }
        return null;
    }


    private String formatColumn(String column, String func, String jdbcUrl, String dbVersion, boolean isLable) {
    	if(StrUtil.isEmpty(func)){
    		return column;
		}
        if (isLable) {
            return String.join(EMPTY, func.trim(), PARENTHESES_START, column.trim(), PARENTHESES_END);
        } else {
            StringBuilder sb = new StringBuilder();
            if ("COUNTDISTINCT".equals(func.trim().toUpperCase())) {
                sb.append("COUNT").append(PARENTHESES_START).append("DISTINCT").append(SPACE);
                sb.append(ViewExecuteParam.getField(column, jdbcUrl, dbVersion));
                sb.append(PARENTHESES_END);
                sb.append(" AS `").append("COUNTDISTINCT").append(PARENTHESES_START);
                sb.append(column);
                sb.append(PARENTHESES_END).append("`");
            } else {
                sb.append(func.trim()).append(PARENTHESES_START);
                sb.append(ViewExecuteParam.getField(column, jdbcUrl, dbVersion));
                sb.append(PARENTHESES_END);
                sb.append(" AS `");
                sb.append(func.trim()).append(PARENTHESES_START);
                sb.append(column);
                sb.append(PARENTHESES_END).append("`");
            }
            return sb.toString();
        }
    }

    public static String getField(String field, String jdbcUrl, String dbVersion) {
        return field;
	}

	public List<String> getSqlGroups() {
		List<String> sqlGroups = Lists.newArrayList();
		if (CollUtil.isNotEmpty(this.groups)) {
			sqlGroups = groups.stream().map(g -> g.getName()).collect(Collectors.toList());
		}
		return sqlGroups;
	}
}
