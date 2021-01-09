package cc.admin.modules.bi.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.system.base.controller.BaseController;
import cc.admin.common.system.query.QueryGenerator;
import cc.admin.modules.bi.entity.Screen;
import cc.admin.modules.bi.service.IScreenService;
import cn.hutool.core.util.IdUtil;
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
 * @Description: 大屏
 * @Author: ZhangHouYing
 * @Date:   2020-06-13
 * @Version: V1.0
 */
@Api(tags="大屏")
@RestController
@RequestMapping("/bi/screen")
@Slf4j
public class ScreenController extends BaseController<Screen, IScreenService> {
	@Autowired
	private IScreenService screenService;

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
	@AutoLog(value = "大屏-分页列表查询")
	@ApiOperation(value="大屏-分页列表查询", notes="大屏-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name="key",required = false) String key,
			@RequestParam(name = "catalog",required = false) String catalog,
			@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
			@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<Screen> queryWrapper = QueryGenerator.initQueryWrapper(new Screen(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.or(i->i.like("name", key).or().like("description", key));
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.type='BiScreen' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}
		queryWrapper.orderByDesc("create_time");

		Page<Screen> page = new Page<Screen>(pageNo, pageSize);
		IPage<Screen> pageList = screenService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 *   添加
	 *
	 * @param screen
	 * @return
	 */
	@AutoLog(value = "大屏-添加")
	@ApiOperation(value="大屏-添加", notes="大屏-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody Screen screen) {
		screen.setId(IdUtil.fastUUID());
		screenService.save(screen);
		return Result.ok("添加成功！");
	}

	 /**
	  *   复制
	  * @param id
	  * @param name
	  * @return
	  */
	 @AutoLog(value = "大屏-复制")
	 @ApiOperation(value="大屏-复制", notes="大屏-复制")
	 @PostMapping(value = "/copy")
	 public Result<?> copy(@RequestParam(name="id",required=true) String id,@RequestParam(name="name",required=true) String name) {
		 Screen screen = screenService.getById(id);
		 screen.setId(IdUtil.fastUUID());
		 screen.setName(name);
		 screenService.save(screen);
		 return Result.ok("复制成功！");
	 }

	/**
	 *  编辑
	 *
	 * @param screen
	 * @return
	 */
	@AutoLog(value = "大屏-编辑")
	@ApiOperation(value="大屏-编辑", notes="大屏-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody Screen screen) {
		Screen orgin = screenService.getById(screen.getId());
		orgin.setName(screen.getName());
		orgin.setDescription(screen.getDescription());
		screenService.updateById(screen);
		return Result.ok("编辑成功!");
	}

	 /**
	  *  编辑
	  *
	  * @param screen
	  * @return
	  */
	 @AutoLog(value = "大屏-更新配置信息")
	 @ApiOperation(value="大屏-更新配置信息", notes="大屏-更新配置信息")
	 @PutMapping(value = "/editConfig")
	 public Result<?> editConfig(@RequestBody Screen screen) {
		 Screen orgin = screenService.getById(screen.getId());
		 orgin.setConfig(screen.getConfig());
		 screenService.updateById(orgin);
		 return Result.ok("更新配置信息成功!");
	 }

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "大屏-通过id删除")
	@ApiOperation(value="大屏-通过id删除", notes="大屏-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		screenService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "大屏-批量删除")
	@ApiOperation(value="大屏-批量删除", notes="大屏-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.screenService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "大屏-通过id查询")
	@ApiOperation(value="大屏-通过id查询", notes="大屏-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		Screen screen = screenService.getById(id);
		if(screen==null) {
			return Result.error("未找到对应数据");
		}
		return Result.ok(screen);
	}

	 /**
	  * 检查名称是否重复
	  *
	  * @param id
	  * @param name
	  * @return
	  */
	 @AutoLog(value = "检查名称是否重复")
	 @ApiOperation(value = "检查名称是否重复", notes = "检查名称是否重复")
	 @GetMapping(value = "/checkName")
	 public Result<?> checkName(@RequestParam(name = "id", required = false) String id, @RequestParam(name = "name", required = true) String name) {
		 boolean exist = screenService.checkNameExist(id, name);
		 if (exist) {
			 return Result.error("名称已经存在");
		 }
		 return Result.ok();
	 }


    /**
    * 导出excel
    *
    * @param request
    * @param screen
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Screen screen) {
        return super.exportXls(request, screen, Screen.class, "大屏");
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
        return super.importExcel(request, response, Screen.class);
    }

}
