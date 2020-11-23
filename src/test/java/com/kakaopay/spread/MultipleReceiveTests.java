package com.kakaopay.spread;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.spread.repository.SpreadRepository;
import com.kakaopay.spread.util.SpreadUtil;
import com.kakaopay.spread.vo.DistributionVO;
import com.kakaopay.spread.vo.RequestSpreadVO;
import com.kakaopay.spread.vo.ResponseVO;
import com.kakaopay.spread.vo.SpreadVO;
import com.kakaopay.spread.vo.TokenVO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(
	properties ={
			     "X-USER_ID=100",  //테스트에 사용할 User ID (뿌린 사람)
				"X-ROOM-ID=KakaoPay", //테스트에 사용할 Room ID (뿌리기 방)
			    "RECEIVE-USER-ID=101"//테스트에 사용할 받는 User ID (받는 사람) 
				}
		,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
		)


@AutoConfigureMockMvc
@Slf4j
public class MultipleReceiveTests {
	
	@Value("${X-USER_ID}") 
	private String userId;
	
	@Value("${X-ROOM-ID}") 
	private String roomId;
	
	@Value("${RECEIVE-USER-ID}") 
	private String receive;

	@Autowired
	MockMvc mvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	SpreadRepository spreadRepository;

	@BeforeEach()
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true)) //필터																												// 추가
				   .alwaysDo(print()).build();
	}

	@Test 
	void SpreadTest() throws Exception { 
	log.info("##### 뿌리기 테스트 (정상) #####"); 
	
	RequestSpreadVO requestSpreadVO = new RequestSpreadVO();
	requestSpreadVO.setMoney(10000);
	requestSpreadVO.setPersonCount(1);
	String content = objectMapper.writeValueAsString(requestSpreadVO);
	 HttpHeaders httpHeaders = new HttpHeaders();
     httpHeaders.set("X-ROOM-ID",roomId);
     httpHeaders.set("X-USER-ID",userId);
     MvcResult spreadResult = mvc.perform(post("/spread")
    		.headers(httpHeaders)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
    
    content = spreadResult.getResponse().getContentAsString();
    TokenVO token = objectMapper.readValue(content, TokenVO.class);
	log.info("##### 뿌리기 완료 : 할당된 토큰:" +  token.getToken() + "#####"); 
	
      log.info("##### 다수 요청 받기 테스트 #####"); 
 	 
     MultipleReceiveRequest multipleReceiveRequest = new MultipleReceiveRequest();
     log.info("##### 10번 받기 요청 시도(분배인원:1) #####");
     for(int i=0; i<10; i++) {
    	 multipleReceiveRequest.request(token, receive + Integer.toString(i));
     }    

	}

	
	public class MultipleReceiveRequest
	{
				
		void request(TokenVO token, String receiver) throws Exception
		{
			log.info("받기 요청 유저" + receiver + ",  요청 시간:" + SpreadUtil.getCurrentDate(new Date()));
			 HttpHeaders httpHeaders = new HttpHeaders();
		     httpHeaders.set("X-ROOM-ID",roomId);
		     httpHeaders.set("X-USER-ID",receiver);
			 String content = objectMapper.writeValueAsString(token);
			
			 mvc.perform(post("/spread/receive")
			    		.headers(httpHeaders)
			            .content(content)
			            .contentType(MediaType.APPLICATION_JSON)
			            .accept(MediaType.APPLICATION_JSON));				 			
		}		
	}
	
}