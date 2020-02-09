package main.java.controller.EntityController;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import main.java.bean.Device;
import main.java.bean.User;
import main.java.utils.TransactionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import main.java.utils.*;

import java.util.Objects;

/**
 * @author ChaosWong
 * @date 2020/1/8 17:18
 * @title
 */
public class UserController {

    public static void createUser( String openid ){

            TransactionUtils tran = new TransactionUtils();
            Criteria criteria = tran.getSession().createCriteria(User.class);
            criteria.add( Restrictions.eq("openId",openid ));

            if(criteria.list().size()==0) {

                User user = new User();
                user.setOpenId( openid );
                tran.getSession().save( user );
                tran.endTransaction();

            }else {
              tran.endTransaction();
            }

    }
    public static void addUserInfo( WxMaUserInfo userInfo ){

        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("openId",userInfo.getOpenId()) );
        User u = (User)(criteria.list().get( 0 ));
        u.setUserName( u.getUId().substring( 0 , 4 ).concat( u.getOpenId().substring( 0,4 ) ) );
        u.setAvatarUrl( userInfo.getAvatarUrl() );
        u.setCity( userInfo.getCity() );
        u.setCountry( userInfo.getCountry() );
        u.setNickName( userInfo.getNickName() );
        u.setOpenId( userInfo.getOpenId() );
        u.setProvince( userInfo.getProvince() );

        tran.getSession().update( u );
        tran.endTransaction();
    }
    public static boolean changePasswordByOpenId(String oldPassword, String newPassword, String openId){
        TransactionUtils tran = new TransactionUtils();

        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("openId", openId ) );
        User u = (User)(criteria.list().get( 0 ));
        if( Objects.equals( u.getPassword(), oldPassword ) || u.getPassword()== null ){
            u.setPassword( newPassword );
            tran.getSession().update( u );
            tran.endTransaction();
            return true;
        }else{
            tran.endTransaction();
            return false;
        }
    }
    public static boolean changePasswordByUserName(String oldPassword, String newPassword, String userName){
        TransactionUtils tran = new TransactionUtils();

        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("userName", userName ) );
        User u = (User)(criteria.list().get( 0 ));
        if( Objects.equals( u.getPassword(), oldPassword ) || u.getPassword()== null ){
            u.setPassword( newPassword );
            tran.getSession().update( u );
            tran.endTransaction();
            return true;
        }else{
            tran.endTransaction();
            return false;
        }
    }
    public static void addPhone( WxMaUserInfo userInfo, String phone ){

        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("openId",userInfo.getOpenId()) );
        User u = (User)(criteria.list().get( 0 ));
        u.setPhone( phone );

        tran.getSession().update( u );
        tran.endTransaction();
    }
    public static String loginWithPwd( String password, String userName ){
        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("userName",userName ));
        User u = (User)(criteria.list().get( 0 ));
        tran.endTransaction();

        if( u.getPassword().equals( password ) ) {
          return JsonUtils.toJson(u);
        } else {
          return "wrong password or account not exist";
        }

    }
    public static String getUserInfoWithOpenId( String openId ){

        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("openId",openId ) );
        User u = (User)(criteria.list().get( 0 ));
        tran.endTransaction();
        return JsonUtils.toJson(u);

    }
    public static String getUserInfoWithUserName( String userName ){

        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("userName", userName ) );
        User u = (User)(criteria.list().get( 0 ));
        tran.endTransaction();
        return JsonUtils.toJson(u);
    }
    public static User getUserObject( String userName ){

        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(User.class);
        criteria.add( Restrictions.eq("userName", userName ) );
        User u = (User)(criteria.list().get( 0 ));
        tran.endTransaction();
        return u;
    }
    public static  String getDeviceIdWithUserName( String userName ){
        String uid = getUserObject( userName ).getUId();
        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria( Device.class);
        criteria.add( Restrictions.eq("u_id", uid ) );
        tran.endTransaction();

        return criteria.list().size()==0?"未绑定":((Device)(criteria.list().get(0))).getDeviceId();
    }
    public static  String getDeviceIdWithUId( String uId ){
        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria( Device.class);
        criteria.add( Restrictions.eq("u_id", uId ) );
        tran.endTransaction();

        return criteria.list().size()==0?"未绑定":((Device)(criteria.list().get(0))).getDeviceId();
    }

}
