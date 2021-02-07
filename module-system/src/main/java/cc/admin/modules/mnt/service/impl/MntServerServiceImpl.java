package cc.admin.modules.mnt.service.impl;

import cc.admin.modules.mnt.entity.MntServer;
import cc.admin.modules.mnt.mapper.MntServerMapper;
import cc.admin.modules.mnt.service.IMntServerService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 服务器管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Service
public class MntServerServiceImpl extends ServiceImpl<MntServerMapper, MntServer> implements IMntServerService {

}
