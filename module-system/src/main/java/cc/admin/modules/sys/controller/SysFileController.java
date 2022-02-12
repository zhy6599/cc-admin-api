package cc.admin.modules.sys.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.common.sys.base.controller.BaseController;
import cc.admin.common.sys.query.QueryGenerator;
import cc.admin.common.util.CommonUtils;
import cc.admin.common.util.oConvertUtils;
import cc.admin.modules.sys.entity.SysFile;
import cc.admin.modules.sys.service.ISysFileService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * @Description: 文件管理
 * @Author: cc-admin
 * @Date: 2021-04-23
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/sys/file")
public class SysFileController extends BaseController<SysFile, ISysFileService> {
	@Autowired
	private ISysFileService sysFileService;

	@Value(value = "${cc.admin.path.upload}")
	private String uploadPath;

	/**
	 * 分页列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文件管理-分页列表查询")
	@ApiOperation(value = "文件管理-分页列表查询", notes = "文件管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		QueryWrapper<SysFile> queryWrapper = QueryGenerator.initQueryWrapper(new SysFile(), req.getParameterMap());
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.and(i -> i.like("name", key).or().like("type", key).or().like("main_id", key).or().like("path", key));
		}
		Page<SysFile> page = new Page<SysFile>(pageNo, pageSize);
		IPage<SysFile> pageList = sysFileService.page(page, queryWrapper);
		return Result.ok(pageList);
	}

	/**
	 * 分页列表查询
	 *
	 * @param mainId
	 * @param req
	 * @return
	 */
	@AutoLog(value = "文件管理-分页列表查询")
	@ApiOperation(value = "文件管理-分页列表查询", notes = "文件管理-分页列表查询")
	@GetMapping(value = "/listByMainId")
	public Result<?> listByMainId(
			@RequestParam(name = "mainId", required = false) String mainId,
			HttpServletRequest req) {
		QueryWrapper<SysFile> queryWrapper = QueryGenerator.initQueryWrapper(new SysFile(), req.getParameterMap());
		queryWrapper.and(i -> i.eq("main_id", mainId));
		return Result.ok(sysFileService.list(queryWrapper));
	}

	/**
	 * 添加
	 *
	 * @param sysFile
	 * @return
	 */
	@AutoLog(value = "文件管理-添加")
	@ApiOperation(value = "文件管理-添加", notes = "文件管理-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysFile sysFile) {
		sysFileService.save(sysFile);
		return Result.ok("添加成功！");
	}

	/**
	 * 编辑
	 *
	 * @param sysFile
	 * @return
	 */
	@AutoLog(value = "文件管理-编辑")
	@ApiOperation(value = "文件管理-编辑", notes = "文件管理-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysFile sysFile) {
		sysFileService.updateById(sysFile);
		return Result.ok("编辑成功!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "文件管理-通过id删除")
	@ApiOperation(value = "文件管理-通过id删除", notes = "文件管理-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		sysFileService.removeById(id);
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "文件管理-批量删除")
	@ApiOperation(value = "文件管理-批量删除", notes = "文件管理-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		this.sysFileService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.ok("批量删除成功！");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "文件管理-通过id查询")
	@ApiOperation(value = "文件管理-通过id查询", notes = "文件管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		SysFile sysFile = sysFileService.getById(id);
		return Result.ok(sysFile);
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param sysFile
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysFile sysFile) {
		return super.exportXls(request, sysFile, SysFile.class, "文件管理");
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
		return super.importExcel(request, response, SysFile.class);
	}

	/**
	 * 文件上传统一方法
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/upload")
	public Result<?> upload(HttpServletRequest request, HttpServletResponse response) {
		Result<?> result = new Result<>();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 获取上传文件对象
		MultipartFile file = multipartRequest.getFile("file");
		String mainId = request.getParameter("mainId");
		String savePath = this.uploadLocal(file, mainId);
		if (oConvertUtils.isNotEmpty(savePath)) {
			Result.ok(savePath);
		} else {
			result.setMessage("上传失败！");
			result.setSuccess(false);
		}
		return result;
	}

	/**
	 * 本地文件上传
	 *
	 * @param mf 文件
	 * @return
	 */
	private String uploadLocal(MultipartFile mf, String mainId) {
		try {
			String ctxPath = uploadPath;
			String bizPath = "sysFile" + File.separator + DateUtil.format(new Date(), "yyyyMM") + File.separator;
			//每个月存放一个目录
			File file = new File(ctxPath + File.separator + bizPath);
			if (!file.exists()) {
				file.mkdirs();// 创建文件根目录
			}
			// 获取文件名
			String orgName = mf.getOriginalFilename();
			orgName = CommonUtils.getFileName(orgName);
			String fileName = IdUtil.objectId() + orgName.substring(orgName.indexOf("."));
			String savePath = file.getPath() + File.separator + fileName;
			File saveFile = new File(savePath);
			FileCopyUtils.copy(mf.getBytes(), saveFile);
			String dbpath = bizPath + File.separator + fileName;
			if (dbpath.contains("\\")) {
				dbpath = dbpath.replace("\\", "/");
			}
			SysFile sysFile = new SysFile();
			sysFile.setId(IdUtil.objectId());
			sysFile.setName(orgName);
			String type = getFileType(orgName);
			sysFile.setType(type);
			sysFile.setMainId(mainId);
			sysFile.setPath(dbpath);
			QueryWrapper<SysFile> queryWrapper = QueryGenerator.initQueryWrapper(new SysFile(), Maps.newHashMap());
			queryWrapper.and(i -> i.eq("main_id", mainId));
			Integer sortOrder = sysFileService.count(queryWrapper);
			sysFile.setSortOrder(sortOrder);
			sysFileService.save(sysFile);
			return dbpath;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * 文件类型
	 *
	 * @param orgName
	 * @return
	 */
	private String getFileType(String orgName) {
		String type = "unknown";
		orgName = orgName.toLowerCase();
		if (orgName.endsWith("jpg") || orgName.endsWith("png") || orgName.endsWith("jpeg") || orgName.endsWith("bmp")) {
			type = "image";
		} else if (orgName.endsWith("mp4") || orgName.endsWith("mov") || orgName.endsWith("3gp") || orgName.endsWith("flv") || orgName.endsWith("rm") || orgName.endsWith("rmvb") || orgName.endsWith("avi")) {
			type = "video";
		} else {
			try {
				type = type.substring(type.lastIndexOf(".") + 1);
			} catch (Exception e) {}
		}
		return type;
	}

	public static void main(String[] args) {
		String p = "aaa.jepg";
		System.out.println();
	}
}
