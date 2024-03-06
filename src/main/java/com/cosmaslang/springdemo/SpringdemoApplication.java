package com.cosmaslang.springdemo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.cosmaslang.springdemo.db.CityRepository;
import com.cosmaslang.springdemo.db.entities.City;

@SpringBootApplication
public class SpringdemoApplication {

	public interface SaySomethingService {
		public String saySomething();
	}
	
	@Component
	@Qualifier("sayHelloService")
	public class SayHelloService implements SaySomethingService {
		@Override
		public String saySomething() {
			return "Hello from SayHelloService";
		}
	}
	
	@Configuration  
	public class SaySomethingConfiguration {
        @Bean
        @Primary
        SaySomethingConfigurableService getService() {
			SaySomethingConfigurableService service = new SaySomethingConfigurableService();
			service.setWhatToSay("Hello from ConfigurableService");
			return service;
		}
	}
	
	public class SaySomethingConfigurableService implements SaySomethingService {
		private String whatToSay;
		
		public String getWhatToSay() {
			return whatToSay;
		}
		public void setWhatToSay(String whatToSay) {
			this.whatToSay = whatToSay;
		}
		@Override
		public String saySomething() {
			return whatToSay;
		}
	}
	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringdemoApplication.class, args);
		SaySomethingService service = context.getBean(SaySomethingService.class);
		System.out.printf("SaySomethingService %s: %s\n", service.getClass().getName(), service.saySomething());
		
		CityRepository cityRepo = context.getBean(CityRepository.class);
		if (cityRepo.findByName("Köln").size() == 0) {
			City newCity = new City("Köln", false);
			cityRepo.save(newCity);
		}
		
		Iterable<City> allCities = cityRepo.findAll();
		System.out.printf("CityRepository %s contains %d entries\n\n", cityRepo.getClass().getName(), cityRepo.count());
		allCities.forEach(city -> System.out.printf("City: %s accessed %s times\n", city.getName(), city.getAccessCounter()));
	}

}
