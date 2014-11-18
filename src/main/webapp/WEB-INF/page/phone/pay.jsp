<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="../meta.jsp"%>
<%@ include file="../taglib.jsp"%>
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<meta name="viewport"
	content="initial-scale=1.0,minimum-scale=1.0,maximum-scale=2.0,user-scalable=no" />
<meta name="MobileOptimized" content="240" />
<title>手机验证码支付</title>
<script type="text/javascript" src="<%=request.getContextPath() %>/static/js/jquery-1.7.2.min.js"></script>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/static/css/upweb_phone.css" />
<script type="text/javascript">
function getVerifyCode(){
	//$("#userMobileId_error").html('正在请求获取短信验证码');
	var leftTime = 61;
	var serverResult = false;
	setInterval(function() {
		if(!serverResult){
 			leftTime = leftTime - 1;
 			if (leftTime < 1) {
 				$("#userMobileId_error").html('请求获取短信验证码失败，请返回重试。');
 				return true;
 			}
 			$("#userMobileId_error").html('正在请求获取短信验证码(' + leftTime + ')');
		}
		}, 1000);
	
	
	$('#verifycodeBtn').attr('disabled', 'true');
	$('#verifycodeBtn').attr('class', 'yzm_btn_disabled');
	$.ajax({
		url : '<%=request.getContextPath() %>/web/pay.do',
		type : 'POST',
		dataType : 'json',
		data : {'model' : $('#model').val(),
				'mobileid' :  $('#mobileid').val()
			},
		success : function(data){
					serverResult = true;
					var retCode = data.retCode;
					if(retCode == '0000'){
						$("#userMobileId_error").html('验证码短信已发送，请注意查收。若没有收到，请返回商户页面重新下单。');
						$('#model').val(data.model);
						$('#verifycode')[0].focus();
						if(data.businesstype == '0601'){
							$('#verifycodeBtn').attr('class', 'yzm_hq_disabled');
							
							$('#confirmpayBtn').removeAttr('disabled');
							$('#confirmpayBtn').attr('class', 'red_btn');
						}
					}
					else{
						$('#confirmpayBtn').attr('disabled', 'true');
						$('#confirmpayBtn').attr('class', 'red_btn_disabled');
						var retMsg = data.page_retmsg;
						if(!retMsg || retMsg == ''){
							retMsg = '很抱歉, 短信验证码下发失败，请返回商户页面重新下单。';
						}
				 		var leftTime = 5;
				 		$("#userMobileId_error").html('[' + retCode + "]" + retMsg + '&nbsp;&nbsp;&nbsp;页面将于' + leftTime + '秒后返回');
				 		setInterval(function() {
				 			leftTime = leftTime - 1;
				 			if (leftTime < 1) {
				 				window.location.href = data.page_returl;
				 				return true;
				 			}
				 			$("#userMobileId_error").html('[' + retCode + "]" + retMsg + '&nbsp;&nbsp;&nbsp;页面将于' + leftTime + '秒后返回');
				 		}, 1000);
					}
		},
		error : function(data){
				$('#verifycodeBtn').removeAttr('disabled');
				$('#verifycodeBtn').attr('class', 'yzm_hq');
				var leftTime = 4;
				$("#userMobileId_error").html('很抱歉, 短信验证码下发失败，请返回商户页面重新下单。');
			}
	});		
}

function checkForm(){
	if($('#verifycode').val() == ''){
		$("#userMobileId_error").html('请输入手机收到的短信验证码');
		$('#verifycode')[0].focus();
		return false;
	}
	$('#confirmpayBtn').attr('disabled', 'true');
	$('#confirmpayBtn').attr('class', 'red_btn_disabled');
	$("#userMobileId_error").html('正在提交支付请求，请耐心等待...');
	return true;
}
</script>
</head>

<body>
	<div class="contain">
		<div class="ctn_title">话费支付</div>
		<form action="<%=request.getContextPath() %>/web/confirmPay.do" method="post" onsubmit="return checkForm();">
			<input type="hidden" name="model" id="model" value="${model }" />
			<input type="hidden" name="mobileid" id="mobileid" value="${mobileid }" />
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
					<dt >
						<span style="FLOAT:LEFT;"  class="sty_tit">商品描述：</span><span style="FLOAT:LEFT; WIDTH:55%; COLOR:#8F8F8F; ;">${goodsinfo }</span>
					</dt>
					<div class="clr"></div>
					<dt>
						<span class="sty_tit">客服电话：</span>${cusphone }
					</dt>
					<dt style="border-bottom: 1px solid #d3d3d3; margin: 10px 0px;"></dt>
					<dt  >
						<span class="name_wid">支付手机：</span>
						<span class="pay_ssh"><font color="orange" size="5">${mobileid }</font></span>
					</dt>
					<div class="clr"></div>
					<dt style="margin: 10px 0px 0px 0px; ">
						<span class="name_wid">验证码：</span>
						<input class="yzm_sty" name="verifycode" id="verifycode" type="text" />
						<button class="yzm_btn" id="verifycodeBtn" onclick="getVerifyCode()" type="button">获取验证码</button>
					</dt>
					<div class="clr" ></div>
				</dl>
			</div>
			<div class="btn">
				<button type="submit" id="confirmpayBtn" class="red_btn">&nbsp;确&nbsp;认&nbsp;支&nbsp;付&nbsp;</button>
			</div>
			<div id="userMobileId_error" class="zs_font"></div>
			<c:choose>
				<c:when test="${fn:startsWith(bankid, 'XE') }"></c:when>
				<c:otherwise>
					<div class="ts_ctn">
				<span><img style="float: left; margin: 0px 5px 0px 0px" src="<%=request.getContextPath() %>/static/images/phone/icon_img.png"/>支付提示：本订单通过联动优势的“${servicename }”业务代收，扣费完成后运营商会发送短信到您的手机收件箱。如有疑问，请致电4006125880。</span>
			</div>
				</c:otherwise>
			</c:choose>
		</form>
		<div class="clr"></div>
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
