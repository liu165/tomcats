package com.xiaoliu.demo.util;

import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;

import org.springframework.stereotype.Component;

/**
 * <p>Title:StaticKeys </p>
 * <p>Description: 全局静态变量</p>
 * @author tianshiyeben
 * @date Jun 22, 2016
 */
@Component
public class StaticKeys {

    @Value("${test.dir}")
	String dir;

    //需要监听的tomcat端口，多个端口用英文逗号隔开
    @Value("${test.port}")
	 String PORT = null;
	//服务器名称
    @Value("${test.host}")
	 String HOST = "";
    @Value("${test.ip}")
	 String ip = "";
    @Value("${test.auto}")
    //设置是否要启动脚本
    String  isAuto ="";
 /**
	 * 获取监控tomat端口
	 * @return
	 */
	public String[] getPorts(){
	    String  t[] = null;
	    //如果包含，号则进行分割
		if(PORT.contains(",")){
            t = PORT.split(",");
        }else{
		    t = new String[1];
		    t[0] = PORT;
        }
		return t;
	}
	/**
	 * 获取服务器名称
	 * @return
	 */
	public  String[] getHost(){
        String  t[] = null;
        //如果包含，号则进行分割
        if(HOST.contains(",")){
            t = HOST.split(",");
        }else{
            t = new String[1];
            t[0] = HOST;
        }
		return t;
	}
    public  String[] getDir(){
        String  t[] = null;
        //如果包含，号则进行分割
        if(dir.contains(",")){
            t = dir.split(",");
        }else{
            t = new String[1];
            t[0] = dir;
        }
        return t;
    }
//获取ip地址
    public String getIp() {
        return ip;
    }

    public String getIsAuto() {
        return isAuto;
    }
}
