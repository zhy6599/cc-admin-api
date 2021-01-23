package cc.admin.modules.sys.service.impl;

import cc.admin.common.constant.CommonConstant;
import cc.admin.common.util.RedisUtil;
import cc.admin.modules.sys.entity.SysOnlineUser;
import cc.admin.modules.sys.service.ISysOnlineUserService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 在线用户
 * @Author: cc-admin
 * @Date: 2021-01-17
 * @Version: V1.0.0
 */
@Service
public class SysOnlineUserServiceImpl implements ISysOnlineUserService {

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public Page<SysOnlineUser> onlineUserList(String key, int pageNo, int pageSize) {
		Page<SysOnlineUser> page = new Page<>(pageNo, pageSize);
		List<SysOnlineUser> onlineUserList = Lists.newArrayList();
		List<String> tokenList = redisUtil.scan(CommonConstant.PREFIX_USER_TOKEN + "*");
		for (String token : tokenList) {
			String onlineUserKey = CommonConstant.PREFIX_ONLINE_USER +token.substring(CommonConstant.PREFIX_USER_TOKEN.length());
			SysOnlineUser sysOnlineUser = (SysOnlineUser) redisUtil.get(onlineUserKey);
			if (sysOnlineUser != null) {
				if (StrUtil.isNotEmpty(key)) {
					if (JSONObject.toJSONString(sysOnlineUser).contains(key)) {
						onlineUserList.add(sysOnlineUser);
					}
				} else {
					onlineUserList.add(sysOnlineUser);
				}
			} else {
				//清理失效的key
				redisUtil.del(onlineUserKey);
			}
		}
		onlineUserList.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
		page.setTotal(onlineUserList.size());
		page.setRecords(getPageRecords(onlineUserList, pageNo, pageSize));
		return page;
	}

	public static List<SysOnlineUser> getPageRecords(List<SysOnlineUser> dataList, int pageNo, int pageSize) {
		if (CollUtil.isEmpty(dataList)) {
			return dataList;
		}
		List<SysOnlineUser> resultList = Lists.newArrayList();
		int currIdx = pageNo > 1 ? (pageNo - 1) * pageSize : 0;
		for (int i = 0; i < pageSize && i < dataList.size() - currIdx; i++) {
			resultList.add(dataList.get(currIdx + i));
		}
		return resultList;
	}
}
