package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 发送请求，生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取用户ID
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //从缓存中查询是否存在支付日志信息
        TbPayLog payLog = orderService.searchPayLogFromRedis(name);
        if (payLog != null){
            return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");
        }else{
            return new HashMap();
        }


    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int i = 0;
        while (true) {
            //调用方法查询订单状态
            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {//订单出错
                result = new Result(false, "支付出错");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {//订单支付成功
                result = new Result(true, "支付成功");
                //修改订单状态
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id")+"");
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if (i > 10) {
                result = new Result(false, "二维码过期!");
                break;
            }
        }
        return result;
    }
}
