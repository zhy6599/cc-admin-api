package cc.admin.modules.mnt.entity;

import cc.admin.common.aspect.annotation.Dict;
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
 * @Description: 应用管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Data
@TableName("mnt_app")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="mnt_app对象", description="应用管理")
public class MntApp {

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**应用名称*/
	@Excel(name = "应用名称", width = 15)
    @ApiModelProperty(value = "应用名称")
	private String name;
	/**上传目录*/
	@Excel(name = "上传目录", width = 15)
    @ApiModelProperty(value = "上传目录")
	private String uploadPath;
	/**部署路径*/
	@Excel(name = "部署路径", width = 15)
    @ApiModelProperty(value = "部署路径")
	private String deployPath;
	/**备份路径*/
	@Excel(name = "备份路径", width = 15)
    @ApiModelProperty(value = "备份路径")
	private String backupPath;
	/**应用端口*/
	@Excel(name = "应用端口", width = 15)
    @ApiModelProperty(value = "应用端口")
	private Integer port;
	/**启动脚本*/
	@Excel(name = "启动脚本", width = 15)
    @ApiModelProperty(value = "启动脚本")
	private String startScript;
	/**
	 * 停止脚本
	 */
	@Excel(name = "停止脚本", width = 15)
	@ApiModelProperty(value = "停止脚本")
	private String stopScript;
	/**部署脚本*/
	@Excel(name = "部署脚本", width = 15)
    @ApiModelProperty(value = "部署脚本")
	private String deployScript;
	/**分类目录*/
	@Excel(name = "分类目录", width = 15)
	@Dict(dicCode = "id",dicText = "name",dictTable = "sys_catalog")
    @ApiModelProperty(value = "分类目录")
	private String catalogId;
	/**创建者*/
	@Excel(name = "创建者", width = 15)
    @ApiModelProperty(value = "创建者")
	private String createBy;
	/**更新者*/
	@Excel(name = "更新者", width = 15)
    @ApiModelProperty(value = "更新者")
	private String updateBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}
