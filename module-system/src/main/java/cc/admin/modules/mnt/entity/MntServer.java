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
 * @Description: 服务器管理
 * @Author: cc-admin
 * @Date:   2021-02-08
 * @Version: V1.0.0
 */
@Data
@TableName("mnt_server")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="mnt_server对象", description="服务器管理")
public class MntServer {

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String name;
	/**账号*/
	@Excel(name = "账号", width = 15)
    @ApiModelProperty(value = "账号")
	private String userName;
	/**IP地址*/
	@Excel(name = "IP地址", width = 15)
    @ApiModelProperty(value = "IP地址")
	private String ip;
	/**密码*/
	@Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
	private String password;
	/**端口*/
	@Excel(name = "端口", width = 15)
    @ApiModelProperty(value = "端口")
	private Integer port;
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
