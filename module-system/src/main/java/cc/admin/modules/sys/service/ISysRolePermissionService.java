package cc.admin.modules.sys.service;

import cc.admin.modules.sys.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <getPageFilterFields>
 * 角色权限表 服务类
 * </getPageFilterFields>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysRolePermissionService extends IService<SysRolePermission> {

	/**
	 * 保存授权/先删后增
	 * @param roleId
	 * @param permissionIds
	 */
	public void saveRolePermission(String roleId,String permissionIds);

	/**
	 * 保存授权 将上次的权限和这次作比较 差异处理提高效率
	 * @param roleId
	 * @param permissionIds
	 * @param lastPermissionIds
	 */
	public void saveRolePermission(String roleId,String permissionIds,String lastPermissionIds);

}
