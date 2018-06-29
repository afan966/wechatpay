package com.pay.wechat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.afan.tool.string.GUUid;
import com.afan.tool.xml.XmlIgnore;
import com.afan.tool.xml.XmlProperty;
import com.afan.tool.xml.XmlUtil;


/**
 * 微信企业转账请求
 * @author afan
 *
 */
public class WechatTransferRequest {

	@XmlProperty(alia = "nonce_str")
	private String randStr;//GUUid.getUid();
	@XmlProperty(alia = "mch_appid")
	private String appId;//商户appID
	@XmlProperty(alia = "mchid")
	private String mchId;//微信支付分配的商户号
	@XmlProperty(alia = "desc")
	private String title;//描述
	@XmlProperty(alia = "partner_trade_no")
	private String outOrderId;//商户订单号
	@XmlProperty(alia = "amount")
	private String totalFee;//总金额分
	@XmlProperty(alia = "spbill_create_ip")
	private String clientIp;//终端IP
	@XmlProperty(alia = "check_name")
	private String checkName="NO_CHECK";//检查姓名
	@XmlProperty(alia = "openid")
	private String openId;//用户OPENID
	@XmlProperty(alia = "sign")
	private String sign;
	@XmlIgnore
	private String appSecret;
	
	public WechatTransferRequest(String appId, String mchId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
		this.mchId = mchId;
	}
	
	public String transferData() throws IOException {
		this.randStr = GUUid.getUid();
		Map<String, String> params = new HashMap<String, String>();
		params.put("mch_appid", appId);//微信分配的小程序ID
		params.put("mchid", mchId);//微信支付分配的商户号
		params.put("nonce_str", randStr);//随机字符串
		params.put("desc", title);//
		params.put("partner_trade_no", outOrderId);//商户订单号
		params.put("amount", totalFee);//总金额
		params.put("spbill_create_ip", clientIp);//终端IP
		params.put("check_name", checkName);//检查姓名
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
	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
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
	params.put("mch_appid", Constants.appId);
	params.put("mchid", Constants.mchId);
	params.put("nonce_str", randStr);
	params.put("partner_trade_no", orderId);
	params.put("openid", openId);
	params.put("check_name", "NO_CHECK");
	params.put("amount", amount);
	params.put("desc", desc);
	params.put("spbill_create_ip", Constants.fhdoWxIp);
	String sign = WeChatSignUtil.signPay(params, Constants.appSecret);
	params.put("sign", sign);
	*/
}
