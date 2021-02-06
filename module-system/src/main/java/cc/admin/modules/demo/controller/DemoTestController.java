package cc.admin.modules.demo.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.demo.entity.DemoTest;
import cc.admin.modules.demo.service.IDemoTestService;
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
 * @Description: 测试代码生成
 * @Author: cc-admin
 * @Date:   2021-02-06
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="测试代码生成")
@RestController
@RequestMapping("/demo/test")
public class DemoTestController extends BaseController<DemoTest, IDemoTestService> {
	@Autowired
	private IDemoTestService demoTestService;

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
	@AutoLog(value = "测试代码生成-分页列表查询")
	@ApiOperation(value="测试代码生成-分页列表查询", notes="测试代码生成-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
								   @RequestParam(name="key",required = false) String key,
								   @RequestParam(name = "catalog",required = false) String catalog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<DemoTest> queryWrapper = QueryGenerator.initQueryWrapper(new DemoTest(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='DemoTest' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}

		Page<DemoTest> page = new Page<DemoTest>(pageNo, pageSize);
		IPage<DemoTest> pageList = demoTestService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param demoTest
	 * @return
	 */
	@AutoLog(value = "测试代码生成-添加")
	@ApiOperation(value="测试代码生成-添加", notes="测试代码生成-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody DemoTest demoTest) {
demoTestService.save(demoTest);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param demoTest
	 * @return
	 */
	@AutoLog(value = "测试代码生成-编辑")
	@ApiOperation(value="测试代码生成-编辑", notes="测试代码生成-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody DemoTest demoTest) {
demoTestService.updateById(demoTest);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "测试代码生成-通过id删除")
	@ApiOperation(value="测试代码生成-通过id删除", notes="测试代码生成-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
demoTestService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "测试代码生成-批量删除")
	@ApiOperation(value="测试代码生成-批量删除", notes="测试代码生成-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.demoTestService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "测试代码生成-通过id查询")
	@ApiOperation(value="测试代码生成-通过id查询", notes="测试代码生成-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
DemoTest demoTest = demoTestService.getById(id);
		return Result.ok(demoTest);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param demoTest
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, DemoTest demoTest) {
      return super.exportXls(request, demoTest, DemoTest.class, "测试代码生成");
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
      return super.importExcel(request, response, DemoTest.class);
  }

}
