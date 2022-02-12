package cc.admin.modules.pro.entity;

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
 * @Description: 留言板
 * @Author: cc-admin
 * @Date: 2021-04-05
 * @Version: V1.0.0
 */
@Data
@TableName("pro_remark")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "pro_remark对象", description = "留言板")
public class ProRemark {

	/**
	 * 编号
	 */
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "编号")
	private String id;
	/**
	 * 帖子编号
	 */
	@Excel(name = "帖子编号", width = 15)
	@ApiModelProperty(value = "帖子编号")
	private String pid;
	/**
	 * 留言
	 */
	@Excel(name = "留言", width = 15)
	@ApiModelProperty(value = "留言")
	private String name;
	/**
	 * 点赞数
	 */
	@Excel(name = "点赞数", width = 15)
	@ApiModelProperty(value = "点赞数")
	private Integer upCount;
	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;
	/**
	 * 创建日期
	 */
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**
	 * 更新人
	 */
	@Excel(name = "更新人", width = 15)
	@ApiModelProperty(value = "更新人")
	private String updateBy;
	/**
	 * 更新日期
	 */
	@Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新日期")
	private Date updateTime;
	/**
	 * 所属部门
	 */
	@Excel(name = "所属部门", width = 15)
	@ApiModelProperty(value = "所属部门")
	private String sysOrgCode;
}
