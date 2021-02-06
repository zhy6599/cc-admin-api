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
 * @Description: 测试代码生成
 * @Author: cc-admin
 * @Date:   2021-02-06
 * @Version: V1.0.0
 */
@Data
@TableName("demo_test")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="demo_test对象", description="测试代码生成")
public class DemoTest {

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String name;
	/**目录编号*/
	@Excel(name = "目录编号", width = 15)
	@Dict(dicCode = "id",dicText = "name",dictTable = "sys_catalog")
    @ApiModelProperty(value = "目录编号")
	private String catalogId;
}
