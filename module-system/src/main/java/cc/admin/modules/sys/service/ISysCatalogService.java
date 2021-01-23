package cc.admin.modules.sys.service;

import cc.admin.modules.sys.entity.SysCatalog;
import cc.admin.modules.sys.model.CatalogTree;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 分类目录
 * @Author: ZhangHouYing
 * @Date: 2020-10-17
 * @Version: V1.0.0
 */
public interface ISysCatalogService extends IService<SysCatalog> {

	/**
	 * 查询树列表
	 *
	 * @param queryWrapper
	 * @return
	 */
	List<CatalogTree> queryTreeList(QueryWrapper<SysCatalog> queryWrapper);

	void add(SysCatalog sysCatalog);

	/**
	 * 删除当前的以及下级的
	 * @param id
	 */
	int deleteById(String id);
}
