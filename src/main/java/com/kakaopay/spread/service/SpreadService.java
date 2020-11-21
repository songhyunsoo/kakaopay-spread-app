package com.kakaopay.spread.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.spread.exception.SpreadException;
import com.kakaopay.spread.repository.SpreadRepository;
import com.kakaopay.spread.util.SpreadErrorCode;
import com.kakaopay.spread.util.SpreadUtil;
import com.kakaopay.spread.vo.CompletedRecevingInfoVO;
import com.kakaopay.spread.vo.DistributionVO;
import com.kakaopay.spread.vo.RequestSpreadVO;
import com.kakaopay.spread.vo.ResponseReceiveVO;
import com.kakaopay.spread.vo.ResponseSpreadVO;
import com.kakaopay.spread.vo.SpreadVO;
import com.kakaopay.spread.vo.TokenVO;

@Service
public class SpreadService {

	@Autowired
	SpreadRepository spreadRepository;

	public TokenVO requestSpread(RequestSpreadVO requestSpreadVO, String xUserId, String xRoomId) throws SpreadException
	{
		TokenVO vo = new TokenVO();
		String token = SpreadUtil.TokenGenerate();
		if(token == null)
		{
			//더이상 고유 문자열을 생성할 수 없습니다.
			throw new SpreadException(SpreadErrorCode.EXCEED_TOKEN_CAPACITY.getCode(),SpreadErrorCode.EXCEED_TOKEN_CAPACITY.getDescription());
		}
		vo.setToken(token);
		
		List<Integer> distributedMoneyList = SpreadUtil.distributeMoney(requestSpreadVO.getMoney(), requestSpreadVO.getPersonCount());
		SpreadVO spreadVO = new SpreadVO();
		spreadVO.setCreateUserId(xUserId);
		spreadVO.setCreateTime(new Date());
		spreadVO.setMoney(requestSpreadVO.getMoney());
		spreadVO.setPersonCount(requestSpreadVO.getPersonCount());
		spreadVO.setRoomId(xRoomId);
		spreadVO.setToken(vo.getToken());
		spreadVO.setReceivedUserId(new ArrayList<String>());
		
		
		List<DistributionVO> distributionVOList = new ArrayList<DistributionVO>();
		for(int i=0; i<distributedMoneyList.size(); i++)
		{
			DistributionVO distributionVO = new DistributionVO();
			distributionVO.setDistributionMoney(distributedMoneyList.get(i));
			distributionVO.setExpired(false);
			distributionVO.setReceivedUserId(null);
			distributionVOList.add(distributionVO);
		}
		spreadVO.setDistribution(distributionVOList);
		spreadRepository.insert(spreadVO);
		
		
		return vo;
	}
	
	
	public ResponseSpreadVO spreadSearch(String token, String xUserId, String xRoomId) throws SpreadException
	{
		SpreadVO spreadVO = spreadRepository.findByToken(token);
		if(spreadVO == null)
		{
			//토큰에 대한 유효한 뿌리기가 없습니다.
			throw new SpreadException(SpreadErrorCode.INVALID_TOKEN.getCode(),SpreadErrorCode.INVALID_TOKEN.getDescription());			
		}
		if(!spreadVO.getCreateUserId().equals(xUserId))
		{
			//자신이 뿌리기한 건이 아니면 조회 할 수 없습니다.
			throw new SpreadException(SpreadErrorCode.INVALID_SEARCH_USER_ID.getCode(),SpreadErrorCode.INVALID_SEARCH_USER_ID.getDescription());
		}
		
		if(SpreadUtil.getGapOfTime(spreadVO.getCreateTime()) > 604800)
		{
			//뿌리기 시점으로부터 일주일이 지나 조회 기간이 만료되었습니다.
			throw new SpreadException(SpreadErrorCode.EXPIRE_SEARCH_TIME.getCode(),SpreadErrorCode.EXPIRE_SEARCH_TIME.getDescription());
		}
		
		
		ResponseSpreadVO responseSpreadVO = new ResponseSpreadVO();
		//표준시에서 한국시로 변경하여 사용자에게 보여준다.
		responseSpreadVO.setCreateTime(SpreadUtil.getCurrentDate(spreadVO.getCreateTime()));
		responseSpreadVO.setSpreadMoney((int)spreadVO.getMoney());
		List<DistributionVO> list = spreadVO.getDistribution();
		List<CompletedRecevingInfoVO> infoList = new ArrayList<CompletedRecevingInfoVO>();
		int money = 0;
		for(int i = 0; i<list.size(); i++) {
			
			if(list.get(i).isExpired())
			{
				CompletedRecevingInfoVO vo = new CompletedRecevingInfoVO();
				vo.setReceivedMoney(list.get(i).getDistributionMoney());
				vo.setReceivedUserId(list.get(i).getReceivedUserId());
				infoList.add(vo);
				money = money + list.get(i).getDistributionMoney();
			}
		}
		
		responseSpreadVO.setCompletedRecevingInfoList(infoList);
		responseSpreadVO.setCompletedReceivingMoney(money);
		return responseSpreadVO;
	}
	
