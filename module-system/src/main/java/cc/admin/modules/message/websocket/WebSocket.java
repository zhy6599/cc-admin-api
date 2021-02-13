package cc.admin.modules.message.websocket;

import cc.admin.common.constant.WebsocketConst;
import cc.admin.modules.monitor.service.ServerMonitorService;
import cc.admin.modules.monitor.service.impl.ServerMonitorServiceImpl;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author scott
 * @Date 2019/11/29 9:41
 * @Description: 此注解相当于设置访问URL
 */
@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}") //此注解相当于设置访问URL
public class WebSocket {

	private ServerMonitorService serverMonitorService = new ServerMonitorServiceImpl();

    private Session session;

    private static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();


    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")String userId) {
        try {
			this.session = session;
			webSockets.add(this);
			sessionPool.put(userId, session);
			log.info("【WebSocket消息】有新的连接，总数为:"+webSockets.size());
		} catch (Exception e) {
		}
    }

    @OnClose
    public void onClose() {
        try {
			webSockets.remove(this);
			log.info("【WebSocket消息】连接断开，总数为:"+webSockets.size());
		} catch (Exception e) {
		}
    }

    @OnMessage
    public void onMessage(String message) {
    	log.debug("【WebSocket消息】收到客户端消息:"+message);
		JSONObject obj = new JSONObject();

    	if(WebsocketConst.MSG_SERVER_INFO.equals(message)){
			//服务器监控信息
			obj.put(WebsocketConst.MSG_CMD, WebsocketConst.MSG_SERVER_INFO);
			//消息内容
			obj.put(WebsocketConst.MSG_TXT, serverMonitorService.getServerInfo());
		}else if(WebsocketConst.CMD_CHECK.equals(message)){
			obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHECK);//业务类型
			obj.put(WebsocketConst.MSG_TXT, "心跳响应");//消息内容
		}

    	session.getAsyncRemote().sendText(obj.toJSONString());
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
    	log.info("【WebSocket消息】广播消息:"+message);
        for(WebSocket webSocket : webSockets) {
            try {
            	if(webSocket.session.isOpen()) {
            		webSocket.session.getAsyncRemote().sendText(message);
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
		sendMessage(session, message);
    }

    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
    	for(String userId:userIds) {
    		Session session = sessionPool.get(userId);
			sendMessage(session, message);
    	}

    }

    private void sendMessage(Session session,String message){
		if (session != null&&session.isOpen()) {
			try {
				log.info("【WebSocket消息】 单点消息:"+message);
				session.getAsyncRemote().sendText(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
