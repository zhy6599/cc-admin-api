package cc.admin.modules.sys.service.impl;

import cc.admin.common.constant.CacheConstant;
import cc.admin.common.constant.CommonConstant;
import cc.admin.common.constant.DataBaseConstant;
import cc.admin.common.constant.WebsocketConst;
import cc.admin.common.exception.CcAdminException;
import cc.admin.common.sys.api.ISysBaseAPI;
import cc.admin.common.sys.vo.*;
import cc.admin.common.util.*;
import cc.admin.common.util.oss.OssBootUtil;
import cc.admin.modules.message.entity.SysMessageTemplate;
import cc.admin.modules.message.service.ISysMessageTemplateService;
import cc.admin.modules.message.websocket.WebSocket;
import cc.admin.modules.sys.entity.*;
import cc.admin.modules.sys.mapper.*;
import cc.admin.modules.sys.service.ISysDataSourceService;
import cc.admin.modules.sys.service.ISysDepartService;
import cc.admin.modules.sys.service.ISysDictService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: scott
 * @Date:2019-4-20
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
	/** 当前系统数据库类型 */
	private static String DB_TYPE = "";
	@Autowired
	private ISysMessageTemplateService sysMessageTemplateService;
	@Resource
	private SysLogMapper sysLogMapper;
	@Autowired
	private SysUserMapper userMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysDictService sysDictService;
	@Resource
	private SysAnnouncementMapper sysAnnouncementMapper;
	@Resource
	private SysAnnouncementSendMapper sysAnnouncementSendMapper;
	@Resource
    private WebSocket webSocket;
	@Resource
	private SysRoleMapper roleMapper;
	@Resource
	private SysDepartMapper departMapper;

	@Autowired
	private ISysDataSourceService dataSourceService;

	@Override
	public void addLog(String LogContent, Integer logType, Integer operatetype) {
		SysLog sysLog = new SysLog();
		//注解上的描述,操作日志内容
		sysLog.setLogContent(LogContent);
		sysLog.setLogType(logType);
		sysLog.setOperateType(operatetype);

		//请求的方法名
		//请求的参数

		try {
			//获取request
			HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
			//设置IP地址
			sysLog.setIp(IPUtils.getIpAddress(request));
		} catch (Exception e) {
			sysLog.setIp("127.0.0.1");
		}

		//获取登录用户信息
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		if(sysUser!=null){
			sysLog.setUserid(sysUser.getUsername());
			sysLog.setUsername(sysUser.getRealname());

		}
		sysLog.setCreateTime(new Date());
		//保存系统日志
		sysLogMapper.insert(sysLog);
	}

	@Override
	@Cacheable(cacheNames = CacheConstant.SYS_USERS_CACHE, key = "#username", unless = "#result == null")
	public LoginUser getUserByName(String username) {
		if(oConvertUtils.isEmpty(username)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		SysUser sysUser = userMapper.getUserByName(username);
		if(sysUser==null) {
			return null;
		}
		BeanUtils.copyProperties(sysUser, loginUser);
		return loginUser;
	}

	@Override
	public LoginUser getUserById(String id) {
		if(oConvertUtils.isEmpty(id)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		SysUser sysUser = userMapper.selectById(id);
		if(sysUser==null) {
			return null;
		}
		BeanUtils.copyProperties(sysUser, loginUser);
		return loginUser;
	}

	@Override
	public List<String> getRolesByUsername(String username) {
		return sysUserRoleMapper.getRoleByUserName(username);
	}

	@Override
	public List<String> getDepartIdsByUsername(String username) {
		List<SysDepart> list = sysDepartService.queryDepartsByUsername(username);
		List<String> result = new ArrayList<>(list.size());
		for (SysDepart depart : list) {
			result.add(depart.getId());
		}
		return result;
	}

	@Override
	public List<String> getDepartNamesByUsername(String username) {
		List<SysDepart> list = sysDepartService.queryDepartsByUsername(username);
		List<String> result = new ArrayList<>(list.size());
		for (SysDepart depart : list) {
			result.add(depart.getDepartName());
		}
		return result;
	}

	@Override
	public String getDatabaseType() throws SQLException {
		if(oConvertUtils.isNotEmpty(DB_TYPE)){
			return DB_TYPE;
		}
		DataSource dataSource = SpringContextUtils.getApplicationContext().getBean(DataSource.class);
		return getDatabaseTypeByDataSource(dataSource);
	}

	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_CACHE,key = "#code")
	public List<DictModel> queryDictItemsByCode(String code) {
		return sysDictService.queryDictItemsByCode(code);
	}

	@Override
	public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
		return sysDictService.queryTableDictItemsByCode(table, text, code);
	}

	@Override
	public List<DictModel> queryAllDepartBackDictModel() {
		return sysDictService.queryAllDepartBackDictModel();
	}

    @Override
    public List<JSONObject> queryAllDepart(Wrapper wrapper) {
        //noinspection unchecked
        return JSON.parseArray(JSON.toJSONString(sysDepartService.list(wrapper))).toJavaList(JSONObject.class);
    }

    @Override
	public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent) {
		this.sendSysAnnouncement(fromUser, toUser, title, msgContent, CommonConstant.MSG_CATEGORY_2);
	}

	@Override
	public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory) {
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(msgContent);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(setMsgCategory);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用户通告阅读标记表记录
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
		    	obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
		    	obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
		    	webSocket.sendOneMessage(sysUser.getId(), obj.toJSONString());
			}
		}

	}

	@Override
	public void sendSysAnnouncement(String fromUser, String toUser, String title, String msgContent, String setMsgCategory, String busType, String busId) {
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(msgContent);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(setMsgCategory);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		announcement.setBusId(busId);
		announcement.setBusType(busType);
		announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType).getOpenType());
		announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType).getOpenPage());
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用户通告阅读标记表记录
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendOneMessage(sysUser.getId(), obj.toJSONString());
			}
		}
	}

	@Override
	public void updateSysAnnounReadFlag(String busType, String busId) {
		SysAnnouncement announcement = sysAnnouncementMapper.selectOne(new QueryWrapper<SysAnnouncement>().eq("bus_type",busType).eq("bus_id",busId));
		if(announcement != null){
			LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
			String userId = sysUser.getId();
			LambdaUpdateWrapper<SysAnnouncementSend> updateWrapper = new UpdateWrapper().lambda();
			updateWrapper.set(SysAnnouncementSend::getReadFlag, CommonConstant.HAS_READ_FLAG);
			updateWrapper.set(SysAnnouncementSend::getReadTime, new Date());
			updateWrapper.last("where annt_id ='"+announcement.getId()+"' and user_id ='"+userId+"'");
			SysAnnouncementSend announcementSend = new SysAnnouncementSend();
			sysAnnouncementSendMapper.update(announcementSend, updateWrapper);
		}
	}

	@Override
    public String parseTemplateByCode(String templateCode,Map<String, String> map) {
        List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if(sysSmsTemplates==null||sysSmsTemplates.size()==0){
            throw new CcAdminException("消息模板不存在，模板编码："+templateCode);
        }
        SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
        //模板内容
        String content = sysSmsTemplate.getTemplateContent();
        if(map!=null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String str = "${" + entry.getKey() + "}";
                content = content.replace(str, entry.getValue());
            }
        }
        return content;
    }

	@Override
	public void sendSysAnnouncement(String fromUser, String toUser,String title,Map<String, String> map, String templateCode) {
		List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
        if(sysSmsTemplates==null||sysSmsTemplates.size()==0){
            throw new CcAdminException("消息模板不存在，模板编码："+templateCode);
        }
		SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
		//模板标题
		title = title==null?sysSmsTemplate.getTemplateName():title;
		//模板内容
		String content = sysSmsTemplate.getTemplateContent();
		if(map!=null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String str = "${" + entry.getKey() + "}";
				title = title.replace(str, entry.getValue());
				content = content.replace(str, entry.getValue());
			}
		}

		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(content);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用户通告阅读标记表记录
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendOneMessage(sysUser.getId(), obj.toJSONString());
			}
		}
	}

	@Override
	public void sendSysAnnouncement(String fromUser, String toUser, String title, Map<String, String> map, String templateCode, String busType, String busId) {
		List<SysMessageTemplate> sysSmsTemplates = sysMessageTemplateService.selectByCode(templateCode);
		if(sysSmsTemplates==null||sysSmsTemplates.size()==0){
			throw new CcAdminException("消息模板不存在，模板编码："+templateCode);
		}
		SysMessageTemplate sysSmsTemplate = sysSmsTemplates.get(0);
		//模板标题
		title = title==null?sysSmsTemplate.getTemplateName():title;
		//模板内容
		String content = sysSmsTemplate.getTemplateContent();
		if(map!=null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String str = "${" + entry.getKey() + "}";
				title = title.replace(str, entry.getValue());
				content = content.replace(str, entry.getValue());
			}
		}
		SysAnnouncement announcement = new SysAnnouncement();
		announcement.setTitile(title);
		announcement.setMsgContent(content);
		announcement.setSender(fromUser);
		announcement.setPriority(CommonConstant.PRIORITY_M);
		announcement.setMsgType(CommonConstant.MSG_TYPE_UESR);
		announcement.setSendStatus(CommonConstant.HAS_SEND);
		announcement.setSendTime(new Date());
		announcement.setMsgCategory(CommonConstant.MSG_CATEGORY_2);
		announcement.setDelFlag(String.valueOf(CommonConstant.DEL_FLAG_0));
		announcement.setBusId(busId);
		announcement.setBusType(busType);
		announcement.setOpenType(SysAnnmentTypeEnum.getByType(busType).getOpenType());
		announcement.setOpenPage(SysAnnmentTypeEnum.getByType(busType).getOpenPage());
		sysAnnouncementMapper.insert(announcement);
		// 2.插入用户通告阅读标记表记录
		String userId = toUser;
		String[] userIds = userId.split(",");
		String anntId = announcement.getId();
		for(int i=0;i<userIds.length;i++) {
			if(oConvertUtils.isNotEmpty(userIds[i])) {
				SysUser sysUser = userMapper.getUserByName(userIds[i]);
				if(sysUser==null) {
					continue;
				}
				SysAnnouncementSend announcementSend = new SysAnnouncementSend();
				announcementSend.setAnntId(anntId);
				announcementSend.setUserId(sysUser.getId());
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysAnnouncementSendMapper.insert(announcementSend);
				JSONObject obj = new JSONObject();
				obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
				obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
				obj.put(WebsocketConst.MSG_ID, announcement.getId());
				obj.put(WebsocketConst.MSG_TXT, announcement.getTitile());
				webSocket.sendOneMessage(sysUser.getId(), obj.toJSONString());
			}
		}
	}

	/**
	 * 获取数据库类型
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	private String getDatabaseTypeByDataSource(DataSource dataSource) throws SQLException{
		if("".equals(DB_TYPE)) {
			Connection connection = dataSource.getConnection();
			try {
				DatabaseMetaData md = connection.getMetaData();
				String dbType = md.getDatabaseProductName().toLowerCase();
				if(dbType.indexOf("mysql")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
				}else if(dbType.indexOf("oracle")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
				}else if(dbType.indexOf("sqlserver")>=0||dbType.indexOf("sql server")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
				}else if(dbType.indexOf("postgresql")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
				}else {
					throw new CcAdminException("数据库类型:["+dbType+"]不识别!");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally {
				connection.close();
			}
		}
		return DB_TYPE;

	}

	@Override
	public List<DictModel> queryAllDict() {
		// 查询并排序
		QueryWrapper<SysDict> queryWrapper = new QueryWrapper<SysDict>();
		queryWrapper.orderByAsc("create_time");
		List<SysDict> dicts = sysDictService.list(queryWrapper);
		// 封装成 model
		List<DictModel> list = new ArrayList<DictModel>();
		for (SysDict dict : dicts) {
			list.add(new DictModel(dict.getDictCode(), dict.getDictName()));
		}

		return list;
	}

	@Override
	public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql) {
		return sysDictService.queryTableDictItemsByCodeAndFilter(table,text,code,filterSql);
	}

	@Override
	public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
		return sysDictService.queryTableDictByKeys(table,text,code,keyArray);
	}

	@Override
	public List<ComboModel> queryAllUser() {
		List<ComboModel> list = new ArrayList<ComboModel>();
		List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0));
		for(SysUser user : userList){
			ComboModel model = new ComboModel();
			model.setTitle(user.getRealname());
			model.setId(user.getId());
			model.setUsername(user.getUsername());
			list.add(model);
		}
		return list;
	}

    @Override
    public JSONObject queryAllUser(String[] userIds,int pageNo,int pageSize) {
		JSONObject json = new JSONObject();
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0);
        List<ComboModel> list = new ArrayList<ComboModel>();
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		IPage<SysUser> pageList = userMapper.selectPage(page, queryWrapper);
        for(SysUser user : pageList.getRecords()){
            ComboModel model = new ComboModel();
            model.setUsername(user.getUsername());
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setEmail(user.getEmail());
            if(oConvertUtils.isNotEmpty(userIds)){
                for(int i = 0; i<userIds.length;i++){
                    if(userIds[i].equals(user.getId())){
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
		json.put("list",list);
        json.put("total",pageList.getTotal());
        return json;
    }

    @Override
    public List<JSONObject> queryAllUser(Wrapper wrapper) {
        //noinspection unchecked
        return JSON.parseArray(JSON.toJSONString(userMapper.selectList(wrapper))).toJavaList(JSONObject.class);
    }

    @Override
	public List<ComboModel> queryAllRole() {
		List<ComboModel> list = new ArrayList<ComboModel>();
		List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
		for(SysRole role : roleList){
			ComboModel model = new ComboModel();
			model.setTitle(role.getRoleName());
			model.setId(role.getId());
			list.add(model);
		}
		return list;
	}

    @Override
    public List<ComboModel> queryAllRole(String[] roleIds) {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRole> roleList = roleMapper.selectList(new QueryWrapper<SysRole>());
        for(SysRole role : roleList){
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            model.setRoleCode(role.getRoleCode());
            if(oConvertUtils.isNotEmpty(roleIds)) {
                for (int i = 0; i < roleIds.length; i++) {
                    if (roleIds[i].equals(role.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        return list;
    }

	@Override
	public List<String> getRoleIdsByUsername(String username) {
		return sysUserRoleMapper.getRoleIdByUserName(username);
	}

	@Override
	public String getDepartIdsByOrgCode(String orgCode) {
		return departMapper.queryDepartIdByOrgCode(orgCode);
	}

	@Override
	public DictModel getParentDepartId(String departId) {
		SysDepart depart = departMapper.getParentDepartId(departId);
		DictModel model = new DictModel(depart.getId(),depart.getParentId());
		return model;
	}

	@Override
	public List<SysDepartModel> getAllSysDepart() {
		List<SysDepartModel> departModelList = new ArrayList<SysDepartModel>();
		List<SysDepart> departList = departMapper.selectList(new QueryWrapper<SysDepart>().eq("del_flag","0"));
		for(SysDepart depart : departList){
			SysDepartModel model = new SysDepartModel();
			BeanUtils.copyProperties(depart,model);
			departModelList.add(model);
		}
		return departModelList;
	}

	@Override
	public DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId) {
		SysDataSource dbSource = dataSourceService.getById(dbSourceId);
		return new DynamicDataSourceModel(dbSource);
	}

	@Override
	public List<String> getDeptHeadByDepId(String deptId) {
		List<SysUser> userList = userMapper.selectList(new QueryWrapper<SysUser>().like("depart_ids",deptId).eq("status",1).eq("del_flag",0));
		List<String> list = new ArrayList<>();
		for(SysUser user : userList){
			list.add(user.getUsername());
		}
		return list;
	}

	@Override
	public String upload(MultipartFile file,String bizPath,String uploadType) {
		String url = "";
		if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
			url = MinioUtil.upload(file,bizPath);
		}else{
			url = OssBootUtil.upload(file,bizPath);
		}
		return url;
	}

	@Override
	public String upload(MultipartFile file, String bizPath, String uploadType, String customBucket) {
		String url = "";
		if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
			url = MinioUtil.upload(file,bizPath,customBucket);
		}else{
			url = OssBootUtil.upload(file,bizPath,customBucket);
		}
		return url;
	}

	@Override
	public void viewAndDownload(String filePath, String uploadpath, String uploadType, HttpServletResponse response) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			if(filePath.startsWith("http")){
				String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
				if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
					String bucketName = filePath.replace(MinioUtil.getMinioUrl(),"").split("/")[0];
					String objectName = filePath.replace(MinioUtil.getMinioUrl()+bucketName,"");
					inputStream = MinioUtil.getMinioFile(bucketName,objectName);
					if(inputStream == null){
						bucketName = CommonConstant.UPLOAD_CUSTOM_BUCKET;
						objectName = filePath.replace(OssBootUtil.getStaticDomain()+"/","");
						inputStream = OssBootUtil.getOssFile(objectName,bucketName);
					}
				}else{
					String bucketName = CommonConstant.UPLOAD_CUSTOM_BUCKET;
					String objectName = filePath.replace(OssBootUtil.getStaticDomain()+"/","");
					inputStream = OssBootUtil.getOssFile(objectName,bucketName);
					if(inputStream == null){
						bucketName = filePath.replace(MinioUtil.getMinioUrl(),"").split("/")[0];
						objectName = filePath.replace(MinioUtil.getMinioUrl()+bucketName,"");
						inputStream = MinioUtil.getMinioFile(bucketName,objectName);
					}
				}
				response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("UTF-8"),"iso-8859-1"));
			}else{
				// 本地文件处理
				filePath = filePath.replace("..", "");
				if (filePath.endsWith(",")) {
					filePath = filePath.substring(0, filePath.length() - 1);
				}
				String fullPath = uploadpath + File.separator + filePath;
				String downloadFilePath = uploadpath + File.separator + fullPath;
				File file = new File(downloadFilePath);
				inputStream = new BufferedInputStream(new FileInputStream(fullPath));
				response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1"));
			}
			response.setContentType("application/force-download");// 设置强制下载不打开
			outputStream = response.getOutputStream();
			if(inputStream != null){
				byte[] buf = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0) {
					outputStream.write(buf, 0, len);
				}
				response.flushBuffer();
			}
		} catch (IOException e) {
			response.setStatus(404);
			log.error("预览文件失败" + e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void sendWebSocketMsg(String[] userIds, String cmd) {
		JSONObject obj = new JSONObject();
		obj.put(WebsocketConst.MSG_CMD, cmd);
		webSocket.sendMoreMessage(userIds, obj.toJSONString());
	}

	@Override
	public List<LoginUser> queryAllUserByIds(String[] userIds) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0);
		queryWrapper.in("id",userIds);
		List<LoginUser> loginUsers = new ArrayList<>();
		List<SysUser> sysUsers = userMapper.selectList(queryWrapper);
		for (SysUser user:sysUsers) {
			LoginUser loginUser=new LoginUser();
			BeanUtils.copyProperties(user, loginUser);
			loginUsers.add(loginUser);
		}
		return loginUsers;
	}

	/**
	 * 推送签到人员信息
	 * @param userId
	 */
	@Override
	public void meetingSignWebsocket(String userId) {
		JSONObject obj = new JSONObject();
		obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_SIGN);
		obj.put(WebsocketConst.MSG_USER_ID,userId);
		webSocket.sendAllMessage(obj.toJSONString());
	}

	@Override
	public List<LoginUser> queryUserByNames(String[] userNames) {
		QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>().eq("status",1).eq("del_flag",0);
		queryWrapper.in("username",userNames);
		List<LoginUser> loginUsers = new ArrayList<>();
		List<SysUser> sysUsers = userMapper.selectList(queryWrapper);
		for (SysUser user:sysUsers) {
			LoginUser loginUser=new LoginUser();
			BeanUtils.copyProperties(user, loginUser);
			loginUsers.add(loginUser);
		}
		return loginUsers;
	}
}
