package cc.admin.modules.sys.controller;

import cc.admin.common.api.vo.Result;
import cc.admin.common.constant.CacheConstant;
import cc.admin.common.constant.CommonConstant;
import cc.admin.common.sys.api.ISysBaseAPI;
import cc.admin.common.sys.util.JwtUtil;
import cc.admin.common.sys.vo.LoginUser;
import cc.admin.common.util.*;
import cc.admin.common.util.encryption.EncryptedString;
import cc.admin.modules.cas.constant.Constant;
import cc.admin.modules.cas.service.WxMiniApi;
import cc.admin.modules.shiro.vo.DefContants;
import cc.admin.modules.sys.entity.SysDepart;
import cc.admin.modules.sys.entity.SysOnlineUser;
import cc.admin.modules.sys.entity.SysUser;
import cc.admin.modules.sys.model.SysLoginModel;
import cc.admin.modules.sys.service.*;
import cc.admin.modules.sys.util.RandImageUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags = "用户登录")
@Slf4j
public class LoginController {
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	@Autowired
	private ISysLogService logService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysDictService sysDictService;
	@Autowired
	private ISysOnlineUserService sysOnlineUserService;

	@Autowired
	private ISysPermissionService sysPermissionService;

	@Autowired
	private WxMiniApi wxMiniApi;

	private static final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

	@Value(value = "${cc.admin.wxMini.appId}")
	private String appId;
	@Value(value = "${cc.admin.wxMini.secret}")
	private String secret;

