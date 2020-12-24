package cc.admin.modules.generate.entity;

import cc.admin.poi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 代码生成
 * @Author: ZhangHouYing
 * @Date: 2020-09-12
 * @Version: V1.0.0
 */
@Data
@TableName("sys_generate")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_generate对象", description="代码生成")
public class Generate {

	/**编号*/
	@TableId(type = IdType.INPUT)
    @ApiModelProperty(value = "编号")
	private String id;
	/**表名*/
	@Excel(name = "表名", width = 15)
    @ApiModelProperty(value = "表名")
	private String name;
	/**表描述*/
	@Excel(name = "表描述", width = 15)
    @ApiModelProperty(value = "表描述")
	private String description;
	/**同步数据库状态*/
	@Excel(name = "同步数据库状态", width = 15)
    @ApiModelProperty(value = "同步数据库状态")
	private String isSync = "1";
	/**是否简单查询*/
	@Excel(name = "是否简单查询", width = 15)
	@ApiModelProperty(value = "是否简单查询")
	private String isSimpleQuery = "1";
	/**是否分页*/
	@Excel(name = "是否分页", width = 15)
    @ApiModelProperty(value = "是否分页")
	private String isPage = "1";
	/**是否是树*/
	@Excel(name = "是否是树", width = 15)
    @ApiModelProperty(value = "是否是树")
	private String isTree = "0";
	@Excel(name = "树PID", width = 15)
	@ApiModelProperty(value = "树PID")
	private String treePid = "pid";
	@Excel(name = "树名称字段", width = 15)
	@ApiModelProperty(value = "树名称字段")
	private String treeNameField = "name";
	@Excel(name = "表单风格: 12 一列、6 二列、4 三列、3 四列", width = 15)
	@ApiModelProperty(value = "表单风格: 12 一列、6 二列、4 三列、3 四列")
	private String formType = "12";
	/**主键生成序列*/
	@Excel(name = "主键生成序列", width = 15)
    @ApiModelProperty(value = "主键生成序列")
	private String idSequence;
	/**主键类型*/
	@Excel(name = "主键类型 主键类型 timestamp时间戳序列 uid UID input 手工输入", width = 15)
    @ApiModelProperty(value = "主键类型 timestamp时间戳序列 uid UID input手工输入")
	private String idType = "timestamp";
	@Excel(name = "表类型: single单表、main主表、slave附表、catalog分类", width = 15)
	@ApiModelProperty(value = "表类型: single单表、main主表、slave附表、catalog分类")
	private String tableType = "single";
	@ApiModelProperty(value = "主表")
	private String mainTable = "";
	@ApiModelProperty(value = "从表")
	private String slaveTable = "";


	@ApiModelProperty(value = "主表字段")
	@TableField(exist = false)
	private String mainColumn = "";
	@ApiModelProperty(value = "主表字段Code")
	@TableField(exist = false)
	private String mainColumnCode = "";
	@ApiModelProperty(value = "从表字段")
	@TableField(exist = false)
	private String slaveColumn = "";
	@ApiModelProperty(value = "从表字段Code")
	@TableField(exist = false)
	private String slaveColumnCode = "";

	@Excel(name = "显示样式", width = 15)
	@ApiModelProperty(value = "显示样式")
	private String formStyle = "default";


	/**映射关系 oneToMany一对多  oneToOne一对一*/
	@Excel(name = "映射关系 oneToMany一对多  oneToOne一对一", width = 15)
    @ApiModelProperty(value = "映射关系 oneToMany一对多  oneToOne一对一")
	private String relationType = "oneToMany";
	/**子表*/
	@Excel(name = "子表", width = 15)
    @ApiModelProperty(value = "子表")
	private String subTableStr;
	/**附表排序序号*/
	@Excel(name = "附表排序序号", width = 15)
    @ApiModelProperty(value = "附表排序序号")
	private Integer tabOrderNum=1;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
	private String content;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
	private Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
	private String sysOrgCode;
}
