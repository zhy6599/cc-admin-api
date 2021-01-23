package cc.admin.modules.sys.service.impl;

import cc.admin.common.exception.CcAdminException;
import cc.admin.modules.sys.entity.SysCatalog;
import cc.admin.modules.sys.mapper.SysCatalogMapper;
import cc.admin.modules.sys.model.CatalogTree;
import cc.admin.modules.sys.service.ISysCatalogService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 分类目录
 * @Author: ZhangHouYing
 * @Date: 2020-10-17
 * @Version: V1.0.0
 */
@Service
public class SysCatalogServiceImpl extends ServiceImpl<SysCatalogMapper, SysCatalog> implements ISysCatalogService {

	@Autowired
	private SysCatalogMapper sysCatalogMapper;

	@Override
	public List<CatalogTree> queryTreeList(QueryWrapper<SysCatalog> queryWrapper) {
		List<SysCatalog> catalogList = baseMapper.selectList(queryWrapper);
		//规定根节点是0
		return renderTree(catalogList, "0");
	}

	@Override
	public void add(SysCatalog sysCatalog) {
		if ("0".equalsIgnoreCase(sysCatalog.getParentId())) {
			if (StrUtil.isEmpty(sysCatalog.getType())) {
				sysCatalog.setType(IdUtil.objectId());
			}
		}else{
			// 非根节点需要继承上级的
			SysCatalog parentCatalog = sysCatalogMapper.selectById(sysCatalog.getParentId());
			sysCatalog.setType(parentCatalog.getType());
		}
		sysCatalogMapper.insert(sysCatalog);
		QueryWrapper<SysCatalog> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("type", sysCatalog.getType());
		List<CatalogTree> catalogTreeList = queryTreeList (queryWrapper);
		calcLftRgh(catalogTreeList);
	}

	@Override
	public int deleteById(String id) {
		SysCatalog sysCatalog = sysCatalogMapper.selectById(id);
		if(sysCatalog==null){
			throw new CcAdminException("记录不存在！");
		}
		return sysCatalogMapper.deleteByLftRgh(sysCatalog.getLft(), sysCatalog.getRgh(),sysCatalog.getType());
	}

	/**
	 * 将列表数据转换为树状结构的
	 * @param catalogList
	 * @param parentId
	 * @return
	 */
	public List<CatalogTree> renderTree(List<SysCatalog> catalogList, String parentId) {
		List<CatalogTree> resultList = Lists.newArrayList();
		for (SysCatalog catalog : catalogList) {
			if (parentId.equals(catalog.getParentId())) {
				CatalogTree catalogTree = new CatalogTree();
				catalogTree.setId(catalog.getId());
				catalogTree.setName(catalog.getName());
				resultList.add(catalogTree);
				catalogTree.setChildren(renderTree(catalogList, catalog.getId()));
			}
		}
		return resultList;
	}


	public void calcLftRgh(List<CatalogTree> catalogTreeList) {
		int left = 1;
		if(catalogTreeList!=null){
			for (CatalogTree catalogTree : catalogTreeList) {
				left = calc(catalogTree, left);
			}
		}
	}
	private int calc(CatalogTree catalogTree, int leftValue) {
		int left=leftValue;
		leftValue = leftValue + 1;
		if (catalogTree.getChildren() !=null) {
			List<CatalogTree> sons = catalogTree.getChildren();
			for (CatalogTree sub : sons) {
				leftValue = calc(sub, leftValue);
			}
		}
		// 更新左右值
		sysCatalogMapper.updateLftRgh(catalogTree.getId(),left,leftValue);
		return leftValue + 1;
	}
}
