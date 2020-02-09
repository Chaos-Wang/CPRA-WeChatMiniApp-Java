package main.java.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * @author ChaosWong
 * @date 2020/1/8 16:46
 * @title
 */

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Getter(onMethod_={@GenericGenerator(name = "generator", strategy = "uuid.hex"),@Id,@GeneratedValue(generator = "generator"),
        @Column(name = "u_id", unique = true, nullable = false, length = 64)})
    private String uId;

    @Getter(onMethod_={@Column(name = "openId", length = 64)})
    private String openId;

    @Getter(onMethod_={@Column(name = "avatarUrl", length = 256)})
    private String avatarUrl;

    @Getter(onMethod_={@Column(name = "city", length = 32)})
    private String city;

    @Getter(onMethod_={@Column(name = "country", length = 32)})
    private String country;

    @Getter(onMethod_={@Column(name = "nickName", length = 64)})
    private String nickName;

    @Getter(onMethod_={@Column(name = "province", length = 256)})
    private String province;

    @Getter(onMethod_={@Column(name = "userName", length = 8)})
    private String userName;

    @Getter(onMethod_={@Column(name = "password", length = 64), @ColumnDefault ("null") })
    private String password;

    @Getter(onMethod_={@Column(name = "phone", length = 13)})
    private String phone;

}
