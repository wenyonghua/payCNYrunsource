package me.zohar;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
//@EnableMethodCache(basePackages = "me.zohar.runscore")
@EnableCreateCacheAnnotation
public class MerchantApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(MerchantApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
	}
	@PostConstruct
	void setDefaultTimezone() {
		//东八区
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
}
