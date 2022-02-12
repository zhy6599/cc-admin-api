package cc.admin.modules.sys.util;

import cn.hutool.core.util.StrUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.util.Properties;
/**
 * @author: ZhangHouYing
 * @date: 2021-01-15 17:06
 */
public class JSCHUtil {

	private static JSCHUtil instance;

	private JSCHUtil() {
	}

	public static JSCHUtil getInstance() {
		if (instance == null) {
			instance = new JSCHUtil();
		}
		return instance;
	}

	private Session getSession(String host, int port, String ueseName, String authenticateFile)
			throws Exception {
		JSch jsch = new JSch();
		if (StrUtil.isNotEmpty(authenticateFile)) {
			jsch.addIdentity(authenticateFile, "");
		}
		Session session = jsch.getSession(ueseName, host, port);
		return session;
	}

	public Session connect(String host, int port, String ueseName,
						   String password, String authenticateFile) throws Exception {
		Session session = getSession(host, port, ueseName, authenticateFile);
		session.setPassword(password);
		Properties config = new Properties();
		config.setProperty("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		return session;
	}

	public String execCmd(Session session, String command)
			throws Exception {
		if (session == null) {
			throw new RuntimeException("Session is null!");
		}
		ChannelExec exec = (ChannelExec) session.openChannel("exec");
		InputStream in = exec.getInputStream();
		byte[] b = new byte[1024];
		exec.setCommand(command);
		exec.connect();
		StringBuffer buffer = new StringBuffer();
		while (in.read(b) > 0) {
			buffer.append(new String(b));
		}
		exec.disconnect();
		return buffer.toString();
	}

	public void clear(Session session) {
		if ((session != null) && session.isConnected()) {
			session.disconnect();
			session = null;
		}
	}
}
