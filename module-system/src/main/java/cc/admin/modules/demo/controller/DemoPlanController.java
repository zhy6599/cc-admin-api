package cc.admin.modules.demo.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.demo.entity.DemoPlan;
import cc.admin.modules.demo.service.IDemoPlanService;
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
 * @Description: 审计计划
  * @Author: ZhangHouYing
 * @Date:   2020-11-07
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="审计计划")
@RestController
@RequestMapping("/demo/plan")
public class DemoPlanController extends BaseController<DemoPlan, IDemoPlanService> {
	@Autowired
	private IDemoPlanService demoPlanService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "审计计划-分页列表查询")
	@ApiOperation(value="审计计划-分页列表查询", notes="审计计划-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
								   @RequestParam(name="key",required = false) String key,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DemoPlan> queryWrapper = QueryGenerator.initQueryWrapper(new DemoPlan(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i->i.like("name", key));
		}

		Page<DemoPlan> page = new Page<DemoPlan>(pageNo, pageSize);
		IPage<DemoPlan> pageList = demoPlanService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param demoPlan
	 * @return
	 */
	@AutoLog(value = "审计计划-添加")
	@ApiOperation(value="审计计划-添加", notes="审计计划-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DemoPlan demoPlan) {
demoPlanService.save(demoPlan);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param demoPlan
	 * @return
	 */
	@AutoLog(value = "审计计划-编辑")
	@ApiOperation(value="审计计划-编辑", notes="审计计划-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DemoPlan demoPlan) {
demoPlanService.updateById(demoPlan);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "审计计划-通过id删除")
	@ApiOperation(value="审计计划-通过id删除", notes="审计计划-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
demoPlanService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "审计计划-批量删除")
	@ApiOperation(value="审计计划-批量删除", notes="审计计划-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.demoPlanService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "审计计划-通过id查询")
	@ApiOperation(value="审计计划-通过id查询", notes="审计计划-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
DemoPlan demoPlan = demoPlanService.getById(id);
		return Result.ok(demoPlan);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param demoPlan
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, DemoPlan demoPlan) {
      return super.exportXls(request, demoPlan, DemoPlan.class, "审计计划");
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
      return super.importExcel(request, response, DemoPlan.class);
  }

}
