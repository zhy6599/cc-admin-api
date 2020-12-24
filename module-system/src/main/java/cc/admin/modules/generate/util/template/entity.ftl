package ${bussiPackage}.${entityPackage}.entity;

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
 * @Description: ${geForm.moduleName}
 * @Author: cc-admin
 * @Date:   ${.now?string["yyyy-MM-dd"]}
 * @Version: V1.0.0
 */
@Data
@TableName("${tableName}")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="${tableName}对象", description="${geForm.moduleName}")
public class ${entityName} {

<#list fmColumnList as fm>
	/**${fm.name}*/
    <#if fm.isPk == "1">
	@TableId(type = IdType.ID_WORKER_STR)
    <#else>
        <#if fm.dataType =='date'>
	@Excel(name = "${fm.name}", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
        <#elseif fm.dataType =='datetime'>
	@Excel(name = "${fm.name}", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
        <#else>
	@Excel(name = "${fm.name}", width = 15)
        </#if>
    </#if>
    <#if fm.sysDicCode == "">
        <#if fm.dicTable != "">
    @Dict(dictTable = "${fm.dicTable}", dicText = "${fm.dicText}", dicCode = "${fm.dicCode}")
        </#if>
    <#else>
    @Dict(dicCode = "${fm.sysDicCode}")
    </#if>
    <#if fm.code == "catalogId" && catalogInput??>
	@Dict(dicCode = "id",dicText = "name",dictTable = "sys_catalog")
    </#if>
    @ApiModelProperty(value = "${fm.name}")
	private <#if fm.dataType=='java.sql.Blob'>byte[]<#else>${fm.javaType!''}</#if> ${fm.code};
</#list>
}
