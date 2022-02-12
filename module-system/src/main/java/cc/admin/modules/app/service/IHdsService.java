package cc.admin.modules.app.service;

import java.util.List;
import java.util.Map;
/**
 * 操作业务库
 *
 * @author: ZhangHouYing
 * @date: 2018-07-07 10:15
 */
public interface IHdsService {

	/**
	 * 查询数据列表
	 * @param sql
	 * @return
	 */
	public List<Map<String, Object>> queryForList(String sql);

}
