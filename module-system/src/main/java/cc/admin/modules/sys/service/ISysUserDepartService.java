package cc.admin.modules.sys.service;


import java.util.List;

import cc.admin.modules.sys.entity.SysUser;
import cc.admin.modules.sys.entity.SysUserDepart;
import cc.admin.modules.sys.model.DepartIdModel;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <getPageFilterFields>
 * SysUserDpeart用户组织机构service
 * </getPageFilterFields>
 * @Author ZhiLin
 *
 */
public interface ISysUserDepartService extends IService<SysUserDepart> {


	/**
	 * 根据指定用户id查询部门信息
	 * @param userId
	 * @return
	 */
	List<DepartIdModel> queryDepartIdsOfUser(String userId);


	/**
	 * 根据部门id查询用户信息
	 * @param depId
	 * @return
	 */
	List<SysUser> queryUserByDepId(String depId);
  	/**
	 * 根据部门code，查询当前部门和下级部门的用户信息
	 */
	public List<SysUser> queryUserByDepCode(String depCode,String realname);
}
