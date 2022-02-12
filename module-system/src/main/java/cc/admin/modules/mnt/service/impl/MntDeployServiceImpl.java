package cc.admin.modules.mnt.service.impl;

import cc.admin.common.sys.vo.LoginUser;
import cc.admin.modules.message.websocket.MsgType;
import cc.admin.modules.message.websocket.WebSocket;
import cc.admin.modules.mnt.entity.MntApp;
import cc.admin.modules.mnt.entity.MntDeploy;
import cc.admin.modules.mnt.entity.MntMessage;
import cc.admin.modules.mnt.entity.MntServer;
import cc.admin.modules.mnt.mapper.MntDeployMapper;
import cc.admin.modules.mnt.service.IMntAppService;
import cc.admin.modules.mnt.service.IMntDeployService;
import cc.admin.modules.mnt.service.IMntServerService;
import cc.admin.modules.sys.util.ExecuteCmdForMntServer;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 部署管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Service
public class MntDeployServiceImpl extends ServiceImpl<MntDeployMapper, MntDeploy> implements IMntDeployService {

	@Value(value = "${cc.admin.path.upload}")
	private String uploadPath;

	@Autowired
	private WebSocket webSocket;

	@Autowired
	private IMntServerService mntServerService;
	@Autowired
	private IMntAppService mntAppService;


	/**
	 * 指定端口程序是否在运行
	 *
	 * @param port 端口
	 * @param mntServer
	 * @return true 正在运行  false 已经停止
	 */
	private boolean checkIsRunningStatus(int port,MntServer mntServer) {
		String result = "";
		try {
			result = ExecuteCmdForMntServer.executeCmd(mntServer, String.format("fuser -n tcp %d", port));
		} catch (Exception e) {
			log.error("连接服务器失败：",e);
			return false;
		}
		return result.indexOf("/tcp:")>0;
	}

	@Override
	public String serverStatus(List<String> ids) {
		StringBuilder sb = new StringBuilder();
		for (String id : ids) {
			MntDeploy mntDeploy = getById(id);
			String serverId = mntDeploy.getServerId();
			MntServer mntServer = mntServerService.getById(serverId);

			String appId = mntDeploy.getAppId();
			MntApp mntApp = mntAppService.getById(appId);
			sb.append("服务器:").append(mntServer.getName()).append("<br>应用:").append(mntApp.getName());
			boolean result = checkIsRunningStatus(mntApp.getPort(),mntServer);
			if (result) {
				sb.append("<br>正在运行");
				sendMsg(sb.toString(), MsgType.INFO);
			} else {
				sb.append("<br>已停止!");
				sendMsg(sb.toString(), MsgType.ERROR);
			}
		}
		return sb.toString();
	}

	private void sendMsg(String message, MsgType msgType) {
		try {
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			webSocket.sendOneMessage(sysUser.getId(),new MntMessage(message,msgType).toString());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
}
