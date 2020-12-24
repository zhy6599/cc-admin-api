package cc.admin.modules.baby.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.baby.entity.BabyWord;
import cc.admin.modules.baby.service.IBabyWordService;
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
 * @Description: 字词管理
 * @Author: ZhangHouYing
 * @Date: 2020-11-21
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "字词管理")
@RestController
@RequestMapping("/baby/word")
public class BabyWordController extends BaseController<BabyWord, IBabyWordService> {
	@Autowired
	private IBabyWordService babyWordService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "字词管理-分页列表查询")
	@ApiOperation(value = "字词管理-分页列表查询", notes = "字词管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<BabyWord> queryWrapper = QueryGenerator.initQueryWrapper(new BabyWord(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i -> i.like("name", key).or().like("pinyin", key));
		}
		Page<BabyWord> page = new Page<BabyWord>(pageNo, pageSize);
		IPage<BabyWord> pageList = babyWordService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 分页列表查询
	 * @param model 0学习   1复习
	 * @return
	 */
	@AutoLog(value = "字词管理-学习列表查询")
	@ApiOperation(value = "字词管理-学习列表查询", notes = "字词管理-学习列表查询")
	@GetMapping(value = "/study")
	public Result<?> queryPageList(
			@RequestParam(name = "model", defaultValue = "0") String model) {
		List<BabyWord> resultList = null;
		if ("0".equals(model)) {
			QueryWrapper<BabyWord> queryWrapper = new QueryWrapper<>();
			queryWrapper.orderByDesc("id");
			resultList = babyWordService.list(queryWrapper);
		} else {
			resultList = babyWordService.review();
		}
		return Result.ok(resultList);
	}

	/**
	 * 添加
	 *
	 * @param babyWord
	 * @return
	 */
	@AutoLog(value = "字词管理-添加")
	@ApiOperation(value = "字词管理-添加", notes = "字词管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody BabyWord babyWord) {
		babyWordService.save(babyWord);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param babyWord
	 * @return
	 */
	@AutoLog(value = "字词管理-编辑")
	@ApiOperation(value = "字词管理-编辑", notes = "字词管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody BabyWord babyWord) {
		babyWordService.updateById(babyWord);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "字词管理-通过id删除")
	@ApiOperation(value = "字词管理-通过id删除", notes = "字词管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		babyWordService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "字词管理-批量删除")
	@ApiOperation(value = "字词管理-批量删除", notes = "字词管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.babyWordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "字词管理-通过id查询")
	@ApiOperation(value = "字词管理-通过id查询", notes = "字词管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		BabyWord babyWord = babyWordService.getById(id);
		return Result.ok(babyWord);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param babyWord
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, BabyWord babyWord) {
		return super.exportXls(request, babyWord, BabyWord.class, "字词管理");
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
		return super.importExcel(request, response, BabyWord.class);
	}

}
