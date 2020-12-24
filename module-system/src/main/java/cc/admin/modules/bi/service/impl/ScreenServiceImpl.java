package cc.admin.modules.bi.service.impl;

import cc.admin.modules.bi.entity.Screen;
import cc.admin.modules.bi.mapper.ScreenMapper;
import cc.admin.modules.bi.service.IScreenService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 大屏
 * @Author: ZhangHouYing
 * @Date:   2020-06-13
 * @Version: V1.0
 */
@Service
public class ScreenServiceImpl extends ServiceImpl<ScreenMapper, Screen> implements IScreenService {

	@Autowired
	private ScreenMapper screenMapper;

	@Override
	public boolean checkNameExist(String id, String name) {
		boolean exist = false;
		LambdaQueryWrapper<Screen> query = new LambdaQueryWrapper<Screen>();
		query.eq(Screen::getName, name);
		List<Screen> screenList = screenMapper.selectList(query);
		for (Screen screen : screenList) {
			if (!screen.getId().equals(id)) {
				exist = true;
			}
		}
		return exist;
	}
}
