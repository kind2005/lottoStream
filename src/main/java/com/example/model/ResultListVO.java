package com.example.model;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ResultListVO {
	private List<Map.Entry<Integer, List<Integer>>> step1List;
	private List<Map.Entry<Integer, Long>> step2List;
	private List<Map.Entry<Integer, Long>> step3List;
	private List<Map.Entry<Integer, Long>> step4List;
	private List<Integer> limitList;
}
