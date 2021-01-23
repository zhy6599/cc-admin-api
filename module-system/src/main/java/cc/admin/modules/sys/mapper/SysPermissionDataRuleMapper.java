package cc.admin.modules.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import cc.admin.modules.sys.entity.SysPermissionDataRule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <getPageFilterFields>
 * 权限规则 Mapper 接口
 * </getPageFilterFields>
 *
 * @Author huangzhilin
 * @since 2019-04-01
 */
public interface SysPermissionDataRuleMapper extends BaseMapper<SysPermissionDataRule> {

	/**
	  * 根据用户名和权限id查询
	 * @param username
	 * @param permissionId
	 * @return
	 */
	public List<String> queryDataRuleIds(@Param("username") String username,@Param("permissionId") String permissionId);

}
