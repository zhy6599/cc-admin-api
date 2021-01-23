package cc.admin.modules.monitor.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.modules.monitor.service.ServerMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhang houying
 * @date 2019-11-03
 */
@RestController
@RequestMapping("/monitor/serverMonitor")
@Slf4j
public class ServerMonitorController {

	@Autowired
	private ServerMonitorService serverMonitorService;

	@RequestMapping(value = "/getServerInfo", method = RequestMethod.GET)
	public Result getServerInfo() {
		return Result.ok(serverMonitorService.getServerInfo());
	}

}
