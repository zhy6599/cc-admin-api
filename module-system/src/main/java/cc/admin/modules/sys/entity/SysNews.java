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
 * @Description: 新闻信息
 * @Author: cc-admin
 * @Date: 2021-06-10
 * @Version: V1.0.0
 */
@Data
@TableName("sys_news")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_news对象", description = "新闻信息")
public class SysNews {

	/**
	 * 详细内容
	 */
	@Excel(name = "详细内容", width = 15)
	@ApiModelProperty(value = "详细内容")
	private String content;

	/**
	 * 文本内容
	 */
	@Excel(name = "文本内容", width = 15)
	@ApiModelProperty(value = "文本内容")
	private String txtContent;

	/**
	 * 责任编辑
	 */
	@Excel(name = "责任编辑", width = 15)
	@ApiModelProperty(value = "责任编辑")
	private Long createBy;
	/**
	 * 发布时间
	 */
	@Excel(name = "发布时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "发布时间")
	private Date createTime;
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
	 * 责任单位
	 */
	@Excel(name = "责任单位", width = 15)
	@ApiModelProperty(value = "责任单位")
	private String sysOrgCode;
	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private Long updateBy;
	/**
	 * 修改时间
	 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
}
