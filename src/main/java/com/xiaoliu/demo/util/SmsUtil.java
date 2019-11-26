package com.xiaoliu.demo.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ClassName: SmsUtil <br/>
 * Description: <br/>
 * date: 2019/10/29 17:25<br/>
 * sms工具类
 * @author me<br />
 * @since JDK 1.8
 */
@Component
public class SmsUtil {
    //注入有关属性
    @Value("${sms.smsMob}")
    String smsMob;
    @Value("${sms.Key}")
    String Key;
    @Value("${sms.Uid}")
    String Uid;

    /**
     * 发送短信
     * @param comtent 发送内容
     * @return  是否成功 大于0代表成功
     */
    public  int sendSms(String comtent){
        HttpClientUtil client = HttpClientUtil.getInstance();
        return client.sendMsgGbk(Uid, Key, comtent, smsMob );
    }
}
