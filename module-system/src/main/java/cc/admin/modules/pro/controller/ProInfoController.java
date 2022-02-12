package cc.admin.modules.pro.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.pro.entity.ProInfo;
import cc.admin.modules.pro.service.IProInfoService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 信息发布
 * @Author: cc-admin
 * @Date: 2021-04-05
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "信息发布")
@RestController
@RequestMapping("/pro/info")
public class ProInfoController extends BaseController<ProInfo, IProInfoService> {
	@Autowired
	private IProInfoService proInfoService;

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
	@AutoLog(value = "信息发布-分页列表查询")
	@ApiOperation(value = "信息发布-分页列表查询", notes = "信息发布-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "catalog", required = false) String catalog,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<ProInfo> queryWrapper = QueryGenerator.initQueryWrapper(new ProInfo(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i -> i.like("name", key).or().like("content", key).or().like("phone", key).or().like("wechat", key).or().like("qq", key));
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='ProInfo' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}
		queryWrapper.orderByDesc("create_time");
		Page<ProInfo> page = new Page<ProInfo>(pageNo, pageSize);
		IPage<ProInfo> pageList = proInfoService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param proInfo
	 * @return
	 */
	@AutoLog(value = "信息发布-添加")
	@ApiOperation(value = "信息发布-添加", notes = "信息发布-添加")
	@PostMapping(value = "/add")
	@RequiresRoles({"admin"})
	public Result<?> add(@RequestBody ProInfo proInfo) {
		proInfo.setUpCount(0);
		proInfo.setReadCount(0);
		proInfo.setStatus("open");
		proInfoService.save(proInfo);
		return Result.ok("添加成功！");
	}

	/**
	 * 信息点赞
	 *
	 * @param proInfo
	 * @return
	 */
	@AutoLog(value = "留言板-信息点赞")
	@ApiOperation(value = "留言板-信息点赞", notes = "留言板-信息点赞")
	@PostMapping(value = "/upRemark")
	public Result<?> upRemark(@RequestBody ProInfo proInfo) {
		ProInfo info = proInfoService.getById(proInfo.getId());
		info.setUpCount(info.getUpCount() + 1);
		proInfoService.updateById(info);
		return Result.ok("点赞成功！");
	}

	/**
	 * 编辑
	 *
	 * @param proInfo
	 * @return
	 */
	@AutoLog(value = "信息发布-编辑")
	@ApiOperation(value = "信息发布-编辑", notes = "信息发布-编辑")
	@PutMapping(value = "/edit")
	@RequiresRoles({"admin"})
	public Result<?> edit(@RequestBody ProInfo proInfo) {
		proInfoService.updateById(proInfo);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "信息发布-通过id删除")
	@ApiOperation(value = "信息发布-通过id删除", notes = "信息发布-通过id删除")
	@DeleteMapping(value = "/delete")
	@RequiresRoles({"admin"})
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		proInfoService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "信息发布-批量删除")
	@ApiOperation(value = "信息发布-批量删除", notes = "信息发布-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	@RequiresRoles({"admin"})
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.proInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "信息发布-通过id查询")
	@ApiOperation(value = "信息发布-通过id查询", notes = "信息发布-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		ProInfo proInfo = proInfoService.getById(id);
		return Result.ok(proInfo);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param proInfo
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, ProInfo proInfo) {
		return super.exportXls(request, proInfo, ProInfo.class, "信息发布");
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
		return super.importExcel(request, response, ProInfo.class);
	}

}
