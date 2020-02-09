package main.java.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author ChaosWong
 * @date 2020/1/22 21:12
 * @title main.java.bean
 */
@Getter
@Setter
public class Record implements java.io.Serializable{
    private String time;
    private String latitude;
    private String longitude;

    public Record (){}
    public Record ( ArrayList<String> list ){
        time = list.get( 0 );
        latitude = list .get( 2 );
        longitude = list .get( 3 );
    }
}
