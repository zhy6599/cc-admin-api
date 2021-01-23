package cc.admin.modules.sys.service.impl;

import cc.admin.modules.bi.model.QueryColumn;
import cc.admin.modules.bi.util.SqlUtils;
import cc.admin.modules.sys.entity.SysDataSource;
import cc.admin.modules.sys.mapper.SysDataSourceMapper;
import cc.admin.modules.sys.service.ISysDataSourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Service
public class SysDataSourceServiceImpl extends ServiceImpl<SysDataSourceMapper, SysDataSource> implements ISysDataSourceService {

	@Autowired
	SysDataSourceMapper sysDataSourceMapper;

	@Override
	public List<String> databases(String id) {
		SysDataSource sysDataSource = sysDataSourceMapper.selectById(id);
		return SqlUtils.databases(sysDataSource.getDbUrl(),
				sysDataSource.getDbUsername(),
				sysDataSource.getDbPassword());
	}

	@Override
	public List<QueryColumn> tables(String id, String dbName) {
		SysDataSource sysDataSource = sysDataSourceMapper.selectById(id);
		return SqlUtils.tables(sysDataSource.getDbUrl(),
				sysDataSource.getDbUsername(),
				sysDataSource.getDbPassword(),dbName);
	}
}
