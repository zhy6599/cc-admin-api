package cc.admin.modules.bi.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.bi.core.model.SourceTableInfo;
import cc.admin.modules.bi.entity.View;
import cc.admin.modules.bi.model.PaginateWithQueryColumns;
import cc.admin.modules.bi.model.TableInfo;
import cc.admin.modules.bi.model.ViewExecuteParam;
import cc.admin.modules.bi.model.ViewExecuteSql;
import cc.admin.modules.bi.service.IViewService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @Description: bi_view
 * @Author: ZhangHouYing
 * @Date: 2020-07-11
 * @Version: V1.0
 */
@Api(tags="bi_view")
@RestController
@RequestMapping("/bi/view")
@Slf4j
public class ViewController extends BaseController<View, IViewService> {
	@Autowired
	private IViewService viewService;

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
	 @AutoLog(value = "bi_view-分页列表查询")
	 @ApiOperation(value="bi_view-分页列表查询", notes="bi_view-分页列表查询")
	 @GetMapping(value = "/list")
	 public Result<?> queryPageList(
									@RequestParam(name = "key", required = false) String key,
									@RequestParam(name = "catalog") String catalog,
									@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									HttpServletRequest req) {
		 QueryWrapper<View> queryWrapper = QueryGenerator.initQueryWrapper(new View(), req.getParameterMap());
		 if (StrUtil.isNotEmpty(key)) {
			 queryWrapper.or(i -> i.like("name", key).or().like("description", key));
		 }
		 if (StrUtil.isNotEmpty(catalog)) {
			 queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='BiView' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		 }
		 queryWrapper.orderByDesc("create_time");
		 Page<View> page = new Page<View>(pageNo, pageSize);
		 IPage<View> pageList = viewService.page(page, queryWrapper);
		 return Result.ok(pageList);
	 }


	 /**
	  * 列表查询
	  *
	  * @param view
	  * @param req
	  * @return
	  */
	 @AutoLog(value = "bi_view-列表查询")
	 @ApiOperation(value="bi_view-列表查询", notes="bi_view-列表查询")
	 @GetMapping(value = "/listAll")
	 public Result<?> queryAllList(View view,
									HttpServletRequest req) {
		 QueryWrapper<View> queryWrapper = QueryGenerator.initQueryWrapper(view, req.getParameterMap());
		 return Result.ok(viewService.list(queryWrapper));
	 }



	 /**
	  *   查询视图数据
	  *
	  * @param id
	  * @param executeParam
	  * @return
	  */
	 @AutoLog(value = "bi_view-查询视图数据")
	 @ApiOperation(value="bi_view-查询视图数据", notes="bi_view-查询视图数据")
	 @PostMapping(value = "/getData/{id}")
	 public Result<?> getData(@PathVariable String id,
							  @RequestBody(required = false) ViewExecuteParam executeParam) {
		 try {
			 Page<Map<String, Object>> result = viewService.getData(id, executeParam);
			 return Result.ok(result);
		 } catch (Exception e) {
			 return Result.error("查询失败：" + e.getMessage());
		 }
	 }


	/**
	 *   查询表格数据
	 *
	 * @param id
	 * @param executeParam
	 * @return
	 */
	@AutoLog(value = "bi_view-查询表格数据")
	@ApiOperation(value = "bi_view-查询表格数据", notes = "bi_view-查询表格数据")
	@PostMapping(value = "/getTableData/{id}")
	public Result<?> getTableData(@PathVariable String id,
								  @RequestBody(required = false) ViewExecuteParam executeParam) {
		try {
			Map<String, Object> resultMap = viewService.getTableData(id, executeParam);
			return Result.ok(resultMap);
		} catch (Exception e) {
			return Result.error("查询失败：" + e.getMessage());
		}
	}

