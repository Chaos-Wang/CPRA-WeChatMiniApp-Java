package main.java.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ChaosWong
 * @date 2020/1/22 22:15
 * @title main.java.bean
 */
@Getter
@Setter
public class Location implements java.io.Serializable{
    private String uId;
    private String latitude;
    private String longitude;
}
