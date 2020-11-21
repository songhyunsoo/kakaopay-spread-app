package com.kakaopay.spread;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.spread.vo.RequestSpreadVO;
import com.kakaopay.spread.vo.ResponseReceiveVO;
import com.kakaopay.spread.vo.ResponseSpreadVO;
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
public class SpreadApplicationTests {
	
	@Value("${X-USER_ID}") 
	private String UserId;
	
	@Value("${X-ROOM-ID}") 
	private String RoomId;
	
	@Value("${RECEIVE-USER-ID}") 
	private String receiveUserId;

	@Autowired
	MockMvc mvc;

	@Autowired
	private WebApplicationContext ctx;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach()
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(ctx).addFilters(new CharacterEncodingFilter("UTF-8", true)) //필터																												// 추가
				   .alwaysDo(print()).build();
	}

	@Test 
	void SpreadTest() throws Exception { 
	log.info("##### 뿌리기 테스트 #####"); 
	
	RequestSpreadVO requestSpreadVO = new RequestSpreadVO();
	requestSpreadVO.setMoney(1000000);
	requestSpreadVO.setPersonCount(10);
	String content = objectMapper.writeValueAsString(requestSpreadVO);
	 HttpHeaders httpHeaders = new HttpHeaders();
     httpHeaders.set("X-ROOM-ID",RoomId);
     httpHeaders.set("X-USER-ID",UserId);
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
	
	log.info("##### 받기 테스트 #####"); 
	
	 content = objectMapper.writeValueAsString(token);
	 
     httpHeaders.set("X-ROOM-ID", RoomId);
     httpHeaders.set("X-USER-ID", receiveUserId);
     spreadResult = mvc.perform(post("/spread/receive")
    		.headers(httpHeaders)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
     content = spreadResult.getResponse().getContentAsString();
     
     ResponseReceiveVO responseReceiveVO = objectMapper.readValue(content, ResponseReceiveVO.class);         
      log.info("##### 받기 완료 : 받은 금액:" +  responseReceiveVO.getRecevingMoney() + "#####");
      
      log.info("##### 조회 테스트 #####"); 
  	
 	 content = objectMapper.writeValueAsString(token);
 	 
      httpHeaders.set("X-ROOM-ID", RoomId);
      httpHeaders.set("X-USER-ID", UserId);
      spreadResult = mvc.perform(post("/spread/search")
     		.headers(httpHeaders)
             .content(content)
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.completedRecevingInfoList[0].receivedUserId", is(receiveUserId))) //받아간 사용자와 실제 조회 결과가 일치하는지.
             .andDo(print())
             .andReturn();
      content = spreadResult.getResponse().getContentAsString();
      ResponseSpreadVO responseSpreadVO = objectMapper.readValue(content, ResponseSpreadVO.class);
      
       log.info("##### 조회 결과#####");
       
       log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseSpreadVO));
      
       log.info("##### 조회 완료#####");
	}
}