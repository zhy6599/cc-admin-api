package cc.admin.modules.mnt.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.modules.mnt.entity.MntServer;
import cc.admin.modules.mnt.service.IMntServerService;
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
 * @Description: 服务器管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags="服务器管理")
@RestController
@RequestMapping("/mnt/server")
public class MntServerController extends BaseController<MntServer, IMntServerService> {
	@Autowired
	private IMntServerService mntServerService;

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
	@AutoLog(value = "服务器管理-分页列表查询")
	@ApiOperation(value="服务器管理-分页列表查询", notes="服务器管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
								   @RequestParam(name="key",required = false) String key,
								   @RequestParam(name = "catalog",required = false) String catalog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<MntServer> queryWrapper = QueryGenerator.initQueryWrapper(new MntServer(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i->i.like("name", key).or().like("user_name", key).or().like("ip", key));
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='MntServer' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}

		Page<MntServer> page = new Page<MntServer>(pageNo, pageSize);
		IPage<MntServer> pageList = mntServerService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 添加
	 *
	 * @param mntServer
	 * @return
	 */
	@AutoLog(value = "服务器管理-添加")
	@ApiOperation(value="服务器管理-添加", notes="服务器管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody MntServer mntServer) {
mntServerService.save(mntServer);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param mntServer
	 * @return
	 */
	@AutoLog(value = "服务器管理-编辑")
	@ApiOperation(value="服务器管理-编辑", notes="服务器管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody MntServer mntServer) {
mntServerService.updateById(mntServer);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "服务器管理-通过id删除")
	@ApiOperation(value="服务器管理-通过id删除", notes="服务器管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
mntServerService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "服务器管理-批量删除")
	@ApiOperation(value="服务器管理-批量删除", notes="服务器管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.mntServerService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "服务器管理-通过id查询")
	@ApiOperation(value="服务器管理-通过id查询", notes="服务器管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
MntServer mntServer = mntServerService.getById(id);
		return Result.ok(mntServer);
	}

  /**
   * 导出excel
   *
   * @param request
   * @param mntServer
   */
  @RequestMapping(value = "/exportXls")
  public ModelAndView exportXls(HttpServletRequest request, MntServer mntServer) {
      return super.exportXls(request, mntServer, MntServer.class, "服务器管理");
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
      return super.importExcel(request, response, MntServer.class);
  }

}
