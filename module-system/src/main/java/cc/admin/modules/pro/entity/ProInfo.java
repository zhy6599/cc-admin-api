package cc.admin.modules.pro.entity;

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
 * @Description: 信息发布
 * @Author: cc-admin
 * @Date: 2021-04-05
 * @Version: V1.0.0
 */
@Data
@TableName("pro_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "pro_info对象", description = "信息发布")
public class ProInfo {

	/**
	 * 编号
	 */
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "编号")
	private String id;
	/**
	 * 标题
	 */
	@Excel(name = "标题", width = 15)
	@ApiModelProperty(value = "标题")
	private String name;
	/**
	 * 详细描述
	 */
	@Excel(name = "详细描述", width = 15)
	@ApiModelProperty(value = "详细描述")
	private String content;
	/**
	 * 联系方式
	 */
	@Excel(name = "联系方式", width = 15)
	@ApiModelProperty(value = "联系方式")
	private String contactType;
	/**
	 * 联系人
	 */
	@Excel(name = "联系人", width = 15)
	@ApiModelProperty(value = "联系人")
	private String contactUser;
	/**
	 * 手机号
	 */
	@Excel(name = "手机号", width = 15)
	@ApiModelProperty(value = "手机号")
	private String phone;
	/**
	 * 微信
	 */
	@Excel(name = "微信", width = 15)
	@ApiModelProperty(value = "微信")
	private String wechat;
	/**
	 * QQ
	 */
	@Excel(name = "QQ", width = 15)
	@ApiModelProperty(value = "QQ")
	private String qq;
	/**
	 * 目录编号
	 */
	@Excel(name = "目录编号", width = 15)
	@Dict(dicCode = "id", dicText = "name", dictTable = "sys_catalog")
	@ApiModelProperty(value = "目录编号")
	private String catalogId;
	/**
	 * 围观
	 */
	@Excel(name = "围观", width = 15)
	@ApiModelProperty(value = "围观")
	private Integer readCount;
	/**
	 * 点赞数
	 */
	@Excel(name = "点赞数", width = 15)
	@ApiModelProperty(value = "点赞数")
	private Integer upCount;
	/**
	 * 状态
	 */
	@Excel(name = "状态", width = 15)
	@ApiModelProperty(value = "状态")
	private String status;
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
