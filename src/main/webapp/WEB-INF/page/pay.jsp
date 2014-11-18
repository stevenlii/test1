<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="./meta.jsp"%>
<%@ include file="./taglib.jsp"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="<%=request.getContextPath() %>/static/js/jquery-1.7.2.min.js"></script>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/static/css/upweb.css" />
<title>移动话费支付页面</title>
<script type="text/javascript">
	var sendFlag = false; // 是否点击了发送验证码按钮
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
		$('#verifycodeBtn').attr('class', 'yzm_hq_disabled');
		sendFlag = true;
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
								$('#confirmpayBtn').attr('class', 'qrzf_btn');
							}
						}
						else{
							$('#confirmpayBtn').attr('disabled', 'true');
							$('#confirmpayBtn').attr('class', 'qrzf_btn_disabled');
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
		if(!sendFlag){
			$("#userMobileId_error").html('您还未获取短信验证码。 请点击”获取短信验证码“按钮。');
			return false;
		}
		if($('#verifycode').val() == ''){
			$("#userMobileId_error").html('请输入手机收到的短信验证码');
			$('#verifycode')[0].focus();
			return false;
		}
		$('#confirmpayBtn').attr('disabled', 'true');
		$('#confirmpayBtn').attr('class', 'qrzf_btn_disabled');
		$("#userMobileId_error").html('正在提交支付请求，请耐心等待...');
		return true;
	}
	
</script>
</head>

<body>
	<input type="hidden" id="page_returl" value="${page_returl }"/>
	<div class="contain">
		<!-- 以后这块的logo需要根据gateid来进行显示    目前显示统一的   noted by fanxiangchi 20140929 -->
		<div class="top_logo">
			<img src="<%=request.getContextPath() %>/static/images/logo.png" />
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
		<div class="clr"></div>
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
						<form action="<%=request.getContextPath() %>/web/confirmPay.do" method="post" onsubmit="return checkForm();">
							<input type="hidden" name="model" id="model" value="${model }" />
							<input type="hidden" name="mobileid" id="mobileid" value="${mobileid }" />
							<input type="hidden" name="businesstype" id="businesstype" value="${businesstype }" />
							<dl>
								<dt class="tel_ipt">
									<span>手机号码：</span><font color="orange" size="5">${mobileid }</font>
								</dt>
								<dt>
									<em style="float: left;">
										<span>验证码：</span><input class="yzm_ipt" name="verifycode" id="verifycode" type="text" />
									</em>
									<input class="yzm_hq" id="verifycodeBtn" value="获取短信验证码" name="verifycodeBtn" type="button" onclick="getVerifyCode()" />
								</dt>
							</dl>
							<div class="clr"></div>
							<em class="zs_font">请输入短信收到的验证码，点击确认支付</em>
							<p>
								<button name="confirmpayBtn" id="confirmpayBtn" type="submit" class="qrzf_btn">&nbsp;确&nbsp;认&nbsp;支&nbsp;付&nbsp;</button>
							</p>
							<p id="userMobileId_error" class="zj_zs">
							</p>
						</form>
					</div>
					
					<c:choose>
						<c:when test="${fn:startsWith(bankid, 'XE') }"></c:when>
						<c:otherwise>
							<div class="ctn_nr_right">
								<div class="r_tit">
									<img style="float: left"
										src="<%=request.getContextPath() %>/static/images/icon_img.png" />
									<div class="r_tit_txt">支付提示：</div>
								</div>
								<div class="clr"></div>
								<div class="r_cnt">
									本订单通过联动优势的”${servicename }“业务代收，扣费完成后运营商会发送短信到您的手机收件箱。如有疑问，请致电4006125880<c:if test="${kefu_online_style != 'none'}">或咨询在线客服</c:if>。
								</div>
							</div>
						</c:otherwise>
					</c:choose>
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
	</c:if>
	<div class="clr"></div>
</body>
</html>
