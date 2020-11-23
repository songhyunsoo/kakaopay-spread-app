package com.kakaopay.spread.util;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public final class SpreadUtil {

	private static SecureRandom random = new SecureRandom();
	//중복 허용하지 않기 위해 HashSet 사용
	private static HashSet<String> set = new HashSet<String>();

	/** 랜덤 문자열을 생성한다 **/
    public static String TokenGenerate() {
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
        	return sb.toString();
        }
        else if(set.size() >= 238328)
        {
        	return null;
        }
        else {
        	TokenGenerate();
        }
        return null;
    }
    
    // 지정된 범위의 정수 1개를 램덤하게 반환하는 메서드
    // n1 은 "하한값", n2 는 상한값
    public static int randomRange(int n1, int n2) {
      return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }
    
    
    public static List<Integer> distributeMoney(int money, int personCount)
    {
    	int total = 0;
    	List<Integer> list = new ArrayList<Integer>();
    	int totalPercentage = money;
    	for(int i=0; i<personCount; i++) {
    	//마지막 순번일 때
    	if(i == personCount - 1)
    	{
    		list.add(totalPercentage);
    		total = total + totalPercentage;
    	}
    	else {	
    	int randomPercentage;
    		if(totalPercentage==0) {
    		randomPercentage = 0;
    		}
    		else {
    		randomPercentage = randomRange(1 ,totalPercentage);
    		
    		}
    	list.add(randomPercentage);
    	total = total + randomPercentage;
    	totalPercentage = totalPercentage - randomPercentage;    	
    		}
    	}
		return list;   	
    }
    
    public static long getGapOfTime(Date date)
	{	
		Date currentDate = new Date();
		
		long second = (currentDate.getTime() - date.getTime()) / 1000;
		return second;
		
	}
    
	public static String getCurrentDate(Date date){
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdformat.format(date);
    }
}
