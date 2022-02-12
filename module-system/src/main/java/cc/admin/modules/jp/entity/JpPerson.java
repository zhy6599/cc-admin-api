package cc.admin.modules.jp.entity;

import cc.admin.common.aspect.annotation.Dict;
import cc.admin.poi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import java.util.List;

/**
 * @Description:
 * @Author: cc-admin
 * @Date: 2021-04-04
 * @Version: V1.0.0
 */
@Data
@TableName("jp_person")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="jp_person对象", description="")
public class JpPerson {

	/**编号*/
	@TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "编号")
	private String id;
	/**
	 * 是否儿子
	 */
	@Excel(name = "是否儿子", width = 15)
	@ApiModelProperty(value = "是否儿子")
	private String isSon;
	/**姓名*/
	@Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "姓名")
	private String name;
	/**妻*/
	@Excel(name = "妻", width = 15)
    @ApiModelProperty(value = "妻")
	private String wife;
	/**照片*/
	@Excel(name = "照片", width = 15)
    @ApiModelProperty(value = "照片")
	private String pic;
	/**视频*/
	@Excel(name = "视频", width = 15)
    @ApiModelProperty(value = "视频")
	private String video;
	/**父编号*/
	@Excel(name = "父编号", width = 15)
    @ApiModelProperty(value = "父编号")
	private String pid;
	/**层级*/
	@Excel(name = "层级", width = 15)
    @ApiModelProperty(value = "层级")
	private Integer oid;
	/**居住地*/
	@Excel(name = "居住地", width = 15)
    @Dict(dicCode = "live")
    @ApiModelProperty(value = "居住地")
	private String live;
	/**准确*/
	@Excel(name = "准确", width = 15)
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "准确")
	private String sure;
	/**生平简介*/
	@Excel(name = "生平简介", width = 15)
    @ApiModelProperty(value = "生平简介")
	private String remark;
	/**生于*/
	@Excel(name = "生于", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "生于")
	private Date born;
	/**卒于*/
	@Excel(name = "卒于", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "卒于")
	private Date dead;
	@Excel(name = "坟经纬度", width = 15)
	@ApiModelProperty(value = "坟经纬度")
	private String lonlat;
	@Excel(name = "身份证号", width = 15)
	@ApiModelProperty(value = "身份证号")
	private String idCard;
	@Excel(name = "联系电话", width = 15)
	@ApiModelProperty(value = "联系电话")
	private String phone;
	/**排行（老大1，老二2）*/
	@Excel(name = "排行（老大1，老二2）", width = 15)
    @ApiModelProperty(value = "排行（老大1，老二2）")
	private Integer sortBy;

	@TableField(exist = false)
	private List<JpPerson> children;

	/**
	 * 用于计算坐在位置
	 */
	@TableField(exist = false)
	private Integer sonNum;
	/**
	 * 在列上应该属于什么位置
	 */
	@TableField(exist = false)
	private Integer pos;
	/**
	 * 从什么地方开始，用来计算绝对位置
	 */
	@TableField(exist = false)
	private Integer startPos;
}
