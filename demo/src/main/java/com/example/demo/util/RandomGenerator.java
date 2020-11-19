package com.example.demo.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RandomGenerator {

	private static SecureRandom random = new SecureRandom();
	//중복 허용하지 않기 위해 HashSet 사용
	private static HashSet<String> set = new HashSet<String>();

	/** 랜덤 문자열을 생성한다 **/
    public static String generate() {
    	String ENGLISH_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String ENGLISH_UPPER = ENGLISH_LOWER.toUpperCase();
        String NUMBER = "0123456789";
    	String DATA = ENGLISH_LOWER + ENGLISH_UPPER + NUMBER;
    	int length = 3;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append( DATA.charAt(
            		random.nextInt(DATA.length())
            		));
        }
        if(set.add(sb.toString()))
        {
        	System.out.println("셋사이즈:"+set.size());
        	return sb.toString();
        }
        else {
        	generate();
        }
        return null;
    }
    
    public static void abc()
    {
    	  for (int i = 1; i <= 20; i++)
    	      System.out.println(randomRange(0, 12));
    }
    
    public static List<Integer> distribute(double money, int personCount)
    {
    	List<Integer> list = new ArrayList<Integer>();
    	int totalPercentage = 100;
    	for(int i=0; i<personCount; i++) {
    	//마지막 순번일 때
    	if(i == personCount - 1)
    	{
    		list.add(totalPercentage);
    	}
    	else {	
    	int randomPercentage;
    		if(totalPercentage==0) {
    		randomPercentage = 0;
    		}
    		else {
    		randomPercentage = randomRange(0 ,totalPercentage);
    		
    		}
    	list.add(randomPercentage);
    	totalPercentage = totalPercentage - randomPercentage;    	
    		}
    	}
    	int total = 0;
    	for(int i=0; i<list.size(); i++)
    	{
    		
    		int distributedMoney = (int)(money * ((double)list.get(i) / 100));
    		
    		if(i == personCount - 1) {
    			total = total + distributedMoney;
    			int balance = (int)money - total;
    			list.set(i, distributedMoney + balance);
    			total = total + balance;
    			
    		}
    		else
    		{
    		total = total + distributedMoney;
    			list.set(i, distributedMoney);
        	
    		}
    		
    	}
		return list;   	
    }
    
    // 지정된 범위의 정수 1개를 램덤하게 반환하는 메서드
    // n1 은 "하한값", n2 는 상한값
    public static int randomRange(int n1, int n2) {
      return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }
}
