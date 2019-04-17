package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.untils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    /**
     * 微信公众账号或开放平台 APP 的唯一标识
     */
    @Value("${appid}")
    private String appid;

    /**
     * 财付通平台的商户账号
     */
    @Value("${partner}")
    private String partner;

    /**
     * 财付通平台的商户密钥
     */
    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 回调地址
     */
    @Value("${notifyurl}")
    private String notifyurl;


    /**
     * 发送参数，生成二维码
     * @param out_trade_no 订单号
     * @param total_fee  支付金额（分）
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);//微信公众号
        paramMap.put("mch_id",partner);//财付通账号
        paramMap.put("nonce_str",  WXPayUtil.generateNonceStr());//随机字符串
        paramMap.put("body","品优购商城");//商品描述
        paramMap.put("out_trade_no",out_trade_no);//商户订单号
        paramMap.put("total_fee",total_fee);//标价金额
        paramMap.put("spbill_create_ip","127.0.0.1");//终端IP
        paramMap.put("notify_url","notifyurl");//通知地址
        paramMap.put("trade_type","NATIVE");//交易类型
        try {
            //2.生成要发送的XML文件
            //将封装的参数和商家秘钥通过工具类转换为XML文件
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //创建客户端对象，传入指定的URL地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);//使用HTTPS协议
            client.setXmlParam(paramXml);//传入需要提交的XML文件
            client.post();//指定请求格式为post

            //3.获得结果
            String content = client.getContent();//获取返回的结果
            Map<String, String> toMap = WXPayUtil.xmlToMap(content);//将返回的结果转换为MAP集合
            Map<String, String> resultMap = new HashMap();//创建返回的结果集map集合
            resultMap.put("code_url", toMap.get("code_url"));//支付地址
            resultMap.put("total_fee", total_fee);//总金额
            resultMap.put("out_trade_no",out_trade_no);//订单号

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    /**
     * 查询订单支付状态
     * @param out_trade_no 订单号
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.创建参数
        Map<String,String> paramMap = new HashMap();
        paramMap.put("appid",appid);//微信公众号
        paramMap.put("mch_id",partner);//商户id
        paramMap.put("out_trade_no",out_trade_no);//商户订单号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        try {
            //2.生成要发送的XML文件
            //将封装的参数和商家秘钥通过工具类转换为XML文件
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //创建客户端对象，传入指定的URL地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);//使用HTTPS协议
            client.setXmlParam(paramXml);//传入需要提交的XML文件
            client.post();//指定请求格式为post

            //3.获得结果
            String content = client.getContent();//获取返回的结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);//将返回的结果转换为MAP集合
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 关闭支付订单
     * @param out_trade_no 订单号
     * @return
     */
    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号 ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
