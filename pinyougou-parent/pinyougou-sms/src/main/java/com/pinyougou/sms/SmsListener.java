package com.pinyougou.sms;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

@Component
public class SmsListener {
	
	@Autowired
	private SmsUtil smsUtil;

	@JmsListener(destination="sms")
	public void sendSms(Map<String,String> map){
		
		/*try {
			SendSmsResponse response = smsUtil.sendSms(
					map.get("mobile"),//接收手机号
					map.get("template_code") ,//发送模板号
					map.get("sign_name")  , //签名
					map.get("param") );//发送的短信验证码
			System.out.println("code:"+response.getCode());
			System.out.println("message:"+response.getMessage());
			System.out.println(map.get("mobile"));
		
		} catch (ClientException e) {
			e.printStackTrace();
		}*/
		System.out.println(
				"手机号："+map.get("mobile")//接收手机号
				+"模板号："+map.get("template_code") //发送模板号
				+"签名："+map.get("sign_name")//签名
				+"短信验证码："+map.get("param") //发送的短信验证码
		);
	}
	
}
