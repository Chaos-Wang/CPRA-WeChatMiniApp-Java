package main.java.controller.WebSocket;

import main.java.bean.Point;
import main.java.controller.EntityController.DeviceController;
import main.java.utils.InfluxDBUtils;
import main.java.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.*;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ChaosWong
 * @date 2020/1/19 16:14
 * @title main.java.controller.DeviceServerController.APPWebSocket
 */

@Service
public class CommonWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {
    private Logger logger = LoggerFactory.getLogger( CommonWebSocketHandler.class);
    // 在线用户列表
    private static final Map<String, WebSocketSession> users;
    // 用户标识
    private static final Map<String, Boolean> booleans;
    // 用户标识
    private static final String CLIENT_ID = "mchNo";

    public InfluxDBUtils influx = new InfluxDBUtils();
    public Date date = new Date();
    private boolean deviceMode = true;


    static {
        users = new HashMap<>();
        booleans = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("成功建立websocket-spring连接");
        String mchNo = getMchNo(session);
        if ( StringUtils.isNotEmpty(mchNo)) {
            users.put(mchNo, session);
            session.sendMessage(new TextMessage("成功建立websocket-spring连接"));
            logger.info("用户标识：{}，Session：{}", mchNo, session.toString());
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws JSONException, ParseException, URISyntaxException, IOException, DeploymentException {
        logger.info( "收到客户端消息：{}", message.getPayload() );
        JSONObject msgJson = JSONObject.parseObject( message.getPayload() );
        String to = msgJson.getString( "to" );
        String msg = msgJson.getString( "msg" );

        if ( to.equals( "DeviceServer" ) ) {
            String uid = new DeviceController().getUid( getMchNo( session ) );
            if(!influx.getIsWorking()){
                influx.InfluxDBConnection( "admin","admin","http://chaoswang.cn:11112",uid,"" );
            }
            date = new Date();
            deviceMode = true;
            if(booleans.get( uid )!=null&&booleans.get( uid )) {
              sendMessageToUser( uid, new TextMessage(recordFormat( msg )));
            }
            if ( !isEnd( session ) ) {
                JSONObject content = JSONObject.parseObject( msg );
                Map<String, Object> record = new HashMap<>();
                record.put( "frequency", content.getString( "frequency" ) );
                record.put( "depth", content.getString( "depth" ) );
                record.put( "rate", content.getString( "rate" ) );
                record.put( "oxygen", content.getString( "oxygen" ) );
                influx.insert( String.valueOf(getTime( session )), new HashMap<String, String>(), record, date.getTime(), TimeUnit.MILLISECONDS );

            }
            else {
                String latitude = msgJson.getString( "latitude" );
                String longitude = msgJson.getString( "longitude" );
                JSONObject content = JSONObject.parseObject( msg );
                Map<String, Object> record1 = new HashMap<>();
                Map<String, Object> record2 = new HashMap<>();
                record1.put( "longitude", latitude );
                record1.put( "latitude", longitude );
                record1.put( "flag", "start" );
                influx.insert( "records" , new HashMap<String, String>(), record1, date.getTime(), TimeUnit.MILLISECONDS );

                record2.put( "frequency", content.getString( "frequency" ) );
                record2.put( "depth", content.getString( "depth" ) );
                record2.put( "rate", content.getString( "rate" ) );
                record2.put( "oxygen", content.getString( "oxygen" ) );

                influx.insert( String.valueOf(date.getTime()), new HashMap<String, String>(), record2, getTime( session ), TimeUnit.MILLISECONDS );
            }
        }
        else if(to.equals( "APPServer" )){
            if(!influx.getIsWorking()){
                influx.InfluxDBConnection( "admin","admin","http://chaoswang.cn:11112",getMchNo( session ),"" );
            }
            deviceMode = false;
            JSONObject content = JSONObject.parseObject( msg );
            String time = content.getString( "time" );
            QueryResult res = influx.query( "select * from \"records\" where time >= \'" + time + "\' ORDER BY time DESC" );
            List record = res.getResults().get( 0 ).getSeries().get( 0 ).getValues().get( 0 );
            time = (String) record.get( 0 );
            String flag = (String) record.get( 1 );

            List historyRecord = influx
                .query( "select * from \"" + dateParser( time ) + "\" ORDER BY time DESC" )
                .getResults().get( 0 ).getSeries().get( 0 ).getValues();


            if ( flag.equals( "end" ) ) {
                //传所有信息给用户
                List<Point> list = resultFormat( (ArrayList<ArrayList<String>>) historyRecord );
                String result = main.java.utils.JsonUtils.toJson( list );
                sendMessageToUser( getMchNo( session ), new TextMessage( result ) );
                booleans.put( getMchNo( session ), false );
            }
            else {
                System.out.print( "建立WebSocket关联" );
                List<Point> list = resultFormat( (ArrayList<ArrayList<String>>) historyRecord );
                String result = main.java.utils.JsonUtils.toJson( list );
                sendMessageToUser( getMchNo( session ), new TextMessage( result ) );
                //传所有已记录信息 同步传输上传信息
                booleans.put( getMchNo( session ), true );
            }
        }
        else{
            deviceMode = false;
            WebSocketMessage<?> webSocketMessageServer = new TextMessage( "server:" + message );

        try {
            session.sendMessage( webSocketMessageServer );
            if ( "all".equals( to.toLowerCase() ) ) {
                sendMessageToAllUsers( new TextMessage( msg ) );
            }
            else {
                sendMessageToUser( to, new TextMessage( msg ) );
            }
        } catch ( IOException e ) {
            logger.info( "handleTextMessage method error：{}", e );
        }
    }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        logger.info("连接出错");
        users.remove(getMchNo(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if(deviceMode) {
            Map<String, Object> record = new HashMap<>();
            record.put( "flag", "end" );
            influx.insert( "records", new HashMap<String, String>(), record, getTime( session ), TimeUnit.MILLISECONDS );
        }
        influx.close();
        logger.info("连接已关闭：" + status);
        users.remove(getMchNo(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessage(String jsonData) throws JSONException {
        logger.info("收到客户端消息sendMessage：{}", jsonData);
        JSONObject msgJson = JSONObject.parseObject(jsonData);
        String mchNo = StringUtils.isEmpty(msgJson.getString(CLIENT_ID)) ? "陌生人" : msgJson.getString(CLIENT_ID);
        String to = msgJson.getString("to");
        String msg = msgJson.getString("msg");

        if("all".equals(to.toLowerCase())) {
            sendMessageToAllUsers(new TextMessage(mchNo + ":" +msg));
        }else {
            sendMessageToUser(to, new TextMessage(mchNo + ":" +msg));
        }
    }

    /**
     * 发送信息给指定用户
     * @Title: sendMessageToUser
     * @Description: TODO
     * @Date 2018年8月21日 上午11:01:08
     * @author OnlyMate
     * @param mchNo
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String mchNo, TextMessage message) {
        if (users.get(mchNo) == null) {
          return false;
        }
        WebSocketSession session = users.get(mchNo);
        logger.info("sendMessage：{} ,msg：{}", session, message.getPayload());
        if (!session.isOpen()) {
            logger.info("客户端:{},已断开连接，发送消息失败", mchNo);
            return false;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            logger.info("sendMessageToUser method error：{}", e);
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     * @Title: sendMessageToAllUsers
     * @Description: TODO
     * @Date 2018年8月21日 上午11:01:14
     * @author OnlyMate
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> mchNos = users.keySet();
        WebSocketSession session = null;
        for (String mchNo : mchNos) {
            try {
                session = users.get(mchNo);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }else {
                    logger.info("客户端:{},已断开连接，发送消息失败", mchNo);
                }
            } catch (IOException e) {
                logger.info("sendMessageToAllUsers method error：{}", e);
                allSendSuccess = false;
            }
        }

        return allSendSuccess;
    }

    /**
     * 获取用户标识
     * @Title: getMchNo
     * @Description: TODO
     * @Date 2018年8月21日 上午11:01:01
     * @author OnlyMate
     * @param session
     * @return
     */
    private String getMchNo(WebSocketSession session) {
        try {
            String mchNo = session.getAttributes().get(CLIENT_ID).toString();
            return mchNo;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isEnd(WebSocketSession session){
        List<Result> ress = influx.query( "select * from \"records\" ORDER BY time DESC").getResults();
        QueryResult.Series res = influx.query( "select * from \"records\" ORDER BY time DESC").getResults().get( 0 ).getSeries().get( 0 );
        String flag = (String)res.getValues().get( 0 ).get( 1 );
        if(flag.equals( "start" )) {
          return false;
        } else {
          return true;
        }
    }

    private long getTime( WebSocketSession session) throws ParseException {
        QueryResult.Series res = influx.query( "select * from \"records\" ORDER BY time DESC").getResults().get( 0 ).getSeries().get( 0 );
        String time = (String)res.getValues().get( 0 ).get( 0 );
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        Date date = df.parse( time );

        return date.getTime();
    }

    private ArrayList<Point> resultFormat( ArrayList<ArrayList<String>> l ){
        ArrayList<Point> result = new ArrayList<>();
        for(ArrayList<String> items: l) {
          result.add(new Point(items));
        }
        return result;
    }

    private long dateParser ( String date ) throws  ParseException{
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        Date d = df.parse( date );
        return d.getTime();
    }

    private String dateFormat ( long time ){
        Date date = new Date( time );
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        return df.format( date );
    }

    private String recordFormat( String msg ) throws ParseException {
        JSONObject jsonobj = JSONObject.parseObject( msg );
        Point record = new Point();

        record.setTime( dateFormat( date.getTime() ) );
        record.setDepth( (String)jsonobj.get("depth") );
        record.setFrequency( (String)jsonobj.get("frequency") );
        record.setRate( (String)jsonobj.get("rate") );
        record.setOxygen( (String)jsonobj.get("oxygen") );

        return JsonUtils.toJson( record );

    }
}
