<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="../meta.jsp"%>
<%@ include file="../taglib.jsp"%>
<head>
<meta name="viewport" content="initial-scale=1.0,minimum-scale=1.0,maximum-scale=2.0,user-scalable=no"/>
<meta name="MobileOptimized" content="240"/>
<!--  
<meta http-equiv="Refresh" content="3;url=${page_returl }"/>
-->
<title>支付失败</title>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/static/css/upweb_phone.css" />
</head>

<body>
<div class="contain">
 <div class="ctn_title">话费支付</div>
  <div class="ctn_txt">
  
    <div class="dis_title"><img src="<%=request.getContextPath() %>/static/images/phone/dis.png" align="absmiddle"/><span>交易失败</span></div>
    <dl class="ctn_nr2">
	    <dt>
	    	请尝试重新进行订购！<br /> 
	    	${page_retmsg }<br></br>
	    </dt>
    	<dt>为了能尽快帮您解决问题，致电时请提供失败原因码[${retCode }]</dt>
    </dl>
  </div>
  
  <div class="btn" ><a href="${page_returl }" class="red_btn round">&nbsp;返&nbsp;回&nbsp;</a></div>
  
</div>
</body>
</html>
