package cc.admin.modules.bi.entity;

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
 * @Description: 地图管理
 * @Author: cc-admin
 * @Date:   2021-01-02
 * @Version: V1.0.0
 */
@Data
@TableName("bi_map")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="bi_map对象", description="地图管理")
public class BiMap {

	public BiMap(){

	}

	public BiMap(String id,String name){
		this.id = id;
		this.name = name;
	}

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private String name;
	/**地图轮廓*/
	@Excel(name = "地图轮廓", width = 15)
    @ApiModelProperty(value = "地图轮廓")
	private String json;
	/**视觉映射*/
	@Excel(name = "视觉映射", width = 15)
    @ApiModelProperty(value = "视觉映射")
	private String visualMap;
}
