package cc.admin.modules.pro.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.pro.entity.ProRemark;
import cc.admin.modules.pro.service.IProRemarkService;
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
 * @Description: 留言板
 * @Author: cc-admin
 * @Date: 2021-04-05
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "留言板")
@RestController
@RequestMapping("/pro/remark")
public class ProRemarkController extends BaseController<ProRemark, IProRemarkService> {
	@Autowired
	private IProRemarkService proRemarkService;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "留言板-分页列表查询")
	@ApiOperation(value = "留言板-分页列表查询", notes = "留言板-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<ProRemark> queryWrapper = QueryGenerator.initQueryWrapper(new ProRemark(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i -> i.like("name", key));
		}
		Page<ProRemark> page = new Page<ProRemark>(pageNo, pageSize);
		IPage<ProRemark> pageList = proRemarkService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 列表查询
	 *
	 * @param pid
	 * @return
	 */
	@AutoLog(value = "留言板-列表查询")
	@ApiOperation(value = "留言板-列表查询", notes = "留言板-列表查询")
	@GetMapping(value = "/queryListByPid")
	public Result<?> queryListByPid(
			@RequestParam(name = "pid", required = true) String pid,
			HttpServletRequest req) {
		QueryWrapper<ProRemark> queryWrapper = QueryGenerator.initQueryWrapper(new ProRemark(), req.getParameterMap());
		queryWrapper.and(i -> i.eq("pid", pid));
		queryWrapper.orderByDesc("create_time");
		List<ProRemark> pageList = proRemarkService.list(queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param proRemark
	 * @return
	 */
	@AutoLog(value = "留言板-添加")
	@ApiOperation(value = "留言板-添加", notes = "留言板-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody ProRemark proRemark) {
		proRemarkService.save(proRemark);
		return Result.ok("添加成功！");
	}

	/**
	 * 评论点赞
	 *
	 * @param proRemark
	 * @return
	 */
	@AutoLog(value = "留言板-评论点赞")
	@ApiOperation(value = "留言板-评论点赞", notes = "留言板-评论点赞")
	@PostMapping(value = "/upRemark")
	public Result<?> upRemark(@RequestBody ProRemark proRemark) {
		ProRemark remark = proRemarkService.getById(proRemark.getId());
		remark.setUpCount(remark.getUpCount() + 1);
		proRemarkService.updateById(remark);
		return Result.ok("点赞成功！");
	}

	/**
	 * 编辑
	 *
	 * @param proRemark
	 * @return
	 */
	@AutoLog(value = "留言板-编辑")
	@ApiOperation(value = "留言板-编辑", notes = "留言板-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody ProRemark proRemark) {
		proRemarkService.updateById(proRemark);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "留言板-通过id删除")
	@ApiOperation(value = "留言板-通过id删除", notes = "留言板-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		proRemarkService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "留言板-批量删除")
	@ApiOperation(value = "留言板-批量删除", notes = "留言板-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.proRemarkService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "留言板-通过id查询")
	@ApiOperation(value = "留言板-通过id查询", notes = "留言板-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		ProRemark proRemark = proRemarkService.getById(id);
		return Result.ok(proRemark);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param proRemark
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, ProRemark proRemark) {
		return super.exportXls(request, proRemark, ProRemark.class, "留言板");
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
		return super.importExcel(request, response, ProRemark.class);
	}

}
