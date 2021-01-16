package cc.admin.modules.system.service;

import cc.admin.modules.system.entity.SysOnlineUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: 在线用户
 * @Author: cc-admin
 * @Date:   2021-01-17
 * @Version: V1.0.0
 */
public interface ISysOnlineUserService {

	/**
	 * 在线用户列表
	 * @return
	 */
	Page<SysOnlineUser> onlineUserList(String key, int pageNo, int pageSize);
}
