<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="../meta.jsp"%>
<%@ include file="../taglib.jsp"%>
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<meta name="viewport" content="initial-scale=1.0,minimum-scale=1.0,maximum-scale=2.0,user-scalable=no"/>
<meta name="MobileOptimized" content="240"/>
<meta http-equiv="Refresh" content="5;url=${page_returl }"/>
<title>支付成功</title>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath() %>/static/css/upweb_phone.css" />
</head>
<body>
<div class="contain">
 <div class="ctn_title">话费支付</div>
  <div class="ctn_txt">
  
    <div class="succes_title"><img src="<%=request.getContextPath() %>/static/images/phone/succe.png" align="absmiddle"><span>支付成功</span></img></div>
      <dl class="ctn_nr2">
      <dt>感谢您使用话费支付服务</dt>
      <dt>购买${goodsname }金额为<span class="cor_red">${amount/100 }元</span></dt>
      <dt>支付服务客服：4006125880</dt>
    </dl>
   </div>
  
  <div class="btn" ><a href="${page_returl }" class="red_btn round">&nbsp;返&nbsp;回&nbsp;</a></div>
  
</div>
</body>
</html>
