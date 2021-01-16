package cc.admin.modules.system.entity;

import cc.admin.common.util.IPUtils;
import cc.admin.common.util.SpringContextUtils;
import cc.admin.poi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Description: 在线用户
 * @Author: cc-admin
 * @Date:   2021-01-17
 * @Version: V1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_online_user对象", description="在线用户")
public class SysOnlineUser {

	/**用户账号*/
    @ApiModelProperty(value = "用户账号")
	private String id;
	/**用户姓名*/
	@Excel(name = "用户姓名", width = 15)
    @ApiModelProperty(value = "用户姓名")
	private String name;
	/**IP*/
	@Excel(name = "IP", width = 15)
    @ApiModelProperty(value = "IP")
	private String ip;
	/**IP地址*/
	@Excel(name = "IP地址", width = 15)
    @ApiModelProperty(value = "IP地址")
	private String ipAddress;
	/**浏览器*/
	@Excel(name = "浏览器", width = 15)
    @ApiModelProperty(value = "浏览器")
	private String browser;
	/**操作系统*/
	@Excel(name = "操作系统", width = 15)
	@ApiModelProperty(value = "操作系统")
	private String os;
	/**登录时间*/
	@Excel(name = "登录时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "登录时间")
	private Date loginTime;


	public SysOnlineUser() {

	}

	public SysOnlineUser(String id,String name) {
		HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
		this.ip = IPUtils.getIpAddress(request);
		this.ipAddress = IPUtils.getLocalCityInfo(ip);
		this.browser = IPUtils.getBrowser(request);
		this.os = IPUtils.getOs(request);
		this.loginTime = new Date();
		this.id = id;
		this.name = name;
	}
}
