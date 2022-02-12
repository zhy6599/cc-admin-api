package cc.admin.modules.sys.service.impl;

import cc.admin.common.constant.CacheConstant;
import cc.admin.common.constant.CommonConstant;
import cc.admin.common.exception.CcAdminException;
import cc.admin.common.util.MD5Util;
import cc.admin.common.util.oConvertUtils;
import cc.admin.modules.sys.entity.SysPermission;
import cc.admin.modules.sys.entity.SysPermissionDataRule;
import cc.admin.modules.sys.mapper.SysDepartPermissionMapper;
import cc.admin.modules.sys.mapper.SysDepartRolePermissionMapper;
import cc.admin.modules.sys.mapper.SysPermissionMapper;
import cc.admin.modules.sys.mapper.SysRolePermissionMapper;
import cc.admin.modules.sys.model.SysPermissionTree;
import cc.admin.modules.sys.model.TreeModel;
import cc.admin.modules.sys.service.ISysPermissionDataRuleService;
import cc.admin.modules.sys.service.ISysPermissionService;
import cc.admin.modules.sys.util.PermissionDataUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <getPageFilterFields>
 * 菜单权限表 服务实现类
 * </getPageFilterFields>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

	@Resource
	private SysPermissionMapper sysPermissionMapper;

	@Resource
	private ISysPermissionDataRuleService permissionDataRuleService;

	@Resource
	private SysRolePermissionMapper sysRolePermissionMapper;

	@Resource
	private SysDepartPermissionMapper sysDepartPermissionMapper;

	@Resource
	private SysDepartRolePermissionMapper sysDepartRolePermissionMapper;

	@Override
	public List<TreeModel> queryListByParentId(String parentId) {
		return sysPermissionMapper.queryListByParentId(parentId);
	}

	/**
	 * 真实删除
	 */
	@Override
	@Transactional
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
	public void deletePermission(String id) throws CcAdminException {
		SysPermission sysPermission = this.getById(id);
		if (sysPermission == null) {
			throw new CcAdminException("未找到菜单信息");
		}
		String pid = sysPermission.getParentId();
		if (oConvertUtils.isNotEmpty(pid)) {
			int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
			if (count == 1) {
				//若父节点无其他子节点，则该父节点是叶子节点
				this.sysPermissionMapper.setMenuLeaf(pid, 1);
			}
		}
		sysPermissionMapper.deleteById(id);
		// 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
		this.removeChildrenBy(sysPermission.getId());
		//关联删除
		Map map = new HashMap<>();
		map.put("permission_id", id);
		//删除数据规则
		this.deletePermRuleByPermId(id);
		//删除角色授权表
		sysRolePermissionMapper.deleteByMap(map);
		//删除部门权限表
		sysDepartPermissionMapper.deleteByMap(map);
		//删除部门角色授权
		sysDepartRolePermissionMapper.deleteByMap(map);
	}

	/**
	 * 根据父id删除其关联的子节点数据
	 *
	 * @return
	 */
	public void removeChildrenBy(String parentId) {
		LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
		// 封装查询条件parentId为主键,
		query.eq(SysPermission::getParentId, parentId);
		// 查出该主键下的所有子级
		List<SysPermission> permissionList = this.list(query);
		if (permissionList != null && permissionList.size() > 0) {
			String id = "";
			// 查出的子级数量
			int num = 0;
			// 如果查出的集合不为空, 则先删除所有
			this.remove(query);
			// 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
			for (int i = 0, len = permissionList.size(); i < len; i++) {
				id = permissionList.get(i).getId();
				Map map = new HashMap<>();
				map.put("permission_id", id);
				//删除数据规则
				this.deletePermRuleByPermId(id);
				//删除角色授权表
				sysRolePermissionMapper.deleteByMap(map);
				//删除部门权限表
				sysDepartPermissionMapper.deleteByMap(map);
				//删除部门角色授权
				sysDepartRolePermissionMapper.deleteByMap(map);
				num = this.count(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
				// 如果有, 则递归
				if (num > 0) {
					this.removeChildrenBy(id);
				}
			}
		}
	}

	/**
	 * 逻辑删除
	 */
	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
	//@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true,condition="#sysPermission.menuType==2")
	public void deletePermissionLogical(String id) throws CcAdminException {
		SysPermission sysPermission = this.getById(id);
		if (sysPermission == null) {
			throw new CcAdminException("未找到菜单信息");
		}
		String pid = sysPermission.getParentId();
		int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
		if (count == 1) {
			//若父节点无其他子节点，则该父节点是叶子节点
			this.sysPermissionMapper.setMenuLeaf(pid, 1);
		}
		sysPermission.setDelFlag(1);
		this.updateById(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
	public void addPermission(SysPermission sysPermission) throws CcAdminException {
		//----------------------------------------------------------------------
		//判断是否是一级菜单，是的话清空父菜单
		if (CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
			sysPermission.setParentId(null);
		}
		//----------------------------------------------------------------------
		String pid = sysPermission.getParentId();
		if (oConvertUtils.isNotEmpty(pid)) {
			//设置父节点不为叶子节点
			this.sysPermissionMapper.setMenuLeaf(pid, 0);
		}
		sysPermission.setCreateTime(new Date());
		sysPermission.setDelFlag(0);
		sysPermission.setLeaf(true);
		this.save(sysPermission);
	}

	@Override
	@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
	public void editPermission(SysPermission sysPermission) throws CcAdminException {
		SysPermission p = this.getById(sysPermission.getId());
		if (p == null) {
			throw new CcAdminException("未找到菜单信息");
		} else {
			sysPermission.setUpdateTime(new Date());
			//----------------------------------------------------------------------
			//Step1.判断是否是一级菜单，是的话清空父菜单ID
			if (CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
				sysPermission.setParentId("");
			}
			//Step2.判断菜单下级是否有菜单，无则设置为叶子节点
			int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, sysPermission.getId()));
			if (count == 0) {
				sysPermission.setLeaf(true);
			}
			//----------------------------------------------------------------------
			this.updateById(sysPermission);
			//如果当前菜单的父菜单变了，则需要修改新父菜单和老父菜单的，叶子节点状态
			String pid = sysPermission.getParentId();
			if ((oConvertUtils.isNotEmpty(pid) && !pid.equals(p.getParentId())) || oConvertUtils.isEmpty(pid) && oConvertUtils.isNotEmpty(p.getParentId())) {
				//setProjectPath.设置新的父菜单不为叶子节点
				this.sysPermissionMapper.setMenuLeaf(pid, 0);
				//setTemplatePath.判断老的菜单下是否还有其他子菜单，没有的话则设置为叶子节点
				int cc = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, p.getParentId()));
				if (cc == 0) {
					if (oConvertUtils.isNotEmpty(p.getParentId())) {
						this.sysPermissionMapper.setMenuLeaf(p.getParentId(), 1);
					}
				}

			}
		}

	}

	@Override
	public List<SysPermission> queryByUser(String username) {
		return this.sysPermissionMapper.queryByUser(username);
	}

	/**
	 * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
	 */
	@Override
	public void deletePermRuleByPermId(String id) {
		LambdaQueryWrapper<SysPermissionDataRule> query = new LambdaQueryWrapper<>();
		query.eq(SysPermissionDataRule::getPermissionId, id);
		int countValue = this.permissionDataRuleService.count(query);
		if (countValue > 0) {
			this.permissionDataRuleService.remove(query);
		}
	}

	/**
	 * 获取模糊匹配规则的数据权限URL
	 */
	@Override
	@Cacheable(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE)
	public List<String> queryPermissionUrlWithStar() {
		return this.baseMapper.queryPermissionUrlWithStar();
	}

	@Override
	public boolean hasPermission(String username, SysPermission sysPermission) {
		int count = baseMapper.queryCountByUsername(username, sysPermission);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasPermission(String username, String url) {
		SysPermission sysPermission = new SysPermission();
		sysPermission.setUrl(url);
		int count = baseMapper.queryCountByUsername(username, sysPermission);
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<SysPermissionTree> getSystemSubmenu(String parentId) {
		List<SysPermissionTree> result = Lists.newArrayList();
		try {
			LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
			query.eq(SysPermission::getParentId, parentId);
			query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
			query.orderByAsc(SysPermission::getSortNo);
			List<SysPermission> list = list(query);
			for (SysPermission sysPermission : list) {
				SysPermissionTree sysPermissionTree = new SysPermissionTree(sysPermission);
				result.add(sysPermissionTree);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public JSONObject getUserPermissionByUserName(String username) {
		List<SysPermission> metaList = queryByUser(username);
		//添加首页路由
		//update-begin-author:taoyan date:20200211 for: TASK #3368 【路由缓存】首页的缓存设置有问题，需要根据后台的路由配置来实现是否缓存
		if (!PermissionDataUtil.hasIndexPage(metaList) && metaList.size() > 0) {
			SysPermission indexMenu = list(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName, "首页")).get(0);
			metaList.add(0, indexMenu);
		}
		//update-end-author:taoyan date:20200211 for: TASK #3368 【路由缓存】首页的缓存设置有问题，需要根据后台的路由配置来实现是否缓存
		JSONObject json = new JSONObject();
		JSONArray menujsonArray = new JSONArray();
		this.getPermissionJsonArray(menujsonArray, metaList, null);
		JSONArray authjsonArray = new JSONArray();
		getAuthJsonArray(authjsonArray, metaList);
		//查询所有的权限
		LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
		query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
		query.eq(SysPermission::getMenuType, CommonConstant.MENU_TYPE_2);
		//query.eq(SysPermission::getStatus, "1");
		List<SysPermission> allAuthList = list(query);
		JSONArray allauthjsonArray = new JSONArray();
		getAllAuthJsonArray(allauthjsonArray, allAuthList);
		//路由菜单
		json.put("menu", menujsonArray);
		//按钮权限（用户拥有的权限集合）
		json.put("auth", authjsonArray);
		//全部权限配置集合（按钮权限，访问权限）
		json.put("allAuth", allauthjsonArray);
		return json;
	}

	/**
	 * 获取权限JSON数组
	 *
	 * @param jsonArray
	 * @param allList
	 */
	private void getAllAuthJsonArray(JSONArray jsonArray, List<SysPermission> allList) {
		JSONObject json = null;
		for (SysPermission permission : allList) {
			json = new JSONObject();
			json.put("action", permission.getPerms());
			json.put("status", permission.getStatus());
			json.put("type", permission.getPermsType());
			json.put("describe", permission.getName());
			jsonArray.add(json);
		}
	}

	/**
	 * 获取权限JSON数组
	 *
	 * @param jsonArray
	 * @param metaList
	 */
	private void getAuthJsonArray(JSONArray jsonArray, List<SysPermission> metaList) {
		for (SysPermission permission : metaList) {
			if (permission.getMenuType() == null) {
				continue;
			}
			JSONObject json = null;
			if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2) && CommonConstant.STATUS_1.equals(permission.getStatus())) {
				json = new JSONObject();
				json.put("action", permission.getPerms());
				json.put("type", permission.getPermsType());
				json.put("describe", permission.getName());
				jsonArray.add(json);
			}
		}
	}

	/**
	 * 获取菜单JSON数组
	 *
	 * @param jsonArray
	 * @param metaList
	 * @param parentJson
	 */
	private void getPermissionJsonArray(JSONArray jsonArray, List<SysPermission> metaList, JSONObject parentJson) {
		for (SysPermission permission : metaList) {
			if (permission.getMenuType() == null) {
				continue;
			}
			String tempPid = permission.getParentId();
			JSONObject json = getPermissionJsonObject(permission);
			if (json == null) {
				continue;
			}
			if (parentJson == null && oConvertUtils.isEmpty(tempPid)) {
				jsonArray.add(json);
				if (!permission.isLeaf()) {
					getPermissionJsonArray(jsonArray, metaList, json);
				}
			} else if (parentJson != null && oConvertUtils.isNotEmpty(tempPid) && tempPid.equals(parentJson.getString("id"))) {
				// 类型( 0：一级菜单 1：子菜单 2：按钮 )
				if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
					JSONObject metaJson = parentJson.getJSONObject("meta");
					if (metaJson.containsKey("permissionList")) {
						metaJson.getJSONArray("permissionList").add(json);
					} else {
						JSONArray permissionList = new JSONArray();
						permissionList.add(json);
						metaJson.put("permissionList", permissionList);
					}
					// 类型( 0：一级菜单 1：子菜单 2：按钮 )
				} else if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_1) || permission.getMenuType().equals(CommonConstant.MENU_TYPE_0)) {
					if (parentJson.containsKey("children")) {
						parentJson.getJSONArray("children").add(json);
					} else {
						JSONArray children = new JSONArray();
						children.add(json);
						parentJson.put("children", children);
					}
					if (!permission.isLeaf()) {
						getPermissionJsonArray(jsonArray, metaList, json);
					}
				}
			}

		}
	}

	/**
	 * 根据菜单配置生成路由json
	 *
	 * @param permission
	 * @return
	 */
	private JSONObject getPermissionJsonObject(SysPermission permission) {
		JSONObject json = new JSONObject();
		// 类型(0：一级菜单 1：子菜单 2：按钮)
		if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
			//json.put("action", permission.getPerms());
			//json.put("type", permission.getPermsType());
			//json.put("describe", permission.getName());
			return null;
		} else if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_0) || permission.getMenuType().equals(CommonConstant.MENU_TYPE_1)) {
			json.put("id", permission.getId());
			if (permission.isRoute()) {
				json.put("route", "1");// 表示生成路由
			} else {
				json.put("route", "0");// 表示不生成路由
			}
			if (isWWWHttpUrl(permission.getUrl())) {
				json.put("path", MD5Util.MD5Encode(permission.getUrl(), "utf-8"));
			} else {
				json.put("path", permission.getUrl());
			}
			// 重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)
			if (oConvertUtils.isNotEmpty(permission.getComponentName())) {
				json.put("name", permission.getComponentName());
			} else {
				json.put("name", urlToRouteName(permission.getUrl()));
			}
			// 是否隐藏路由，默认都是显示的
			if (permission.isHidden()) {
				json.put("hidden", true);
			}
			// 聚合路由
			if (permission.isAlwaysShow()) {
				json.put("alwaysShow", true);
			}
			json.put("component", permission.getComponent());
			JSONObject meta = new JSONObject();
			// 由用户设置是否缓存页面 用布尔值
			if (permission.isKeepAlive()) {
				meta.put("keepAlive", true);
			} else {
				meta.put("keepAlive", false);
			}

			/*update_begin author:wuxianquan date:20190908 for:往菜单信息里添加外链菜单打开方式 */
			//外链菜单打开方式
			if (permission.isInternalOrExternal()) {
				meta.put("internalOrExternal", true);
			} else {
				meta.put("internalOrExternal", false);
			}
			/* update_end author:wuxianquan date:20190908 for: 往菜单信息里添加外链菜单打开方式*/
			meta.put("title", permission.getName());
			if (oConvertUtils.isEmpty(permission.getParentId())) {
				// 一级菜单跳转地址
				json.put("redirect", permission.getRedirect());
				if (oConvertUtils.isNotEmpty(permission.getIcon())) {
					meta.put("icon", permission.getIcon());
				}
			} else {
				if (oConvertUtils.isNotEmpty(permission.getIcon())) {
					meta.put("icon", permission.getIcon());
				}
			}
			if (isWWWHttpUrl(permission.getUrl())) {
				meta.put("url", permission.getUrl());
			}
			json.put("meta", meta);
		}
		return json;
	}

	/**
	 * 判断是否外网URL 例如： http://localhost:8080/jeecg-boot/swagger-ui.html#/ 支持特殊格式： {{
	 * window._CONFIG['domianURL'] }}/druid/ {{ JS代码片段 }}，前台解析会自动执行JS代码片段
	 *
	 * @return
	 */
	private boolean isWWWHttpUrl(String url) {
		if (url != null && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("{{"))) {
			return true;
		}
		return false;
	}

	/**
	 * 通过URL生成路由name（去掉URL前缀斜杠，替换内容中的斜杠‘/’为-） 举例： URL = /isystem/role RouteName =
	 * isystem-role
	 *
	 * @return
	 */
	private String urlToRouteName(String url) {
		if (oConvertUtils.isNotEmpty(url)) {
			if (url.startsWith("/")) {
				url = url.substring(1);
			}
			url = url.replace("/", "-");
			// 特殊标记
			url = url.replace(":", "@");
			return url;
		} else {
			return null;
		}
	}

}
