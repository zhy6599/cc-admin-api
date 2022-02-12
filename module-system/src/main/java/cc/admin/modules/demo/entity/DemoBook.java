package cc.admin.modules.demo.entity;

import cc.admin.poi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 图书
 * @Author: ZhangHouYing
 * @Date:   2020-11-07
 * @Version: V1.0.0
 */
@Data
@TableName("demo_book")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="demo_book对象", description="图书")
public class DemoBook {

	/**图书编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "图书编号")
	private String bookId;
	/**图书名称*/
	@Excel(name = "图书名称", width = 15)
    @ApiModelProperty(value = "图书名称")
	private String bookName;
	/**文件名称*/
	@Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名称")
	private String fileName;
	/**目录编号*/
	@Excel(name = "目录编号", width = 15)
    @ApiModelProperty(value = "目录编号")
	private Integer sortId;
	/**排序号*/
	@Excel(name = "排序号", width = 15)
    @ApiModelProperty(value = "排序号")
	private Integer orderNo;
}
