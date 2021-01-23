package cc.admin.modules.sys.service;

import cc.admin.modules.bi.model.QueryColumn;
import cc.admin.modules.sys.entity.SysDataSource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
public interface ISysDataSourceService extends IService<SysDataSource> {

	/**
	 * 查询数据库列表
	 * @param id
	 * @return
	 */
	List<String> databases(String id);

	/**
	 * 查询表列表
	 * @param id
	 * @param dbName
	 * @return
	 */
	List<QueryColumn> tables(String id, String dbName);
}
