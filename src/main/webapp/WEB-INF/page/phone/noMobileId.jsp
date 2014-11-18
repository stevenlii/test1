<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="../meta.jsp"%>
<%@ include file="../taglib.jsp"%>
<%@ page import='java.util.Date'%>
<head>
<meta name="viewport" content="initial-scale=1.0,minimum-scale=1.0,maximum-scale=2.0,user-scalable=no" />
<meta name="MobileOptimized" content="240" />
<title>请输入手机号</title>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/static/css/upweb_phone.css" />
<script type="text/javascript" src="<%=request.getContextPath() %>/static/js/jquery-1.7.2.min.js"></script>

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
		<div class="ctn_title">话费支付</div>
		<form action="<%=request.getContextPath()%>/web/order.do" method="post" onsubmit="return checkForm();">
			<input type="hidden" name="model" value="${model }" /> 
			<input type="hidden" name="from" value="nom" />
			<div class="ctn_txt">
					<dl class="ctn_nr">
						<dt>
							<span class="sty_tit">商品名称：</span>${goodsname }
						</dt>
						<dt>
							<span class="sty_tit">话费支付价：</span><span class="cor_red">${amount/100 }元</span>
						</dt>
						<dt>
							<span class="sty_tit">订单号：</span>${orderid }
						</dt>
						<dt>
							<span style="FLOAT:LEFT;"  class="sty_tit">商品描述：</span><span style="FLOAT:LEFT; WIDTH:55%; COLOR:#8F8F8F; ;">${goodsinfo }</span>
						</dt>
						<div class="clr"></div>
						<dt>
							<span class="sty_tit">客服电话：</span>${cusphone }
						</dt>
						<dt style="border-bottom: 1px solid #d3d3d3; margin: 15px 0px;"></dt>
						<dt>
							<span class="name_wid">支付手机：</span>
							<input name="mobileid" id="mobileid"  class="tel_sty" type="text" size="11" maxlength="11" value="${mobileid }"/>
						</dt>
						<div class="clr"></div>
						<dt style="margin: 15px 0px 0px 0px;">
							<span class="name_wid">验证码：</span>
							<input  class="yzm_sty" id="j_captcha_response" name="j_captcha_response" type="text" />
							<img style="cursor: pointer; margin:0px 0px 0px 10px;" src="${pageContext.request.contextPath}/jcaptcha?now=<%=new Date().getTime()%>" alt="点击更换" onclick="this.src='${pageContext.request.contextPath}/jcaptcha?now=' + new Date().getTime()" />
						</dt>
						<div class="clr"></div>
					</dl>
				
			</div>
			<div class="btn">
				<button type="submit" class="red_btn">&nbsp;提&nbsp;交&nbsp;订&nbsp;单</button>
			</div>
			<c:if test="${fn:startsWith(bankid, 'XE') }">
				<div class="ts_ctn">
					<span>
						<img style="float: left; margin: 0px 5px 0px 0px" src="${pageContext.request.contextPath}/static/images/phone/icon_img.png"/>
						支付提示：本订单通过联动优势的${servicename }业务代收，扣费完成后运营商会发送短信到您的收件箱。如有疑问，请致电4006125880
					</span>
				</div>
			</c:if>
		</form>
		<div id='userMobileId_error' class="zs_font" style="display: none;">
			<c:choose>
				<c:when test="${jcaptchaError != null}">
					验证码不正确，请重新输入，字母不区分大小写
				</c:when>
				<c:otherwise>
					请输入中国移动手机号码
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${fn:startsWith(bankid, 'XE') }">
			<div class="wxts_sty">
				<dl>
					<dt>温馨提示：</dt>
					<dt>1、您将收到10658008下发的验证码短信，支付客服电话4006125880。</dt>
					<dt>2、部分省份“赠送”、“返还”的话费不可用于订单支付。如：北京、黑龙江等。</dt>
					<dt>3、订单消费话费金额在部分省份不计入最低消费、或不纳入套餐和积分。如：福建、江苏等。</dt>
				</dl>
			</div>
		</c:if>
	</div>
</body>
</html>
