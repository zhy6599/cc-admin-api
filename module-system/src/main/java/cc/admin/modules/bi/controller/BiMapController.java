package cc.admin.modules.bi.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.bi.entity.BiMap;
import cc.admin.modules.bi.service.IBiMapService;
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

/**
 * @Description: 地图管理
 * @Author: cc-admin
 * @Date: 2021-01-02
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "地图管理")
@RestController
@RequestMapping("/bi/map")
public class BiMapController extends BaseController<BiMap, IBiMapService> {
	@Autowired
	private IBiMapService biMapService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "地图管理-分页列表查询")
	@ApiOperation(value = "地图管理-分页列表查询", notes = "地图管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<BiMap> queryWrapper = QueryGenerator.initQueryWrapper(new BiMap(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
		}
		Page<BiMap> page = new Page<BiMap>(pageNo, pageSize);
		IPage<BiMap> pageList = biMapService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param biMap
	 * @return
	 */
	@AutoLog(value = "地图管理-添加")
	@ApiOperation(value = "地图管理-添加", notes = "地图管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BiMap biMap) {
		biMap.setId(IdUtil.fastUUID());
		biMapService.save(biMap);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param biMap
	 * @return
	 */
	@AutoLog(value = "地图管理-编辑")
	@ApiOperation(value = "地图管理-编辑", notes = "地图管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BiMap biMap) {
		biMapService.updateById(biMap);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "地图管理-通过id删除")
	@ApiOperation(value = "地图管理-通过id删除", notes = "地图管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		biMapService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "地图管理-批量删除")
	@ApiOperation(value = "地图管理-批量删除", notes = "地图管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.biMapService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "地图管理-通过id查询")
	@ApiOperation(value = "地图管理-通过id查询", notes = "地图管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		BiMap biMap = biMapService.getById(id);
		return Result.ok(biMap);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param biMap
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BiMap biMap) {
		return super.exportXls(request, biMap, BiMap.class, "地图管理");
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
		return super.importExcel(request, response, BiMap.class);
	}

}
