package cc.admin.modules.sys.entity;

import cc.admin.poi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * @Description: 文件管理
 * @Author: cc-admin
 * @Date: 2021-04-23
 * @Version: V1.0.0
 */
@Data
@TableName("sys_file")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_file对象", description = "文件管理")
public class SysFile {

	/**
	 * 编号
	 */
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "编号")
	private String id;
	/**
	 * 名称
	 */
	@Excel(name = "名称", width = 15)
	@ApiModelProperty(value = "名称")
	private String name;
	/**
	 * 类型
	 */
	@Excel(name = "类型", width = 15)
	@ApiModelProperty(value = "类型")
	private String type;
	/**
	 * 主表编号
	 */
	@Excel(name = "主表编号", width = 15)
	@ApiModelProperty(value = "主表编号")
	private String mainId;
	/**
	 * 文件路径
	 */
	@Excel(name = "文件路径", width = 15)
	@ApiModelProperty(value = "文件路径")
	private String path;
	/**
	 * 排序
	 */
	@Excel(name = "排序", width = 15)
	@ApiModelProperty(value = "排序")
	private Integer sortOrder;
	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private Long createBy;
	/**
	 * 创建日期
	 */
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private Long updateBy;
	/**
	 * 修改日期
	 */
	@Excel(name = "修改日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改日期")
	private Date updateTime;
	/**
	 * 所属部门
	 */
	@Excel(name = "所属部门", width = 15)
	@ApiModelProperty(value = "所属部门")
	private String sysOrgCode;
}
