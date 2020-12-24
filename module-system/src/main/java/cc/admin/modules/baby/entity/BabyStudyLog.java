package cc.admin.modules.baby.entity;

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
 * @Description: 学习记录
 * @Author: ZhangHouYing
 * @Date: 2020-11-21
 * @Version: V1.0.0
 */
@Data
@TableName("baby_study_log")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="baby_study_log对象", description="学习记录")
public class BabyStudyLog {

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**字词编号*/
	@Excel(name = "字词编号", width = 15)
    @Dict(dictTable = "baby_word", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "字词编号")
	private String wordId;
	/**认识*/
	@Excel(name = "认识", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "认识")
	private String isKnow;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private Long createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
	private Date createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private Long updateBy;
	/**修改日期*/
	@Excel(name = "修改日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改日期")
	private Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
	private String sysOrgCode;
}
