package cc.admin.modules.baby.service;

import cc.admin.modules.baby.entity.BabyWord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 字词管理
 * @Author: ZhangHouYing
 * @Date: 2020-11-21
 * @Version: V1.0.0
 */
public interface IBabyWordService extends IService<BabyWord> {

	/**
	 * 复习单词
	 * @return
	 */
	List<BabyWord> review();
}
