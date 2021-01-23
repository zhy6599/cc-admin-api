package cc.admin.modules.sys.service;

import cc.admin.modules.sys.entity.SysDictItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<SysDictItem> {
    public List<SysDictItem> selectItemsByMainId(String mainId);

	public List<SysDictItem> selectItemsByDictCode(String dictCode);

	public List<SysDictItem> selectItemsByTable(String dicTable, String dicCode, String dicText);
}
