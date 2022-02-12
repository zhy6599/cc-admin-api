package cc.admin.modules.cas.entity;

import cc.admin.modules.sys.entity.SysUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 认证用户
 *
 * @author zhuhuix
 * @date 2020-04-03
 */
@ApiModel(value = "授权用户信息")
@Data
public class AuthUserDto {

	/**
	 * 授权类型：0--WEB端 1--微信端
	 */
	@ApiModelProperty(value = "授权类型")
	private Integer authType;

	@ApiModelProperty(value = "用户名")
	private String userName;

	@JsonIgnore
	private String password;

	@ApiModelProperty(value = "临时登录凭证")
	private String code;

	@ApiModelProperty(value = "用户登录id")
	private String uuid = "";
	//**********************************
	//以下为微信类传输字段

	@ApiModelProperty(value = "微信openId")
	private String openId;

	@ApiModelProperty(value = "微信用户非敏感信息")
	private String rawData;

	@ApiModelProperty(value = "微信用户签名")
	private String signature;

	@ApiModelProperty(value = "微信用户敏感信息")
	private String encryptedData;

	@ApiModelProperty(value = "微信用户解密算法的向量")
	private String iv;

	/**
	 * 会话密钥
	 */
	@JsonIgnore
	private String sessionKey;

	/**
	 * 用户在开放平台的唯一标识符
	 */
	@JsonIgnore
	private String unionId;
	//以上为微信类传输字段
	//**********************************

	@ApiModelProperty(value = "服务器jwt token")
	private String token;

	@ApiModelProperty(value = "用户信息")
	private SysUser sysUser;

	@Override
	public String toString() {
		return "AuthUser{" +
				"userName='" + userName + '\'' +
				", password='" + "*********" + '\'' +
				", code='" + code + '\'' +
				", uuid='" + uuid + '\'' +
				", openId='" + openId + '\'' +
				", token='" + token + '\'' +
				", sysUser=" + sysUser +
				'}';
	}
}
