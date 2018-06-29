package com.pay.wechat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.afan.tool.string.GUUid;
import com.afan.tool.xml.XmlIgnore;
import com.afan.tool.xml.XmlProperty;
import com.afan.tool.xml.XmlUtil;


/**
 * ΢��ͳһ֧������
 * @author afan
 *
 */
public class WechatPrePayRequest {

	@XmlProperty(alia = "nonce_str")
	private String randStr;//GUUid.getUid();
	@XmlProperty(alia = "appid")
	private String appId;//΢�ŷ����С����ID
	@XmlProperty(alia = "mch_id")
	private String mchId;//΢��֧��������̻���
	@XmlProperty(alia = "body")
	private String title;//��������
	@XmlProperty(alia = "out_trade_no")
	private String outOrderId;//�̻�������
	@XmlProperty(alia = "total_fee")
	private String totalFee;//�ܽ���
	@XmlProperty(alia = "spbill_create_ip")
	private String clientIp;//�ն�IP
	@XmlProperty(alia = "notify_url")
	private String payBackUrl;//�µ�֪ͨURL
	@XmlProperty(alia = "trade_type")
	private String tradeType="JSAPI";//�������͡�JSAPI��
	@XmlProperty(alia = "openid")
	private String openId;//�û�OPENID
	@XmlProperty(alia = "sign")
	private String sign;
	@XmlIgnore
	private String appSecret;
	
	public WechatPrePayRequest(String appId, String mchId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
		this.mchId = mchId;
	}
	
	public String prepayData() throws IOException {
		this.randStr = GUUid.getUid();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", appId);//΢�ŷ����С����ID
		params.put("mch_id", mchId);//΢��֧��������̻���
		params.put("nonce_str", randStr);//����ַ���
		params.put("body", title);//����
		params.put("out_trade_no", outOrderId);//�̻�������
		params.put("total_fee", totalFee);//�ܽ��
		params.put("spbill_create_ip", clientIp);//�ն�IP
		params.put("notify_url", payBackUrl);//֪ͨ��ַ 	
		params.put("trade_type", tradeType);//�������� 	
		params.put("openid", openId);
		this.sign = WeChatSignUtil.signPay(params, appSecret);
		return XmlUtil.toXml(this);
	}
	
	
	public String getRandStr() {
		return randStr;
	}
	public void setRandStr(String randStr) {
		this.randStr = randStr;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getMchId() {
		return mchId;
	}
	public void setMchId(String mchId) {
		this.mchId = mchId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOutOrderId() {
		return outOrderId;
	}
	public void setOutOrderId(String outOrderId) {
		this.outOrderId = outOrderId;
	}
	public String getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getPayBackUrl() {
		return payBackUrl;
	}
	public void setPayBackUrl(String payBackUrl) {
		this.payBackUrl = payBackUrl;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	/*
	params.put("appid", Constants.appId);//
	params.put("mch_id", Constants.mchId);//
	params.put("nonce_str", randStr);//����ַ���
	params.put("body", "����-"+item.getTitle());//
	params.put("out_trade_no", order.getOrderId()+"");//�̻�������
	params.put("total_fee", ((int)(order.getPayment()*100))+"");//�ܽ��
	params.put("spbill_create_ip", ip);//�ն�IP
	params.put("notify_url", payBackUrl);//֪ͨ��ַ 	
	params.put("trade_type", "JSAPI");//�������� 	
	params.put("openid", open.getToken());
	String sign = WeChatSignUtil.signPay(params, Constants.appSecret);
	params.put("sign", sign);//ǩ��
	String prepayData = WeChatSignUtil.mapToXML(params);
	*/
}
