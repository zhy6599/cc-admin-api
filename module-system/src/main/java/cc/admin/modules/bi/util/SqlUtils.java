package cc.admin.modules.bi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import cc.admin.common.util.MD5Util;
import cc.admin.modules.bi.core.common.Consts;
import cc.admin.modules.bi.core.exception.ServerException;
import cc.admin.modules.bi.core.exception.SourceException;
import cc.admin.modules.bi.core.utils.SqlParseUtils;
import cc.admin.modules.bi.model.PaginateWithQueryColumns;
import cc.admin.modules.bi.model.QueryColumn;
import cc.admin.modules.bi.model.TableInfo;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;

import static cc.admin.modules.bi.core.common.Consts.*;
/**
 * @author /
 */
@Slf4j
public class SqlUtils {

	public static final String COLON = ":";

	private static volatile Map<String, DruidDataSource> map = new HashMap<>();
	private static int resultLimit = 100000;

	/**
	 * 检查敏感操作
	 *
	 * @param sql
	 * @throws ServerException
	 */
	public static void checkSensitiveSql(String sql) throws ServerException {
		Matcher matcher = PATTERN_SENSITIVE_SQL.matcher(sql.toLowerCase());
		if (matcher.find()) {
			String group = matcher.group();
			log.warn("Sensitive SQL operations are not allowed: {}", group.toUpperCase());
			throw new ServerException("Sensitive SQL operations are not allowed: " + group.toUpperCase());
		}
	}

