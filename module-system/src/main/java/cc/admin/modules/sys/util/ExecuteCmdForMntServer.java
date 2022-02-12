package cc.admin.modules.sys.util;

import cc.admin.modules.mnt.entity.MntServer;
import com.google.common.collect.ImmutableList;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
/**
 * @author: ZhangHouYing
 * @date: 2021-03-25 9:23
 */
@Slf4j
public class ExecuteCmdForMntServer {

	/**
	 * 执行一条命令
	 *
	 * @param mntServer
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public static String executeCmd(MntServer mntServer, String cmd) throws Exception {
		return executeCmdList(mntServer, ImmutableList.of(cmd));
	}

	/**
	 * 执行多个命令
	 *
	 * @param mntServer
	 * @param cmdList
	 * @return
	 * @throws Exception
	 */
	public static String executeCmdList(MntServer mntServer, List<String> cmdList) throws Exception {
		StringBuilder sb = new StringBuilder();
		JSCHUtil jschUtil = JSCHUtil.getInstance();
		Session session = null;
		try {
			session = jschUtil.connect(mntServer.getIp(), mntServer.getPort(), mntServer.getUserName(), mntServer.getPassword(), mntServer.getKeyFile());
			for (String cmd : cmdList) {
				log.info("服务器:{} 执行命令:{}", mntServer.getIp(), cmd);
				String result = jschUtil.execCmd(session, cmd);
				log.info("服务器:{} 命令返回:{}", mntServer.getIp(), result);
				sb.append(result).append("\n");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			jschUtil.clear(session);
		}
		return sb.toString();
	}

	/**
	 * 批量执行
	 *
	 * @param mntServer
	 * @param cmdList
	 * @return
	 * @throws Exception
	 */
	public static String executeBatchCmd(MntServer mntServer, List<String> cmdList) throws Exception {
		JSCHUtil jschUtil = JSCHUtil.getInstance();
		Session session = null;
		String result = "";
		try {
			session = jschUtil.connect(mntServer.getIp(), mntServer.getPort(), mntServer.getUserName(), mntServer.getPassword(), mntServer.getKeyFile());
			//规定30秒超时
			session.setTimeout(30000);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < cmdList.size(); i++) {
				String cmd = cmdList.get(i);
				sb.append(cmd).append("\n");
			}
			log.info("服务器:{} 批量执行命令:{}", mntServer.getIp(), sb.toString());
			result = JSCHUtil.getInstance().execCmd(session, sb.toString());
			log.info("服务器:{} 批量返回结果:{}", mntServer.getIp(), result);
		} catch (Exception e) {
			throw e;
		} finally {
			jschUtil.clear(session);
		}
		return result;
	}

	public static void uploadFile(MntServer mntServer, String localFile, String remoteFile) {
		ScpClientUtil scpClientUtil = ScpClientUtil.getInstance(mntServer.getIp(), mntServer.getPort(), mntServer.getUserName(), mntServer.getPassword(), mntServer.getKeyFile());
		scpClientUtil.putFile(localFile, remoteFile);
	}
}
