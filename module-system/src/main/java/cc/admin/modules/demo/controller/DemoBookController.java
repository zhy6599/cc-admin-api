package cc.admin.modules.demo.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.demo.entity.DemoBook;
import cc.admin.modules.demo.service.IDemoBookService;
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
 * @Description: 图书
  * @Author: ZhangHouYing
 * @Date:   2020-11-07
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="图书")
@RestController
@RequestMapping("/demo/book")
public class DemoBookController extends BaseController<DemoBook, IDemoBookService> {
	@Autowired
	private IDemoBookService demoBookService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "图书-分页列表查询")
	@ApiOperation(value="图书-分页列表查询", notes="图书-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
								   @RequestParam(name="key",required = false) String key,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DemoBook> queryWrapper = QueryGenerator.initQueryWrapper(new DemoBook(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i->i.like("book_name", key).or().like("file_name", key).or().like("sort_id", key));
		}

		Page<DemoBook> page = new Page<DemoBook>(pageNo, pageSize);
		IPage<DemoBook> pageList = demoBookService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param demoBook
	 * @return
	 */
	@AutoLog(value = "图书-添加")
	@ApiOperation(value="图书-添加", notes="图书-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DemoBook demoBook) {
demoBookService.save(demoBook);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param demoBook
	 * @return
	 */
	@AutoLog(value = "图书-编辑")
	@ApiOperation(value="图书-编辑", notes="图书-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DemoBook demoBook) {
demoBookService.updateById(demoBook);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "图书-通过id删除")
	@ApiOperation(value="图书-通过id删除", notes="图书-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
demoBookService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "图书-批量删除")
	@ApiOperation(value="图书-批量删除", notes="图书-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.demoBookService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "图书-通过id查询")
	@ApiOperation(value="图书-通过id查询", notes="图书-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
DemoBook demoBook = demoBookService.getById(id);
		return Result.ok(demoBook);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param demoBook
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, DemoBook demoBook) {
      return super.exportXls(request, demoBook, DemoBook.class, "图书");
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
      return super.importExcel(request, response, DemoBook.class);
  }

}
