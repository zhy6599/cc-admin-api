/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cc.admin.modules.sys.util;

import cc.admin.modules.mnt.entity.MntServer;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 远程执行linux命令
 * @author: ZhangHouYing
 * @date: 2021-02-08 22:33
 */
@Slf4j
public class ScpClientUtil {

	private String ip;
	private int port;
	private String username;
	private String password;
	private String authenticateFile;
	/**
	 * keyFile   password
	 */
	private String loginType = "password";

	public ScpClientUtil(MntServer mntServer) {
		this.ip = mntServer.getIp();
		this.username = mntServer.getUserName();
		this.password = mntServer.getPassword();
		this.port = mntServer.getPort();
		this.authenticateFile = mntServer.getKeyFile();
		this.loginType = mntServer.getLoginType();
	}

	public void getFile(String remoteFile, String localTargetDirectory) {
		Connection conn = new Connection(ip, port);
		try {
			conn.connect();
			author(conn);
			SCPClient client = new SCPClient(conn);
			client.get(remoteFile, localTargetDirectory);
		} catch (IOException ex) {
			log.error("获取文件失败",ex);
		}finally{
			conn.close();
		}
	}

	public void putFile(String localFile, String remoteTargetDirectory) {
		putFile(localFile, null, remoteTargetDirectory);
	}

	public void putFile(String localFile, String remoteFileName, String remoteTargetDirectory) {
		putFile(localFile, remoteFileName, remoteTargetDirectory,null);
	}

	public void putFile(String localFile, String remoteFileName, String remoteTargetDirectory, String mode) {
		Connection conn = new Connection(ip, port);
		try {
			conn.connect();
			author(conn);
			SCPClient client = new SCPClient(conn);
			if ((mode == null) || (mode.length() == 0)) {
				mode = "0600";
			}
			if (remoteFileName == null) {
				client.put(localFile, remoteTargetDirectory);
			} else {
				client.put(localFile, remoteFileName, remoteTargetDirectory, mode);
			}
		} catch (IOException ex) {
			log.error("上传文件失败",ex);
		}finally{
			conn.close();
		}
	}

	private void author(Connection conn) throws IOException {
		if ("password".equals(loginType)) {
			boolean isAuthenticated = conn.authenticateWithPassword(username, password);
			if (!isAuthenticated) {
				log.error("authentication failed");
			}
		}

		if ("keyFile".equals(loginType)) {
			boolean isAuthenticated = conn.authenticateWithPublicKey(username, new File(authenticateFile),"");
			if (!isAuthenticated) {
				log.error("authentication failed");
			}
		}
	}

}
