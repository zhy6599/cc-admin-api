package cc.admin.modules.sys.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.sys.entity.SysNews;
import cc.admin.modules.sys.service.ISysNewsService;
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
 * @Description: 新闻信息
 * @Author: cc-admin
 * @Date: 2021-06-10
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "新闻信息")
@RestController
@RequestMapping("/sys/news")
public class SysNewsController extends BaseController<SysNews, ISysNewsService> {
	@Autowired
	private ISysNewsService sysNewsService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "新闻信息-分页列表查询")
	@ApiOperation(value = "新闻信息-分页列表查询", notes = "新闻信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<SysNews> queryWrapper = QueryGenerator.initQueryWrapper(new SysNews(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i -> i.like("content", key).or().like("name", key));
		}
		Page<SysNews> page = new Page<SysNews>(pageNo, pageSize);
		IPage<SysNews> pageList = sysNewsService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param sysNews
	 * @return
	 */
	@AutoLog(value = "新闻信息-添加")
	@ApiOperation(value = "新闻信息-添加", notes = "新闻信息-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysNews sysNews) {
		sysNewsService.save(sysNews);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param sysNews
	 * @return
	 */
	@AutoLog(value = "新闻信息-编辑")
	@ApiOperation(value = "新闻信息-编辑", notes = "新闻信息-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysNews sysNews) {
		sysNewsService.updateById(sysNews);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "新闻信息-通过id删除")
	@ApiOperation(value = "新闻信息-通过id删除", notes = "新闻信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysNewsService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "新闻信息-批量删除")
	@ApiOperation(value = "新闻信息-批量删除", notes = "新闻信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.sysNewsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "新闻信息-通过id查询")
	@ApiOperation(value = "新闻信息-通过id查询", notes = "新闻信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		SysNews sysNews = sysNewsService.getById(id);
		return Result.ok(sysNews);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param sysNews
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysNews sysNews) {
		return super.exportXls(request, sysNews, SysNews.class, "新闻信息");
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
		return super.importExcel(request, response, SysNews.class);
	}

}
