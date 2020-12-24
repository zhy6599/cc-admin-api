package cc.admin.modules.system.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.modules.system.entity.SysUser;
import cc.admin.modules.system.entity.SysUserRole;
import cc.admin.modules.system.service.ISysUserRoleService;
import cc.admin.modules.system.service.ISysUserService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 用户角色表
 * @Author: ZhangHouYing
 * @Date: 2020-10-31
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "用户角色表")
@RestController
@RequestMapping("/sys/userRole")
public class SysUserRoleController extends BaseController<SysUserRole, ISysUserRoleService> {
	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@Autowired
	private ISysUserService sysUserService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户角色表-分页列表查询")
	@ApiOperation(value = "用户角色表-分页列表查询", notes = "用户角色表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "roleId", required = true) String roleId,
			@RequestParam(name = "selected", required = true) boolean selected,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.nested(i -> i.like("username", key).or().like("realname", key).or().like("phone", key));
		}
		if (selected) {
			queryWrapper.nested(i -> i.inSql("id", String.format("select user_id from sys_user_role where role_id='%s'", roleId)));
		} else {
			queryWrapper.nested(i -> i.notInSql("id", String.format("select user_id from sys_user_role where role_id='%s'", roleId)));
		}
		Page<SysUser> page = new Page<>(pageNo, pageSize);
		IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param sysUserRoleList
	 * @return
	 */
	@AutoLog(value = "用户角色表-添加")
	@ApiOperation(value = "用户角色表-添加", notes = "用户角色表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody List<SysUserRole> sysUserRoleList) {
		sysUserRoleList.forEach(sysUserRole -> {
			sysUserRoleService.save(sysUserRole);
		});
		return Result.ok("添加成功！");
	}

	/**
	 * 删除
	 *
	 * @param userId
	 * @param roleId
	 * @return
	 */
	@AutoLog(value = "用户角色表-通过id删除")
	@ApiOperation(value = "用户角色表-通过id删除", notes = "用户角色表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "userId", required = true) String userId,
							@RequestParam(name = "roleId", required = true) String roleId) {
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setUserId(userId);
		sysUserRole.setRoleId(roleId);
		QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper(sysUserRole);
		sysUserRoleService.remove(queryWrapper);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param sysUserRoleList
	 * @return
	 */
	@AutoLog(value = "用户角色表-批量删除")
	@ApiOperation(value = "用户角色表-批量删除", notes = "用户角色表-批量删除")
	@PostMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestBody List<SysUserRole> sysUserRoleList) {
		sysUserRoleList.forEach(sysUserRole -> {
			QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper(sysUserRole);
			sysUserRoleService.remove(queryWrapper);
		});
		return Result.ok("批量删除成功！");
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param sysUserRole
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysUserRole sysUserRole) {
		return super.exportXls(request, sysUserRole, SysUserRole.class, "用户角色表");
	}

	/**
	 * 通过excel导入数据
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysUserRole.class);
	}

}
