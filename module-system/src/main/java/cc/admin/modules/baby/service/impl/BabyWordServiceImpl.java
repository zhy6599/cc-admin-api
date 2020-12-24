package cc.admin.modules.baby.service.impl;

import cc.admin.modules.baby.entity.BabyWord;
import cc.admin.modules.baby.mapper.BabyWordMapper;
import cc.admin.modules.baby.service.IBabyWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 字词管理
 * @Author: ZhangHouYing
 * @Date: 2020-11-21
 * @Version: V1.0.0
 */
@Service
public class BabyWordServiceImpl extends ServiceImpl<BabyWordMapper, BabyWord> implements IBabyWordService {

	@Autowired
	BabyWordMapper babyWordMapper;

	@Override
	public List<BabyWord> review() {
		return babyWordMapper.review();
	}
}
