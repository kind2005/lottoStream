package com.example.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.*;

public class StreamExample {
	
	public static void main(String[] args) {
		//Listing 1. Java SE 7 이하에서의 처리 방법
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(new Transaction(1, 2, "100"));
		transactions.add(new Transaction(3, 1, "80"));
		transactions.add(new Transaction(6, 1, "120"));
		transactions.add(new Transaction(7, 2, "40"));
		transactions.add(new Transaction(10, 1, "50"));
		System.out.println("Listing 1. Java SE 7 이하에서의 처리 방법");
		System.out.println("1. " + transactions.toString());
		
		List<Transaction> groceryTransactions = new ArrayList<>();
		for(Transaction t : transactions){
			if(t.getType() == Transaction.GROCERY){
				groceryTransactions.add(t);
			}
		}
		System.out.println("2. " + groceryTransactions.toString());
	
		Collections.sort(groceryTransactions, new Comparator<Transaction>(){
			@Override
			public int compare(Transaction t1, Transaction t2) {
				return t2.getValue().compareTo(t1.getValue());
			}
		});
		System.out.println("3. " + groceryTransactions.toString());
	
		List<Integer> transactionIds = new ArrayList<>();
		for(Transaction t : groceryTransactions){
			transactionIds.add(t.getId());
		}
		System.out.println("4. " + transactionIds.toString());
	
		//Listing 2. Java SE 8 에서의 처리 방법
		System.out.println("Listing 2. Java SE 8 에서의 처리 방법");
		System.out.println("1. " + transactions.toString());
		List<Integer> transactionsIds = 
				transactions.stream()
					.filter(t -> t.getType() == Transaction.GROCERY)
					.sorted(Comparator.comparing(Transaction::getValue).reversed())	//(x, y) -> Transaction.getValue(x, y)
					.map(Transaction::getId)
					.collect(Collectors.toList());
		System.out.println("2. " + transactionIds.toString());
		
		//Listing 3. 병렬 처리
		System.out.println("Listing 3. 병렬 처리");
		System.out.println("1. " + transactions.toString());
		List<Integer> transactionsIds2 = 
				transactions.parallelStream()	//병렬 처리
					.filter(t -> t.getType() == Transaction.GROCERY)
					.sorted(Comparator.comparing(Transaction::getValue).reversed())
					.map(Transaction::getId)
					.collect(Collectors.toList());
		System.out.println("2. " + transactionsIds2.toString());
		
		//Listing 4. 컬렉션에서 외부 반복작업
		System.out.println("Listing 4. 컬렉션에서 외부 반복작업");
		System.out.println("1. " + transactions.toString());
		List<Integer> transactionIds3 = new ArrayList<>(); 
		for(Transaction t : transactions){
			transactionIds3.add(t.getId()); 
		}
		System.out.println("2. " + transactionIds3.toString());
		
		//Listing 5. Stream의 내부 반복작업
		System.out.println("Listing 5. Stream의 내부 반복작업");
		System.out.println("1. " + transactions.toString());
		List<Integer> transactionIds4 = 
			    transactions.stream()
			        .map(Transaction::getId)
			        .collect(Collectors.toList());
		System.out.println("2. " + transactionIds4.toString());
		
		//Listing 6. Lazy
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
		List<Integer> twoEvenSquares = numbers.stream()
				.filter(n -> {
						System.out.println("filtering " + n);
						return n % 2 == 0;
					})
				.map(n -> {
						System.out.println("mapping " + n);
						return n * n;
					})
				.limit(2)
				.collect(Collectors.toList());
	}
}


class Transaction {
	private int id = 0;
	private int type = 0;
	private String value;

	public static final int GROCERY = 1;

	public Transaction(){}
	public Transaction(int id, int type, String value){
		this.id = id;
		this.type = type;
		this.value = value;
	}

	public int getId() {
		return id;
	}
	public int getType() {
		return type;
	}
	public String getValue(){
		return value;
	}
	
	@Override
	public String toString() {
		return "Transaction [id=" + id + ", type=" + type + ", value=" + value + "]";
	}
}

