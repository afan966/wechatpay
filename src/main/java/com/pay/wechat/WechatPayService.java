package com.pay.wechat;

import com.afan.tool.http.WebUtil;

/**
 * 微信统一支付，适用于公众号，小程序
 * 接口文档 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
 * 需要申请商户号 https://pay.weixin.qq.com/index.php/partner/public/home
 * 
 * @author afan
 *
 */
public class WechatPayService {
	
	public static void main(String[] args) {
		WechatPayService payService = new WechatPayService();
		payService.prePay();
	}
	
	/**
	 * 生成统一支付ID供小程序端，公众号端 发起支付组件
	 * @return
	 */
	public String prePay(){
		//统一支付接口
		String unifiedorderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		//支付回调接口
		String payBackUrl = "https://xxx/wechatPay/notify.do";
		
		String appId = "";//app
		String appSecret = "";//secret
		String mchId = "";//商户号
		String title = "买个表";
		String orderId = "20180101";
		String payment = "100";
		String ip = "127.0.0.1";
		String openId = "oy10b0ekbCLlcM5wTgsHoqvxfBtU";
		
		WechatPrePayRequest request = new WechatPrePayRequest(appId, appSecret, mchId);
		request.setTitle(title);
		request.setOutOrderId(orderId);
		request.setTotalFee(payment);
		request.setClientIp(ip);
		request.setPayBackUrl(payBackUrl);
		request.setOpenId(openId);
		try {
			String data = request.prepayData();
			String result = WebUtil.post(unifiedorderUrl, data, "text/html", "UTF-8");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
