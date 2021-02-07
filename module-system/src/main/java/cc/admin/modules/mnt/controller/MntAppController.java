package cc.admin.modules.mnt.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.mnt.entity.MntApp;
import cc.admin.modules.mnt.service.IMntAppService;
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
 * @Description: 应用管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="应用管理")
@RestController
@RequestMapping("/mnt/app")
public class MntAppController extends BaseController<MntApp, IMntAppService> {
	@Autowired
	private IMntAppService mntAppService;

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
	@AutoLog(value = "应用管理-分页列表查询")
	@ApiOperation(value="应用管理-分页列表查询", notes="应用管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
								   @RequestParam(name="key",required = false) String key,
								   @RequestParam(name = "catalog",required = false) String catalog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<MntApp> queryWrapper = QueryGenerator.initQueryWrapper(new MntApp(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i->i.like("name", key).or().like("upload_path", key).or().like("deploy_path", key).or().like("backup_path", key));
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='MntApp' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}

		Page<MntApp> page = new Page<MntApp>(pageNo, pageSize);
		IPage<MntApp> pageList = mntAppService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param mntApp
	 * @return
	 */
	@AutoLog(value = "应用管理-添加")
	@ApiOperation(value="应用管理-添加", notes="应用管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody MntApp mntApp) {
mntAppService.save(mntApp);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param mntApp
	 * @return
	 */
	@AutoLog(value = "应用管理-编辑")
	@ApiOperation(value="应用管理-编辑", notes="应用管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody MntApp mntApp) {
mntAppService.updateById(mntApp);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应用管理-通过id删除")
	@ApiOperation(value="应用管理-通过id删除", notes="应用管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
mntAppService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "应用管理-批量删除")
	@ApiOperation(value="应用管理-批量删除", notes="应用管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.mntAppService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "应用管理-通过id查询")
	@ApiOperation(value="应用管理-通过id查询", notes="应用管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
MntApp mntApp = mntAppService.getById(id);
		return Result.ok(mntApp);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param mntApp
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, MntApp mntApp) {
      return super.exportXls(request, mntApp, MntApp.class, "应用管理");
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
      return super.importExcel(request, response, MntApp.class);
  }

}
