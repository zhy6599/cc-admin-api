package ${bussiPackage}.${entityPackage}.controller;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import cc.admin.common.api.vo.Result;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.util.oConvertUtils;
import ${bussiPackage}.${entityPackage}.entity.${entityName};
import ${bussiPackage}.${entityPackage}.service.I${entityName}Service;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.poi.excel.ExcelImportUtil;
import cc.admin.poi.excel.def.NormalExcelConstants;
import cc.admin.poi.excel.entity.ExportParams;
import cc.admin.poi.excel.entity.ImportParams;
import cc.admin.poi.excel.view.BaseEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: ${geForm.moduleName}
 * @Author: cc-admin
 * @Date:   ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="${geForm.moduleName}")
@RestController
@RequestMapping("/${requestMapping}")
public class ${entityName}Controller extends BaseController<${entityName}, I${entityName}Service> {
	@Autowired
	private I${entityName}Service ${entityName?uncap_first}Service;

	/**
	 * 分页列表查询
	 *
<#if generate.isSimpleQuery == "1">
	 * @param key
<#else>
	 * @param ${entityName?uncap_first}
</#if>
<#if generate.tableType == "catalog">
	 * @param catalog
</#if>
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "${geForm.moduleName}-分页列表查询")
	@ApiOperation(value="${geForm.moduleName}-分页列表查询", notes="${geForm.moduleName}-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(@RequestParam(name = "${slaveColumnCode}") String ${slaveColumnCode},
<#if generate.isSimpleQuery == "1">
								   @RequestParam(name="key",required = false) String key,
<#else>
								   ${entityName} ${entityName?uncap_first},
</#if>
<#if generate.tableType == "catalog">
								   @RequestParam(name = "catalog",required = false) String catalog,
</#if>
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
<#if generate.isSimpleQuery == "1">
		QueryWrapper<${entityName}> queryWrapper = QueryGenerator.initQueryWrapper(new ${entityName}(), req.getParameterMap());
<#else>
		QueryWrapper<${entityName}> queryWrapper = QueryGenerator.initQueryWrapper(${entityName?uncap_first}, req.getParameterMap());
</#if>
		queryWrapper.eq("${slaveColumn}", ${slaveColumnCode});
<#if generate.isSimpleQuery == "1">
		if (StrUtil.isNotEmpty(key)) {
			${simpleQueryContent}
		}
</#if>
<#if generate.tableType == "catalog">
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='${entityName}' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}
</#if>

		Page<${entityName}> page = new Page<${entityName}>(pageNo, pageSize);
		IPage<${entityName}> pageList = ${entityName?uncap_first}Service.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param ${entityName?uncap_first}
	 * @return
	 */
	@AutoLog(value = "${geForm.moduleName}-添加")
	@ApiOperation(value="${geForm.moduleName}-添加", notes="${geForm.moduleName}-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ${entityName} ${entityName?uncap_first}) {
${entityName?uncap_first}Service.save(${entityName?uncap_first});
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param ${entityName?uncap_first}
	 * @return
	 */
	@AutoLog(value = "${geForm.moduleName}-编辑")
	@ApiOperation(value="${geForm.moduleName}-编辑", notes="${geForm.moduleName}-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ${entityName} ${entityName?uncap_first}) {
${entityName?uncap_first}Service.updateById(${entityName?uncap_first});
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "${geForm.moduleName}-通过id删除")
	@ApiOperation(value="${geForm.moduleName}-通过id删除", notes="${geForm.moduleName}-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
${entityName?uncap_first}Service.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "${geForm.moduleName}-批量删除")
	@ApiOperation(value="${geForm.moduleName}-批量删除", notes="${geForm.moduleName}-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.${entityName?uncap_first}Service.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "${geForm.moduleName}-通过id查询")
	@ApiOperation(value="${geForm.moduleName}-通过id查询", notes="${geForm.moduleName}-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
${entityName} ${entityName?uncap_first} = ${entityName?uncap_first}Service.getById(id);
		return Result.ok(${entityName?uncap_first});
	}

  /**
   * 导出excel
   *
   * @param request
   * @param ${entityName?uncap_first}
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, ${entityName} ${entityName?uncap_first}) {
      return super.exportXls(request, ${entityName?uncap_first}, ${entityName}.class, "${geForm.moduleName}");
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
      return super.importExcel(request, response, ${entityName}.class);
  }

}
