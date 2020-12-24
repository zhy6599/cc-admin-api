package cc.admin.modules.system.mapper;

import cc.admin.modules.system.entity.SysCatalog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 分类目录
 * @Author: ZhangHouYing
 * @Date: 2020-10-17
 * @Version: V1.0.0
 */
public interface SysCatalogMapper extends BaseMapper<SysCatalog> {

	/**
	 * 更新左右值
	 * @param id
	 * @param lft
	 * @param rgh
	 */
	public void updateLftRgh(@Param("id") String id, @Param("lft")  int lft, @Param("rgh")  int rgh);

	/**
	 * 根据左右值删除记录
	 * @param lft
	 * @param rgh
	 * @return
	 */
	int deleteByLftRgh(@Param("lft") Integer lft,@Param("rgh")  Integer rgh,@Param("type")  String type);
}
