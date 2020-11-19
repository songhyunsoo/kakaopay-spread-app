package com.example.demo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.SpreadRepository;
import com.example.demo.util.RandomGenerator;
import com.example.demo.vo.DistributionVO;
import com.example.demo.vo.RequestSpreadVO;
import com.example.demo.vo.SpreadVO;
import com.example.demo.vo.TokenVO;

@Service
public class SpreadService {

	@Autowired
	SpreadRepository spreadRepository;

	public TokenVO requestSpread(RequestSpreadVO requestSpreadVO, int xUserId, String xRoomId)
	{
		TokenVO vo = new TokenVO();
		String token = RandomGenerator.generate();
		if(token == null)
		{
			//더이상 고유 문자열을 생성할 수 없음.
		}
		vo.setToken(token);
		
		List<Integer> distributedMoneyList = RandomGenerator.distribute(requestSpreadVO.getMoney(), requestSpreadVO.getPersonCount());
		SpreadVO spreadVO = new SpreadVO();
		spreadVO.setCreateUserId(xUserId);
		//spreadVO.getCreateTime(new Date());
		spreadVO.setMoney(requestSpreadVO.getMoney());
		spreadVO.setPersonCount(requestSpreadVO.getPersonCount());
		spreadVO.setRoomId(xRoomId);
		spreadVO.setToken(vo.getToken());
		
		
		List<DistributionVO> distributionVOList = new ArrayList<DistributionVO>();
		for(int i=0; i<distributedMoneyList.size(); i++)
		{
			DistributionVO distributionVO = new DistributionVO();
			distributionVO.setDistributionMoney(distributedMoneyList.get(i));
			distributionVO.setExpired(false);
			distributionVO.setReceivedUserId("");
			distributionVOList.add(distributionVO);
		}
		spreadVO.setDistribution(distributionVOList);
		spreadRepository.insert(spreadVO);
		
		
		return vo;
	}
	
	
	public List<SpreadVO> spreadSearch()
	{
		return spreadRepository.findAll();
	}
	
}
