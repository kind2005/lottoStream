package com.example.model;

import java.util.List;

import lombok.Data;

enum ParamType {
	A, B, C, D
}

@Data
public class LottoParamVO {
	private List<Integer> paramTypes;
	private int startDrwNo = 1;
	private int endDrwNo;
	private boolean bonusNoFlag = false;
	private String selectNo;
	private String exceptNo;
	private int exceptCount;
	
	private List<Integer> selectNoList;
	private List<Integer> exceptNoList;
	
}
