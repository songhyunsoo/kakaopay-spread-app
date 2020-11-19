package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.SpreadService;
import com.example.demo.vo.RequestSpreadVO;
import com.example.demo.vo.SpreadVO;
import com.example.demo.vo.TokenVO;

@RestController
public class SpreadController {

	@Autowired
	SpreadService spreadService;
	
	@PostMapping(value = "/spared")
	public TokenVO spread(@RequestHeader(value="X-USER-ID") int xUserId,
			@RequestHeader(value="X-ROOM-ID") String xRoomId,
			@RequestBody RequestSpreadVO requestSpread)
	{
		
		return spreadService.requestSpread(requestSpread, xUserId, xRoomId);
		
	}
	
	@PostMapping(value = "/sparedSearch")
	public List<SpreadVO> spreadSearch(@RequestHeader(value="X-USER-ID") int xUserId,
			@RequestHeader(value="X-ROOM-ID") String xRoomId,
			@RequestBody RequestSpreadVO requestSpread)
	{
		
		return spreadService.spreadSearch();
		
	}
	
}
