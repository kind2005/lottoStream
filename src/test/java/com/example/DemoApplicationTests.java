package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}

	public static void main(String[] args) {
		//SpringApplication.run(DemoApplication.class, args);
		String data = "서울시 노원구 중계1989동 20001동 1999호";
		int hangulByte = 3; //인코딩을 고려한 한글 1자의 바이트수
		int maxLength = 40; //최대 추출할 바이트 길이
		
		System.out.println(cutHangul(data, hangulByte, maxLength));
	}
	
	/**
	 * 한글 1글자의 바이트수를 선택해서 자름 (2 or 3바이트)
	 * @param inputString 입력문자열
	 * @param hangulByte 인코딩을 고려한 한글 1자의 바이트수
	 * @param maxByte 최대 추출할 바이트 길이
	 * @return
	 */
	public static String cutHangul(String inputString, int hangulByte, int maxByte) {
	
		byte[] inputByte = inputString.getBytes();
		int cutByte = 0;
		for (int i = 0; i < inputString.length(); i++) {
			if (isIncludeHangul(inputString.substring(i, i + 1))) {
				if (cutByte + hangulByte > maxByte) {
					break;
				}
				cutByte += hangulByte;
			} else {
				if (cutByte + 1 > maxByte) {
					break;
				}
				cutByte += 1;
			}
		}
		System.out.println(cutByte+" 바이트까지 추출");
		return new String(inputByte, 0, cutByte);
	
	}
	
	/**
	 * 1바이트 이상의 글자를 포함하는지 확인
	 * @param input
	 * @return
	 */
	public static boolean isIncludeHangul(String input){
		for (int k = 0; k < input.length(); k++) {
			if(Character.getType(input.charAt(k))==Character.OTHER_LETTER){
				return true;
			}
		}
		return false;
	}
}
