package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.vo.SpreadVO;

public interface SpreadRepository extends MongoRepository<SpreadVO, String>
{
	

}
