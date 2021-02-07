package cc.admin.modules.generate.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.view.EntityViewConstants;
import cc.admin.common.view.ZipEntityView;
import cc.admin.modules.generate.entity.Generate;
import cc.admin.modules.generate.model.GenerateContent;
import cc.admin.modules.generate.model.GenerateForm;
import cc.admin.modules.generate.model.RelateData;
import cc.admin.modules.generate.model.TableSchema;
import cc.admin.modules.generate.service.IGenerateService;
import cc.admin.modules.generate.util.FreemarkerUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.CaseFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 代码生成
 * @Author: ZhangHouYing
 * @Date: 2020-09-12
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "代码生成")
@RestController
@RequestMapping("/generate")
public class GenerateController extends BaseController<Generate, IGenerateService> {
	@Autowired
	private IGenerateService generateService;

	/**
	 * 分页列表查询
	 *
	 * @param filter
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-分页列表查询")
	@ApiOperation(value = "代码生成-分页列表查询", notes = "代码生成-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(@RequestParam(name = "filter", required = false) String filter,
								   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<Generate> queryWrapper = new QueryWrapper();
		if (StrUtil.isNotEmpty(filter)) {
			queryWrapper.like("name", filter);
			queryWrapper.or().like("description", filter);
		}
		queryWrapper.orderByDesc("create_time");
		Page<Generate> page = new Page<Generate>(pageNo, pageSize);
		IPage<Generate> pageList = generateService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 列表查询
	 *
	 * @param filter
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-列表查询")
	@ApiOperation(value = "代码生成-列表查询", notes = "代码生成-列表查询")
	@GetMapping(value = "/listAll")
	public Result<?> queryListAll(@RequestParam(name = "filter", required = false) String filter,
								  HttpServletRequest req) {
		QueryWrapper<Generate> queryWrapper = new QueryWrapper();
		if (StrUtil.isNotEmpty(filter)) {
			queryWrapper.like("name", filter);
			queryWrapper.or().like("description", filter);
		}
		queryWrapper.orderByDesc("create_time");
		List<Generate> resultList = generateService.list(queryWrapper);
		return Result.ok(resultList);
	}

	/**
	 * 数据库表分页列表查询
	 *
	 * @param filter
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-数据库表分页列表查询")
	@ApiOperation(value = "代码生成-数据库表分页列表查询", notes = "代码生成-数据库表分页列表查询")
	@GetMapping(value = "/tableList")
	public Result<?> queryPageTableList(@RequestParam(name = "filter") String filter,
										@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
										@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
										HttpServletRequest req) {
		IPage<TableSchema> pageList = generateService.queryPageTableList(filter, pageNo, pageSize);
		return Result.ok(pageList);
	}

	/**
	 * 同步表结构-根据表名
	 *
	 * @param tableName
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-根据表名同步到配置信息")
	@ApiOperation(value = "代码生成-根据表名同步到配置信息", notes = "代码生成-根据表名同步到配置信息")
	@GetMapping(value = "/syncTableByName")
	public Result<?> syncTableByName(@RequestParam(name = "tableName") String tableName,
									 HttpServletRequest req) {
		return Result.ok(generateService.syncTableByName(tableName));
	}

	/**
	 * 同步表结构-根据代码生成ID
	 *
	 * @param id
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-同步数据库表结构到配置信息")
	@ApiOperation(value = "代码生成-同步数据库表结构到配置信息", notes = "代码生成-同步数据库表结构到配置信息")
	@GetMapping(value = "/syncTableById")
	public Result<?> syncTable(@RequestParam(name = "id") String id,
							   HttpServletRequest req) {
		return Result.ok(generateService.syncTableById(id));
	}

	/**
	 * 同步表结构-同步表结构到数据库
	 *
	 * @param id
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-同步表结构到数据库")
	@ApiOperation(value = "代码生成-同步表结构到数据库", notes = "代码生成-同步表结构到数据库")
	@GetMapping(value = "/syncTableToDb")
	@RequiresRoles({"admin"})
	public Result<?> syncTableToDb(@RequestParam(name = "id") String id,
								   HttpServletRequest req) {
		generateService.syncTableToDb(id);
		return Result.ok(generateService.syncTableById(id));
	}


	/**
	 * 同步表结构-同步表结构到数据库
	 *
	 * @param id
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代码生成-同步表结构到配置信息")
	@ApiOperation(value = "代码生成-同步表结构到配置信息", notes = "代码生成-同步表结构到配置信息")
	@GetMapping(value = "/syncTableToConfig")
	@RequiresRoles({"admin"})
	public Result<?> syncTableToConfig(@RequestParam(name = "id") String id,HttpServletRequest req) {
		return Result.ok(generateService.syncTableToConfig(id));
	}

	/**
	 * 获取表列信息
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "代码生成-获取表列信息")
	@ApiOperation(value = "代码生成-获取表列信息", notes = "代码生成-获取表列信息")
	@GetMapping(value = "/queryColumns")
	public Result<?> queryColumns(@RequestParam(name = "id") String id) {
		return Result.ok(generateService.queryColumns(id));
	}

	/**
	 * 添加
	 *
	 * @param generate
	 * @return
	 */
	@AutoLog(value = "代码生成-添加")
	@ApiOperation(value = "代码生成-添加", notes = "代码生成-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Generate generate) {
		generateService.save(generate);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param generate
	 * @return
	 */
	@AutoLog(value = "代码生成-编辑")
	@ApiOperation(value = "代码生成-编辑", notes = "代码生成-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Generate generate) {
		if ("1".equalsIgnoreCase(generate.getIsSync())) {
			generate.setIsSync(generateService.isSync(generate));
		}
		generateService.updateById(generate);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "代码生成-通过id删除")
	@ApiOperation(value = "代码生成-通过id删除", notes = "代码生成-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		generateService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "代码生成-批量删除")
	@ApiOperation(value = "代码生成-批量删除", notes = "代码生成-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.generateService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "代码生成-通过id查询")
	@ApiOperation(value = "代码生成-通过id查询", notes = "代码生成-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		Generate generate = generateService.getById(id);
		return Result.ok(generate);
	}

	/**
	 * 通过id查询代码
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "代码生成-通过id查询代码")
	@ApiOperation(value = "代码生成-通过id查询代码", notes = "代码生成-通过id查询代码")
	@GetMapping(value = "/queryCodeById")
	public Result<?> queryCodeById(@RequestParam(name = "id", required = true) String id) {
		Generate generate = generateService.getById(id);
		GenerateContent generateContent = JSONObject.parseObject(generate.getContent(), GenerateContent.class);
		GenerateForm generateForm = generateContent.getGenerateForm();
		generateForm.setId(id);
		Map<String, String> result = FreemarkerUtil.generateCode(generateForm, generate);
		return Result.ok(result);
	}

	/**
	 * 编辑
	 *
	 * @param generateForm
	 * @return
	 */
	@AutoLog(value = "代码生成-生成代码")
	@ApiOperation(value = "代码生成-生成代码", notes = "代码生成-生成代码")
	@PutMapping(value = "/doGenerateCode")
	public Result<?> doGenerateCode(@RequestBody GenerateForm generateForm) {
		FileUtil.del(FreemarkerUtil.PATH);
		Generate generate = generateService.getById(generateForm.getId());
		//如果是附表，那么主表显示字段还需要设置下
		if ("main".equals(generate.getTableType())) {
			//先把主表生成好
			generateService.doGenerateCode(generateForm, generate);
			//然后生成从表的
			GenerateContent generateContent = JSONObject.parseObject(generate.getContent(), GenerateContent.class);
			List<RelateData> relateDataList = generateContent.getRelateDataList();
			relateDataList.forEach(relateData -> {
				if (StrUtil.isNotEmpty(relateData.getSlaveTable())) {
					Generate slaveGenerate = generateService.getById(relateData.getSlaveTable());
					// 这里把主表和主表对应的字段写上
					GenerateContent slaveGenerateContent = JSONObject.parseObject(slaveGenerate.getContent(), GenerateContent.class);
					slaveGenerateContent.getRelateDataList().forEach(slaveRelateData -> {
						if (relateData.getSlaveColumn().equals(slaveRelateData.getId())) {
							if (StrUtil.isNotEmpty(relateData.getId())) {
								slaveGenerate.setMainColumn(relateData.getId());
								String mainColumnCode = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, relateData.getId());
								slaveGenerate.setMainColumnCode(mainColumnCode);
							}
							if (StrUtil.isNotEmpty(slaveRelateData.getId())) {
								slaveGenerate.setSlaveColumn(slaveRelateData.getId());
								String slaveCode = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, slaveRelateData.getId());
								slaveGenerate.setSlaveColumnCode(slaveCode);
							}
						}
					});
					generateService.doGenerateCode(slaveGenerateContent.getGenerateForm(), slaveGenerate);
				}
			});

		} else if ("middle".equals(generate.getTableType())) {
		} else {
			generateService.doGenerateCode(generateForm, generate);
		}
		return Result.ok("生成代码成功!");
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param generate
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, Generate generate) {
		return super.exportXls(request, generate, Generate.class, "代码生成");
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
		return super.importExcel(request, response, Generate.class);
	}


	/**
	 * 代码下载
	 * @param id
	 */
	@RequestMapping(value = "/downloadCode")
	public ModelAndView exportXls(@RequestParam(name = "id", required = true) String id) {


		Generate generate = generateService.getById(id);
		GenerateContent generateContent = JSONObject.parseObject(generate.getContent(), GenerateContent.class);
		GenerateForm generateForm = generateContent.getGenerateForm();
		generateForm.setId(id);
		FreemarkerUtil.generateCode(generateForm, generate);

		ModelAndView mv = new ModelAndView(new ZipEntityView());
		String fileName = String.format("generate-%s.zip",String.valueOf(System.currentTimeMillis()));
		mv.addObject(EntityViewConstants.FILE_NAME, fileName);
		//这里是生成好的代码
		mv.addObject(EntityViewConstants.SRC_PATH, FreemarkerUtil.PATH);
		String tempPath =System.getProperty("java.io.tmpdir")+ File.separator;
		mv.addObject(EntityViewConstants.ZIP_PATH, String.format("%s%s",tempPath, fileName));
		return mv;
	}
}
