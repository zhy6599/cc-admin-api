package cc.admin.modules.sys.service.impl;

import cc.admin.modules.sys.entity.SysDictItem;
import cc.admin.modules.sys.mapper.SysDictItemMapper;
import cc.admin.modules.sys.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper sysDictItemMapper;

    @Override
    public List<SysDictItem> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

	@Override
	public List<SysDictItem> selectItemsByDictCode(String dictCode) {
		return sysDictItemMapper.selectItemsByDictCode(dictCode);
	}


	@Override
	public List<SysDictItem> selectItemsByTable(String dicTable, String dicCode, String dicText) {
		return sysDictItemMapper.selectItemsByTable(dicTable, dicCode, dicText);
	}
}
