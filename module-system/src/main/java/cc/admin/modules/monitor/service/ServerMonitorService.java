package cc.admin.modules.monitor.service;

import java.util.Map;
/**
 * @author: ZhangHouYing
 * @date: 2021-01-20 11:08
 */
public interface ServerMonitorService {

	/**
	 * 获取服务器监控信息
	 * @return
	 */
	public Map<String, Object> getServerInfo();
}
