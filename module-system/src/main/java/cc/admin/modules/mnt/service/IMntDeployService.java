package cc.admin.modules.mnt.service;

import cc.admin.modules.mnt.entity.MntDeploy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 部署管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
public interface IMntDeployService extends IService<MntDeploy> {

	/**
	 * 查询服务运行状态
	 * @param ids
	 * @return
	 */
	String serverStatus(List<String> ids);
}
