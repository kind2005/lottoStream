package com.example.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Order;
import com.example.model.OrderBookVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RestController
public class CoinRestController {
	static int limitCnt = 2;
	static Map<String, OrderBookVO> prevOrderBookMap = Collections.emptyMap();

	@RequestMapping("/orderbook")
	public String orderbook(String currency, @RequestParam(defaultValue="2") int count, @RequestParam(defaultValue="0.4D") double percent) throws Exception{
		System.out.println("========== orderbook ==========");
		if(currency == null || "".equals(currency)) currency = "BTC|ETH|DASH|LTC|ETC|XRP|BCH|XMR|ZEC|QTUM";
		String[] currencys = currency.split("\\W");
		limitCnt = count;
		double limitPercent = percent;

		List<OrderBookVO> orderBookList = new ArrayList<OrderBookVO>();
		Map<String, OrderBookVO> bitthumbOrderbookMap = null;
		if(currencys.length == 1){
			bitthumbOrderbookMap = bitthumbOrderbook(currency);
		}else{
			bitthumbOrderbookMap = bitthumbOrderbook("ALL");
			List<String> paramNameList = Arrays.asList(currencys);
			Map<String, OrderBookVO> m = Collections.synchronizedMap(bitthumbOrderbookMap);
			Set<Entry<String, OrderBookVO>> entrySet = m.entrySet();
			synchronized (m) {
				Iterator<Entry<String, OrderBookVO>> entryIter = entrySet.iterator();
				while(entryIter.hasNext()){
					Entry<String, OrderBookVO> entry = entryIter.next();
					if(!paramNameList.contains(entry.getKey())){
						entryIter.remove();
					}
				}
			}
		}

		Map<String, OrderBookVO> korbitOrderbookMap = new HashMap<String, OrderBookVO>();
		Map<String, OrderBookVO> coinoneOrderbookMap = new HashMap<String, OrderBookVO>();
		for(String currencyName : currencys){
			korbitOrderbookMap.put(currencyName, korbitOrderbook(currencyName));
			coinoneOrderbookMap.put(currencyName, coinoneOrderbook(currencyName));
		}

		for(String currencyName : currencys){
			OrderBookVO ob1 = bitthumbOrderbookMap.get(currencyName);
			OrderBookVO ob2 = korbitOrderbookMap.get(currencyName);
			OrderBookVO ob3 = coinoneOrderbookMap.get(currencyName);

			List<Order> bidList = new ArrayList<Order>();
			if(ob1 != null) bidList.addAll(ob1.getBids());
			if(ob2 != null) bidList.addAll(ob2.getBids());
			if(ob3 != null) bidList.addAll(ob3.getBids());
			double maxPrice = 0D;
			Order maxOrder = null;
			for(Order o : bidList){
				double price = Double.parseDouble(o.getPrice());
				if(price > maxPrice){
					maxPrice = price;
					if(maxPrice != 0D)
						maxOrder = o;
				}
			}

			List<Order> askList = new ArrayList<Order>();
			if(ob1 != null) askList.addAll(ob1.getAsks());
			if(ob2 != null) askList.addAll(ob2.getAsks());
			if(ob3 != null) askList.addAll(ob3.getAsks());
			double minPrice = 0D;
			Order minOrder = null;
			for(Order o : askList){
				double price = Double.parseDouble(o.getPrice());
				if(price < minPrice){
					minPrice = price;
					if(minPrice != 0D)
						minOrder = o;
				}

				if(price < maxPrice){
					double profit = ((price + maxPrice) / 2) * (limitPercent / 100);
					if((maxPrice - price) > profit){
						o.setNotice("orange");
						maxOrder.setNotice("red");
					}
				}
			}

			for(Order o : bidList){
				double price = Double.parseDouble(o.getPrice());
				if(price > minPrice){
					double profit = ((price + minPrice) / 2) * (limitPercent / 100);
					if((price - maxPrice) > profit){
						o.setNotice("orange");
						minOrder.setNotice("red");
					}
				}
			}
		}

		orderBookList.addAll(bitthumbOrderbookMap.values());
		orderBookList.addAll(korbitOrderbookMap.values());
		orderBookList.addAll(coinoneOrderbookMap.values());

		//내용이 바뀌면 static 변수에 저장
		boolean changeFlag = false;
		Map<String, OrderBookVO> currOrderBookMap = new HashMap<String, OrderBookVO>();
		for(OrderBookVO vo : orderBookList){
			String key = vo.getExchange() + "_" + vo.getOrder_currency();
			currOrderBookMap.put(key, vo);
			if(!vo.equals(prevOrderBookMap.get(key))){
				changeFlag = true;
			}
		}
		if(changeFlag){
			prevOrderBookMap = currOrderBookMap;
		}

		Gson gson = new Gson();
		return gson.toJson(orderBookList);
	}

