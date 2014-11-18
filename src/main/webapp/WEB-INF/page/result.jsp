<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="./meta.jsp"%>
<%@ include file="./taglib.jsp"%>
<head>
<meta http-equiv="Refresh" content="5;url=${page_returl }"/>
<title>支付成功</title>
<link type="text/css" rel="stylesheet"
	href="<%=request.getContextPath()%>/static/css/upweb.css" />
	<script type="text/javascript" src="<%=request.getContextPath() %>/static/js/jquery-1.7.2.min.js"></script>
</head>
<body>
	<div class="contain">
		<div class="top_logo">
			<img src="<%=request.getContextPath()%>/static/images/logo.png" />
		</div>
		<div class="clr"></div>
		<div class="yd_tip">移动话费支付交易订单</div>

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
			<div class="cnt_right2">
				<div class="suce_img">
					<img src="<%=request.getContextPath()%>/static/images/right.png" />交易成功！
				</div>
				<div class="suce_txt">
					感谢您使用联动优势提供的支付服务。<br />
					<c:choose>
						<c:when test="${fn:startsWith(bankid, 'XE') }">
							本订单通过话费支付，请注意查收支付结果短信。
						</c:when>
						<c:otherwise>
							本订单通过联动优势的”${servicename }“业务代收，请注意查收运营商发送的支付结果短信。
						</c:otherwise>
					</c:choose>
					如有疑问，请致电4006125880。
				</div>
				<div class="suce_btn">
					<a href="${page_returl }" class="lq_btn">&nbsp;返&nbsp;回&nbsp;商&nbsp;家&nbsp;</a>
				</div>
				<div class="tip_txt_ft">
					<span id="tip">页面将于5秒后自动返回</span>
				</div>
			</div>
		</div>
	</div>
	<div class="clr"></div>
<script type="text/javascript">
	var leftTime = 5;
	setInterval(function() {
		leftTime = leftTime - 1;
		if (leftTime < 1) {
			return true;
		}
		$("#tip").html('页面将于' + leftTime + '秒后自动返回');
	}, 1000);
</script>
</body>
</html>