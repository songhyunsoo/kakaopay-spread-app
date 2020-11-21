package com.kakaopay.spread.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.spread.exception.SpreadException;
import com.kakaopay.spread.service.SpreadService;
import com.kakaopay.spread.vo.RequestSpreadVO;
import com.kakaopay.spread.vo.ResponseReceiveVO;
import com.kakaopay.spread.vo.ResponseSpreadVO;
import com.kakaopay.spread.vo.TokenVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = {"뿌리기 API"})
@RestController
public class SpreadController {

	@Autowired
	SpreadService spreadService;
	
	@ApiOperation(notes = "뿌리기", value = "뿌리기")
	@PostMapping(value = "/spread")
	public TokenVO spread(@RequestHeader(value="X-USER-ID") String xUserId,
			@RequestHeader(value="X-ROOM-ID") String xRoomId,
			@RequestBody RequestSpreadVO requestSpread) throws SpreadException
	{		
		return spreadService.requestSpread(requestSpread, xUserId, xRoomId);		
	}
	
	@ApiOperation(notes = "받기", value = "받기")
	@PostMapping(value = "/spread/receive")
	public ResponseReceiveVO receive(@RequestHeader(value="X-USER-ID") String xUserId,
			@RequestHeader(value="X-ROOM-ID") String xRoomId,
			@RequestBody TokenVO tokenVO) throws SpreadException
	{		
		return spreadService.receive(tokenVO.getToken(), xUserId, xRoomId);
	}
	
	@ApiOperation(notes = "뿌리기 조회", value = "뿌리기 조회")
	@PostMapping(value = "/spread/search")
	public ResponseSpreadVO spreadSearch(@RequestHeader(value="X-USER-ID") String xUserId,
			@RequestHeader(value="X-ROOM-ID") String xRoomId,
			@RequestBody TokenVO tokenVO) throws SpreadException
	{				
		return spreadService.spreadSearch(tokenVO.getToken(), xUserId, xRoomId);		
	}	
}
