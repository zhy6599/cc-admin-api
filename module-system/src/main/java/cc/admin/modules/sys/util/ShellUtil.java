package cc.admin.modules.sys.util;

import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
/**
 * @author: ZhangHouYing
 * @date: 2021-03-10 10:45
 */
@Slf4j
public class ShellUtil {

	private String userName = "";
	private String password = "";
	private String authenticateFile = "";

	public ShellUtil() {
	}

	public ShellUtil(String userName, String password, String authenticateFile) {
		this.userName = userName;
		this.password = password;
		this.authenticateFile = authenticateFile;
	}

	synchronized public ShellUtil getInstance(String userName, String password, String authenticateFile) {
		return new ShellUtil(userName, password, authenticateFile);
	}

	public void executeCmdList(String ip, List<String> cmdList) throws Exception {
		Session session = JSCHUtil.getInstance().connect(ip, 22, userName, password, authenticateFile);
		for (int i = 0; i < cmdList.size(); i++) {
			String cmd = cmdList.get(i);
			log.info("服务器:{} 执行命令:{}", ip, cmd);
			String result = JSCHUtil.getInstance().execCmd(session, cmd);
			log.info("返回结果:{}", result);
		}
	}

	public void executeCmd(String ip, String cmd) throws Exception {
		Session session = JSCHUtil.getInstance().connect(ip, 22, userName, password, authenticateFile);
		log.info("服务器:{} 执行命令:{}", ip, cmd);
		String result = JSCHUtil.getInstance().execCmd(session, cmd);
		log.info("返回结果:{}", result);
	}

	public void executeBatchCmd(String ip, List<String> cmdList) throws Exception {
		Session session = JSCHUtil.getInstance().connect(ip, 22, userName, password, authenticateFile);
		//规定5秒超时
		session.setTimeout(5000);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cmdList.size(); i++) {
			String cmd = cmdList.get(i);
			sb.append(cmd).append("\n");
		}
		log.info("服务器:{} 批量执行命令:{}", ip, sb.toString());
		String result = JSCHUtil.getInstance().execCmd(session, sb.toString());
		log.info("批量返回结果:{}", result);
	}

	public void uploadFile(String ip, String localFile, String remoteFile) {
		ScpClientUtil scpClientUtil = ScpClientUtil.getInstance(ip, 22, userName, password, authenticateFile);
		scpClientUtil.putFile(localFile, remoteFile);
	}

	public static void sleep(int second) {
		try {
			System.out.println(String.format("开始睡眠%d秒", second));
			Thread.sleep(second * 1000);
			System.out.println(String.format("睡眠结束", second));
		} catch (Exception e) {
		}
	}

}
