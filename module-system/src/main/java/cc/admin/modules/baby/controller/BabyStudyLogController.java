package cc.admin.modules.baby.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.baby.entity.BabyStudyLog;
import cc.admin.modules.baby.service.IBabyStudyLogService;
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
 * @Description: 学习记录
 * @Author: ZhangHouYing
 * @Date: 2020-11-21
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "学习记录")
@RestController
@RequestMapping("/baby/studyLog")
public class BabyStudyLogController extends BaseController<BabyStudyLog, IBabyStudyLogService> {
	@Autowired
	private IBabyStudyLogService babyStudyLogService;

	/**
	 * 分页列表查询
	 *
	 * @param babyStudyLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "学习记录-分页列表查询")
	@ApiOperation(value = "学习记录-分页列表查询", notes = "学习记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			BabyStudyLog babyStudyLog,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<BabyStudyLog> queryWrapper = QueryGenerator.initQueryWrapper(babyStudyLog, req.getParameterMap());
		Page<BabyStudyLog> page = new Page<BabyStudyLog>(pageNo, pageSize);
		IPage<BabyStudyLog> pageList = babyStudyLogService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param babyStudyLog
	 * @return
	 */
	@AutoLog(value = "学习记录-添加")
	@ApiOperation(value = "学习记录-添加", notes = "学习记录-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BabyStudyLog babyStudyLog) {
		babyStudyLogService.save(babyStudyLog);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param babyStudyLog
	 * @return
	 */
	@AutoLog(value = "学习记录-编辑")
	@ApiOperation(value = "学习记录-编辑", notes = "学习记录-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BabyStudyLog babyStudyLog) {
		babyStudyLogService.updateById(babyStudyLog);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "学习记录-通过id删除")
	@ApiOperation(value = "学习记录-通过id删除", notes = "学习记录-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		babyStudyLogService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "学习记录-批量删除")
	@ApiOperation(value = "学习记录-批量删除", notes = "学习记录-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.babyStudyLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "学习记录-通过id查询")
	@ApiOperation(value = "学习记录-通过id查询", notes = "学习记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		BabyStudyLog babyStudyLog = babyStudyLogService.getById(id);
		return Result.ok(babyStudyLog);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param babyStudyLog
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BabyStudyLog babyStudyLog) {
		return super.exportXls(request, babyStudyLog, BabyStudyLog.class, "学习记录");
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
		return super.importExcel(request, response, BabyStudyLog.class);
	}

}
