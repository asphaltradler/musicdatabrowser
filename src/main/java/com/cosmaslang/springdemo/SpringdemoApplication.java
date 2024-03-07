package com.cosmaslang.springdemo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
		
		CityRepository cityRepository = context.getBean(CityRepository.class);
		createCity(cityRepository, "Berlin", true, true);
		createCity(cityRepository, "Frankfurt", false, true);
		createCity(cityRepository, "München", false, true);
		
		//consolidateData(context);
		
		Iterable<City> allCities = cityRepository.findAll();
		System.out.printf("CityRepository %s contains %d entries\n\n", cityRepository.getClass().getName(), cityRepository.count());
		allCities.forEach(city -> System.out.printf("City: %s accessed %s times\n", city.getName(), city.getCount()));
	}
	
	private static void createCity(CityRepository cityRepository, String name, boolean isCapital, boolean onlyCreateNew) {
		if (!onlyCreateNew || cityRepository.findByName(name).size() == 0) {
			City city = new City(name, isCapital);
			cityRepository.save(city);
		}

	}
	
	private static void consolidateData(ConfigurableApplicationContext context) {
		CityRepository cityRepository = context.getBean(CityRepository.class);
		Iterator<City> allCities = cityRepository.findAll().iterator();
		
		cityRepository.deleteAll();

		List<City> cities = new ArrayList<City>();
		allCities.forEachRemaining(cities::add);
		Map<String, List<City>> uniqueCities = cities.stream()
			.collect(Collectors.groupingBy(City::getName));
		for (Entry<String, List<City>> entry: uniqueCities.entrySet()) {
			long count = entry.getValue().stream().mapToLong(city -> city.getCount()).sum();
			System.out.println(entry.getKey() + ": " + count);
			
			City newCity = entry.getValue().get(0);
			newCity.setCount(count);
			cityRepository.save(newCity);
		}
		
		
	}

}
