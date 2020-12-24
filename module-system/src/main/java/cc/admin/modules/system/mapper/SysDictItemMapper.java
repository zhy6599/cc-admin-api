package cc.admin.modules.system.mapper;

import cc.admin.modules.system.entity.SysDictItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {
    @Select("SELECT * FROM sys_dict_item WHERE DICT_ID = #{mainId} order by sort_order asc, item_value asc")
    public List<SysDictItem> selectItemsByMainId(String mainId);


	@Select("SELECT * FROM sys_dict_item WHERE DICT_ID in (select id from sys_dict where dict_code = #{dictCode}) order by sort_order asc, item_value asc")
	public List<SysDictItem> selectItemsByDictCode(String dictCode);

	List<SysDictItem> selectItemsByTable(@Param("table") String table, @Param("code") String code, @Param("text") String text);
}
