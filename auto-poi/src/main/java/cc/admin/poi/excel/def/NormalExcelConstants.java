/**
 * Copyright 2013-2015 JEECG (jeecgos@163.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.admin.poi.excel.def;

/**
 * 正常导出Excel
 *
 * @Author JEECG on 14-3-8. 静态常量
 */
public interface NormalExcelConstants extends BasePOIConstants {
	/**
	 * 单Sheet导出
	 */
	public final static String CC_ADMIN_ENTITY_EXCEL_VIEW = "ccEntityExcelView";
	/**
	 * 模型Sheet导出
	 */
	public final static String CC_ADMIN_DATA_MODEL_EXCEL_VIEW = "dataModelExcelView";
	/**
	 * 数据列表
	 */
	public final static String DATA_LIST = "data";

	/**
	 * 多Sheet 对象
	 */
	public final static String MAP_LIST = "mapList";

	/**
	 * 导出字段自定义
	 */
	public final static String EXPORT_FIELDS = "exportFields";
	/**
	 * EXPORT_WORKBOOK
	 */
	public final static String EXPORT_WORKBOOK = "exportWorkbook";

}
