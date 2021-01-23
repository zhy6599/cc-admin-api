package cc.admin.modules.sys.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.common.sys.vo.DynamicDataSourceModel;
import cc.admin.common.util.dynamic.db.DataSourceCachePool;
import cc.admin.common.util.dynamic.db.DynamicDBUtil;
import cc.admin.modules.sys.entity.SysDataSource;
import cc.admin.modules.sys.service.ISysCatalogService;
import cc.admin.modules.sys.service.ISysDataSourceService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "多数据源管理")
@RestController
@RequestMapping("/sys/dataSource")
public class SysDataSourceController extends BaseController<SysDataSource, ISysDataSourceService> {

    @Autowired
    private ISysDataSourceService sysDataSourceService;
	@Autowired
	private ISysCatalogService sysCatalogService;

    /**
     * 分页列表查询
     *
     * @param sysDataSource
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "多数据源管理-分页列表查询")
    @ApiOperation(value = "多数据源管理-分页列表查询", notes = "多数据源管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            SysDataSource sysDataSource,
			@RequestParam(name = "key",required = false) String key,
			@RequestParam(name = "catalog",required = false) String catalog,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req
    ) {
        QueryWrapper<SysDataSource> queryWrapper = QueryGenerator.initQueryWrapper(sysDataSource, req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.or(i->i.like("name", key).or().like("remark", key));
		}
		if (StrUtil.isNotEmpty(catalog)) {
			queryWrapper.inSql("catalog_id", String.format("select b.id from sys_catalog a, sys_catalog b where a.id='%s' and a.lft <= b.lft and a.rgh >= b.rgh", catalog));
		}
        Page<SysDataSource> page = new Page<>(pageNo, pageSize);
        IPage<SysDataSource> pageList = sysDataSourceService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/options")
    public Result<?> queryOptions(SysDataSource sysDataSource, HttpServletRequest req) {
        QueryWrapper<SysDataSource> queryWrapper = QueryGenerator.initQueryWrapper(sysDataSource, req.getParameterMap());
        List<SysDataSource> pageList = sysDataSourceService.list(queryWrapper);
        JSONArray array = new JSONArray(pageList.size());
        for (SysDataSource item : pageList) {
            JSONObject option = new JSONObject(3);
            option.put("value", item.getId());
            option.put("label", item.getName());
            option.put("text", item.getName());
            array.add(option);
        }
        return Result.ok(array);
    }

    /**
     * 添加
     *
     * @param sysDataSource
     * @return
     */
    @AutoLog(value = "多数据源管理-添加")
    @ApiOperation(value = "多数据源管理-添加", notes = "多数据源管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysDataSource sysDataSource) {
        sysDataSourceService.save(sysDataSource);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sysDataSource
     * @return
     */
    @AutoLog(value = "多数据源管理-编辑")
    @ApiOperation(value = "多数据源管理-编辑", notes = "多数据源管理-编辑")
    @PutMapping(value = "/edit")
	@RequiresRoles({"admin"})
    public Result<?> edit(@RequestBody SysDataSource sysDataSource) {
        SysDataSource d = sysDataSourceService.getById(sysDataSource.getId());
        DataSourceCachePool.removeCache(d.getId());
        sysDataSourceService.updateById(sysDataSource);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "多数据源管理-通过id删除")
    @ApiOperation(value = "多数据源管理-通过id删除", notes = "多数据源管理-通过id删除")
    @DeleteMapping(value = "/delete")
	@RequiresRoles({"admin"})
    public Result<?> delete(@RequestParam(name = "id") String id) {
        SysDataSource sysDataSource = sysDataSourceService.getById(id);
        DataSourceCachePool.removeCache(sysDataSource.getId());
        sysDataSourceService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "多数据源管理-批量删除")
    @ApiOperation(value = "多数据源管理-批量删除", notes = "多数据源管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
	@RequiresRoles({"admin"})
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(item->{
            SysDataSource sysDataSource = sysDataSourceService.getById(item);
            DataSourceCachePool.removeCache(sysDataSource.getId());
        });
        this.sysDataSourceService.removeByIds(idList);
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "多数据源管理-通过id查询")
    @ApiOperation(value = "多数据源管理-通过id查询", notes = "多数据源管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        SysDataSource sysDataSource = sysDataSourceService.getById(id);
        return Result.ok(sysDataSource);
    }

	/**
	 * 添加
	 *
	 * @param sysDataSource
	 * @return
	 */
	@AutoLog(value = "多数据源管理-测试连接")
	@ApiOperation(value = "多数据源管理-测试连接", notes = "多数据源管理-测试连接")
	@PostMapping(value = "/testConnection")
	public Result<?> testConnection(@RequestBody SysDataSource sysDataSource) {
		DruidPooledConnection connection = null;
		try {
			DynamicDataSourceModel dbSource = new DynamicDataSourceModel();
			BeanUtils.copyProperties(sysDataSource, dbSource);
			DruidDataSource druidDataSource = DynamicDBUtil.getJdbcDataSource(dbSource);
			connection = druidDataSource.getConnection();
		} catch (SQLException e) {
			return Result.error(String.format("连接失败！错误信息：%s",e.getMessage()));
		}finally {
			DbUtil.close(connection);
		}
		return Result.ok("连接成功！");
	}

	/**
	 * 查询数据库列表
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "多数据源管理-查询数据库列表")
	@ApiOperation(value="多数据源管理-查询数据库列表", notes="多数据源管理-查询数据库列表")
	@GetMapping(value = "/databases")
	public Result<?> databases(@RequestParam(name = "id", required = true) String id) {
		return Result.ok(sysDataSourceService.databases(id));
	}


	/**
	 * 查询表列表
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "多数据源管理-查询表列表")
	@ApiOperation(value="多数据源管理-查询表列表", notes="多数据源管理-查询表列表")
	@GetMapping(value = "/tables")
	public Result<?> tables(@RequestParam(name = "id", required = true) String id,@RequestParam(name = "dbName", required = true) String dbName) {
		return Result.ok(sysDataSourceService.tables(id,dbName));
	}


    /**
     * 导出excel
     *
     * @param request
     * @param sysDataSource
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysDataSource sysDataSource) {
        return super.exportXls(request, sysDataSource, SysDataSource.class, "多数据源管理");
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
        return super.importExcel(request, response, SysDataSource.class);
    }

}
