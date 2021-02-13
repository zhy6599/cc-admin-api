package cc.admin.modules.sys.util;

import cc.admin.modules.mnt.entity.MntServer;
import cn.hutool.core.io.IoUtil;
import com.google.common.collect.ImmutableList;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
/**
 * @author: ZhangHouYing
 * @date: 2021-02-08 22:33
 */
@Slf4j
public class JSCHUtil {

	private String ip = "";
	private int port = 22;
	private String username = "root";
	/**
	 * keyFile   password
	 */
	private String loginType = "password";
	private String password;
	private String authenticateFile = "";
	private String uploadPath;
	private Session session;

	public JSCHUtil(MntServer mntServer, String uploadPath) throws Exception {
		this.ip = mntServer.getIp();
		this.username = mntServer.getUserName();
		this.password = mntServer.getPassword();
		this.port = mntServer.getPort();
		this.authenticateFile = mntServer.getKeyFile();
		this.loginType = mntServer.getLoginType();
		this.uploadPath = uploadPath;
		session = getSession();
	}

	public Session getSession() throws Exception {
		JSch jsch = new JSch();
		if ("keyFile".equals(loginType)) {
			jsch.addIdentity(uploadPath + File.separator + authenticateFile, "");
		}
		Session session = jsch.getSession(username, ip, port);
		if ("password".equals(loginType)) {
			session.setPassword(password);
		}
		Properties config = new Properties();
		config.setProperty("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		return session;
	}

	public String execCmd(String command) throws Exception {
		return execCmdList(ImmutableList.of(command));
	}

	public String execCmdList(List<String> commands) throws Exception {
		ChannelShell channel = null;
		PrintWriter printWriter = null;
		BufferedReader input = null;
		StringBuilder sb = new StringBuilder();
		try {
			channel = (ChannelShell) session.openChannel("shell");
			channel.connect();
			input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			printWriter = new PrintWriter(channel.getOutputStream());
			for (String command : commands) {
				printWriter.println(command);
				log.info("The command is: {}",command);
			}
			printWriter.println("exit");
			printWriter.flush();
			String line;
			while ((line = input.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			IoUtil.close(printWriter);
			IoUtil.close(input);
			if (channel != null) {
				channel.disconnect();
			}
		}
		log.info("Result: {}",sb.toString());
		return sb.toString();
	}

	public void clear() {
		if ((session != null) && session.isConnected()) {
			session.disconnect();
			session = null;
		}
	}

}
