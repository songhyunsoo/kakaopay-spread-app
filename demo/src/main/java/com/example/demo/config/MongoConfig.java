package com.example.demo.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.example.demo.repository.SpreadRepository;
import com.example.demo.vo.SpreadVO;
import com.mongodb.client.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

@Configuration
public class MongoConfig implements ApplicationRunner{

	@Autowired
	SpreadRepository spreadRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {

	SpreadVO sd = new SpreadVO();
	sd.setCreateUserId(123);
	
	
	spreadRepository.insert(sd);
	}

	
	/*
	 * @Bean public MongoTemplate mongoTemplate() throws IOException {
	 * 
	 * MongoTemplate mongoTemplate = null;
	 * 
	 * //logger로 변경해야댐 System.out.println("====mongoDB Setting====="); MongodStarter
	 * starter = MongodStarter.getDefaultInstance();
	 * 
	 * String bindIp = "127.0.0.1"; int port = 27017;
	 * 
	 * IMongodConfig mongodConfig = new MongodConfigBuilder()
	 * .version(Version.Main.PRODUCTION) .net(new Net(bindIp, port,
	 * Network.localhostIsIPv6())) .build();
	 * 
	 * MongodExecutable mongodExecutable = null;
	 * 
	 * try { mongodExecutable = starter.prepare(mongodConfig); MongodProcess mongod
	 * = mongodExecutable.start();
	 * 
	 * 
	 * }
	 * 
	 * return null;
	 * 
	 * }
	 */
	
}
