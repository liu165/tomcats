package com.xiaoliu.demo.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.Date;

//@Configuration //1.主要用于标记配置类，兼备Component的效果。
//@EnableScheduling // 2.openRegularTasks 开启定时任务
//实现多个port的监听
@Component
@Lazy(false)
public class  Tomcatscedule {

    @Autowired
    MailUtils mailUtils;
    @Autowired
    StaticKeys staticKeys;
    @Autowired
    SmsUtil sms;

    //3.Add regular tasks
    //判断第二次重启了过了

        String[] port =null ;
        int[] isstwo  ;
        Boolean one = true;
        Boolean isRestart =true;
//设置每几分钟执行一次 cron表达式，，每分钟执行一次
    @Scheduled(cron = "0 0/1 * * * ?")
    private void configureTasks() {
        if(one) {
            //初始化，
            port = staticKeys.getPorts();
            isstwo =new int [port.length];
            isRestart = staticKeys.getIsAuto().equals("0")?false:true;
          one = false;
         }
        //允许连续触发两次访问失败事件
            System.err.println("正在执行定时任务1: " +new Date().toString());
            InputStream in = null;
            String dir[] = staticKeys.getDir();
            String host[] = staticKeys.getHost();
            Process process;
            String ip = staticKeys.getIp();
            String[] port = staticKeys.getPorts();
            //如果一个端口对应的服务器重启过了，还是报错误，那就停止重启
            for(int j=0;j<port.length; j++){

                int i = keepTomcatAlive(ip, port[j]);
                if (i!=2) {
                    isstwo[j]++;
                    //如果重启了两次还没有解决问题，就不重新执行脚本了。只重启一次 //4xx要重启 5xx不用重启
                    if (i == 4) {
                       if (isRestart&&isstwo[j] < 2) {
                           System.out.println(new Date() + "重新启动tomcat端口---" + port[j]);
                           String cmd = "cmd /c start " + dir[j];// pass
                           try {
                               Process ps = Runtime.getRuntime().exec(cmd);
                               ps.waitFor();
                           } catch (Exception e) {
                               e.printStackTrace();
                               mailUtils.sendMail("主机：" + host[j] + ",端口：" + port[j] + "的tomcat未响应通知", "端口：" + port[j] + "的tomcat已经宕机，不能正常启动重启脚本请人工重启");
                               sms.sendSms("主机：" + host[j] + "端口：" + port[j] + "" + "的tomcat已经宕机，不能正常启动服务器请人工重启");
                           }
                       }
                            if (isRestart&&isstwo[j] < 2) {
                                mailUtils.sendMail("主机：" + host[j] + ",端口：" + port[j] + "的tomcat未响应通知", "端口：" + port[j] + "，进程ID：" + "的tomcat已经宕机，正在重启服务");
                                sms.sendSms("主机：" + host[j] +"端口：" + port[j] + "，进程ID：" + "的tomcat已经宕机，正在重启服务");
                            } else {
                                mailUtils.sendMail("主机：" + host[j] + ",端口：" + port[j] + "的tomcat未响应通知", "端口：" + port[j] + "，进程ID：" + "的tomcat已经宕机，无法重启请人工尝试");
                                sms.sendSms("主机：" + host[j]+"端口：" + port[j] + "，进程ID：" + "的tomcat已经宕机，重启失败请人工尝试");
                            }
                        }else{
                            mailUtils.sendMail("主机：" + host[j] + ",端口：" + port[j] + "的tomcat500错误通知", "端口：" + port[j] + "，进程ID：" + "的tomcat发生500错误，请人工查验");
                            sms.sendSms("主机：" + host[j] +"端口：" + port[j] + "" + "的tomcat发生500错误，请人工查验");
                        }
                    }
          else {
                    //若下一次访问正常了，则是否第二次重启了设置为true
                    if(isstwo[j]>0){
                        mailUtils.sendMail("主机：" + host[j] + ",端口：" + port[j] + "的tomcat", "端口：" + port[j] + "，进程ID：" + "的tomcat已经正常了，请进行人工校验");
                        sms.sendSms("主机：" + host[j] + "端口：" + port[j] + "" + "的tomcat已经重启成功，请进行人工校验");
                    }
                    isstwo[j] = 0;
                }

            }

    }

    /**
     * 检测该端口的tomcat是否有响应  2正常  4服务器挂了 5服务器发送故障
     * @param port
     * @return
     * @throws NullPointerException
     */
    int keepTomcatAlive(String ip,String port) {
        String s;
        int isTomcatAlive = 2;
        BufferedReader in;
        System.setProperty("sun.net.client.defaultConnectTimeout", "8000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        try {
            URL url = new URL("http://"+ip+":"+port);
           System.out.println("正在访问：http://"+ip+":"+port);
            HttpURLConnection con =(HttpURLConnection)url.openConnection();
            String s1 = Integer.toString(con.getResponseCode());
           System.out.println("请求状态："+con.getResponseCode());
            //不是4xx或者5xx即不是服务器发送故障
            if(s1.startsWith("4")){
                return 4;//服务器挂了
            }
            if(s1.startsWith("5")){
                return 5;//服务器故障
            }

//            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            con.setConnectTimeout(6000);//设置连接超时时间
//            con.setReadTimeout(10000);
//             while ((s = in.readLine()) != null) {
//                if (s.length() > 0) {
//                    //accessed page successful.
//                    return true;
//                }
//            }
//            in.close();
        } catch (Exception ex) {
           System.out.println("端口读取异常："+ex.toString());
            return 4;
        }
            return isTomcatAlive;
    }

}
