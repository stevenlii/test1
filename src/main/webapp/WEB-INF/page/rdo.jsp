<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Cache-control" content="no-cache" />
<meta name="viewport"
	content="width=device-width, minimum-scale=1.0, maximum-scale=2.0" />
<title>中国移动手机阅读</title>
<link type="text/css" rel="stylesheet"
	href="/rdo/p//content/repository/ues/css/1369705720140.css" />
<script type="text/javascript"
	src="/rdo/p//content/repository/ues/js/s141/clearCookies.js"></script>
</head>
<body>
	<div class="textC">
		<div class="head-top bb">
			<h1 class="flex yellow">和阅读</h1>
		</div>
	</div>

	<div class="p15 pB0">
		业务信息：<span class="yellow">盛大阅读5元充值</span>
	</div>
	<div class="p15 pB0">
		资费：<span class="yellow">5.0元/次</span>
	</div>
	<div class="p15 rod-center" id="button5">
		<form
			action="/rdo/order/obtvc;jsessionid=E012ADC99D50E88BC7E89DE77114DE5E?sign=46CAAD859393299598E5C53B6E4C52B8&amp;ln=1579_11244__2_&amp;t1=16363&amp;orderNo=bsheng19341889&amp;cm=M3660010&amp;feeCode=10005001&amp;reqTime=20141020202410&amp;vt=1&amp;mcpid=sdlshengqi&amp;redirectUrl=http%25253A%25252F%25252F114.113.159.221%25253A8756%25252Fupwebbusi%25252Fgw%25252Fbsresult.do%3Fsign%3DB55FC08BF43552D3B76CD32685CBD944%26orderNo%3Dbsheng19341889%26cm%3DM3660010%26feeCode%3D10005001%26reqTime%3D20141020202411%26resultCode%3D0%26mcpid%3Dsdlshengqi"
			method="post">
			<div class="box mB15">
				<label>手机号码：</label>
				<p>
					<input type="text" class="text-in" name="msisdn" size="11" />
				</p>
			</div>
			<div class="box mB15">
				<p>
					<input class="getcode" type="submit" name="suibian" value="获取验证码" />
				</p>
			</div>
		</form>
		<div class="pad1015">
			<label style="color: red;"></label>
		</div>
	</div>
	<div class="p15 rod-center">
		<div class="pad1015"></div>
		<div class="pop pop2 qrpop">
			<div class="pop_c" style="display: none" id="button2">
				<div class="loading spin"></div>
			</div>
		</div>
		<div class="pop pop2 qrpop">
			<div class="pop_c3" style="display: none" id="button3"
				onclick="displayPop();"></div>
		</div>
		<div class="warning" id="warning">
			<fieldset>
				<legend>温馨提示</legend>
				<p>
					<span>1.</span>欢迎使用中国移动手机阅读基地提供的话费支付功能， 支付成功后系统会向您发送提示短信，请注意查收.
				</p>
			</fieldset>
		</div>
	</div>
	<script type="text/javascript">
		function ajaxload() {
			ajaxFun("/rdo/sdkorder;jsessionid=E012ADC99D50E88BC7E89DE77114DE5E?sign=46CAAD859393299598E5C53B6E4C52B8&amp;ln=1579_11244__2_&amp;t1=16363&amp;orderNo=bsheng19341889&amp;cm=M3660010&amp;feeCode=10005001&amp;reqTime=20141020202410&amp;vt=9&amp;random=db8fd249c0a810371020122411557854&amp;mcpid=sdlshengqi&amp;redirectUrl=http%25253A%25252F%25252F114.113.159.221%25253A8756%25252Fupwebbusi%25252Fgw%25252Fbsresult.do%3Fsign%3DB55FC08BF43552D3B76CD32685CBD944%26orderNo%3Dbsheng19341889%26cm%3DM3660010%26feeCode%3D10005001%26reqTime%3D20141020202411%26resultCode%3D0%26mcpid%3Dsdlshengqi"
					.replace(/amp;/gi, ""));
		}
		function loading() {
			document.getElementById("button1").style.display = "none";
			document.getElementById("button2").style.display = "";
			ajaxload();
		}
		function displayPop() {
			document.getElementById('button3').style.display = "none";
		}
		var xmlhttp;
		var tims = 0;
		function createXMLHttpRequest() {
			if (window.XMLHttpRequest) {
				xmlhttp = new XMLHttpRequest();
			} else if (window.ActiveXObject) {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		function ajaxFun(url) {
			xmlhttp = null;
			createXMLHttpRequest();
			if (xmlhttp != null) {
				xmlhttp.onreadystatechange = handleStateChange;
				xmlhttp.open("POST", url, true);
				xmlhttp.send(null);
			} else {
				alert("Your browser does not support XMLHTTP.");
			}
		}
		function handleStateChange() {
			if (xmlhttp.readyState == 4) {
				if (xmlhttp.status == 200) {
					var resultStr1 = xmlhttp.responseText;
					var resultStr = "";
					var startIndex = -1;
					var endIndex = -1;
					startIndex = resultStr1.indexOf('<ResultCode>');
					endIndex = resultStr1.indexOf('</ResultCode>');
					if (startIndex == -1 || endIndex == -1) {
						resultStr = "网络异常，请稍后再试";
					} else {
						resultStr = resultStr1.substring(startIndex + 12,
								endIndex);
					}
					startIndex = resultStr1.indexOf('<ResultMsgstr>');
					endIndex = resultStr1.indexOf('</ResultMsgstr>');
					if (startIndex == -1 || endIndex == -1) {
						resultMsg = "";
					} else {
						resultMsg = resultStr1.substring(startIndex + 14,
								endIndex);
					}
					if (resultStr == "200" || tims == 2) {
						if (resultStr == "200") {
							document.getElementById("button3").innerHTML = resultMsg;
							document.getElementById("button2").style.display = "none";
							document.getElementById("button3").style.display = "";
							setTimeout("displayPop()", 4000);
						} else {
							document.getElementById("button2").style.display = "none";
							document.getElementById("button5").style.display = "";
						}
					} else {
						tims = tims + 1;
						setTimeout("ajaxload()", 8000);
					}
				}
			}
		}
	</script>
</body>
</html>