	@ApiOperation("登录接口")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		/**
		 String captcha = sysLoginModel.getCaptcha();
		 if(captcha==null){
		 result.error500("验证码无效");
		 return result;
		 }
		 String lowerCaseCaptcha = captcha.toLowerCase();
		 String realKey = MD5Util.MD5Encode(lowerCaseCaptcha+sysLoginModel.getCheckKey(), "utf-8");
		 Object checkCode = redisUtil.get(realKey);
		 if(checkCode==null || !checkCode.equals(lowerCaseCaptcha)) {
		 result.error500("验证码错误");
		 return result;
		 }
		 **/
		// 1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if (!result.isSuccess()) {
			return result;
		}
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}
		//用户登录信息
		userInfo(sysUser, result);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 退出登录
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) {
		//用户退出逻辑
		String token = request.getHeader(DefContants.X_ACCESS_TOKEN);
		if (oConvertUtils.isEmpty(token)) {
			return Result.error("退出登录失败！");
		}
		String username = JwtUtil.getUsername(token);
		LoginUser sysUser = sysBaseAPI.getUserByName(username);
		if (sysUser != null) {
			sysBaseAPI.addLog("用户名: " + sysUser.getRealname() + ",退出成功！", CommonConstant.LOG_TYPE_1, null);
			log.info(" 用户名:  " + sysUser.getRealname() + ",退出成功！ ");
			//清空用户登录Token缓存
			redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
			//清空用户缓存信息
			redisUtil.del(CommonConstant.PREFIX_ONLINE_USER + token);
			//清空用户登录Shiro权限缓存
			redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
			//清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
			redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
			//调用shiro的logout
			SecurityUtils.getSubject().logout();
			return Result.ok("退出登录成功！");
		} else {
			return Result.error("Token无效!");
		}
	}

	/**
	 * 获取访问量
	 *
	 * @return
	 */
	@GetMapping("loginfo")
	public Result<JSONObject> loginfo() {
		Result<JSONObject> result = new Result<JSONObject>();
		JSONObject obj = new JSONObject();
		//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		// 获取一天的开始和结束时间
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date dayStart = calendar.getTime();
		calendar.add(Calendar.DATE, 1);
		Date dayEnd = calendar.getTime();
		// 获取系统访问记录
		Long totalVisitCount = logService.findTotalVisitCount();
		obj.put("totalVisitCount", totalVisitCount);
		Long todayVisitCount = logService.findTodayVisitCount(dayStart, dayEnd);
		obj.put("todayVisitCount", todayVisitCount);
		Long todayIp = logService.findTodayIp(dayStart, dayEnd);
		//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
		obj.put("todayIp", todayIp);
		Long onlineUserCount = sysOnlineUserService.onlineUserList("", 1, 10000).getTotal();
		obj.put("onlineUserCount", onlineUserCount);

		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 获取访问量
	 *
	 * @return
	 */
	@GetMapping("visitInfo")
	public Result<List<Map<String, Object>>> visitInfo() {
		Result<List<Map<String, Object>>> result = new Result<List<Map<String, Object>>>();
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date dayEnd = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		Date dayStart = calendar.getTime();
		List<Map<String, Object>> list = logService.findVisitCount(dayStart, dayEnd);
		result.setResult(oConvertUtils.toLowerCasePageList(list));
		return result;
	}

	/**
	 * 登陆成功选择用户当前部门
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
	public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = user.getUsername();
		if (oConvertUtils.isEmpty(username)) {
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			username = sysUser.getUsername();
		}
		String orgCode = user.getOrgCode();
		this.sysUserService.updateUserDepart(username, orgCode);
		SysUser sysUser = sysUserService.getUserByName(username);
		JSONObject obj = new JSONObject();
		obj.put("userInfo", sysUser);
		result.setResult(obj);
		return result;
	}

	/**
	 * 短信登录接口
	 *
	 * @param jsonObject
	 * @return
	 */
	@PostMapping(value = "/sms")
	@ApiOperation("手机获取验证码")
	public Result<String> sms(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String mobile = jsonObject.get("mobile").toString();
		//手机号模式 登录模式: "2"  注册模式: "1"
		String smsmode = jsonObject.get("smsmode").toString();
		log.info(mobile);
		if (oConvertUtils.isEmpty(mobile)) {
			result.setMessage("手机号不允许为空！");
			result.setSuccess(false);
			return result;
		}
		Object object = redisUtil.get(mobile);
		if (object != null) {
			result.setMessage("验证码10分钟内，仍然有效！");
			result.setSuccess(false);
			return result;
		}
		//随机数
		String captcha = RandomUtil.randomNumbers(6);
		JSONObject obj = new JSONObject();
		obj.put("code", captcha);
		try {
			boolean b = false;
			//注册模板
			if (CommonConstant.SMS_TPL_TYPE_1.equals(smsmode)) {
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				if (sysUser != null) {
					result.error500(" 手机号已经注册，请直接登录！");
					sysBaseAPI.addLog("手机号已经注册，请直接登录！", CommonConstant.LOG_TYPE_1, null);
					return result;
				}
				b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.REGISTER_TEMPLATE_CODE);
			} else {
				//登录模式，校验用户有效性
				SysUser sysUser = sysUserService.getUserByPhone(mobile);
				result = sysUserService.checkUserIsEffective(sysUser);
				if (!result.isSuccess()) {
					return result;
				}
				/**
				 * smsmode 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
				 */
				if (CommonConstant.SMS_TPL_TYPE_0.equals(smsmode)) {
					//登录模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.LOGIN_TEMPLATE_CODE);
				} else if (CommonConstant.SMS_TPL_TYPE_2.equals(smsmode)) {
					//忘记密码模板
					b = DySmsHelper.sendSms(mobile, obj, DySmsEnum.FORGET_PASSWORD_TEMPLATE_CODE);
				}
			}
			if (b == false) {
				result.setMessage("短信验证码发送失败,请稍后重试");
				result.setSuccess(false);
				return result;
			}
			//验证码10分钟内有效
			redisUtil.set(mobile, captcha, 600);
			//update-begin--Author:scott  Date:20190812 for：issues#391
			//result.setResult(captcha);
			//update-end--Author:scott  Date:20190812 for：issues#391
			result.setSuccess(true);

		} catch (ClientException e) {
			e.printStackTrace();
			result.error500(" 短信接口未配置，请联系管理员！");
			return result;
		}
		return result;
	}

	/**
	 * 手机号登录接口
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("手机号登录接口")
	@PostMapping("/phoneLogin")
	public Result<JSONObject> phoneLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("mobile");
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByPhone(phone);
		result = sysUserService.checkUserIsEffective(sysUser);
		if (!result.isSuccess()) {
			return result;
		}
		String smscode = jsonObject.getString("captcha");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		//添加日志
		sysBaseAPI.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 用户信息
	 *
	 * @param sysUser
	 * @param result
	 * @return
	 */
	private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
		String password = sysUser.getPassword();
		String username = sysUser.getUsername();
		// 生成token
		String token = JwtUtil.sign(username, password);
		// 设置token缓存有效时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		cacheLoginInfo(new SysOnlineUser(sysUser.getUsername(),sysUser.getRealname()), redisUtil, token);
		// 获取用户部门信息
		JSONObject obj = new JSONObject();
		List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
		obj.put("departs", departs);
		if (departs == null || departs.size() == 0) {
			obj.put("multi_depart", 0);
		} else if (departs.size() == 1) {
			sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
			obj.put("multi_depart", 1);
		} else {
			obj.put("multi_depart", 2);
		}
		obj.put("token", token);
		obj.put("userInfo", sysUser);
		//还需要把用户菜单信息也添加进来
		JSONObject json = sysPermissionService.getUserPermissionByUserName(username);
		obj.put("permissionInfo", json);
		result.setResult(obj);
		result.success("登录成功");
		return result;
	}

	/**
	 * 用户登录后将用户最新的登录信息存入到redis中，永不过期，用户注销则删除登录信息
	 * @param sysOnlineUser
	 * @param redisUtil
	 */
	public static void cacheLoginInfo(SysOnlineUser sysOnlineUser, RedisUtil redisUtil,String token) {
		try {
			redisUtil.set(CommonConstant.PREFIX_ONLINE_USER + token, sysOnlineUser);
			redisUtil.expire(CommonConstant.PREFIX_ONLINE_USER + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		} catch (Exception e) {
			log.error("记录在线用户出错！", e);
		}
	}

	/**
	 * 获取加密字符串
	 *
	 * @return
	 */
	@GetMapping(value = "/getEncryptedString")
	public Result<Map<String, String>> getEncryptedString() {
		Result<Map<String, String>> result = new Result<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", EncryptedString.key);
		map.put("iv", EncryptedString.iv);
		result.setResult(map);
		return result;
	}

	/**
	 * 后台生成图形验证码 ：有效
	 *
	 * @param response
	 * @param key
	 */
	@ApiOperation("获取验证码")
	@GetMapping(value = "/randomImage/{key}")
	public Result<String> randomImage(HttpServletResponse response, @PathVariable String key) {
		Result<String> res = new Result<String>();
		try {
			String code = RandomUtil.randomString(BASE_CHECK_CODES, 4);
			String lowerCaseCode = code.toLowerCase();
			String realKey = MD5Util.MD5Encode(lowerCaseCode + key, "utf-8");
			redisUtil.set(realKey, lowerCaseCode, 60);
			String base64 = RandImageUtil.generate(code);
			res.setSuccess(true);
			res.setResult(base64);
		} catch (Exception e) {
			res.error500("获取验证码出错" + e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * app登录
	 *
	 * @param sysLoginModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mLogin", method = RequestMethod.POST)
	public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
		Result<JSONObject> result = new Result<JSONObject>();
		String username = sysLoginModel.getUsername();
		String password = sysLoginModel.getPassword();
		//1. 校验用户是否有效
		SysUser sysUser = sysUserService.getUserByName(username);
		result = sysUserService.checkUserIsEffective(sysUser);
		if (!result.isSuccess()) {
			return result;
		}
		//2. 校验用户名或密码是否正确
		String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
		String syspassword = sysUser.getPassword();
		if (!syspassword.equals(userpassword)) {
			result.error500("用户名或密码错误");
			return result;
		}
		String orgCode = sysUser.getOrgCode();
		if (oConvertUtils.isEmpty(orgCode)) {
			//如果当前用户无选择部门 查看部门关联信息
			List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
			if (departs == null || departs.size() == 0) {
				result.error500("用户暂未归属部门,不可登录!");
				return result;
			}
			orgCode = departs.get(0).getOrgCode();
			sysUser.setOrgCode(orgCode);
			this.sysUserService.updateUserDepart(username, orgCode);
		}
		JSONObject obj = new JSONObject();
		//用户登录信息
		obj.put("userInfo", sysUser);
		// 生成token
		String token = JwtUtil.sign(username, syspassword);
		// 设置超时时间
		redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
		redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
		//token 信息
		obj.put("token", token);
		result.setResult(obj);
		result.setSuccess(true);
		result.setCode(200);
		sysBaseAPI.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

	/**
	 * 图形验证码
	 *
	 * @param sysLoginModel
	 * @return
	 */
	@RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
	public Result<?> checkCaptcha(@RequestBody SysLoginModel sysLoginModel) {
		String captcha = sysLoginModel.getCaptcha();
		String checkKey = sysLoginModel.getCheckKey();
		if (captcha == null) {
			return Result.error("验证码无效");
		}
		String lowerCaseCaptcha = captcha.toLowerCase();
		String realKey = MD5Util.MD5Encode(lowerCaseCaptcha + checkKey, "utf-8");
		Object checkCode = redisUtil.get(realKey);
		if (checkCode == null || !checkCode.equals(lowerCaseCaptcha)) {
			return Result.error("验证码错误");
		}
		return Result.ok();
	}

	/**
	 * 小程序登录接口
	 *
	 * @param jsonObject
	 * @return
	 */
	@ApiOperation("小程序登录接口")
	@PostMapping("/miniLogin")
	public Result<JSONObject> miniLogin(@RequestBody JSONObject jsonObject) {
		Result<JSONObject> result = new Result<JSONObject>();
		String code = jsonObject.getString("code");
		String encryptedData = jsonObject.getString("encryptedData");
		String iv = jsonObject.getString("iv");
		JSONObject resultObject = wxMiniApi.authCode2Session(appId, secret, code);
		if (resultObject == null) {
			throw new RuntimeException("调用微信端授权认证接口错误");
		}
		String openId = resultObject.getString(Constant.OPEN_ID);
		String sessionKey = resultObject.getString(Constant.SESSION_KEY);
		String unionId = resultObject.getString(Constant.UNION_ID);
		JSONObject userInfoObject = wxMiniApi.getUserInfo(encryptedData, sessionKey, iv);
		if (userInfoObject == null) {
			throw new RuntimeException("解密用户信息错误");
		}
		String nickName = userInfoObject.getString("nickName");
		String avatarUrl = userInfoObject.getString("avatarUrl");
		//校验用户有效性
		SysUser sysUser = sysUserService.getUserByThirdId(openId);
		//没有就创建一个返回回去
		if (sysUser == null) {
			sysUser = new SysUser();
			sysUser.setId(IdUtil.simpleUUID());
			sysUser.setThirdId(openId);
			sysUser.setThirdType("miniProgram");
			sysUser.setCreateTime(new Date());
			String salt = oConvertUtils.randomGen(8);
			sysUser.setSalt(salt);
			String userName = IdUtil.simpleUUID();
			sysUser.setUsername(userName);
			String passwordEncode = PasswordUtil.encrypt(userName, "123456", salt);
			sysUser.setRealname(nickName);
			sysUser.setAvatar(avatarUrl);
			sysUser.setPassword(passwordEncode);
			sysUser.setStatus(1);
			sysUser.setDelFlag(CommonConstant.DEL_FLAG_0);
			String selectedRoles = "2";
			sysUserService.addUserWithRole(sysUser, selectedRoles);
			sysUserService.save(sysUser);
		} else {
			result = sysUserService.checkUserIsEffective(sysUser);
		}
		if (!result.isSuccess()) {
			return result;
		}
		//用户信息
		userInfo(sysUser, result);
		//添加日志
		sysBaseAPI.addLog("用户名: " + sysUser.getUsername() + ",登录成功！", CommonConstant.LOG_TYPE_1, null);
		return result;
	}

}
