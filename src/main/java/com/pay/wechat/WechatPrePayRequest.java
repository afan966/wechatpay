package com.pay.wechat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.afan.tool.string.GUUid;
import com.afan.tool.xml.XmlIgnore;
import com.afan.tool.xml.XmlProperty;
import com.afan.tool.xml.XmlUtil;


/**
 * 微信统一支付请求
 * @author afan
 *
 */
public class WechatPrePayRequest {

	@XmlProperty(alia = "nonce_str")
	private String randStr;//GUUid.getUid();
	@XmlProperty(alia = "appid")
	private String appId;//微信分配的小程序ID
	@XmlProperty(alia = "mch_id")
	private String mchId;//微信支付分配的商户号
	@XmlProperty(alia = "body")
	private String title;//订单标题
	@XmlProperty(alia = "out_trade_no")
	private String outOrderId;//商户订单号
	@XmlProperty(alia = "total_fee")
	private String totalFee;//总金额分
	@XmlProperty(alia = "spbill_create_ip")
	private String clientIp;//终端IP
	@XmlProperty(alia = "notify_url")
	private String payBackUrl;//下单通知URL
	@XmlProperty(alia = "trade_type")
	private String tradeType="JSAPI";//交易类型“JSAPI”
	@XmlProperty(alia = "openid")
	private String openId;//用户OPENID
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
		params.put("appid", appId);//微信分配的小程序ID
		params.put("mch_id", mchId);//微信支付分配的商户号
		params.put("nonce_str", randStr);//随机字符串
		params.put("body", title);//标题
		params.put("out_trade_no", outOrderId);//商户订单号
		params.put("total_fee", totalFee);//总金额
		params.put("spbill_create_ip", clientIp);//终端IP
		params.put("notify_url", payBackUrl);//通知地址 	
		params.put("trade_type", tradeType);//交易类型 	
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
	params.put("nonce_str", randStr);//随机字符串
	params.put("body", "风火递-"+item.getTitle());//
	params.put("out_trade_no", order.getOrderId()+"");//商户订单号
	params.put("total_fee", ((int)(order.getPayment()*100))+"");//总金额
	params.put("spbill_create_ip", ip);//终端IP
	params.put("notify_url", payBackUrl);//通知地址 	
	params.put("trade_type", "JSAPI");//交易类型 	
	params.put("openid", open.getToken());
	String sign = WeChatSignUtil.signPay(params, Constants.appSecret);
	params.put("sign", sign);//签名
	String prepayData = WeChatSignUtil.mapToXML(params);
	*/
}
