package cc.admin.modules.bi.service;

import cc.admin.modules.bi.entity.View;
import cc.admin.modules.bi.model.PaginateWithQueryColumns;
import cc.admin.modules.bi.model.TableInfo;
import cc.admin.modules.bi.model.ViewExecuteParam;
import cc.admin.modules.bi.model.ViewExecuteSql;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @Description: bi_view
 * @Author: ZhangHouYing
 * @Date:   2020-07-03
 * @Version: V1.0
 */
public interface IViewService extends IService<View> {

	Page<Map<String, Object>> getData(String id, ViewExecuteParam executeParam);

	public PaginateWithQueryColumns executeSql(ViewExecuteSql executeSql);

	TableInfo columns(String id, String dbName, String tableName);

	/**
	 * 获取图形所需要的数据
	 * @param id
	 * @param executeParam
	 * @return
	 */
	Map<String, Object> getChartData(String id, ViewExecuteParam executeParam);
}
