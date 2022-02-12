package cc.admin.modules.jp.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.jp.entity.JpPerson;
import cc.admin.modules.jp.entity.JpPersonTree;
import cc.admin.modules.jp.entity.JpTreeChartData;
import cc.admin.modules.jp.service.IJpPersonService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "")
@RestController
@RequestMapping("/jp/person")
public class JpPersonController extends BaseController<JpPerson, IJpPersonService> {
	@Autowired
	private IJpPersonService jpPersonService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "-分页列表查询")
	@ApiOperation(value = "-分页列表查询", notes = "-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<JpPerson> queryWrapper = QueryGenerator.initQueryWrapper(new JpPerson(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i -> i.like("name", key).or().like("wife", key).or().like("remark", key));
		}
		Page<JpPerson> page = new Page<JpPerson>(pageNo, pageSize);
		IPage<JpPerson> pageList = jpPersonService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 查询数据 树状数据列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
	public Result<List<JpPersonTree>> queryTreeList() {
		Result<List<JpPersonTree>> result = new Result<>();
		try {
			List<JpPersonTree> jpPersonTreeList = jpPersonService.queryJpPersonTree();
			result.setResult(jpPersonTreeList);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 查询数据 图形数据格式
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryTreeChartData", method = RequestMethod.GET)
	public Result<JpTreeChartData> queryTreeChartData() {
		Result<JpTreeChartData> result = new Result<>();
		try {
			JpTreeChartData jpTreeChartData = jpPersonService.queryTreeChartData();
			result.setResult(jpTreeChartData);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}


	/**
	 * 添加
	 *
	 * @param jpPerson
	 * @return
	 */
	@AutoLog(value = "-添加")
	@ApiOperation(value = "-添加", notes = "-添加")
	@PostMapping(value = "/add")
	@RequiresRoles({"admin"})
	public Result<?> add(@RequestBody JpPerson jpPerson) {
		jpPerson.setId(IdUtil.fastUUID());
		jpPersonService.save(jpPerson);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param jpPerson
	 * @return
	 */
	@AutoLog(value = "-编辑")
	@ApiOperation(value = "-编辑", notes = "-编辑")
	@PutMapping(value = "/edit")
	@RequiresRoles({"admin"})
	public Result<?> edit(@RequestBody JpPerson jpPerson) {
		jpPersonService.updateById(jpPerson);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "-通过id删除")
	@ApiOperation(value = "-通过id删除", notes = "-通过id删除")
	@DeleteMapping(value = "/delete")
	@RequiresRoles({"admin"})
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		jpPersonService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "-批量删除")
	@ApiOperation(value = "-批量删除", notes = "-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@RequiresRoles({"admin"})
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.jpPersonService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "-通过id查询")
	@ApiOperation(value = "-通过id查询", notes = "-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		JpPerson jpPerson = jpPersonService.getById(id);
		return Result.ok(jpPerson);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param jpPerson
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, JpPerson jpPerson) {
		return super.exportXls(request, jpPerson, JpPerson.class, "");
	}



	/**
	 * 导出word
	 *
	 * @param request
	 * @param jpPerson
	 */
	@RequestMapping(value = "/exportWord")
	public ResponseEntity<FileSystemResource> exportWord(HttpServletRequest request, JpPerson jpPerson) {
		return jpPersonService.exportWord(request, jpPerson, JpPerson.class, "");
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
		return super.importExcel(request, response, JpPerson.class);
	}

}
