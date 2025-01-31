package com.example.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.model.LottoParamVO;
import com.example.service.LottoCalc;
import com.example.model.ResultListVO;

@Controller
@SessionAttributes({"paramVO", "resultListVO"})
public class LottoController {
//	@ExceptionHandler(HttpSessionRequiredException.class)
//	ResponseEntity<Error> unauthorizedException(HttpSessionRequiredException e) {
//		if(e.getAttributeName().equals("username")){
//			//do something else,
//		}
//		return //something
//	}

	@ModelAttribute("paramVO")
	public LottoParamVO paramVO(HttpSession session){
		return new LottoParamVO();
	}
	
	@ModelAttribute("resultListVO")
	public ResultListVO resultListVO(HttpSession session){
		ResultListVO resultListVO = (ResultListVO)session.getAttribute("resultListVO");
		if(resultListVO == null)
			resultListVO = new ResultListVO();
		return resultListVO;
	}

	@RequestMapping(path="/calcNum", method=RequestMethod.GET)
	public String calcNum(Model model, @ModelAttribute("paramVO") LottoParamVO paramVO
			, SessionStatus status) {
//		status.setComplete();
		model.addAttribute("paramVO", paramVO);
		
		return "/lotto/calcNum";
	}
	
	@RequestMapping(path="/calcNum", method=RequestMethod.POST)
	public String calcNum(Model model, @ModelAttribute("paramVO") LottoParamVO paramVO
			, @ModelAttribute("resultListVO") ResultListVO resultListVO) {

		String[] selectedNos = paramVO.getSelectNo().split("\\D");
		List<Integer> selectedNoList = new ArrayList<>();
		for(String noStr : selectedNos){
			if(noStr.isEmpty() == false){
				selectedNoList.add(Integer.parseInt(noStr));
				selectedNoList.sort(Comparator.naturalOrder());	//입력숫자 정렬
			}
		}
		paramVO.setSelectNoList(selectedNoList);
		
		String[] exceptNos = paramVO.getExceptNo().split("\\D");
		List<Integer> exceptNoList = new ArrayList<>();
		for(String noStr : exceptNos){
			if(noStr.isEmpty() == false){
				exceptNoList.add(Integer.parseInt(noStr));
				exceptNoList.sort(Comparator.naturalOrder());	//입력숫자 정렬
			}
		}
		paramVO.setExceptNoList(exceptNoList);
		System.out.println(paramVO.toString());
		
		resultListVO = LottoCalc.withNumberList(paramVO);
		List<Map.Entry<Integer, List<Integer>>> step1List = resultListVO.getStep1List();
		List<Map.Entry<Integer, Long>> step2List = resultListVO.getStep2List();
		List<Map.Entry<Integer, Long>> step3List = resultListVO.getStep3List();
		List<Map.Entry<Integer, Long>> step4List = resultListVO.getStep4List();
		List<Integer> limitList = resultListVO.getLimitList();
		
		model.addAttribute("paramVO", paramVO);
		model.addAttribute("step1List", step1List);
		model.addAttribute("step2List", step2List);
		model.addAttribute("step3List", step3List);
		model.addAttribute("step4List", step4List);
		model.addAttribute("limitList", limitList);
		
		return "/lotto/calcNum";
	}
	

	@RequestMapping(path="/analyzeNum1")
	public String analyzeNum1(Model model, @ModelAttribute("paramVO") LottoParamVO paramVO
			, @ModelAttribute("resultListVO") ResultListVO resultListVO) {

		String[] selectedNos = paramVO.getSelectNo().split("\\D");
		List<Integer> selectedNoList = new ArrayList<>();
		for(String noStr : selectedNos){
			if(noStr.isEmpty() == false){
				selectedNoList.add(Integer.parseInt(noStr));
				selectedNoList.sort(Comparator.naturalOrder());	//입력숫자 정렬
			}
		}
		paramVO.setSelectNoList(selectedNoList);
		
		String[] exceptNos = paramVO.getExceptNo().split("\\D");
		List<Integer> exceptNoList = new ArrayList<>();
		for(String noStr : exceptNos){
			if(noStr.isEmpty() == false){
				exceptNoList.add(Integer.parseInt(noStr));
				exceptNoList.sort(Comparator.naturalOrder());	//입력숫자 정렬
			}
		}
		paramVO.setExceptNoList(exceptNoList);
		System.out.println(paramVO);
		
		resultListVO = LottoCalc.withNumberList(paramVO);
		System.out.println(resultListVO);
		List<Map.Entry<Integer, List<Integer>>> step1List = resultListVO.getStep1List();
		
		model.addAttribute("paramVO", paramVO);
		model.addAttribute("resultListVO", resultListVO);
		model.addAttribute("step1List", step1List);
		
		return "/lotto/analyzeNum1";
	}

	@RequestMapping(path="/analyzeNum2")
	public String analyzeNum2(Model model, @ModelAttribute("paramVO") LottoParamVO paramVO
			, @ModelAttribute("resultListVO") ResultListVO resultListVO) {

		System.out.println(paramVO);
		List<Map.Entry<Integer, Long>> step2List = resultListVO.getStep2List();
		System.out.println(step2List);

		model.addAttribute("step2List", step2List);
		
		return "/lotto/analyzeNum2";
	}

	@RequestMapping(path="/analyzeNum3")
	public String analyzeNum3(Model model, @ModelAttribute("paramVO") LottoParamVO paramVO
			, @ModelAttribute("resultListVO") ResultListVO resultListVO) {

		System.out.println(paramVO);
		List<Map.Entry<Integer, Long>> step3List = resultListVO.getStep3List();
		System.out.println(step3List);

		model.addAttribute("step3List", step3List);
		
		return "/lotto/analyzeNum3";
	}

	@RequestMapping(path="/analyzeNum4")
	public String analyzeNum4(Model model, @ModelAttribute("paramVO") LottoParamVO paramVO
			, @ModelAttribute("resultListVO") ResultListVO resultListVO) {

		System.out.println(paramVO);
		List<Map.Entry<Integer, Long>> step4List = resultListVO.getStep4List();
		List<Integer> limitList = resultListVO.getLimitList();
		System.out.println(step4List);

		model.addAttribute("step4List", step4List);
		model.addAttribute("limitList", limitList);
		
		return "/lotto/analyzeNum4";
	}
}
