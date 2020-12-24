package cc.admin.modules.system.service.impl;

import cc.admin.modules.system.entity.SysUserRole;
import cc.admin.modules.system.mapper.SysUserRoleMapper;
import cc.admin.modules.system.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <getPageFilterFields>
 * 用户角色表 服务实现类
 * </getPageFilterFields>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

	@Autowired
	SysUserRoleMapper sysUserRoleMapper;

	@Override
	public void removeAllByRoleId(String roleId) {
		sysUserRoleMapper.removeAllByRoleId(roleId);
	}
}
