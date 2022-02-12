package cc.admin.modules.app.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.modules.app.entity.TestPerson;
import cc.admin.modules.app.service.IHdsService;
import cc.admin.modules.app.service.ITestPersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description: test_person
 * @Author: ZhangHouYing
 * @Date:   2020-05-31
 * @Version: V1.0
 */
@Api(tags="hds")
@RestController
@RequestMapping("/app/hds")
@Slf4j
public class HdsController extends BaseController<TestPerson, ITestPersonService> {
	@Autowired
	private IHdsService hdsService;

	/**
	 * 分页列表查询
	 * @return
	 */
	@AutoLog(value = "hds-分页列表查询")
	@ApiOperation(value="hds-分页列表查询", notes="hds-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList() {
		List<Map<String, Object>> data = hdsService.queryForList("select * from DRV_BGKSD");
		return Result.ok(data);
	}

}
