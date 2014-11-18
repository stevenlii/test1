<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="./meta.jsp"%>
<%@ include file="./taglib.jsp"%>
<head>
<!-- 
<meta http-equiv="Refresh" content="3;url=${page_returl }"/>
 -->
<title>支付失败</title>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/static/css/upweb.css" />
</head>
<body>
	<div class="contain">
		<div class="top_logo">
			<img src="<%=request.getContextPath()%>/static/images/logo.png" />
		</div>
		<div class="clr"></div>
		<div class="yd_tip"><span style="float:left;">移动话费支付交易订单</span><a target="_blank" href="${kefu_online_url }" 
				<c:if test="${kefu_online_style == 'none'}">
					style="display: none"
				</c:if>
			><span style="float:left; margin:0px 0px 0px 160px;"><img width="30"  height="35" src="<%=request.getContextPath() %>/static/images/kefu.gif"/></span>在线客服</a></div>
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
			<div class="cnt_right2">
				<div class="dis_img">
					<img src="<%=request.getContextPath()%>/static/images/dis.png" alt="" style="float: left;" /><span>交易失败！</span>
				</div>
				<div class="dis_txt">
					请尝试重新进行订购！<br /> 
					${page_retmsg }<br></br>
					为了能尽快帮您解决问题，致电时请提供失败原因码[${retCode }]。
				</div>
				<div class="dis_btn">
					<a href="${page_returl }" class="lq_btn">&nbsp;返&nbsp;回&nbsp;商&nbsp;家&nbsp;</a>
				</div>
				<div class="tip_txt_ft">
					<!-- 
					<span>3秒后自动返回</span>
					 -->
				</div>
			</div>
		</div>
		<div class="clr"></div>
	</div>
</body>
</html>