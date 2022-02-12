package cc.admin.modules.sys.service;

import cc.admin.common.exception.CcAdminException;
import cc.admin.modules.sys.entity.SysPermission;
import cc.admin.modules.sys.model.SysPermissionTree;
import cc.admin.modules.sys.model.TreeModel;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <getPageFilterFields>
 * 菜单权限表 服务类
 * </getPageFilterFields>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysPermissionService extends IService<SysPermission> {

	public List<TreeModel> queryListByParentId(String parentId);

	/**真实删除*/
	public void deletePermission(String id) throws CcAdminException;
	/**逻辑删除*/
	public void deletePermissionLogical(String id) throws CcAdminException;

	public void addPermission(SysPermission sysPermission) throws CcAdminException;

	public void editPermission(SysPermission sysPermission) throws CcAdminException;

	public List<SysPermission> queryByUser(String username);

	/**
	 * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
	 *
	 * @param id
	 * @return
	 */
	public void deletePermRuleByPermId(String id);

	/**
	 * 查询出带有特殊符号的菜单地址的集合
	 * @return
	 */
	public List<String> queryPermissionUrlWithStar();

	/**
	 * 判断用户否拥有权限
	 * @param username
	 * @param sysPermission
	 * @return
	 */
	public boolean hasPermission(String username, SysPermission sysPermission);

	/**
	 * 根据用户和请求地址判断是否有此权限
	 * @param username
	 * @param url
	 * @return
	 */
	public boolean hasPermission(String username, String url);

	/**
	 * 查询所有子菜单
	 * @param parentId
	 * @return
	 */
	public List<SysPermissionTree> getSystemSubmenu(String parentId);

	/**
	 * 根据用户名获取用户权限信息
	 *
	 * @param username
	 * @return
	 */
	JSONObject getUserPermissionByUserName(String username);
}
