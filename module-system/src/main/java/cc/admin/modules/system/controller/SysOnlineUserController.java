package cc.admin.modules.system.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.aspect.annotation.AutoLog;
import cc.admin.modules.system.service.ISysOnlineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 在线用户
 * @Author: cc-admin
 * @Date: 2021-01-17
 * @Version: V1.0.0
 */
@Slf4j
@Api(tags = "在线用户")
@RestController
@RequestMapping("/sys/onlineUser")
public class SysOnlineUserController {

	@Autowired
	private ISysOnlineUserService sysOnlineUserService;

	/**
	 * 列表查询
	 *
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "在线用户-列表查询")
	@ApiOperation(value = "在线用户-列表查询", notes = "在线用户-列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(
			@RequestParam(name = "key", required = false) String key,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		return Result.ok(sysOnlineUserService.onlineUserList(key, pageNo, pageSize));
	}

	/**
	 * 在线用户-下线
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "在线用户-下线")
	@ApiOperation(value = "在线用户-下线", notes = "在线用户-下线")
	@DeleteMapping(value = "/offline")
	public Result<?> offline(@RequestParam(name = "id", required = true) String id) {
		return Result.error("用户权限不足!");
	}

}
