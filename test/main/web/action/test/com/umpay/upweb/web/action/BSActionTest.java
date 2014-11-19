package com.umpay.upweb.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bs.mpsp.util.DateTimeUtil;
import com.umpay.upweb.system.action.BaseAction;
import com.umpay.upweb.system.common.Constants;
import com.umpay.upweb.system.common.FunCode;
import com.umpay.upweb.system.common.HTTPUtil;

@Controller
@RequestMapping("/test/bs")
public class BSActionTest extends BaseAction {

	@Override
	public FunCode getFunCode() {
		return FunCode.WEBUPORDER;
	}
	
	@RequestMapping("/orderpage")
	public String orderPage(ModelMap modelMap){
		modelMap.put("order_id", Math.round(Math.random() * 1000000000L));
		
		return "test";
	}
	
	
	@RequestMapping("/rdopage")
	public String rdopage(ModelMap modelMap){
		modelMap.put("linkid", Math.round(Math.random() * 1000000000L));
		modelMap.put("time", DateTimeUtil.getDateTimeString(System.currentTimeMillis()));
		try {
			modelMap.put("redurl", URLEncoder.encode("http://114.113.159.221:8756/upwebbusi/gw/bsresult.do", Constants.CHART_UTF8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "rdopage";
	}
	
	
	/**
	 * 选择博升的支付流程
	 * 	破解版或者非破解版
	 * 		破解版直接发送短信
	 * 		非破解版返回跳转MM页面的url
	 */
	@RequestMapping("/pay")
	public void selectBSChannel(BSOrderVO order, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response){
		logger.info("****************************order**********************************");
		PrintWriter out = null;
		try {
//			order = (BSOrderVO) super.requestJson2Bean(request, BSOrderVO.class);
			
			// 商家账号
			String biz_id = order.getBiz_id();
			
			// 产品ID
			String product_id = order.getProduct_id();
			
			// 合作方订单号
			String order_id = order.getOrder_id();
			
			// 购买人登录账号
			String account_id = order.getAccount_id();
			
			// www/wap
			String client_type = order.getClient_type();
			
			// 0 新用户订购      1 老用户续订      按次交易默认为0
			String order_type = order.getOrder_type();
			
			// 移动手机号
			String mobile = order.getMobile();
			
			// 博升分配的密钥
			String secrtykey = order.getSecrtykey();
			
			StringBuffer signBuffer = new StringBuffer();
			
			signBuffer.append("biz_id=");
			signBuffer.append(biz_id);
			
			signBuffer.append("&product_id=");
			signBuffer.append(product_id);
			
			signBuffer.append("&order_id=");
			signBuffer.append(order_id);
			
			signBuffer.append("&account_id=");
			signBuffer.append(account_id);
			
			signBuffer.append("&client_type=");
			signBuffer.append(client_type);
			
			signBuffer.append("&mobile=");
			signBuffer.append(mobile);
			
			signBuffer.append("&order_type=");
			signBuffer.append(order_type);
			
			String signString = signBuffer.toString();
			
			logger.debug("准备MD5的字符串：" + signString + secrtykey);
			
			String sign = DigestUtils.md5Hex(signString + secrtykey);
			logger.debug("MD5加密后：" + sign);
			
			
			// 请求参数Map
			Map<String, String> requestMap = new HashMap<String, String>();
			
			requestMap.put("biz_id", biz_id);
			requestMap.put("product_id", product_id);
			requestMap.put("order_id", order_id);
			requestMap.put("account_id", account_id);
			requestMap.put("client_type", client_type);
			requestMap.put("mobile", mobile);
			requestMap.put("order_type", order_type);
			requestMap.put("sign", sign); 
			
			// BS.ORDER.URL
			String url = "http://cmpay.dalasu.com/mmwap/JumpOrderPageServlet";
			logger.info("请求博升优势下单接口url：" + url);
			Object o = HTTPUtil.get(url, requestMap);
			
			if(o != null){
				String result = o.toString();
				logger.info("博升优势的下单接口返回值：" + result);
				try {
					out = response.getWriter();
					
					// 这是一个代码  把retCode和retMsg以json形式返回给sdk
					out.write(result);
					
					out.flush();
					
				} catch (IOException e) {
					logger.error("向博升请求下单接口出现异常", e);
				}
			}
			else{
				//TODO 返回下单错误
			}
		} catch (Exception e) {
			logger.error("向博升请求下单失败。", e);
			//TODO 回写失败数据
		} finally{
			// 
			if(out != null){
				out.close();
			}
		}
		
	}
	
	@RequestMapping("/rdo")
	public void bsRDO(BSRDO order, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response){
		logger.info("****************************bsRDO**********************************");
		PrintWriter out = null;
		try {
			// 商家账号
			String biz_productid = order.getBiz_productid();
			
			// 产品ID
			String productid = order.getProductid();
			
			// 合作方订单号
			String linkid = order.getLinkid();
			String time = order.getTime();
			String redurl = URLEncoder.encode(order.getRedurl(), Constants.CHART_UTF8);
			String vt = order.getVt();
			String fee = order.getFee();
			
			StringBuffer signBuffer = new StringBuffer();
			signBuffer.append("productid=");
			signBuffer.append(productid);
			signBuffer.append("&linkid=");
			signBuffer.append(linkid);
			signBuffer.append("&time=");
			signBuffer.append(time);
			signBuffer.append("&redurl=");
			signBuffer.append(redurl);
			signBuffer.append("&vt=");
			signBuffer.append(vt);
			signBuffer.append("biz_productid=");
			signBuffer.append(biz_productid);
			signBuffer.append("&fee=");
			signBuffer.append(fee);
			logger.info("请求参数：" + signBuffer.toString());
			
			// 请求参数Map
			Map<String, String> requestMap = new HashMap<String, String>();
			
			requestMap.put("productid", productid);
			requestMap.put("linkid", linkid);
			requestMap.put("time", time);
			requestMap.put("redurl", redurl);
			requestMap.put("vt", vt);
			requestMap.put("biz_productid", biz_productid);
			requestMap.put("fee", fee);
			
			// BS.ORDER.URL
			String url = "http://cmpay_cmcc.dalasu.com/bsrdo/MobileRdoMo";
			logger.info("请求博升优势RDO下单接口url：" + url);
			Object o = HTTPUtil.get(url, requestMap);
			
			if(o != null){
				String result = o.toString();
				logger.info("博升优势的RDO下单接口返回值：" + result);
				try {
					out = response.getWriter();
					
					// 这是一个代码  把retCode和retMsg以json形式返回给sdk
					out.write(result);
					
					out.flush();
					
				} catch (IOException e) {
					logger.error("向博升请求RDO下单接口出现异常", e);
				}
			}
			else{
			}
		} catch (Exception e) {
			logger.error("向博升请求RDO下单失败。", e);
			//TODO 回写失败数据
		} finally{
			// 
			if(out != null){
				out.close();
			}
		}
		
	}

}
