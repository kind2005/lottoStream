<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>pollingTest</title>
<link rel="stylesheet" href="/webjars/bootstrap/3.3.7-1/css/bootstrap.css"></link>
<script type="text/javascript" src="/webjars/jquery/3.1.0/jquery.js"></script>
<script type="text/javascript" src="/webjars/jquery-ui/1.12.0/jquery-ui.js"></script>
<script type="text/javascript" src="/webjars/bootstrap/3.3.7-1/js/bootstrap.js"></script>
<script type="text/javascript">
var startFlag = true;
var pollCnt = 0;

(poll)();

function poll(){
	if(!startFlag) return false;
	$.ajax({
		url: "/orderbook"
		, data	: {
			currency : "ETH|BCH|XRP"
			, count : "2"
			, percent : "0.3"
		}
		, dataType: "json"
		, success: function(data){
			//console.log(data);
			for(var i=0; i<data.length; i++){
				var exchange = data[i].exchange;
				var currency = data[i].order_currency.toLowerCase();
				var bids = data[i].bids;
				var asks = data[i].asks;
				var insertTAG = "<ul class=\"list-group\" style=\"width:300px;\">";
				for(var j=asks.length-1; j>=0; j--){
					var notice = (asks[j].notice == "red" ? "list-group-item-danger" : asks[j].notice == "orange" ? "list-group-item-warning" : "");
					insertTAG += "<li class=\"list-group-item " + notice + "\"><span>" + asks[j].price + "</span><span class=\"badge badge-pill\">" + asks[j].quantity + "</span></li>";
				}
				insertTAG += "<br/>";
				for(var j=0; j<bids.length; j++){
					var notice = (bids[j].notice == "red" ? "list-group-item-danger" : bids[j].notice == "orange" ? "list-group-item-warning" : "");
					insertTAG += "<li class=\"list-group-item " + notice + "\"><span>" + bids[j].price + "</span><span class=\"badge badge-pill\">" + bids[j].quantity + "</span></li>";
				}
				insertTAG += "</ul>";
				$("." + exchange + "_" + currency).html(insertTAG);
			}
			$('#pollCnt').text(++pollCnt);
		}
		, complete: function(){setTimeout(poll, 3000);}
		, timeout: 4000
	});
}

function startToggle(){
	if($('#startBtn').attr("aria-pressed") == "true"){
		$('#startBtn').text("START");
		startFlag = false;
	}else{
		$('#startBtn').text("STOP");
		startFlag = true;
		poll();
	}
}
</script>
</head>
<body>
	<table class="table table-bordered">
		<colgroup>
			<col width="10%">
			<col width="30%">
			<col width="30%">
			<col width="30%">
		</colgroup>
		<thead>
			<tr>
				<th scope="col">
					<button type="button" id="startBtn" class="btn btn-primary active" data-toggle="button" aria-pressed="true" autocomplete="off" onclick="startToggle()">STOP</button>
					<span id="pollCnt">-</span>
				</th>
				<th scope="col">빗썸</th>
				<th scope="col">코인원</th>
				<th scope="col">코빗</th>
			</tr>
		</thead>
		<tbody>
			<tr id="eth">
				<th scope="row">ETH</th>
				<td class="bitthumb_eth">
					<ul class="list-group" style="width: 300px;">
						<li class="list-group-item "><span>test1</span><span class="badge badge-pill">3.24404638</span></li>
						<li class="list-group-item "><span>test2</span><span class="badge badge-pill">15.29020000</span></li>
						<br/>
						<li class="list-group-item "><span>테슷1</span><span class="badge badge-pill">0.00020000</span></li>
						<li class="list-group-item "><span>테슷2</span><span class="badge badge-pill">46.02400000</span></li>
					</ul>
				</td>
				<td class="coinone_eth">
					<ul class="list-group" style="width: 300px;">
						<li class="list-group-item "><span>test1</span><span class="badge badge-pill">3.24404638</span></li>
						<li class="list-group-item "><span>test2</span><span class="badge badge-pill">15.29020000</span></li>
						<br/>
						<li class="list-group-item "><span>테슷1</span><span class="badge badge-pill">0.00020000</span></li>
						<li class="list-group-item "><span>테슷2</span><span class="badge badge-pill">46.02400000</span></li>
					</ul>
				</td>
				<td class="korbit_eth">
					<ul class="list-group" style="width: 300px;">
						<li class="list-group-item "><span>test1</span><span class="badge badge-pill">3.24404638</span></li>
						<li class="list-group-item "><span>test2</span><span class="badge badge-pill">15.29020000</span></li>
						<br/>
						<li class="list-group-item "><span>테슷1</span><span class="badge badge-pill">0.00020000</span></li>
						<li class="list-group-item "><span>테슷2</span><span class="badge badge-pill">46.02400000</span></li>
					</ul>
				</td>
			</tr>
			<tr id="bch">
				<th scope="row">BCH</th>
				<td class="bitthumb_bch"></td>
				<td class="coinone_bch"></td>
				<td class="korbit_bch"></td>
			</tr>
			<tr id="xrp">
				<th scope="row">XRP</th>
				<td class="bitthumb_xrp"></td>
				<td class="coinone_xrp"></td>
				<td class="korbit_xrp"></td>
			</tr>
		</tbody>
	</table>
</body>
</html>