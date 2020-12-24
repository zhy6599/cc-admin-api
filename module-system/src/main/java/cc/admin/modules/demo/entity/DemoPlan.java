package cc.admin.modules.demo.entity;

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
 * @Description: 审计计划
 * @Author: ZhangHouYing
 * @Date:   2020-11-07
 * @Version: V1.0.0
 */
@Data
@TableName("demo_plan")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="demo_plan对象", description="审计计划")
public class DemoPlan {

	/**计划编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "计划编号")
	private String id;
	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
	private String name;
	/**创建机构*/
	@Excel(name = "创建机构", width = 15)
    @ApiModelProperty(value = "创建机构")
	private String createOrg;
	/**计划年度*/
	@Excel(name = "计划年度", width = 15)
    @Dict(dicCode = "plan_year")
    @ApiModelProperty(value = "计划年度")
	private String planYear;
	/**计划类型*/
	@Excel(name = "计划类型", width = 15)
    @Dict(dicCode = "plan_type")
    @ApiModelProperty(value = "计划类型")
	private String planType;
	/**计划状态*/
	@Excel(name = "计划状态", width = 15)
    @Dict(dicCode = "plan_status")
    @ApiModelProperty(value = "计划状态")
	private String plansTatus;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;
	/**编制人*/
	@Excel(name = "编制人", width = 15)
    @ApiModelProperty(value = "编制人")
	private String createBy;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private String updateBy;
}