	@Transactional(isolation=Isolation.READ_COMMITTED)
	public ResponseReceiveVO receive(String token, String xUserId, String xRoomId) throws SpreadException
	{
		SpreadVO spreadVO = spreadRepository.findByToken(token);
		int receivingMoney = 0;
		if(spreadVO == null)
		{
			//토큰에 대한 유효한 뿌리기가 없습니다.
			throw new SpreadException(SpreadErrorCode.INVALID_TOKEN.getCode(),SpreadErrorCode.INVALID_TOKEN.getDescription());		
		}
		
		if(spreadVO.getCreateUserId().equals(xUserId))
		{
			//자신이 뿌리기한 건은 자신이 받을 수 없습니다.
			throw new SpreadException(SpreadErrorCode.NOT_ALLOWED_SELF_RECEIVING.getCode(),SpreadErrorCode.NOT_ALLOWED_SELF_RECEIVING.getDescription());		
		}
		
		if(!spreadVO.getRoomId().equals(xRoomId))
		{
			//뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다.
			throw new SpreadException(SpreadErrorCode.NOT_ALLOWED_ROOM_ID_DIFFERENCE.getCode(),SpreadErrorCode.NOT_ALLOWED_ROOM_ID_DIFFERENCE.getDescription());		
		}
		if(spreadVO.getReceivedUserId().contains(xUserId))
		{
			//이미 해당 토큰에 대해 돈을 받은 사용자는 중복해서 받을 수 없습니다.
			throw new SpreadException(SpreadErrorCode.NOT_ALLOWED_DUPLICATION_RECEIVING.getCode(),SpreadErrorCode.NOT_ALLOWED_DUPLICATION_RECEIVING.getDescription());		
		}
		
		if(SpreadUtil.getGapOfTime(spreadVO.getCreateTime()) > 600)
		{
			///뿌리기 시점으로부터 10분이 지나 받기 기간이 만료되었습니다.
			throw new SpreadException(SpreadErrorCode.EXPIRE_SPREAD_TIME.getCode(),SpreadErrorCode.EXPIRE_SPREAD_TIME.getDescription());
		}

		for(int i=0; i<spreadVO.getDistribution().size(); i++)
		{
			//아직 할당이 안된 돈이라면 사용자에게 할당
			if(!spreadVO.getDistribution().get(i).isExpired())
			{
				receivingMoney = spreadVO.getDistribution().get(i).getDistributionMoney();
				spreadVO.getDistribution().get(i).setExpired(true);
				spreadVO.getDistribution().get(i).setReceivedUserId(xUserId);
				spreadVO.getReceivedUserId().add(xUserId);
				spreadRepository.save(spreadVO);
				ResponseReceiveVO responseReceiveVO = new ResponseReceiveVO();
				responseReceiveVO.setRecevingMoney(receivingMoney);
				return responseReceiveVO;
			}
		}
		//여기까지 오면 이미 모든 돈이 expired 되었음.
		throw new SpreadException(SpreadErrorCode.NOT_FOUND_VALID_RECEIVING.getCode(),SpreadErrorCode.NOT_FOUND_VALID_RECEIVING.getDescription());		
	}
}
