package cc.admin.modules.bi.entity;

import cc.admin.common.aspect.annotation.Dict;
import cc.admin.modules.bi.core.model.SqlVariable;
import cc.admin.poi.excel.annotation.Excel;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: bi_view
 * @Author: ZhangHouYing
 * @Date: 2020-07-04
 * @Version: V1.0
 */
@Data
@TableName("bi_view")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bi_view对象", description="bi_view")
public class View implements Serializable {
    private static final long serialVersionUID = 1L;

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
    private String id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String name;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @ApiModelProperty(value = "描述")
    private String description;
	/**
	 * 目录编号
	 */
	@Excel(name = "目录编号", width = 15)
	@Dict(dicCode = "id", dicText = "name", dictTable = "sys_catalog")
	@ApiModelProperty(value = "目录编号")
	private String catalogId;
	/**数据源*/
	@Excel(name = "数据源", width = 15)
    @ApiModelProperty(value = "数据源")
	private String sourceId;
	/**SQL*/
	@Excel(name = "SQL", width = 15)
    @ApiModelProperty(value = "SQL")
    private String viewSql;
	/**图形设置*/
	@Excel(name = "图形设置", width = 15)
    @ApiModelProperty(value = "图形设置")
    private String model;
	/**参数*/
	@Excel(name = "参数", width = 15)
    @ApiModelProperty(value = "参数")
    private String variable;
	/**配置信息*/
	@Excel(name = "配置信息", width = 15)
    @ApiModelProperty(value = "配置信息")
    private String config;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private Integer createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
    private Integer updateBy;
	/**修改日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "修改日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;

	@JSONField(serialize = false)
	public List<SqlVariable> getVariables() {
		if (StringUtils.isEmpty(variable) || StringUtils.isEmpty(viewSql)) {
			return null;
		}

		try {
			return JSONObject.parseArray(variable, SqlVariable.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
