package cc.admin.modules.demo.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.demo.entity.DemoOrderMain;
import cc.admin.modules.demo.service.IDemoOrderMainService;
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
 * @Description: 订单主表
 * @Author: ZhangHouYing
 * @Date: 2020-09-26
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="订单主表")
@RestController
@RequestMapping("/demo/orderMain")
public class DemoOrderMainController extends BaseController<DemoOrderMain, IDemoOrderMainService> {
	@Autowired
	private IDemoOrderMainService demoOrderMainService;

	/**
	 * 分页列表查询
	 *
	 * @param demoOrderMain
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "订单主表-分页列表查询")
	@ApiOperation(value="订单主表-分页列表查询", notes="订单主表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(DemoOrderMain demoOrderMain,
								   @RequestParam(name="key",required = false) String key,
								   @RequestParam(name="catalog",required = false) String catalog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DemoOrderMain> queryWrapper = QueryGenerator.initQueryWrapper(demoOrderMain, req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.or().like("order_code", key);
			queryWrapper.or().like("ctype", key);
			queryWrapper.or().like("content", key);
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("ctype", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}

		Page<DemoOrderMain> page = new Page<DemoOrderMain>(pageNo, pageSize);
		IPage<DemoOrderMain> pageList = demoOrderMainService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param demoOrderMain
	 * @return
	 */
	@AutoLog(value = "订单主表-添加")
	@ApiOperation(value="订单主表-添加", notes="订单主表-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DemoOrderMain demoOrderMain) {
		demoOrderMainService.save(demoOrderMain);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param demoOrderMain
	 * @return
	 */
	@AutoLog(value = "订单主表-编辑")
	@ApiOperation(value="订单主表-编辑", notes="订单主表-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DemoOrderMain demoOrderMain) {
		demoOrderMainService.updateById(demoOrderMain);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "订单主表-通过id删除")
	@ApiOperation(value="订单主表-通过id删除", notes="订单主表-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		demoOrderMainService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "订单主表-批量删除")
	@ApiOperation(value="订单主表-批量删除", notes="订单主表-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.demoOrderMainService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "订单主表-通过id查询")
	@ApiOperation(value="订单主表-通过id查询", notes="订单主表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		DemoOrderMain demoOrderMain = demoOrderMainService.getById(id);
		return Result.ok(demoOrderMain);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param demoOrderMain
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, DemoOrderMain demoOrderMain) {
		return super.exportXls(request, demoOrderMain, DemoOrderMain.class, "订单主表");
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
		return super.importExcel(request, response, DemoOrderMain.class);
	}

}
