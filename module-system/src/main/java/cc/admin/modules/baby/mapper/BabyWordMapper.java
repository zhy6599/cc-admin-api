package cc.admin.modules.baby.mapper;

import cc.admin.modules.baby.entity.BabyWord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 字词管理
 * @Author: ZhangHouYing
 * @Date: 2020-11-21
 * @Version: V1.0.0
 */
public interface BabyWordMapper extends BaseMapper<BabyWord> {

	/**
	 * 复习单词列表
	 * @return
	 */
	@Select("select w.* from (select word_id,count(1) weight from baby_study_log where is_know='0' GROUP BY word_id) l , baby_word w where l.word_id = w.id order by l.weight desc limit 1000")
	List<BabyWord> review();
}
