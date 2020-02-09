package main.java.bean;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author ChaosWong
 * @date 2020/1/8 16:47
 * @title
 */

@Entity
@Table(name = "device")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Getter(onMethod_={@GenericGenerator(name = "generator", strategy = "uuid.hex"),@Id,@GeneratedValue(generator = "generator"),
        @Column(name = "d_id", unique = true, nullable = false, length = 64)})
    private String dId;

    @Getter(onMethod_={ @ManyToOne,@JoinColumn (name = "u_id")})
    private User user;

    @Getter(onMethod_={@Column(name = "deviceId", length = 8)})
    private String deviceId;
}
