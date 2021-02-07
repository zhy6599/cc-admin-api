package cc.admin.modules.mnt.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import cc.admin.poi.excel.annotation.Excel;
import cc.admin.common.aspect.annotation.Dict;

/**
 * @Description: 部署管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Data
@TableName("mnt_deploy")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="mnt_deploy对象", description="部署管理")
public class MntDeploy {

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String name;
	/**应用编号*/
	@Excel(name = "应用编号", width = 15)
    @Dict(dictTable = "mnt_app", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "应用编号")
	private String appId;
	/**服务器编号*/
	@Excel(name = "服务器编号", width = 15)
    @Dict(dictTable = "mnt_server", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "服务器编号")
	private String serverId;
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
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private Date updateTime;
}
