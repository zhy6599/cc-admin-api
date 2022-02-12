package cc.admin.modules.cas.entity;

import lombok.Data;

import java.io.Serializable;
/**
 * API统一返回基类
 *
 * @author zhuhuix
 * @date 2020-04-03
 */
@Data
public class Result<T> implements Serializable {

	/**
	 * 是否成功
	 */
	private Boolean success;

	/**
	 * 错误码
	 */
	private String errCode;

	/**
	 * 错误信息
	 */
	private String errMsg;

	/**
	 * 返回数据
	 */
	private T module;

	@Override
	public String toString() {
		return "Result{" +
				"success=" + success +
				", errCode='" + errCode + '\'' +
				", errMsg='" + errMsg + '\'' +
				", module=" + module +
				'}';
	}

	public Result<T> ok(T module) {
		this.setSuccess(true);
		this.setErrCode("0");
		this.setErrCode("ok");
		this.setModule(module);
		return this;
	}

	public Result<T> error(String errCode, String errMsg) {
		this.setSuccess(false);
		this.setErrCode(errCode);
		this.setErrMsg(errMsg);
		return this;
	}

	public Result<T> error(String errMsg) {
		this.setSuccess(false);
		this.setErrCode("-1");
		this.setErrMsg(errMsg);
		return this;
	}
}