	private Map<String, OrderBookVO> bitthumbOrderbook(String currency) throws Exception{
		String apiURL = "https://api.bithumb.com";
		System.out.println("@@@ connect : " + apiURL);
		List<String> currencyNames = Arrays.asList(new String[]{ "BTC", "ETH", "DASH", "LTC", "ETC", "XRP", "BCH", "XMR", "ZEC", "QTUM" });
		if(!currencyNames.contains(currency)){
			currency = "ALL";
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("count", String.valueOf(limitCnt));
		paramMap.put("group_orders", "1");
		String result = "";
		try {
			result = urlConnection(apiURL + "/public/orderbook/" + currency, paramMap, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return Collections.emptyMap();
		}

		/*
		BCH = {"status":"0000","data":{"timestamp":"1508912560716","payment_currency":"KRW","order_currency":"BCH","bids":[{"quantity":"32.07830000","price":"376300"},{"quantity":"0.12100000","price":"376200"}],"asks":[{"quantity":"25.64010000","price":"376700"},{"quantity":"41.11000000","price":"376800"}]}}
		ALL = {"status":"0000","data":{"timestamp":"1508739813513","payment_currency":"KRW","BTC":{"order_currency":"BTC","bids":[{"quantity":"0.05030000","price":"6945000"},{"quantity":"7.11440000","price":"6943000"}],"asks":[{"quantity":"9.68290000","price":"6949000"},{"quantity":"12.79631942","price":"6950000"}]},"ETH":{"order_currency":"ETH","bids":[{"quantity":"2.62790000","price":"338250"},{"quantity":"18.02350000","price":"338200"}],"asks":[{"quantity":"13.70060000","price":"338700"},{"quantity":"9.41988266","price":"338750"}]},"DASH":{"order_currency":"DASH","bids":[{"quantity":"1.09850000","price":"317000"},{"quantity":"41.00000000","price":"316900"}],"asks":[{"quantity":"2.20000000","price":"317400"},{"quantity":"3.36390000","price":"317450"}]},"LTC":{"order_currency":"LTC","bids":[{"quantity":"0.10000000","price":"64260"},{"quantity":"16.93900000","price":"64250"}],"asks":[{"quantity":"77.70000000","price":"64340"},{"quantity":"26.47540000","price":"64350"}]},"ETC":{"order_currency":"ETC","bids":[{"quantity":"20.53630000","price":"12270"},{"quantity":"101.50000000","price":"12265"}],"asks":[{"quantity":"0.00007557","price":"12280"},{"quantity":"0.99850000","price":"12285"}]},"XRP":{"order_currency":"XRP","bids":[{"quantity":"5589765.32930000","price":"230"},{"quantity":"1458372.04220000","price":"229"}],"asks":[{"quantity":"640077.51803100","price":"231"},{"quantity":"2282762.76330400","price":"232"}]},"BCH":{"order_currency":"BCH","bids":[{"quantity":"39.31930000","price":"380200"},{"quantity":"53.50280000","price":"380100"}],"asks":[{"quantity":"15.77986519","price":"380400"},{"quantity":"143.53500000","price":"380500"}]},"XMR":{"order_currency":"XMR","bids":[{"quantity":"71.12740000","price":"100220"},{"quantity":"16.42110000","price":"100210"}],"asks":[{"quantity":"2.45350000","price":"100390"},{"quantity":"1.00000000","price":"100400"}]},"ZEC":{"order_currency":"ZEC","bids":[{"quantity":"19.97250000","price":"243750"},{"quantity":"10.00000000","price":"243400"}],"asks":[{"quantity":"0.28910000","price":"243900"},{"quantity":"21.99400000","price":"244550"}]},"QTUM":{"order_currency":"QTUM","bids":[{"quantity":"345.83050000","price":"12065"},{"quantity":"384.60830000","price":"12060"}],"asks":[{"quantity":"236.18808701","price":"12100"},{"quantity":"320.12698951","price":"12105"}]}}}
		*/

		/*GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Map.class, new JsonDeserializer<Map>() {
			@Override
			public Map deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				JsonObject obj = json.getAsJsonObject();
				Entry entry = obj.entrySet().iterator().next();
				HashMap resultMap = new HashMap();
				resultMap.put(entry.getKey(), entry.getValue());
				return resultMap;
			}
		});
		Gson gson = builder.create();*/
		Gson gson = new Gson();

		JsonObject root = gson.fromJson(result, JsonObject.class);
		String status = root.get("status").getAsString();
		Map<String, Object> data = gson.fromJson(root.get("data").toString(), Map.class);
		String timestamp = data.get("timestamp").toString();
		String paymentCurrency = (String)data.get("payment_currency");

		Map<String, OrderBookVO> orderBookMap = new HashMap<String, OrderBookVO>();
		if(!currencyNames.contains(currency)){	//ALL
			for(String currencyName : currencyNames){
				OrderBookVO orderBook = gson.fromJson(data.get(currencyName).toString(), OrderBookVO.class);
				orderBook.setExchange("bitthumb");
				orderBook.setTimestamp(timestamp);
				orderBook.setPayment_currency(paymentCurrency);

				orderBookMap.put(currencyName, orderBook);
			}
		}else{
			OrderBookVO orderBook = gson.fromJson(data.toString(), OrderBookVO.class);
			orderBook.setExchange("bitthumb");
			orderBookMap.put(currency, orderBook);
		}

		System.out.println(orderBookMap);

		return orderBookMap;
	}

	private OrderBookVO korbitOrderbook(String currency) throws Exception{
		String apiURL = "https://api.korbit.co.kr";
		System.out.println("@@@ connect : " + apiURL);
		List<String> currencyNames = Arrays.asList(new String[]{ "BTC", "ETH", "ETC", "XRP", "BCH" });
		if(!currencyNames.contains(currency)){
			System.out.println(currency + " currency is not available");
			return null;
		}
		String currency_pair = currency.toLowerCase() + "_krw";

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("currency_pair", currency_pair);
		String result = "";
		try {
			result = urlConnection(apiURL + "/v1/orderbook", paramMap, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		/*
		bch_krw = {"timestamp":1508917461331,"bids":[["378500","42.58200271","1"],["378000","29","1"],["377500","48","1"],["377000","86.60178778","1"],["376500","23.81225973","1"],["376000","43.28835903","1"],["375500","239","1"],["375000","80.36979731","1"],["374500","109.52002134","1"],["374000","89.71165507","1"],["373500","58.46799196","1"],["373000","131.66806432","1"],["372500","61.57378791","1"],["372000","181.78711827","1"],["371000","55","1"],["370500","1.01591363","1"],["370000","492.88733142","1"],["369500","240.93099201","1"],["369000","136.2199783","1"],["368500","80.57912075","1"],["368000","140.56673792","1"],["367500","15.19911836","1"],["367000","54.95121524","1"],["366500","7.99210095","1"],["366000","60.47849452","1"],["365500","69.26320109","1"],["365000","175.4359305","1"],["364500","0.1","1"],["364000","167.08407415","1"],["363500","30","1"]],"asks":[["379000","20.1","1"],["379500","58.05552093","1"],["380000","154.59217684","1"],["380500","33.11571845","1"],["381000","32.21730316","1"],["381500","214.81827995","1"],["382000","84.61661513","1"],["382500","35.08127502","1"],["383000","53.14809782","1"],["383500","50.28373845","1"],["384000","26.552","1"],["384500","18.90146917","1"],["385000","72.74549541","1"],["385500","23.10700514","1"],["386000","34.25591777","1"],["386500","20","1"],["387000","128.76184679","1"],["387500","10.85863804","1"],["388000","323.2058879","1"],["388500","239.20495377","1"],["389000","349.09065679","1"],["389500","172.9485591","1"],["390000","892.34125712","1"],["391000","107.04140026","1"],["391500","45.35323243","1"],["392000","127.07649617","1"],["392500","13.99350015","1"],["393000","374.27521464","1"],["393500","12.75641395","1"],["394000","110.84652224","1"]]}
		 */

		Gson gson = new Gson();

		JsonObject root = gson.fromJson(result, JsonObject.class);
		String timestamp = root.get("timestamp").getAsString();
		List<List<Object>> bidArrList = gson.fromJson(root.get("bids").toString(), List.class);		//내림차순
		List<List<Object>> askArrList = gson.fromJson(root.get("asks").toString(), List.class);	//오름차순

		OrderBookVO orderBook = new OrderBookVO();
		orderBook.setExchange("korbit");
		orderBook.setTimestamp(timestamp);
		orderBook.setPayment_currency("KRW");
		orderBook.setOrder_currency(currency);

		List<Order> bidOrderList = new ArrayList<Order>();
		if(bidArrList != null && !bidArrList.isEmpty()){
			for(int i=0; i<limitCnt; i++){
				List<Object> bidArr = bidArrList.get(i);
				Order order = new Order(bidArr.get(0).toString(), bidArr.get(1).toString());
				bidOrderList.add(order);
			}
		}
		orderBook.setBids(bidOrderList);

		List<Order> askOrderList = new ArrayList<Order>();
		if(askArrList != null && !askArrList.isEmpty()){
			for(int i=0; i<limitCnt; i++){
				List<Object> askArr = askArrList.get(i);
				Order order = new Order(askArr.get(0).toString(), askArr.get(1).toString());
				askOrderList.add(order);
			}
		}
		orderBook.setAsks(askOrderList);

		System.out.println(orderBook.toString());

		return orderBook;
	}

	private OrderBookVO coinoneOrderbook(String currency) throws Exception{
		String apiURL = "https://api.coinone.co.kr";
		System.out.println("@@@ connect : " + apiURL);
		List<String> currencyNames = Arrays.asList(new String[]{ "BTC", "BCH", "ETH", "ETC", "XRP", "QTUM" });
		if(!currencyNames.contains(currency)){
			System.out.println(currency + " currency is not available");
			return null;
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("currency", currency.toLowerCase());
		paramMap.put("format", "json");
		String result = "";
		try {
			result = urlConnection(apiURL + "/orderbook", paramMap, null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		/*

{
    "timestamp": "1509072011",
    "bid": [
    	.......
        {
            "price": "367500",
            "qty": "5.3508"
        },
        {
            "price": "367200",
            "qty": "1.0000"
        }
    ],
    "errorCode": "0",
    "currency": "bch",
    "result": "success",
    "ask": [
        {
            "price": "394800",
            "qty": "10.0000"
        },
        {
            "price": "394900",
            "qty": "28.4135"
        },
        .......
    ]
}
		 */

		Gson gson = new Gson();

		JsonObject root = gson.fromJson(result, JsonObject.class);
		String timestamp = root.get("timestamp").getAsString();
		String errorCode = root.get("errorCode").getAsString();
		String currencyValue = root.get("currency").getAsString();
		String resultValue = root.get("result").getAsString();
		List<Map> bidMapList = gson.fromJson(root.get("bid").toString(), List.class);		//내림차순
		List<Map> askMapList = gson.fromJson(root.get("ask").toString(), List.class);		//오름차순

		OrderBookVO orderBook = new OrderBookVO();
		orderBook.setExchange("coinone");
		orderBook.setTimestamp(timestamp);
		orderBook.setPayment_currency("KRW");
		orderBook.setOrder_currency(currency);

		List<Order> bidOrderList = new ArrayList<Order>();
		if(bidMapList != null && !bidMapList.isEmpty()){
			for(int i=0; i<limitCnt; i++){
				Map<String, String> bidMap = bidMapList.get(i);
				Order order = new Order(bidMap.get("price"), bidMap.get("qty"));
				bidOrderList.add(order);
			}
		}
		orderBook.setBids(bidOrderList);

		List<Order> askOrderList = new ArrayList<Order>();
		if(askMapList != null && !askMapList.isEmpty()){
			for(int i=0; i<limitCnt; i++){
				Map<String, String> askMap = askMapList.get(i);
				Order order = new Order(askMap.get("price"), askMap.get("qty"));
				askOrderList.add(order);
			}
		}
		orderBook.setAsks(askOrderList);

		System.out.println(orderBook.toString());

		return orderBook;
	}

	private String urlConnection(String urlStr, Map<String, String> paramMap, Map<String, String> headerMap) throws IOException {
		final int CONN_TIMEOUT = 3;
		final int READ_TIMEOUT = 3;
		String method = "GET";

		StringBuffer paramBuffer = new StringBuffer("");
		for(Entry<String, String> entry : paramMap.entrySet()){
			paramBuffer.append("&");
			paramBuffer.append(entry.getKey());
			paramBuffer.append("=");
			paramBuffer.append(entry.getValue());
		}
		String params = paramBuffer.toString();
		if(!urlStr.contains("?")) params = params.replaceFirst("&", "?");

		if(method.equals("GET")){
			urlStr += params;
		}
		System.out.println(urlStr);

		// HttpURLConnection 객체 생성.
		HttpURLConnection conn = null;

		URL url = new URL(urlStr);

		// URL 연결 (웹페이지 URL 연결.)
		conn = (HttpURLConnection)url.openConnection();

		// TimeOut 시간 (서버 접속시 연결 시간)
		conn.setConnectTimeout(CONN_TIMEOUT * 1000);

		// TimeOut 시간 (Read시 연결 시간)
		conn.setReadTimeout(READ_TIMEOUT * 1000);

		// Request Header값 셋팅 setRequestProperty(String key, String value)
//		conn.setRequestProperty("NAME", "name");
//		conn.setRequestProperty("MDN", "mdn");
//		conn.setRequestProperty("APPID", "appid");

		// 서버 Response Data를 JSON 형식의 타입으로 요청.
		conn.setRequestProperty("Accept", "application/json");

		// 타입설정(application/json) 형식으로 전송 (Request Body 전달시 application/json로 서버에 전달.)
		conn.setRequestProperty("Content-Type", "application/json");

		// 컨트롤 캐쉬 설정
		conn.setRequestProperty("Cache-Control","no-cache");

		// 타입길이 설정(Request Body 전달시 Data Type의 길이를 정함.)
		conn.setRequestProperty("Content-Length", "0");

		// User-Agent 값 설정
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

		if(method.equals("POST")){
			// 요청 방식 선택 (GET, POST)
			conn.setRequestMethod("POST");

			// OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
//			conn.setDoOutput(true);

			// Request Body에 Data를 담기위해 OutputStream 객체를 생성.
//			OutputStream os = conn.getOutputStream();

			// Request Body에 Data 셋팅.
//			os.write(params.getBytes("UTF-8"));

			// Request Body에 Data 입력.
//			os.flush();

			// OutputStream 종료.
//			os.close();

		}else{
			// 요청 방식 선택 (GET, POST)
			conn.setRequestMethod("GET");
		}

		// InputStream으로 서버로 부터 응답을 받겠다는 옵션.
		conn.setDoInput(true);

		// 실제 서버로 Request 요청 하는 부분. (응답 코드를 받는다. 200 성공, 나머지 에러)
		int responseCode = conn.getResponseCode();

		StringBuffer sb = new StringBuffer();
		if(responseCode == HttpURLConnection.HTTP_OK){
			InputStream is = conn.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			while((line = in.readLine()) != null){
				if(sb.length() != 0) sb.append("\n");
				sb.append(line);
			}

			System.out.println("json: " + sb);
		}

        System.out.println("Response: " + conn.getResponseCode() + " " + conn.getResponseMessage());

		// 접속해지
		conn.disconnect();

		return sb.toString();
	}

}
