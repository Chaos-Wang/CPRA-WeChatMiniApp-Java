package main.java.controller.DeviceServerController;

import main.java.controller.EntityController.DeviceController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ChaosWong
 * @date 2020/1/14 12:45
 * @title
 */

@RestController
@RequestMapping ("/common/device")
public class DeviceServerController {

    private DeviceController dc = new DeviceController();

    @GetMapping ("/bind")
    public boolean bind( String openId , String deviceId ) {
        return dc.bind( openId, deviceId );
    }
    @GetMapping("/getRecordsList")
    public String getRecordsList( String deviceId ){ return dc.getRecordsList(deviceId);}
    @GetMapping("/getDeviceLocation")
    public String getDeviceLocation(){ return  dc.getDeviceLocation(); }
}
