package com.pay.wechat;

import java.io.File;

import com.afan.tool.http.SSLConnectionFactory;
import com.afan.tool.http.WebUtil;

/**
 * ΢����ҵ���
 * �ӿ��ĵ� https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers
 * @author afan
 *
 */
public class WechatTransferService {
	
	public static void main(String[] args) {
		WechatTransferService service = new WechatTransferService();
		service.transfer();
	}
	
	public boolean transfer(){
		//��ҵת��
		String transferUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
		
		String appId = "";//app
		String appSecret = "";//secret
		String mchId = "";//�̻���
		String title = "�������";
		String orderId = "20180101";
		String payment = "100";
		String ip = "127.0.0.1";
		String openId = "xxxx";
		
		
		WechatTransferRequest request = new WechatTransferRequest(appId, mchId, appSecret);
		request.setTitle(title);
		request.setOutOrderId(orderId);
		request.setTotalFee(payment);
		request.setClientIp(ip);
		request.setOpenId(openId);
		try {
			String data = request.transferData();
			SSLConnectionFactory factory = new SSLConnectionFactory("E:\\Workspaces\\MyEclipse10\\fhdoredpackagetask\\main\\resource\\cert" + File.separator + "1487722302" + ".p12", "1487722302");
			String result = WebUtil.post(transferUrl, data, "text/html", "UTF-8", factory);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
