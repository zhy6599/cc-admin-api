package cc.admin.modules.app.service.impl;

import cc.admin.modules.app.service.IHdsService;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * 这里提供业务库相关操作方法
 *
 * @author: ZhangHouYing
 * @date: 2018-07-07 10:15
 */
@DS("hds")
@Service
@Slf4j
public class HdsServiceImpl implements IHdsService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private String schema;

	@Override
	public List<Map<String, Object>> queryForList(String sql) {
		return jdbcTemplate.queryForList(sql);
	}

}
