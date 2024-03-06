package com.cosmaslang.springdemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	@RestController
	public class SaySomethingController {
		@Autowired
		@Qualifier("sayHelloService")
		SaySomethingService service;
		
		@GetMapping("/")
		public String root() {
			return service.saySomething();
		}
	}
	
	@RestController
	public class CityController {
		@Autowired
		CityRepository cityRepository;
		
		@GetMapping("/city")
		public List<City> getCities(@RequestParam(value = "name", required = false) String name) {
			List<City> cities;
			if (StringUtils.hasLength(name)) {
				cities = cityRepository.findByName(name);
			} else {
				Iterator<City> allCities = cityRepository.findAll().iterator();
				cities = new ArrayList<City>();
				allCities.forEachRemaining(cities::add);
			}
			return cities;
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
		
		//City berlin = new City("Berlin", true);
		//City mainz = new City("Mainz", false);
		
		CityRepository cityRepo = context.getBean(CityRepository.class);
		//cityRepo.save(berlin);
		//cityRepo.save(mainz);
		Iterable<City> allCities = cityRepo.findAll();
		System.out.printf("CityRepository %s contains %d entries\n\n", cityRepo.getClass().getName(), cityRepo.count());
		allCities.forEach(city -> System.out.printf("City: %s\n", city.getName()));
		
	}

}
