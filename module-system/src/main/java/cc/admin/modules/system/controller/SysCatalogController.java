package cc.admin.modules.system.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.system.entity.SysCatalog;
import cc.admin.modules.system.model.CatalogTree;
import cc.admin.modules.system.service.ISysCatalogService;
import cn.hutool.core.util.IdUtil;
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
import java.util.List;

/**
 * @Description: 分类目录
 * @Author: ZhangHouYing
 * @Date: 2020-10-17
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "分类目录")
@RestController
@RequestMapping("/sys/catalog")
public class SysCatalogController extends BaseController<SysCatalog, ISysCatalogService> {
	@Autowired
	private ISysCatalogService sysCatalogService;

	/**
	 * 分页列表查询
	 *
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "分类目录-分页列表查询")
	@ApiOperation(value = "分类目录-分页列表查询", notes = "分类目录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<SysCatalog> queryWrapper = QueryGenerator.initQueryWrapper(new SysCatalog(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.or().like("name", key);
		}
		Page<SysCatalog> page = new Page<SysCatalog>(pageNo, pageSize);
		IPage<SysCatalog> pageList = sysCatalogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 树列表
	 *
	 * @param type
	 * @param req
	 * @return
	 */
	@AutoLog(value = "分类目录-树查询")
	@ApiOperation(value = "分类目录-树查询", notes = "分类目录-树查询")
	@GetMapping(value = "/treeList")
	public Result<?> queryTreeList(@RequestParam(name = "type") String type,
								   HttpServletRequest req) {
		QueryWrapper<SysCatalog> queryWrapper = new QueryWrapper<>();
		if (StrUtil.isNotEmpty(type)) {
			queryWrapper.eq("type", type);
		}
		List<CatalogTree> pageList = sysCatalogService.queryTreeList(queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param sysCatalog
	 * @return
	 */
	@AutoLog(value = "分类目录-添加")
	@ApiOperation(value = "分类目录-添加", notes = "分类目录-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysCatalog sysCatalog) {
		sysCatalog.setId(IdUtil.objectId());
		sysCatalogService.add(sysCatalog);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param sysCatalog
	 * @return
	 */
	@AutoLog(value = "分类目录-编辑")
	@ApiOperation(value = "分类目录-编辑", notes = "分类目录-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysCatalog sysCatalog) {
		sysCatalogService.updateById(sysCatalog);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "分类目录-通过id删除")
	@ApiOperation(value = "分类目录-通过id删除", notes = "分类目录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysCatalogService.deleteById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "分类目录-批量删除")
	@ApiOperation(value = "分类目录-批量删除", notes = "分类目录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.sysCatalogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "分类目录-通过id查询")
	@ApiOperation(value = "分类目录-通过id查询", notes = "分类目录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		SysCatalog sysCatalog = sysCatalogService.getById(id);
		return Result.ok(sysCatalog);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param sysCatalog
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysCatalog sysCatalog) {
		return super.exportXls(request, sysCatalog, SysCatalog.class, "分类目录");
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
		return super.importExcel(request, response, SysCatalog.class);
	}

}
