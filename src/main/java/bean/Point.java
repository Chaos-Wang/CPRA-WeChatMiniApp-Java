package main.java.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author ChaosWong
 * @date 2020/1/22 17:03
 * @title main.java.bean
 */
@Getter
@Setter
public class Point implements java.io.Serializable {
    private String time;
    private String depth;
    private String frequency;
    private String oxygen;
    private String rate;

    public Point (){}
    public Point ( ArrayList<String> list ){
        time = list.get( 0 );
        depth = list.get( 1 );
        frequency = list.get( 2 );
        oxygen = list.get( 3 );
        rate = list.get( 4 );
    }
}
