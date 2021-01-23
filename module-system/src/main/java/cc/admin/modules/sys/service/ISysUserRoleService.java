package cc.admin.modules.sys.service;

import cc.admin.modules.sys.entity.SysUserRole;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <getPageFilterFields>
 * 用户角色表 服务类
 * </getPageFilterFields>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysUserRoleService extends IService<SysUserRole> {
	/**
	 * 将角色对应的所有用户删除
	 * @param roleId
	 */
	void removeAllByRoleId(String roleId);
}