	private static String getKey(String jdbcUrl, String username, String password) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(username)) {
			sb.append(username);
		}
		if (!StringUtils.isEmpty(password)) {
			sb.append(COLON).append(password);
		}
		sb.append(COLON).append(jdbcUrl.trim());
		return SecureUtil.md5(sb.toString());
	}

	/**
	 * 获取数据源
	 *
	 * @param jdbcUrl  /
	 * @param userName /
	 * @param password /
	 * @return DataSource
	 */
	private static DataSource getDataSource(String jdbcUrl, String userName, String password) {
		String key = getKey(jdbcUrl, userName, password);
		if (!map.containsKey(key) || null == map.get(key)) {
			DruidDataSource druidDataSource = new DruidDataSource();
			String className;
			try {
				className = DriverManager.getDriver(jdbcUrl.trim()).getClass().getName();
			} catch (SQLException e) {
				throw new RuntimeException("Get class name error: =" + jdbcUrl);
			}
			if (StringUtils.isEmpty(className)) {
				DataTypeEnum dataTypeEnum = DataTypeEnum.urlOf(jdbcUrl);
				if (null == dataTypeEnum) {
					throw new RuntimeException("Not supported data type: jdbcUrl=" + jdbcUrl);
				}
				druidDataSource.setDriverClassName(dataTypeEnum.getDriver());
			} else {
				druidDataSource.setDriverClassName(className);
			}
			druidDataSource.setUrl(jdbcUrl);
			druidDataSource.setUsername(userName);
			druidDataSource.setPassword(password);
			// 配置获取连接等待超时的时间
			druidDataSource.setMaxWait(3000);
			// 配置初始化大小、最小、最大
			druidDataSource.setInitialSize(1);
			druidDataSource.setMinIdle(1);
			druidDataSource.setMaxActive(1);
			// 配置间隔多久才进行一次检测需要关闭的空闲连接，单位是毫秒
			druidDataSource.setTimeBetweenEvictionRunsMillis(50000);
			// 配置一旦重试多次失败后等待多久再继续重试连接，单位是毫秒
			druidDataSource.setTimeBetweenConnectErrorMillis(18000);
			// 配置一个连接在池中最小生存的时间，单位是毫秒
			druidDataSource.setMinEvictableIdleTimeMillis(300000);
			// 这个特性能解决 MySQL 服务器8小时关闭连接的问题
			druidDataSource.setMaxEvictableIdleTimeMillis(25200000);
			try {
				druidDataSource.init();
			} catch (SQLException e) {
				log.error("Exception during pool initialization", e);
				throw new RuntimeException(e.getMessage());
			}
			map.put(key, druidDataSource);
		}
		return map.get(key);
	}

	private static Connection getConnection(String jdbcUrl, String userName, String password) {
		DataSource dataSource = getDataSource(jdbcUrl, userName, password);
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (Exception ignored) {
		}
		try {
			int timeOut = 5;
			if (null == connection || connection.isClosed() || !connection.isValid(timeOut)) {
				log.info("connection is closed or invalid, retry get connection!");
				connection = dataSource.getConnection();
			}
		} catch (Exception e) {
			log.error("create connection error, jdbcUrl: {}", jdbcUrl);
			throw new RuntimeException("create connection error, jdbcUrl: " + jdbcUrl);
		}
		return connection;
	}

	private static void releaseConnection(Connection connection) {
		if (null != connection) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("connection close error：" + e.getMessage());
			}
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

	public static boolean testConnection(String jdbcUrl, String userName, String password) {
		Connection connection = null;
		try {
			connection = getConnection(jdbcUrl, userName, password);
			if (null != connection) {
				return true;
			}
		} catch (Exception e) {
			log.info("Get connection failed:" + e.getMessage());
		} finally {
			releaseConnection(connection);
		}
		return false;
	}

	public static List<String> databases(String jdbcUrl, String userName, String password) {
		List<String> databaseList = Lists.newArrayList();
		databaseList.add(JdbcUrlUtil.findDataBaseNameByUrl(jdbcUrl));
		return databaseList;
	}

	public static List<QueryColumn> tables(String jdbcUrl, String userName, String password, String dbName) {
		List<QueryColumn> tableList = Lists.newArrayList();
		Connection connection = null;
		try {
			connection = getConnection(jdbcUrl, userName, password);
			if (null != connection) {
				DatabaseMetaData metaData = connection.getMetaData();
				String schema = null;
				try {
					schema = metaData.getConnection().getSchema();
				} catch (Throwable t) {
					// ignore
				}
				ResultSet tables = metaData.getTables(dbName, null, "%", TABLE_TYPES);
				if (null == tables) {
					return tableList;
				}
				tableList = new ArrayList<>();
				while (tables.next()) {
					String name = tables.getString(TABLE_NAME);
					if (!StringUtils.isEmpty(name)) {
						String type = TABLE;
						try {
							type = tables.getString(TABLE_TYPE);
						} catch (Exception e) {
							// ignore
						}
						tableList.add(new QueryColumn(name, type));
					}
				}
			}
		} catch (Exception e) {
			log.info("Get connection failed:" + e.getMessage());
		} finally {
			releaseConnection(connection);
		}
		return tableList;
	}

	public static String execute(String jdbcUrl, String userName, String password, String sql) {
		Connection connection = getConnection(jdbcUrl, userName, password);
		try {
			Statement st = connection.createStatement();
			st.execute(sql);
		} catch (Exception e) {
			log.error("sql脚本执行发生异常:{}", e.getMessage());
			return e.getMessage();
		} finally {
			releaseConnection(connection);
		}
		return "success";
	}

	public static List<Map<String, Object>> executeSql(String jdbcUrl, String userName, String password, String sql) {
		List<Map<String, Object>> resultList = Lists.newArrayList();
		Connection connection = getConnection(jdbcUrl, userName, password);
		try {
			PreparedStatement pst = connection.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			return convertList(rs);
		} catch (Exception e) {
			log.error("sql脚本执行发生异常:{}", e.getMessage());
		} finally {
			releaseConnection(connection);
		}
		return resultList;
	}

	public static List<Map<String, Object>> convertList(ResultSet rs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> rowData = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				closeResult(rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static Map<String, Object> convertMap(ResultSet rs) {
		Map<String, Object> map = new TreeMap<String, Object>();
		try {
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					map.put(md.getColumnName(i), rs.getObject(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResult(rs);
			return map;
		}
	}

	/**
	 * 批量执行sql
	 *
	 * @param connection /
	 * @param sqlList    /
	 */
	public static void batchExecute(Connection connection, List<String> sqlList) throws SQLException {
		Statement st = connection.createStatement();
		for (String sql : sqlList) {
			if (sql.endsWith(";")) {
				sql = sql.substring(0, sql.length() - 1);
			}
			st.addBatch(sql);
		}
		st.executeBatch();
	}

	/**
	 * 将文件中的sql语句以；为单位读取到列表中
	 *
	 * @param sqlFile /
	 * @return /
	 * @throws Exception getUuid
	 */
	private static List<String> readSqlList(File sqlFile) throws Exception {
		List<String> sqlList = Lists.newArrayList();
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {
			String tmp;
			while ((tmp = reader.readLine()) != null) {
				log.info("line:{}", tmp);
				if (tmp.endsWith(";")) {
					sb.append(tmp);
					sqlList.add(sb.toString());
					sb.delete(0, sb.length());
				} else {
					sb.append(tmp);
				}
			}
			if (!"".endsWith(sb.toString().trim())) {
				sqlList.add(sb.toString());
			}
		}
		return sqlList;
	}


	/**
	 * 过滤sql中的注释
	 *
	 * @param sql
	 * @return
	 */
	public static String filterAnnotate(String sql) {
		sql = PATTERN_SQL_ANNOTATE.matcher(sql).replaceAll("$1");
		sql = sql.replaceAll(NEW_LINE_CHAR, SPACE).replaceAll("(;+\\s*)+", SEMICOLON);
		return sql;
	}

	public static char getSqlTempDelimiter(String sqlTempDelimiter) {
		return sqlTempDelimiter.charAt(sqlTempDelimiter.length() - 1);
	}

	public static PaginateWithQueryColumns syncQuery4Paginate(String jdbcUrl, String userName, String password, String sql, Integer pageNo, Integer pageSize, Integer totalCount, Integer limit, Set<String> excludeColumns) throws Exception {
		if (null == pageNo || pageNo < 1) {
			pageNo = 0;
		}
		if (null == pageSize || pageSize < 1) {
			pageSize = 0;
		}
		if (null == totalCount || totalCount < 1) {
			totalCount = 0;
		}
		if (null == limit) {
			limit = -1;
		}
		PaginateWithQueryColumns paginate = query4Paginate( jdbcUrl,  userName,  password,sql, pageNo, pageSize, totalCount, limit, excludeColumns);
		return paginate;
	}


	public static JdbcTemplate jdbcTemplate(String jdbcUrl, String userName, String password) {
		DataSource dataSource = getDataSource(jdbcUrl, userName, password);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setFetchSize(1000);
		return jdbcTemplate;
	}

	public static PaginateWithQueryColumns query4Paginate(String jdbcUrl, String userName, String password, String sql, int pageNo, int pageSize, int totalCount, int limit, Set<String> excludeColumns) throws Exception {
		PaginateWithQueryColumns paginateWithQueryColumns = new PaginateWithQueryColumns();
		sql = filterAnnotate(sql);
		checkSensitiveSql(sql);

		String md5 = MD5Util.MD5Encode(sql + pageNo + pageSize + limit, null);

		long before = System.currentTimeMillis();

		JdbcTemplate jdbcTemplate = jdbcTemplate(jdbcUrl, userName, password);
		jdbcTemplate.setMaxRows(resultLimit);

		if (pageNo < 1 && pageSize < 1) {

			if (limit > 0) {
				resultLimit = limit > resultLimit ? resultLimit : limit;
			}

			log.info("{}  >> \n{}", md5, sql);

			jdbcTemplate.setMaxRows(resultLimit);
			getResultForPaginate(sql, paginateWithQueryColumns, jdbcTemplate, excludeColumns, -1);
			paginateWithQueryColumns.setPages(1);

		} else {
			paginateWithQueryColumns.setPages(pageNo);
			paginateWithQueryColumns.setSize(pageSize);

			int startRow = (pageNo - 1) * pageSize;

			if (pageNo == 1 || totalCount == 0) {
				Object o = jdbcTemplate.queryForList(getCountSql(sql), Object.class).get(0);
				totalCount = Integer.parseInt(String.valueOf(o));
			}

			if (limit > 0) {
				limit = limit > resultLimit ? resultLimit : limit;
				totalCount = limit < totalCount ? limit : totalCount;
			}

			paginateWithQueryColumns.setTotal(totalCount);

			sql = sql + " LIMIT " + startRow + ", " + pageSize;
			md5 = MD5Util.MD5Encode(sql, null);
			log.info("{}  >> \n{}", md5, sql);
			getResultForPaginate(sql, paginateWithQueryColumns, jdbcTemplate, excludeColumns, startRow);
		}

		log.info("{} query for >> {} ms", md5, System.currentTimeMillis() - before);

		return paginateWithQueryColumns;
	}

	private static void getResultForPaginate(String sql, PaginateWithQueryColumns paginateWithQueryColumns, JdbcTemplate jdbcTemplate, Set<String> excludeColumns, int startRow) {
		Set<String> queryFromsAndJoins = getQueryFromsAndJoins(sql);
		jdbcTemplate.query(sql, rs -> {
			if (null == rs) {
				return paginateWithQueryColumns;
			}

			ResultSetMetaData metaData = rs.getMetaData();
			List<QueryColumn> queryColumns = new ArrayList<>();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				String key = getColumnLabel(queryFromsAndJoins, metaData.getColumnLabel(i));
				if (!CollUtil.isEmpty(excludeColumns) && excludeColumns.contains(key)) {
					continue;
				}
				queryColumns.add(new QueryColumn(key, metaData.getColumnTypeName(i)));
			}
			paginateWithQueryColumns.setColumns(queryColumns);

			List<Map<String, Object>> resultList = new ArrayList<>();

			try {
				if (startRow > 0) {
					rs.absolute(startRow);
				}
				while (rs.next()) {
					resultList.add(getResultObjectMap(excludeColumns, rs, metaData, queryFromsAndJoins));
				}
			} catch (Throwable e) {
				int currentRow = 0;
				while (rs.next()) {
					if (currentRow >= startRow) {
						resultList.add(getResultObjectMap(excludeColumns, rs, metaData, queryFromsAndJoins));
					}
					currentRow++;
				}
			}

			paginateWithQueryColumns.setRecords(resultList);

			return paginateWithQueryColumns;
		});
	}


	private static Map<String, Object> getResultObjectMap(Set<String> excludeColumns, ResultSet rs, ResultSetMetaData metaData, Set<String> queryFromsAndJoins) throws SQLException {
		Map<String, Object> map = new LinkedHashMap<>();

		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			String key = metaData.getColumnLabel(i);
			String label = getColumnLabel(queryFromsAndJoins, key);

			if (!CollUtil.isEmpty(excludeColumns) && excludeColumns.contains(label)) {
				continue;
			}
			map.put(label, rs.getObject(key));
		}
		return map;
	}


	public static Set<String> getQueryFromsAndJoins(String sql) {
		Set<String> columnPrefixs = new HashSet<>();
		try {
			net.sf.jsqlparser.statement.Statement parse = CCJSqlParserUtil.parse(sql);
			Select select = (Select) parse;
			SelectBody selectBody = select.getSelectBody();
			if (selectBody instanceof PlainSelect) {
				PlainSelect plainSelect = (PlainSelect) selectBody;
				columnPrefixExtractor(columnPrefixs, plainSelect);
			}

			if (selectBody instanceof SetOperationList) {
				SetOperationList setOperationList = (SetOperationList) selectBody;
				List<SelectBody> selects = setOperationList.getSelects();
				for (SelectBody optSelectBody : selects) {
					PlainSelect plainSelect = (PlainSelect) optSelectBody;
					columnPrefixExtractor(columnPrefixs, plainSelect);
				}
			}

			if (selectBody instanceof WithItem) {
				WithItem withItem = (WithItem) selectBody;
				PlainSelect plainSelect = (PlainSelect) withItem.getSelectBody();
				columnPrefixExtractor(columnPrefixs, plainSelect);
			}
		} catch (JSQLParserException e) {
			log.debug(e.getMessage(), e);
		}
		return columnPrefixs;
	}


	private static void columnPrefixExtractor(Set<String> columnPrefixs, PlainSelect plainSelect) {
		getFromItemName(columnPrefixs, plainSelect.getFromItem());
		List<Join> joins = plainSelect.getJoins();
		if (!CollUtil.isEmpty(joins)) {
			joins.forEach(join -> getFromItemName(columnPrefixs, join.getRightItem()));
		}
	}


	public static String getCountSql(String sql) {
		String countSql = String.format(Consts.QUERY_COUNT_SQL, sql);
		try {
			Select select = (Select) CCJSqlParserUtil.parse(sql);
			PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
			plainSelect.setOrderByElements(null);
			countSql = String.format(QUERY_COUNT_SQL, select.toString());
		} catch (JSQLParserException e) {
			log.debug(e.getMessage(), e);
		}
		return SqlParseUtils.rebuildSqlWithFragment(countSql);
	}

	private static void getFromItemName(Set<String> columnPrefixs, FromItem fromItem) {
		Alias alias = fromItem.getAlias();
		if (alias != null) {
			if (alias.isUseAs()) {
				columnPrefixs.add(alias.getName().trim() + DOT);
			} else {
				columnPrefixs.add(alias.toString().trim() + DOT);
			}
		} else {
			fromItem.accept(getFromItemTableName(columnPrefixs));
		}
	}


	private static FromItemVisitor getFromItemTableName(Set<String> set) {
		return new FromItemVisitor() {
			@Override
			public void visit(Table tableName) {
				set.add(tableName.getName() + DOT);
			}

			@Override
			public void visit(SubSelect subSelect) {
			}

			@Override
			public void visit(SubJoin subjoin) {
			}

			@Override
			public void visit(LateralSubSelect lateralSubSelect) {
			}

			@Override
			public void visit(ValuesList valuesList) {
			}

			@Override
			public void visit(TableFunction tableFunction) {
			}

			@Override
			public void visit(ParenthesisFromItem aThis) {
			}
		};
	}


	public static String getColumnLabel(Set<String> columnPrefixs, String columnLable) {
		if (!CollUtil.isEmpty(columnPrefixs)) {
			for (String prefix : columnPrefixs) {
				if (columnLable.startsWith(prefix)) {
					return columnLable.replaceFirst(prefix, EMPTY);
				}
				if (columnLable.startsWith(prefix.toLowerCase())) {
					return columnLable.replaceFirst(prefix.toLowerCase(), EMPTY);
				}
				if (columnLable.startsWith(prefix.toUpperCase())) {
					return columnLable.replaceFirst(prefix.toUpperCase(), EMPTY);
				}
			}
		}
		return columnLable;
	}

	/**
	 * 获取指定表列信息
	 *
	 * @param tableName
	 * @return
	 * @throws SourceException
	 */
	public static TableInfo getTableInfo(String jdbcUrl, String username, String password, String dbName, String tableName) throws SourceException {
		TableInfo tableInfo = null;
		Connection connection = null;
		try {
			connection = getConnection(jdbcUrl,username,password);
			if (null != connection) {
				DatabaseMetaData metaData = connection.getMetaData();
				List<String> primaryKeys = getPrimaryKeys(dbName, tableName, metaData);
				List<QueryColumn> columns = getColumns(dbName, tableName, metaData);
				tableInfo = new TableInfo(tableName, primaryKeys, columns);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new SourceException(e.getMessage() + ", jdbcUrl=" + jdbcUrl);
		} finally {
			releaseConnection(connection);
		}
		return tableInfo;
	}


	/**
	 * 获取数据表主键
	 *
	 * @param tableName
	 * @param metaData
	 * @return
	 * @throws ServerException
	 */
	private static List<String> getPrimaryKeys(String dbName, String tableName, DatabaseMetaData metaData) throws ServerException {
		ResultSet rs = null;
		List<String> primaryKeys = new ArrayList<>();
		try {
			rs = metaData.getPrimaryKeys(dbName, null, tableName);
			if (rs == null) {
				return primaryKeys;
			}
			while (rs.next()) {
				primaryKeys.add(rs.getString("COLUMN_NAME"));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeResult(rs);
		}
		return primaryKeys;
	}


	/**
	 * 获取数据表列
	 *
	 * @param tableName
	 * @param metaData
	 * @return
	 * @throws ServerException
	 */
	private static List<QueryColumn> getColumns(String dbName, String tableName, DatabaseMetaData metaData) throws ServerException {
		ResultSet rs = null;
		List<QueryColumn> columnList = new ArrayList<>();
		try {
			rs = metaData.getColumns(dbName, null, tableName, "%");
			if (rs == null) {
				return columnList;
			}
			while (rs.next()) {
				columnList.add(new QueryColumn(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME")));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeResult(rs);
		}
		return columnList;
	}

}
