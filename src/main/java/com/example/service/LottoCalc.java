package com.example.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.model.LottoParamVO;
import com.example.model.LottoVO;
import com.example.model.ResultListVO;

public class LottoCalc {
	static int startDrwNo = 1;	//시작회차
	static int endDrwNo = 0;	//종료회차
	static boolean bonusNoFlag = true;	//보너스번호 유무
	static int exceptCount = 7;
	static List<LottoVO> rottoList = LottoDataReader.initLottoData();

	public static void main(String[] args) {
		List<Integer> scanNoList = new ArrayList<>();	//선택번호
		List<Integer> exceptNoList = Collections.emptyList();	//제외번호
		List<Integer> resultList = new ArrayList<>();	//결과번호

		String drwDateStr = rottoList.get(0).getDrwNoDate();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
			Date drwDate = sdf.parse(drwDateStr + " 21:00");
			boolean newFlag = drwDate.before(new Date(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)));
			if(newFlag){
				rottoList = LottoDataReader.initLottoData();
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		//기간 시작/종료(no, 날짜) + 보너스
		//포함 most, not
		//제외번호
		//최신
		//횟수 상하위

		//홀/짝
		//번호대


		//회차별 당첨번호
		System.out.println("========= 회차별 당첨번호 =========");
		rottoList.stream().filter(drwNoFilter)
				.forEach(r -> {
					System.out.print(
							"회차 : " + r.getDrwNo() + "\t\t"
							+ "발표일 : " + r.getDrwNoDate() + "\t\t"
							+ "번호 : " + r.getDrwtNo1() + "\t" + r.getDrwtNo2() + "\t" + r.getDrwtNo3() + "\t" + r.getDrwtNo4() + "\t" + r.getDrwtNo5() + "\t" + r.getDrwtNo6()
							+ (bonusNoFlag ? "\t + " + r.getBnusNo() : "") + "\t\t");

					List<Integer> list = new ArrayList(Arrays.asList(r.getDrwtNo1(), r.getDrwtNo2(), r.getDrwtNo3(), r.getDrwtNo4(), r.getDrwtNo5(), r.getDrwtNo6()));
					if(bonusNoFlag){
						list.add(r.getBnusNo());
					}
					System.out.print("합계 : " + list.stream().collect(Collectors.summingInt(Integer::intValue)) + "\t\t");

					Map<Boolean, Integer> oddMap = list.stream().collect(Collectors.groupingBy(o -> o%2==1, Collectors.summingInt(m -> 1)));
					System.out.println("홀/짝 : " + oddMap.getOrDefault(true, 0) + " / " + oddMap.getOrDefault(false, 0));
				});

		//번호별 당첨횟수
		System.out.println("========= 번호별 당첨횟수 =========");
		Stream<Integer> numStream = rottoList.stream().filter(drwNoFilter)
				.map(dwrtNoArrayFunc)
				.flatMap(e -> e.getValue().stream());

		Map<Integer, Long> sumByNum = numStream
					.collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));

		sumByNum.entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry<Integer, Long>::getValue).reversed())
				.forEach(System.out::println);

		//번호별 당첨횟수
		System.out.println("========= 색깔별 당첨횟수 =========");
		Stream<Integer> numStream2 = rottoList.stream().filter(drwNoFilter)
				.map(dwrtNoArrayFunc)
				.flatMap(e -> e.getValue().stream());

		Map<Integer, Long>  countByColor = numStream2
				.collect( Collectors.groupingBy(n -> (int)((n.intValue()-1) / 10), Collectors.counting()) );

		countByColor.entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry<Integer, Long>::getKey))
				.forEach(e -> {
					int startNo = e.getKey()*10 +1;
					int endNo = 0;
					switch(e.getKey()){
					case 4:
						endNo = e.getKey()*10 +5;
						break;
					default:
						endNo = e.getKey()*10 +10;
						break;
					}
					System.out.println(startNo + " ~ " + endNo + " = " + e.getValue());
				});

		//홀, 짝 개수 통계
		System.out.println("========= 홀, 짝 개수 통계 =========");
		Map<String, Long> sumByOddNum = rottoList.stream()
				.filter(drwNoFilter)
				.map(l -> dwrtNoArrayFunc.apply(l).getValue().stream())
				.map(st -> {
					Map<Boolean, Long> oddMap = st.collect(Collectors.groupingBy(o -> o%2==1, Collectors.counting()));
					return oddMap.getOrDefault(true, 0L) + "/" + oddMap.getOrDefault(false, 0L);
				})
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.groupingBy(String::toString, Collectors.counting()));

		sumByOddNum.entrySet().stream()
		.forEach(System.out::println);

		//번호 합계별 횟수
		System.out.println("========= 번호 합계별 횟수 =========");
		Map<Integer, Long> countSum = rottoList.stream().filter(drwNoFilter)
				.map(l -> dwrtNoArrayFunc.apply(l).getValue().stream())
				.map(st -> st.collect(Collectors.summingInt(Integer::intValue)))
		//		.collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));
				.collect( Collectors.groupingBy(n -> (int)((n.intValue()-1) / 10), Collectors.counting()) );

				countSum.entrySet().stream()
				.sorted(Comparator.comparingLong(Map.Entry<Integer, Long>::getKey))
		//		.limit(10)
				.forEach(e -> {
					int startNo = e.getKey()*10 +1;
					int endNo = e.getKey()*10 +10;
					String bar = "";
					for(int i=0; i<e.getValue(); i++){
						bar += "|";
					}
					System.out.println(startNo + " ~ " + endNo + " = " + e.getValue() + "\t" + bar);
				});

		//최근 제외할 회차 범위
		int cnt = 15;
		System.out.println("최근 제외할 회차 범위 (몇주) : \n");
		Scanner scanner0 = new Scanner(System.in);
		String scanText0 = scanner0.nextLine();
		try{
			cnt = Integer.parseInt(scanText0);
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		//TODO 삭제
		int temp = exceptCount;
		exceptCount = cnt;

		List<Integer> limitList = currentLimitList(cnt);
		System.out.println("========= 최근 " + cnt + "회차 동안 나왔던 번호 =========");
		System.out.println(limitList);
		step4(sumByNum.entrySet().stream(), limitList)
		.sorted(Comparator.comparing(Map.Entry<Integer, Long>::getValue).reversed())
		.forEach(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"));
		//exceptCount = temp;	//TODO 삭제


		//선택번호와 같이 나온 수 통계
		System.out.println("========= 번호 선택 =========");
		while(true){
			System.out.println("1~45 범위에서 입력 (종료는 공백): \n");
			Scanner scanner = new Scanner(System.in);
			String scanText = scanner.nextLine();
			scanNoList.clear();
			for(String noStr : scanText.split("\\D")){
				if(noStr.isEmpty() == false){
					scanNoList.add(Integer.parseInt(noStr));
					scanNoList.sort(Comparator.naturalOrder());	//입력숫자 정렬
				}
			}

			if(scanNoList.isEmpty()){
				System.out.println("EXIT");
				break;
			}

			/*
			System.out.println("========= " + scanNoList + " =========");
			System.out.println("========= " + scanNoList + " 번호와 같이 나온 수 통계 =========");
			List<Map.Entry<Integer, Long>> entryList = withNumber(rottoList, scanNoList, exceptNoList);

			List<Integer> limitList = currentLimitList(exceptCount);
			System.out.println("========= 최근 " + exceptCount + "회 숫자 " + limitList + " =========");

			entryList = entryList.stream()
			.limit(10)
			.filter(e ->
				limitList.contains(e.getKey()) == false)
			.collect(Collectors.toList());
			System.out.println("========= 최근 " + exceptCount + "회 동안 나온 숫자 제거" + entryList + " =========");
			*/
			LottoParamVO paramVO = new LottoParamVO();
			paramVO.setSelectNoList(scanNoList);
			paramVO.setBonusNoFlag(true);
			paramVO.setExceptCount(exceptCount);
			withNumberList(paramVO);

			if(scanNoList.size() == 6){
				System.out.println("========= " + scanNoList + " 번호 등수 통계 =========");
				rottoList.stream().filter(drwNoFilter)
				.map(r -> {
					int drwNo = r.getDrwNo();
					int bonusNo = r.getBnusNo();
					int grade = 0;
					List<Integer> drwtList = Arrays.asList(r.getDrwtNo1(), r.getDrwtNo2(), r.getDrwtNo3(), r.getDrwtNo4(), r.getDrwtNo5(), r.getDrwtNo6());
					List<Integer> drwtListClone = new ArrayList<Integer>(drwtList);

					drwtListClone.retainAll(scanNoList);
					switch (drwtListClone.size()) {
					case 3:
						grade = 5;
						break;
					case 4:
						grade = 4;
						break;
					case 5:
						grade = 3;
						if(scanNoList.contains(bonusNo))
							grade = 2;
						break;
					case 6:
						grade = 1;
						break;
					}
					if(grade == 0)
						return Collections.emptyMap();
					Map<String, Object> m = new HashMap<>();
					m.put("drwNo", drwNo);
					m.put("bonusNo", bonusNo);
					m.put("grade", grade);
					m.put("drwtList", drwtList);
					return m;
				})
				.filter(m -> !m.isEmpty())
				.sorted(Comparator.comparingInt(m-> Integer.parseInt(m.get("grade").toString())))
				.forEach(m -> {
					System.out.println(m.get("grade") + "등\t" + m.get("drwNo") + "회  \t" + m.get("drwtList") + " + " + m.get("bonusNo"));
				});
			}
		}
	}

	public static ResultListVO withNumberList(LottoParamVO paramVO){
		//파라미터 세팅
		startDrwNo = paramVO.getStartDrwNo();
		endDrwNo = paramVO.getEndDrwNo();
		bonusNoFlag = paramVO.isBonusNoFlag();
		exceptCount = paramVO.getExceptCount();

		List<Integer> selectNoList = paramVO.getSelectNoList();
		if(paramVO.getSelectNoList() == null){
			return null;
		}
		List<Integer> exceptNoList = paramVO.getExceptNoList();

		System.out.println("========= " + selectNoList + " 번호와 같이 나온 수 통계 =========");
//		List<Map.Entry<Integer, Long>> entryList = withNumber(rottoList, selectNoList, exceptNoList);
		Stream<Map.Entry<Integer, List<Integer>>> step1Stream = step1(selectNoList);
		List<Map.Entry<Integer, List<Integer>>> step1List = step1Stream
				.peek(l -> System.out.println(l.getValue()))
				.collect(Collectors.toList());
		step1Stream = step1List.stream();

		Stream<Map.Entry<Integer, Long>> step2Stream = step2(step1Stream.map(e -> e.getValue()), selectNoList);
		List<Map.Entry<Integer, Long>> step2List = step2Stream
				.peek(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"))
				.collect(Collectors.toList());
		step2Stream = step2List.stream();

		Stream<Map.Entry<Integer, Long>> step3Stream = step3(step2Stream, exceptNoList);
		List<Map.Entry<Integer, Long>> step3List = step3Stream
				.peek(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"))
				.collect(Collectors.toList());
		step3Stream = step3List.stream();

		List<Integer> limitList = currentLimitList(exceptCount);
		System.out.println("========= 최근 " + exceptCount + "회 동안 나온 번호 =========");
		System.out.println(limitList);

		Stream<Map.Entry<Integer, Long>> step4Stream = step4(step3Stream, limitList);
		List<Map.Entry<Integer, Long>> step4List = (List<Map.Entry<Integer, Long>>) step4Stream
				.peek(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"))
				.collect(Collectors.toList());

		ResultListVO resultListVO = new ResultListVO();
		resultListVO.setStep1List(step1List);
		resultListVO.setStep2List(step2List);
		resultListVO.setStep3List(step3List);
		resultListVO.setStep4List(step4List);
		resultListVO.setLimitList(limitList);

		return resultListVO;
	}

	/**
	 * 1. 선택 기간 회차 필터, 선택번호 포함 필터, 보너스번호 유무 필터
	 * @param selectedList 선택번호
	 * @return
	 */
	private static Stream<Map.Entry<Integer, List<Integer>>> step1(List<Integer> selectedList){
		System.out.println("=====1==== " + selectedList + " 번호를 포함하는 회차의 숫자 모음 =========");
		Stream<Map.Entry<Integer, List<Integer>>> stream = rottoList.stream()
				.filter(drwNoFilter)
				.map(dwrtNoArrayFunc)
				.filter(e -> e.getValue().containsAll(selectedList))	//선택번호를 전부 포함하는 회
//				.peek(System.out::println)
				;
		return stream;
	}

	/**
	 * 2. 선택번호 제외, 그룹 카운팅, 정렬
	 * @param step1Stream
	 * @param selectedList 선택번호
	 * @return
	 */
	private static Stream<Map.Entry<Integer, Long>> step2(Stream<List<Integer>> step1Stream, List<Integer> selectedList){
		System.out.println("=====2==== " + selectedList + " 번호를 제외하고 카운팅 정렬 =========");
		Map<Integer, Long> noMap = step1Stream
				.flatMap(Collection::stream)
				.filter(t -> selectedList.contains(t) == false)	//선택번호와 같지 않은 번호만
				.collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));

		Stream<Map.Entry<Integer, Long>> entryStream = noMap.entrySet().stream()
				.sorted(Comparator.comparingLong(Map.Entry<Integer, Long>::getValue).reversed())
//				.peek(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"))
				;

		return entryStream;
	}

	/**
	 * 3. 제외번호 필터
	 * @param step2Stream
	 * @param exceptNoList 제외번호
	 * @return
	 */
	private static Stream<Map.Entry<Integer, Long>> step3(Stream<Map.Entry<Integer, Long>> step2Stream, List<Integer> exceptNoList){
		if(exceptNoList == null){
			System.out.println("=====3==== 제외번호 없음 =========");
			return step2Stream;
		}
		System.out.println("=====3==== " + exceptNoList + " 제외번호를 제거 =========");
		Stream<Map.Entry<Integer, Long>> entryStream = step2Stream
				.filter(t -> exceptNoList.contains(t.getKey()) == false)	//제외번호 필터
//				.peek(System.out::println)
				;

		return entryStream;
	}

	/**
	 * 4. 최근회차 번호 제외 필터
	 * @param step3Stream
	 * @param limitList 최근 n회 동안 나온 번호
	 * @return
	 */
	private static Stream<Map.Entry<Integer, Long>> step4(Stream<Map.Entry<Integer, Long>> step3Stream, List<Integer> limitList){
		System.out.println("=====4==== 최근 " + exceptCount + "회 동안 나온 번호 제거 =========");
		Stream<Map.Entry<Integer, Long>> entryStream = step3Stream
//				.limit(10)
				.filter(e -> limitList.contains(e.getKey()) == false)
//				.peek(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"))
				;

		return entryStream;
	}

	//회차 필터
	private static Predicate<LottoVO> drwNoFilter = new Predicate<LottoVO>(){
		@Override
		public boolean test(LottoVO t) {
			boolean result = false;
			if(startDrwNo > 0)
				result = (t.getDrwNo() >= startDrwNo);
			if(endDrwNo > 0)
				result = (t.getDrwNo() <= endDrwNo);
			return result;
		}
	};

	//<회차, 번호 Array> 구조
	private static Function<LottoVO, Map.Entry<Integer, List<Integer>>> dwrtNoArrayFunc = new Function<LottoVO, Map.Entry<Integer, List<Integer>>>() {
		@Override
		public Map.Entry<Integer, List<Integer>> apply(LottoVO l) {
			List<Integer> list = new ArrayList(Arrays.asList(l.getDrwtNo1(), l.getDrwtNo2(), l.getDrwtNo3(), l.getDrwtNo4(), l.getDrwtNo5(), l.getDrwtNo6()));
			if (bonusNoFlag) {
				list.add(l.getBnusNo());
			}
			Map.Entry<Integer, List<Integer>> entry = new AbstractMap.SimpleEntry<Integer, List<Integer>>(l.getDrwNo(), list);
			return entry;
		}
	};

	/**
	 * 같이나온 횟수가 높은 번호
	 * @param rottoList		전체번호
	 * @param noList		선택번호
	 * @param exceptNoList	제외번호
	 * @return
	 * @deprecated
	 */
	private static List<Map.Entry<Integer, Long>> withNumber(List<LottoVO> rottoList, List<Integer> noList, List<Integer> exceptNoList){
		Map<Integer, Long> noMap = rottoList.stream()
		.filter(drwNoFilter)
		.map(dwrtNoArrayFunc)
		.map(e -> e.getValue())
		.filter(list -> list.containsAll(noList))		//선택번호를 전부 포함하는 회
//		.peek(System.out::println)
		.flatMap(Collection::stream)
		.filter(t -> noList.contains(t) == false)	//선택번호와 같지 않은 번호만
		.collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));

		List<Map.Entry<Integer, Long>> entryList = Collections.emptyList();
		if(noMap.isEmpty()){
			System.out.println("결과가 없음");
		}else{
			entryList = noMap.entrySet().stream()
			.sorted(Comparator.comparingLong(Map.Entry<Integer, Long>::getValue).reversed())
			.peek(s -> System.out.println("번호 " + s.getKey() + " : " + s.getValue() + "회"))
	//		.findFirst().get().getKey()
			.filter(t -> !exceptNoList.contains(t.getKey()))
			.collect(Collectors.toList());
		}

		return entryList;
	}

	/**
	 * 최근 나온 회차 중복제거 번호
	 * @param exceptCount
	 * @return
	 */
	private static List<Integer> currentLimitList(int exceptCount){
		List<Integer> limitList = rottoList.stream()
		.limit(exceptCount)
		.map(dwrtNoArrayFunc)
		.flatMap(e -> e.getValue().stream())
		.distinct()
		.sorted()
		.collect(Collectors.toList());

		return limitList;
	}


}