	/**
	 * 查询视图数据
	 *
	 * @param id
	 * @param executeParam
	 * @return
	 */
	@AutoLog(value = "bi_view-查询图形数据")
	@ApiOperation(value = "bi_view-查询图形数据", notes = "bi_view-查询图形数据")
	@PostMapping(value = "/getChartData/{id}")
	public Result<?> getChartData(@PathVariable String id,
								  @RequestBody(required = false) ViewExecuteParam executeParam) {
		try {
			Map<String, Object> resultMap = viewService.getChartData(id, executeParam);
			return Result.ok(resultMap);
		} catch (Exception e) {
			return Result.error("查询失败：" + e.getMessage());
		}
	}


	/**
	 *  查询表列数据
	 * @param id
	 * @param dbName
	 * @param tableName
	 * @return
	 */
	@AutoLog(value = "bi_view-查询表列数据")
	@ApiOperation(value="bi_view-查询表列数据", notes="bi_view-查询表列数据")
	@GetMapping(value = "/columns")
	public Result<?> columns(@RequestParam(name = "id", required = true) String id,
							 @RequestParam(name = "dbName", required = true) String dbName,
							 @RequestParam(name = "tableName", required = true) String tableName) {
		try {
			TableInfo tableInfo = viewService.columns(id, dbName,tableName);
			SourceTableInfo sourceTableInfo = new SourceTableInfo();
			sourceTableInfo.setSourceId(id);
			sourceTableInfo.setTableName(tableName);
			BeanUtils.copyProperties(tableInfo, sourceTableInfo);

			return Result.ok(sourceTableInfo);
		} catch (Exception e) {
			return Result.error("查询失败：" + e.getMessage());
		}
	}

	/**
	 *   执行SQL查询数据
	 *
	 * @param executeSql
	 * @return
	 */
	@AutoLog(value = "bi_view-执行SQL查询数据")
	@ApiOperation(value="bi_view-执行SQL查询数据", notes="bi_view-执行SQL查询数据")
	@PostMapping(value = "/executeSql")
	@RequiresRoles({"admin"})
	public Result<?> executeSql(@RequestBody ViewExecuteSql executeSql) {
		try {
			PaginateWithQueryColumns result = viewService.executeSql(executeSql);
			return Result.ok(result);
		} catch (Exception e) {
			return Result.error("查询失败：" + e.getMessage());
		}
	}


	/**
	 *   添加
	 *
	 * @param view
	 * @return
	 */
	@AutoLog(value = "bi_view-添加")
	@ApiOperation(value="bi_view-添加", notes="bi_view-添加")
	@PostMapping(value = "/add")
	@RequiresRoles({"admin"})
	public Result<?> add(@RequestBody View view) {
		view.setId(IdUtil.fastUUID());
		viewService.save(view);
		return Result.ok("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param view
	 * @return
	 */
	@AutoLog(value = "bi_view-编辑")
	@ApiOperation(value="bi_view-编辑", notes="bi_view-编辑")
	@PutMapping(value = "/edit")
	@RequiresRoles({"admin"})
	public Result<?> edit(@RequestBody View view) {
		viewService.updateById(view);
		return Result.ok("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bi_view-通过id删除")
	@ApiOperation(value="bi_view-通过id删除", notes="bi_view-通过id删除")
	@DeleteMapping(value = "/delete")
	@RequiresRoles({"admin"})
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		viewService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "bi_view-批量删除")
	@ApiOperation(value="bi_view-批量删除", notes="bi_view-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@RequiresRoles({"admin"})
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.viewService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "bi_view-通过id查询")
	@ApiOperation(value="bi_view-通过id查询", notes="bi_view-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		View view = viewService.getById(id);
		if(view==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(view);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param view
    */
    @RequestMapping(value = "/exportXls")
	@RequiresRoles({"admin"})
    public ModelAndView exportXls(HttpServletRequest request, View view) {
        return super.exportXls(request, view, View.class, "bi_view");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@RequiresRoles({"admin"})
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, View.class);
    }

}
