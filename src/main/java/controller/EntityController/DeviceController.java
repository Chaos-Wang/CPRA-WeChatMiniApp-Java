package main.java.controller.EntityController;

import com.sun.xml.bind.v2.runtime.reflect.Lister;
import main.java.bean.Device;
import main.java.bean.Location;
import main.java.bean.Record;
import main.java.bean.User;
import main.java.utils.InfluxDBUtils;
import main.java.utils.JsonUtils;
import main.java.utils.TransactionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ChaosWong
 * @date 2020/1/15 11:21
 * @title
 */
public class DeviceController {

    InfluxDBUtils influx = new InfluxDBUtils();

    public boolean bind ( String openId, String deviceId ) {

        influx.InfluxDBConnection( "admin","admin","http://chaoswang.cn:11112",getUid( deviceId ),"" );

        TransactionUtils tran = new TransactionUtils();

        Criteria criteria1 = tran.getSession().createCriteria( User.class);

        criteria1.add( Restrictions.eq("openId", openId ) );
        User u = (User)(criteria1.list().get( 0 ));
        Device d = new Device();

        if(criteria1.list().size()==1) {
            d.setUser( u );
            d.setDeviceId( deviceId );
            tran.getSession().save( d );
            tran.endTransaction();

            Map<String,Object> record_field = new HashMap<>();

            record_field.put( "longitude" , "39.9046900000");
            record_field.put( "latitude" , "116.4071700000");
            record_field.put( "flag" , "init");

            influx.insert( "records", new HashMap<String,String>(), record_field,System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            influx.close();

            return true;
        }else {
          return false;
        }
    }
    public String getUid(String deviceId) {
        TransactionUtils tran = new TransactionUtils();
        Criteria criteria = tran.getSession().createCriteria(Device.class);
        criteria.add( Restrictions.eq("deviceId",deviceId ) );
        Device d = (Device)(criteria.list().get( 0 ));
        tran.endTransaction();
        return d.getUser().getUId();
    }
    public String getRecordsList( String deviceId ){
        influx.InfluxDBConnection( "admin","admin","http://chaoswang.cn:11112",getUid( deviceId ),"" );

        List list = influx.query( "select * from \"records\"" )
            .getResults().get( 0 ).getSeries().get( 0 ).getValues();
        ArrayList<Record> result = new ArrayList<>();
        for(ArrayList<String> items: (ArrayList<ArrayList<String>>)list) {
          result.add( new Record( items ) );
        }
        influx.close();

        return JsonUtils.toJson( result );
    }
    public String getDeviceLocation(){
        TransactionUtils tran = new TransactionUtils();
        String sql = "select u_id from device";
        Query query = tran.getSession().createSQLQuery(sql);
        ArrayList<Location> res = new ArrayList<>();
        List lista = query.list();
        for( Object i : query.list()){
            Location l_tmp = new Location();
            l_tmp.setUId( (String)i );

            influx.InfluxDBConnection( "admin","admin","http://chaoswang.cn:11112",(String)i,"" );

            l_tmp.setLatitude((String)influx
                .query( "select latitude from \"records\"" )
                .getResults().get( 0 ).getSeries()
                .get( 0 ).getValues().get( 0 ).get( 0 ));
            l_tmp.setLongitude((String)influx
                .query( "select longitude from \"records\"" )
                .getResults().get( 0 ).getSeries()
                .get( 0 ).getValues().get( 0 ).get( 0 ));
            res.add( l_tmp );
            influx.close();
        }
        return JsonUtils.toJson( res );
    }

}
