package cc.admin.modules.system.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: 目录管理
 * @Author: ZhangHouYing
 * @Date:   2019-08-10
 * @Version: V1.0.0
 */
@Data
@ApiModel(value="分类目录对象", description="分类目录")
public class CatalogTree {

	List<CatalogTree> children;
	/**编号*/
    @ApiModelProperty(value = "编号")
	private String id;
	/**名称*/
    @ApiModelProperty(value = "名称")
	private String name;
}
