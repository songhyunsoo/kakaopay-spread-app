package com.kakaopay.spread.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kakaopay.spread.vo.SpreadVO;

public interface SpreadRepository extends MongoRepository<SpreadVO, String>
{
	
	SpreadVO findByToken(String token);

}
