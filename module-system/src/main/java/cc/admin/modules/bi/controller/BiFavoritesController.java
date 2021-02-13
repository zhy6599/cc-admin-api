package cc.admin.modules.bi.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.bi.entity.BiFavorites;
import cc.admin.modules.bi.service.IBiFavoritesService;
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
 * @Description: 收藏夹
 * @Author: cc-admin
 * @Date: 2021-02-10
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "收藏夹")
@RestController
@RequestMapping("/bi/favorites")
public class BiFavoritesController extends BaseController<BiFavorites, IBiFavoritesService> {
	@Autowired
	private IBiFavoritesService biFavoritesService;

	/**
	 * 分页列表查询
	 *
	 * @param biFavorites
	 * @param catalog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "收藏夹-分页列表查询")
	@ApiOperation(value = "收藏夹-分页列表查询", notes = "收藏夹-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			BiFavorites biFavorites,
			@RequestParam(name = "catalog", required = false) String catalog,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<BiFavorites> queryWrapper = QueryGenerator.initQueryWrapper(new BiFavorites(), req.getParameterMap());
		if (StrUtil.isNotEmpty(biFavorites.getName())) {
			queryWrapper.and(i->i.like("name", biFavorites.getName()));
		}
		if (StrUtil.isNotEmpty(biFavorites.getType())) {
			queryWrapper.and(i->i.eq("type", biFavorites.getType()));
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='BiFavorites' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}
		Page<BiFavorites> page = new Page<BiFavorites>(pageNo, pageSize);
		IPage<BiFavorites> pageList = biFavoritesService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param biFavorites
	 * @return
	 */
	@AutoLog(value = "收藏夹-添加")
	@ApiOperation(value = "收藏夹-添加", notes = "收藏夹-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BiFavorites biFavorites) {
		biFavoritesService.save(biFavorites);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param biFavorites
	 * @return
	 */
	@AutoLog(value = "收藏夹-编辑")
	@ApiOperation(value = "收藏夹-编辑", notes = "收藏夹-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BiFavorites biFavorites) {
		biFavoritesService.updateById(biFavorites);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "收藏夹-通过id删除")
	@ApiOperation(value = "收藏夹-通过id删除", notes = "收藏夹-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		biFavoritesService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "收藏夹-批量删除")
	@ApiOperation(value = "收藏夹-批量删除", notes = "收藏夹-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.biFavoritesService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "收藏夹-通过id查询")
	@ApiOperation(value = "收藏夹-通过id查询", notes = "收藏夹-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		BiFavorites biFavorites = biFavoritesService.getById(id);
		return Result.ok(biFavorites);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param biFavorites
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BiFavorites biFavorites) {
		return super.exportXls(request, biFavorites, BiFavorites.class, "收藏夹");
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
		return super.importExcel(request, response, BiFavorites.class);
	}

}
