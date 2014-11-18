<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="./meta.jsp"%>
<%@ include file="./taglib.jsp"%>
<head>
<c:if test="${isPhone == true}">
	<meta name="viewport" content="initial-scale=1.0,minimum-scale=1.0,maximum-scale=2.0,user-scalable=no" />
	<meta name="MobileOptimized" content="240" />
</c:if>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="<%=request.getContextPath() %>/static/js/jquery-1.7.2.min.js"></script>
<title>正在加签...</title>
</head>
<body onload="submitForm()">
	<div id="errorDiv">${error }</div>
	<div align="center">
		<form id="orderForm" action="<%=request.getContextPath() %>/mer/queryOrder.do" method="post">
			<input type="hidden" name="platType" value="${platType }"/>
			<input type="hidden" name="accountid" value="${accountid }"/>
			<input type="hidden" name="amttype" value="${amttype }"/>
			<input type="hidden" name="banktype" value="${banktype }"/>
			<input type="hidden" name="upversion" value="${upversion }"/>
			<input type="hidden" name="returl" value="${returl }"/>
			<input type="hidden" name="gateid" value="${gateid }"/>
			<input type="hidden" name="expand" value="${expand }"/>
			
			<table>
				<tr>
					<td align="right">商户号：</td>
					<td align="left"><input type="text" readonly="readonly" name="merid" id="merid" value="${merid }"></input></td>
				</tr>
				<tr>
					<td align="right">商品号：</td>
					<td align="left"><input readonly="readonly" type="text" name="goodsid" id="goodsid" value="${goodsid }"></input></td>
				</tr>
				<!--  
				<tr>
					<td align="right">商户名称：</td>
					<td align="left"><input readonly="readonly" type="text" name="mername" value="${mername }"></input></td>
				</tr>
				-->
				<tr>
					<td align="right">商品描述：</td>
					<td align="left"><input readonly="readonly" type="text" name="goodsinfo" value="${goodsinfo }"></input></td>
				</tr>
				<tr>
					<td align="right">商品金额：</td>
					<td align="left"><input readonly="readonly" type="text" name="amount" value="${amount }"></input></td>
				</tr>
				<tr>
					<td align="right">商户订单号：</td>
					<td align="left"><input readonly="readonly" type="text" name="orderid" id="orderid" value="${orderid }"></input></td>
				</tr>
				<tr>
					<td align="right">应用ID：</td>
					<td align="left"><input readonly="readonly" type="text" name="appid" value="${appid }"></input></td>
				</tr>
				<tr>
					<td align="right">渠道ID：</td>
					<td align="left"><input readonly="readonly" type="text" name="channelid" value="${channelid }"></input></td>
				</tr>
				<tr>
					<td align="right">订单日期：</td>
					<td align="left"><input readonly="readonly" type="text" name="orderdate" value="${orderdate }"></input></td>
				</tr>
				<tr>
					<td align="right">商户私有信息：</td>
					<td align="left"><input readonly="readonly" type="text" name="merpriv" value="${merpriv }"></input></td>
				</tr>
				<tr>
					<td align="right">手机号：</td>
					<td align="left"><input readonly="readonly" type="text" name="mobileid" value="${mobileid }"></input></td>
				</tr>
				<tr>
					<td align="right">sign：</td>
					<td align="left"><input readonly="readonly" type="text" name="sign" value="${sign }"></input></td>
				</tr>
				<tr>
					<td align="right">
						<button type="submit">提交</button>
					</td>
					<td>
						&nbsp;&nbsp;&nbsp;<a href="<%=request.getContextPath() %>/mertest/queryOrder.do">返回</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
<script type="text/javascript">
	function submitForm(){
		$('#orderForm')[0].submit();
	}
</script>
</body>
</html>