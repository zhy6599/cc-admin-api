package cc.admin.modules.system.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.constant.CacheConstant;
import cc.admin.modules.generate.model.DictTable;
import cc.admin.modules.system.entity.SysDictItem;
import cc.admin.modules.system.service.ISysDictItemService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/sys/dictItem")
@Slf4j
public class SysDictItemController implements InitializingBean {

	@Autowired
	private ISysDictItemService sysDictItemService;

	private Map<String, DictTable> dictTableMap = Maps.newHashMap();


	@Override
	public void afterPropertiesSet() throws Exception {
		dictTableMap.put("demoOrderType", new DictTable("demo_order_type", "id", "name"));
		dictTableMap.put("biView", new DictTable("bi_view", "id", "name"));
		dictTableMap.put("biMap", new DictTable("bi_map", "id", "name"));
		dictTableMap.put("sysPosition", new DictTable("sys_position", "id", "name"));
		dictTableMap.put("sysRole", new DictTable("sys_role", "id", "role_name"));
		dictTableMap.put("demoOrderType", new DictTable("demo_order_type", "id", "name"));
		dictTableMap.put("babyWord", new DictTable("baby_word", "id", "name"));

	}

	/**
	 * @param dictId
	 * @param key
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 * @功能：查询字典数据
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDictItem>> queryPageList(@RequestParam(name = "dictId") String dictId, @RequestParam(name = "key",required=false) String key, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
													@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		Result<IPage<SysDictItem>> result = new Result<IPage<SysDictItem>>();
		QueryWrapper<SysDictItem> queryWrapper = new QueryWrapper();
		queryWrapper.eq("dict_id", dictId);
		if (StrUtil.isNotEmpty(key)) {
			queryWrapper.nested(i -> i.like("item_text", key).or().like("item_value", key).or().like("description", key));
		}
		queryWrapper.orderByAsc("sort_order");
		Page<SysDictItem> page = new Page<SysDictItem>(pageNo, pageSize);
		IPage<SysDictItem> pageList = sysDictItemService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * @param dictCode
	 * @return
	 * @功能：根据字典code 查询字典项
	 */
	@RequestMapping(value = "/selectItemsByDictCode", method = RequestMethod.GET)
	public List<Map<String, String>> selectItemsByDictCode(@RequestParam(name = "dictCode") String dictCode){
		List<Map<String, String>> resultList = Lists.newArrayList();
		List<SysDictItem> itemList = sysDictItemService.selectItemsByDictCode(dictCode);
		for(SysDictItem item: itemList){
			resultList.add(convertSysDictItem(item));
		}
		return resultList;
	}


	/**
	 *
	 * @param defId
	 * @return
	 * @功能： 根据预置表查询字典项
	 */
	@RequestMapping(value = "/selectItemsByDefId", method = RequestMethod.GET)
	public List<Map<String, String>> selectItemsByDefId(@RequestParam(name = "defId") String defId){
		List<Map<String, String>> resultList = Lists.newArrayList();
		DictTable dictTable = dictTableMap.get(defId);
		if (dictTable != null) {
			List<SysDictItem> sysDictItemList = sysDictItemService.selectItemsByTable(dictTable.getDicTable(), dictTable.getDicCode(), dictTable.getDicText());
			for(SysDictItem item: sysDictItemList){
				resultList.add(convertSysDictItem(item));
			}
		}
		return resultList;
	}

	private Map<String, String> convertSysDictItem(SysDictItem item){
		Map<String, String> map = Maps.newHashMap();
		map.put("label", item.getItemText());
		map.put("value", item.getItemValue());
		return map;
	}

	/**
	 * @param sysDictItem
	 * @return
	 * @功能：新增
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
	public Result<SysDictItem> add(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		try {
			sysDictItem.setCreateTime(new Date());
			sysDictItemService.save(sysDictItem);
			result.success("保存成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * @param sysDictItem
	 * @return
	 * @功能：编辑
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
	public Result<SysDictItem> edit(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem sysDict = sysDictItemService.getById(sysDictItem.getId());
		if (sysDict == null) {
			result.error500("未找到对应实体");
		} else {
			sysDictItem.setUpdateTime(new Date());
			boolean ok = sysDictItemService.updateById(sysDictItem);
			if (ok) {
				result.success("编辑成功!");
			} else {
				result.success("编辑失败!");
			}

		}
		return result;
	}

	/**
	 * @param sysDictItem
	 * @return
	 * @功能：修改字典项状态
	 */
	@RequestMapping(value = "/status", method = RequestMethod.PUT)
	@CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
	public Result<SysDictItem> status(@RequestBody SysDictItem sysDictItem) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem sysDict = sysDictItemService.getById(sysDictItem.getId());
		if (sysDict == null) {
			result.error500("未找到对应实体");
		} else {
			String opt = sysDictItem.getStatus() == 1 ? "禁用" : "启用";
			sysDictItem.setUpdateTime(new Date());
			sysDictItem.setStatus(Math.abs(sysDictItem.getStatus() - 1));
			boolean ok = sysDictItemService.updateById(sysDictItem);
			if (ok) {
				result.success(String.format("%s成功!", opt));
			} else {
				result.success(String.format("%s失败!", opt));
			}

		}
		return result;
	}

	/**
	 * @param id
	 * @return
	 * @功能：删除字典数据
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
	public Result<SysDictItem> delete(@RequestParam(name = "id", required = true) String id) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		SysDictItem joinSystem = sysDictItemService.getById(id);
		if (joinSystem == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = sysDictItemService.removeById(id);
			if (ok) {
				result.success("删除成功!");
			}
		}
		return result;
	}

	/**
	 * @param ids
	 * @return
	 * @功能：批量删除字典数据
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
	public Result<SysDictItem> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<SysDictItem> result = new Result<SysDictItem>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.sysDictItemService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
}
