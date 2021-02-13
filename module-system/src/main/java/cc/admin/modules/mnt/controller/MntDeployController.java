package cc.admin.modules.mnt.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.mnt.entity.MntDeploy;
import cc.admin.modules.mnt.service.IMntDeployService;
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
import java.util.Arrays;

/**
 * @Description: 部署管理
 * @Author: cc-admin
 * @Date: 2021-02-08
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "部署管理")
@RestController
@RequestMapping("/mnt/deploy")
public class MntDeployController extends BaseController<MntDeploy, IMntDeployService> {

	@Autowired
	private IMntDeployService mntDeployService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param catalog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "部署管理-分页列表查询")
	@ApiOperation(value = "部署管理-分页列表查询", notes = "部署管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "catalog", required = false) String catalog,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<MntDeploy> queryWrapper = QueryGenerator.initQueryWrapper(new MntDeploy(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='MntDeploy' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}
		Page<MntDeploy> page = new Page<MntDeploy>(pageNo, pageSize);
		IPage<MntDeploy> pageList = mntDeployService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param mntDeploy
	 * @return
	 */
	@AutoLog(value = "部署管理-添加")
	@ApiOperation(value = "部署管理-添加", notes = "部署管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody MntDeploy mntDeploy) {
		mntDeployService.save(mntDeploy);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param mntDeploy
	 * @return
	 */
	@AutoLog(value = "部署管理-编辑")
	@ApiOperation(value = "部署管理-编辑", notes = "部署管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody MntDeploy mntDeploy) {
		mntDeployService.updateById(mntDeploy);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "部署管理-通过id删除")
	@ApiOperation(value = "部署管理-通过id删除", notes = "部署管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		mntDeployService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "部署管理-批量删除")
	@ApiOperation(value = "部署管理-批量删除", notes = "部署管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.mntDeployService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "部署管理-通过id查询")
	@ApiOperation(value = "部署管理-通过id查询", notes = "部署管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		MntDeploy mntDeploy = mntDeployService.getById(id);
		return Result.ok(mntDeploy);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param mntDeploy
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, MntDeploy mntDeploy) {
		return super.exportXls(request, mntDeploy, MntDeploy.class, "部署管理");
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
		return super.importExcel(request, response, MntDeploy.class);
	}


	@AutoLog(value = "部署管理-查询服务器状态")
	@ApiOperation(value = "部署管理-查询服务器状态", notes = "部署管理-查询服务器状态")
	@GetMapping(value = "/serverStatus")
	public Result<?> serverStatus(@RequestParam(name = "ids", required = true) String ids) {
		String result = mntDeployService.serverStatus(Arrays.asList(ids.split(",")));
		return Result.ok(result);
	}

}
