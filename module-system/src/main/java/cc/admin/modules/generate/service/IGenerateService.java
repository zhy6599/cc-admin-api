package cc.admin.modules.generate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import cc.admin.modules.generate.entity.Generate;
import cc.admin.modules.generate.model.GenerateForm;
import cc.admin.modules.generate.model.TableSchema;

import java.util.List;
import java.util.Map;

/**
 * @Description: 代码生成
 * @Author: ZhangHouYing
 * @Date: 2020-09-12
 * @Version: V1.0.0
 */
public interface IGenerateService extends IService<Generate> {

	/**
	 * 查询数据库表
	 * @param filter
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	IPage<TableSchema> queryPageTableList(String filter, Integer pageNo, Integer pageSize);

	/**
	 * 同步表结构
	 * @param id 代码生成ID
	 * @return
	 */
	boolean syncTableById(String id);

	/**
	 * 同步表结构 到配置信息
	 * @param tableName 表名
	 * @return
	 */
	boolean syncTableByName(String tableName);

	/**
	 * 获取表结构信息
	 * @param tableName 表名
	 * @return
	 */
	TableSchema getTableSchema(String tableName);


	/**
	 * 代码生成
	 * @param generateForm
	 */
	Map<String, String> doGenerateCode(GenerateForm generateForm,Generate generate);


	/**
	 * 同步表结构 到 数据库
	 * @param id
	 * @return
	 */
	boolean syncTableToDb(String id);

	/**
	 *	将表结构同步到配置信息
	 * @param id
	 * @return
	 */
	String syncTableToConfig(String id);

	/**
	 * 是否和数据库同步
	 * @param generate
	 * @return
	 */
	String isSync(Generate generate);

	/**
	 * 查询列信息
	 * @param id
	 * @return
	 */
	List<Map<String, String>> queryColumns(String id);

}
