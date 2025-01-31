package com.example.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.example.model.LottoVO;

public class LottoDataReader {
	static String drwDateStr = "yyyy.MM.dd";	//종료회차
	static List<LottoVO> rottoList = null;
	
	public static void main(String[] args) {
		initLottoData();
	}
	
	public static List<LottoVO> initLottoData() {
		Workbook workbook = loadExcelFile();
		rottoList = loadExcelData(workbook);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		boolean newFlag = false;
		int lastDrwNo = 0;
		int cnt = 0;
		try {
			do{
				lastDrwNo = rottoList.get(0).getDrwNo();
				drwDateStr = rottoList.get(0).getDrwNoDate();
				Date drwDate = sdf.parse(drwDateStr + " 21:00");
				//마지막회차 날짜가 7일전 날짜 이전이면 1회씩 다음회차 추가
				newFlag = drwDate.before(new Date(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)));
				System.out.println(newFlag);
				if(newFlag){
					System.out.println(sdf.format(drwDate) + " - " + sdf.format(new Date()));
					LottoVO lottoVO = parseJsonRotto(lastDrwNo+1);
					rottoList.add(0, lottoVO);
					
					Sheet sheet = workbook.getSheetAt(0);
					sheet.shiftRows(3, sheet.getLastRowNum(), 1);
					Row row = sheet.getRow(3);
					row.createCell(1).setCellValue(lottoVO.getDrwNo());
					row.createCell(2).setCellValue(lottoVO.getDrwNoDate());
					row.createCell(3).setCellValue(lottoVO.getFirstPrzwnerCo());
					row.createCell(4).setCellValue(lottoVO.getFirstWinamnt());
					row.createCell(13).setCellValue(lottoVO.getDrwtNo1());
					row.createCell(14).setCellValue(lottoVO.getDrwtNo2());
					row.createCell(15).setCellValue(lottoVO.getDrwtNo3());
					row.createCell(16).setCellValue(lottoVO.getDrwtNo4());
					row.createCell(17).setCellValue(lottoVO.getDrwtNo5());
					row.createCell(18).setCellValue(lottoVO.getDrwtNo6());
					row.createCell(19).setCellValue(lottoVO.getBnusNo());
					System.out.println("count " + ++cnt);
				}
			}while(newFlag && lastDrwNo != rottoList.get(0).getDrwNo());
			
			if(cnt > 0){
				saveExcelFile(workbook);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return rottoList;
	}

	/**
	 * 엑셀파일 읽기
	 * @return
	 */
	private static Workbook loadExcelFile(){

		//엑셀 주소 : http://www.nlotto.co.kr/lotto645Confirm.do?method=allWinExel&nowPage=1&drwNoStart=1&drwNoEnd=719
		String path = ClassLoader.getSystemResource("").getPath();
		String filePath = path + "excel.xls";
		Workbook workbook = null;
		System.out.println(filePath);
		try(FileInputStream excelFIS = new FileInputStream(new File(filePath))) {
            File file = new File(filePath);
            // 2. 파일 존재 여부 체크
            if (!file.exists()){
                throw new FileNotFoundException("File Not Found");
            }
            
			workbook = WorkbookFactory.create(excelFIS);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return workbook;
	}
	
	/**
	 * 엑셀파일 저장
	 * @return
	 */
	private static void saveExcelFile(Workbook workbook){
		
		String path = ClassLoader.getSystemResource("").getPath();
		String filePath = path + "excel.xls";
		System.out.println(filePath);
		File file = new File(filePath);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			workbook.write(fos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 엑셀 읽어서 List로 반환
	 * @return
	 */
	private static List<LottoVO> loadExcelData(Workbook workbook){
		Sheet sheet = null;
		List<LottoVO> list = new LinkedList<>();

		int sheetNum = workbook.getNumberOfSheets();
		for (int k = 0; k < sheetNum; k++) {
			sheet = workbook.getSheetAt(k);
			int rows = sheet.getPhysicalNumberOfRows();
			Row row = null;
			int cells = 59;
			row = sheet.getRow(3);
			cells = row.getLastCellNum();
			for (int r = 3; r < rows; r++) {
				row = sheet.getRow(r);
				LottoVO rotto = new LottoVO();

				//row에 값이 한개라도 있는지 체크하기 위한 flag
				boolean emptyRow = true;
				Cell cell = null;
				String tmpStr = "";
				for (short c = 0; c < cells; c++) {
					cell = row.getCell(c);
					if (cell == null) {
						continue;
					}else if(emptyRow && cell.getNumericCellValue() != 0.0){
						emptyRow = false;
					}
					switch (cell.getCellTypeEnum()) {
					case NUMERIC:
						if(HSSFDateUtil.isCellDateFormatted(cell)){
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
							tmpStr = formatter.format(cell.getDateCellValue());
					    }else{
							tmpStr = ""+cell.getNumericCellValue();
					    }
						break;
					case STRING:
						tmpStr =  cell.getStringCellValue();
						break;
					case FORMULA:
						tmpStr =  cell.getCellFormula();
						break;
					default:
						tmpStr =  "";
					}
					
					switch (c) {
					case 1 : 
						rotto.setDrwNo(Double.valueOf(tmpStr).intValue());
						break;
					case 2 : 
						rotto.setDrwNoDate(tmpStr);
						break;
					case 3 : 
						rotto.setFirstPrzwnerCo(Double.valueOf(tmpStr).intValue());
						break;
					case 4 : 
						tmpStr = tmpStr.replaceAll("\\D", "");
						rotto.setFirstWinamnt(Double.valueOf(tmpStr).longValue());
						break;
					case 13 : 
						rotto.setDrwtNo1(Double.valueOf(tmpStr).intValue());
						break;
					case 14 : 
						rotto.setDrwtNo2(Double.valueOf(tmpStr).intValue());
						break;
					case 15 : 
						rotto.setDrwtNo3(Double.valueOf(tmpStr).intValue());
						break;
					case 16 : 
						rotto.setDrwtNo4(Double.valueOf(tmpStr).intValue());
						break;
					case 17 : 
						rotto.setDrwtNo5(Double.valueOf(tmpStr).intValue());
						break;
					case 18 : 
						rotto.setDrwtNo6(Double.valueOf(tmpStr).intValue());
						break;
					case 19 : 
						rotto.setBnusNo(Double.valueOf(tmpStr).intValue());
						break;
					}
				}// row

				if(!emptyRow){
					list.add(rotto);
				}
			}
		}

		return list;
	}
	
	/**
	 * 부족한 회차를 json API를 통해 받아옴
	 * @param drwNo
	 * @return
	 */
	public static LottoVO parseJsonRotto(int drwNo){
		LottoVO lottoVO = new LottoVO();
		String urlStr = "http://www.nlotto.co.kr/common.do?method=getLottoNumber&drwNo=" + drwNo;
		System.out.println(urlStr);
		try{
			URL url = new URL(urlStr);
			URLConnection urlconn = url.openConnection();
			urlconn.setUseCaches(false);
			urlconn.setDoOutput(true);
			urlconn.setDoInput(true);
			urlconn.setConnectTimeout(1000*10);
			urlconn.setReadTimeout(1000*30);
			urlconn.connect();
			
			java.io.InputStream is = urlconn.getInputStream() ;
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	
			StringBuffer sb = new StringBuffer();
			String str;
			while((str = br.readLine()) != null){
				sb.append(str);
			}
			System.out.println(sb.toString());
			JSONParser jsonparser = new JSONParser();
			JSONObject jsonObject = (JSONObject)jsonparser.parse(sb.toString());
			lottoVO.setDrwNo(Integer.parseInt(jsonObject.get("drwNo").toString()));
			lottoVO.setDrwNoDate(jsonObject.get("drwNoDate").toString().replaceAll("-", "."));
			lottoVO.setFirstPrzwnerCo(Integer.parseInt(jsonObject.get("firstPrzwnerCo").toString()));
			lottoVO.setFirstWinamnt(Long.parseLong(jsonObject.get("firstWinamnt").toString()));
			lottoVO.setDrwtNo1(Integer.parseInt(jsonObject.get("drwtNo1").toString()));
			lottoVO.setDrwtNo2(Integer.parseInt(jsonObject.get("drwtNo2").toString()));
			lottoVO.setDrwtNo3(Integer.parseInt(jsonObject.get("drwtNo3").toString()));
			lottoVO.setDrwtNo4(Integer.parseInt(jsonObject.get("drwtNo4").toString()));
			lottoVO.setDrwtNo5(Integer.parseInt(jsonObject.get("drwtNo5").toString()));
			lottoVO.setDrwtNo6(Integer.parseInt(jsonObject.get("drwtNo6").toString()));
			lottoVO.setBnusNo(Integer.parseInt(jsonObject.get("bnusNo").toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		return lottoVO;
	}

}
