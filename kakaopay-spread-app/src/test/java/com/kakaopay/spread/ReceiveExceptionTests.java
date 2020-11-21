package com.kakaopay.spread;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import com.kakaopay.spread.repository.SpreadRepository;
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
public class ReceiveExceptionTests {
	
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
	requestSpreadVO.setMoney(1000000);
	requestSpreadVO.setPersonCount(10);
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
	
	
	log.info("##### 유효하지 않은 토큰에 대한 받기 테스트 #####"); 
	 //유효하지 않은 토큰값으로 변경
	 TokenVO invalidToken = new TokenVO();
	 invalidToken.setToken("invalidToken");	
	 content = objectMapper.writeValueAsString(invalidToken);
	 
     httpHeaders.set("X-ROOM-ID", roomId);
     httpHeaders.set("X-USER-ID", receive);
     spreadResult = mvc.perform(post("/spread/receive")
    		.headers(httpHeaders)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError()) //유효하지 않은 토큰에 대한 에러 발생
            .andDo(print())
            .andReturn();
     
     content = spreadResult.getResponse().getContentAsString();
     
     ResponseVO ResponseVO = objectMapper.readValue(content, ResponseVO.class);  
     
      log.info("##### 에러 응답 메세지 #####");
      log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ResponseVO));
      
      
      
      log.info("##### 중복 받기 테스트 #####"); 
 	 
 	  content = objectMapper.writeValueAsString(token);
 	 
 	 log.info("##### 정상적으로 1회 받기 #####");
 	 
 	 httpHeaders.set("X-ROOM-ID", roomId);
     httpHeaders.set("X-USER-ID", receive);
     mvc.perform(post("/spread/receive")
    		.headers(httpHeaders)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
            
     
     log.info("##### 중복 받기 시도 #####");
     
      httpHeaders.set("X-ROOM-ID", roomId);
      httpHeaders.set("X-USER-ID", receive);
      spreadResult = mvc.perform(post("/spread/receive")
     		.headers(httpHeaders)
             .content(content)
             .contentType(MediaType.APPLICATION_JSON)
             .accept(MediaType.APPLICATION_JSON))
             .andExpect(status().is5xxServerError()) //중복 받기 에러 발생
             .andDo(print())
             .andReturn();
      
      content = spreadResult.getResponse().getContentAsString();
      
      ResponseVO = objectMapper.readValue(content, ResponseVO.class);  
      
       log.info("##### 에러 응답 메세지 #####");
       log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ResponseVO));
       
       
       log.info("##### 자신이 뿌리기한 건에 대한 받기 테스트 #####");
       content = objectMapper.writeValueAsString(token);
       httpHeaders.set("X-ROOM-ID", roomId);
       httpHeaders.set("X-USER-ID", userId); //받는 USER ID를 뿌린 USER ID 변경
       spreadResult = mvc.perform(post("/spread/receive")
      		.headers(httpHeaders)
              .content(content)
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().is5xxServerError()) //자신이 뿌리기한 건 자신이 받기 에러 발생
              .andDo(print())
              .andReturn();
       
       content = spreadResult.getResponse().getContentAsString();
       
       ResponseVO = objectMapper.readValue(content, ResponseVO.class);  
       
        log.info("##### 에러 응답 메세지 #####");
        log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ResponseVO));
       
        
        log.info("##### 동일한 대화방에 속하지 않은 사용자의 받기 테스트 #####");
        content = objectMapper.writeValueAsString(token);
        httpHeaders.set("X-ROOM-ID", roomId + "A"); //roomId를 임의로 변경
        httpHeaders.set("X-USER-ID", receive); 
        spreadResult = mvc.perform(post("/spread/receive")
       		.headers(httpHeaders)
               .content(content)
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is5xxServerError()) //동일한 대화방에 속하지 않은 사용자의 받기 에러 발생
               .andDo(print())
               .andReturn();
        
        content = spreadResult.getResponse().getContentAsString();
        
        ResponseVO = objectMapper.readValue(content, ResponseVO.class);  
        
         log.info("##### 에러 응답 메세지 #####");
         log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ResponseVO));
     
       
        //임의의 10분이 지난, 뿌리기 객체 DB 삽입  
        SpreadVO sv = new SpreadVO();
        Date dt = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        // 지금시간에서 10분 +1초 전 시간으로 변경
        cal.add(Calendar.MINUTE, -10);
        cal.add(Calendar.SECOND, -1);
        dt = cal.getTime();
        sv.setCreateTime(dt);
     	sv.setCreateUserId(userId);
     	sv.setMoney(100000);
     	sv.setPersonCount(1);
     	sv.setRoomId(roomId);
     	sv.setToken("ABC");
     	List<DistributionVO> list = new ArrayList<DistributionVO>();
     	DistributionVO tempvo = new DistributionVO();
     	tempvo.setDistributionMoney(100000);
     	tempvo.setExpired(false);
     	tempvo.setReceivedUserId("");
     	list.add(tempvo);
     	sv.setDistribution(list);
     	sv.setReceivedUserId(new ArrayList<String>());
     	//DB 삽입
     	spreadRepository.insert(sv);
     	
     	 log.info("##### 뿌리기 10분 지난 건에 대한 받기 테스트 #####");
     	 TokenVO tempToken = new TokenVO();
     	 tempToken.setToken("ABC");
         content = objectMapper.writeValueAsString(tempToken);
         httpHeaders.set("X-ROOM-ID", roomId);
         httpHeaders.set("X-USER-ID", receive); 
         spreadResult = mvc.perform(post("/spread/receive")
        		.headers(httpHeaders)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError()) // 받기 유효 기간 만료 에러 발생
                .andDo(print())
                .andReturn();
         
         content = spreadResult.getResponse().getContentAsString();
         
         ResponseVO = objectMapper.readValue(content, ResponseVO.class);  
         
          log.info("##### 에러 응답 메세지 #####");
          log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ResponseVO));
         
	}
}