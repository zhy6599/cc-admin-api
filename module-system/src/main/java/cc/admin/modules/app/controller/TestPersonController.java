package cc.admin.modules.app.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.app.entity.TestPerson;
import cc.admin.modules.app.service.ITestPersonService;
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
 * @Description: test_person
 * @Author: ZhangHouYing
 * @Date:   2020-05-31
 * @Version: V1.0
 */
@Api(tags="test_person")
@RestController
@RequestMapping("/app/testPerson")
@Slf4j
public class TestPersonController extends BaseController<TestPerson, ITestPersonService> {
	@Autowired
	private ITestPersonService testPersonService;

	/**
	 * 分页列表查询
	 *
	 * @param testPerson
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "test_person-分页列表查询")
	@ApiOperation(value="test_person-分页列表查询", notes="test_person-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(TestPerson testPerson,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TestPerson> queryWrapper = QueryGenerator.initQueryWrapper(testPerson, req.getParameterMap());
		Page<TestPerson> page = new Page<TestPerson>(pageNo, pageSize);
		IPage<TestPerson> pageList = testPersonService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param testPerson
	 * @return
	 */
	@AutoLog(value = "test_person-添加")
	@ApiOperation(value="test_person-添加", notes="test_person-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody TestPerson testPerson) {
		testPersonService.save(testPerson);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param testPerson
	 * @return
	 */
	@AutoLog(value = "test_person-编辑")
	@ApiOperation(value="test_person-编辑", notes="test_person-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody TestPerson testPerson) {
		testPersonService.updateById(testPerson);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "test_person-通过id删除")
	@ApiOperation(value="test_person-通过id删除", notes="test_person-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		testPersonService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "test_person-批量删除")
	@ApiOperation(value="test_person-批量删除", notes="test_person-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.testPersonService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "test_person-通过id查询")
	@ApiOperation(value="test_person-通过id查询", notes="test_person-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		TestPerson testPerson = testPersonService.getById(id);
		if(testPerson==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(testPerson);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param testPerson
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TestPerson testPerson) {
        return super.exportXls(request, testPerson, TestPerson.class, "test_person");
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
        return super.importExcel(request, response, TestPerson.class);
    }

}
