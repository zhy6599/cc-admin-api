package cc.admin.modules.bi.service;

import cc.admin.modules.bi.entity.Screen;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 大屏
 * @Author: ZhangHouYing
 * @Date:   2020-06-13
 * @Version: V1.0
 */
public interface IScreenService extends IService<Screen> {

	boolean checkNameExist(String id, String name);
}
