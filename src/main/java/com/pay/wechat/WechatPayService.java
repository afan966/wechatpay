package com.pay.wechat;

import com.afan.tool.http.WebUtil;

/**
 * ΢��ͳһ֧���������ڹ��ںţ�С����
 * �ӿ��ĵ� https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
 * ��Ҫ�����̻��� https://pay.weixin.qq.com/index.php/partner/public/home
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
	 * ����ͳһ֧��ID��С����ˣ����ںŶ� ����֧�����
	 * @return
	 */
	public String prePay(){
		//ͳһ֧���ӿ�
		String unifiedorderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		//֧���ص��ӿ�
		String payBackUrl = "https://xxx/wechatPay/notify.do";
		
		String appId = "";//app
		String appSecret = "";//secret
		String mchId = "";//�̻���
		String title = "�����";
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
