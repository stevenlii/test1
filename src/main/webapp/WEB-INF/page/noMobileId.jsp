<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="./meta.jsp"%>
<%@ include file="./taglib.jsp"%>
<%@ page import='java.util.Date'%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript"
	src="<%=request.getContextPath()%>/static/js/jquery-1.7.2.min.js"></script>
<link type="text/css" rel="stylesheet"
	href="<%=request.getContextPath()%>/static/css/upweb.css" />
<title>移动话费支付页面</title>
<script type="text/javascript">
	function checkForm(){ 
	    var mobileid = $("#mobileid").val();
	    var regexp = /^(13[4-9]|15[0,1,2,7,8,9]|18[2,3,4,7,8]|147){1}[0-9]{8}$/;
	   
	    if(!(regexp.test(mobileid))){ 
	        $("#userMobileId_error").html('请输入正确的中国移动手机号码');
	        $("#userMobileId_error").show();
	        return false; 
	    }
	    
	    if($.trim($("#j_captcha_response").val()) == ''){
	    	$("#userMobileId_error").html('请输入图片验证码，字母不区分大小写');
	        $("#userMobileId_error").show();
	        return false; 
	    }
	    
	    return true;
	} 
</script>
</head>

<body>
	<div class="contain">
		<div class="top_logo">
			<img src="<%=request.getContextPath()%>/static/images/logo.png" />
		</div>
		<div class="clr"></div>
		<div class="yd_tip">
			<span style="float: left;">移动话费支付交易订单</span>
			<a target="_blank" href="${kefu_online_url }" 
				<c:if test="${kefu_online_style == 'none'}">
					style="display: none"
				</c:if>
			>
				<span style="float: left; margin: 0px 0px 0px 160px;">
					<img width="30" height="35" src="<%=request.getContextPath() %>/static/images/kefu.gif" />
				</span>在线客服
			</a>
		</div>

		<div class="nr_cnt">
			<div class="cnt_left">
				<div class="dd_title">订单信息</div>
				<dl>
					<dt>
						<span>商品名称：</span>${goodsname }</dt>
					<dt>
						<span>话费支付价：</span><span class="red_cor">${amount/100 }元</span>
					</dt>
					<dt>
						<span>订单号：</span>${orderid }</dt>
					<dt>
						<span style="float: left; height: 13px; line-height: 13px;">商品描述：</span><span
							style="float: right; width: 160px; font-weight: normal;">${goodsinfo }</span>
					</dt>
					<div class="clr"></div>
					<dt>
						<span>商户客服：</span>${cusphone }</dt>
				</dl>
			</div>
			<div class="cnt_right">
				<div class="tab_name">话费支付</div>
				<div class="ctn_nr">
					<div class="ctn_nr_left">
						<form action="<%=request.getContextPath()%>/web/order.do" method="post" onsubmit="return checkForm();">
							<input type="hidden" name="model" value="${model }" /> 
							<input type="hidden" name="from" value="nom" />
							<dl>
								<dt class="tel_ipt2">
									<span>手机号码：</span>
									<input name="mobileid" id="mobileid" value="${mobileid }" type="text" size="11" maxlength="11"/>
								</dt>
								<dt>
									<em style="float: left;">
										<span>验证码：</span><input class="yzm_ipt" id="j_captcha_response" name="j_captcha_response" type="text"/>
									</em>
									<img style="cursor: pointer;" src="${pageContext.request.contextPath}/jcaptcha?now=<%=new Date().getTime()%>" alt="点击更换" onclick="this.src='${pageContext.request.contextPath}/jcaptcha?now=' + new Date().getTime()" class="yzm_img" />
								</dt>
							</dl>
							<div class="clr"></div>
							<em class="zs_font">字母不区分大小写，可点击图形码更换。</em>
							<p>
								<button type="submit" class="qrzf_btn2">&nbsp;提&nbsp;交&nbsp;订&nbsp;单&nbsp;</button>
							</p>
							<p id="userMobileId_error" class="zj_zs" style="display: none;">
								<c:choose>
									<c:when test="${jcaptchaError != null}">
										验证码不正确，请重新输入，字母不区分大小写
									</c:when>
									<c:otherwise>
										中国移动话费支付是中国移动电子商务的支付方式之一。
									</c:otherwise>
								</c:choose>
							</p>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${fn:startsWith(bankid, 'XE') }">
		<div class="clr"></div>
		<div class="wenxin_tip">
			<dl>
				<dt class="tip_title">
					<span>温馨提示：</span>
				</dt>
				<dt>1、您将收到10658008下发的验证码短信，支付客服电话4006125880。</dt>
				<dt>2、部分省份“赠送”、“返还”的话费不可用于订单支付。如：北京、黑龙江等。</dt>
				<dt>3、订单消费话费金额在部分省份不计入最低消费、或不纳入套餐和积分。如：福建、江苏等。</dt>
				<c:if test="${kefu_online_style != 'none'}">
					<dt>4、若支付过程中有疑问，可咨询在线客服。</dt>
				</c:if>
			</dl>
		</div>
		<div class="clr"></div>
	</c:if>
</body>
</html>
