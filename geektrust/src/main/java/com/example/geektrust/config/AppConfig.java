package com.example.geektrust.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.geektrust.dto.MetroCardDetails;
import com.example.geektrust.response.PassengerSummary;

@Configuration
public class AppConfig {

	@Bean
	public List<PassengerSummary> passengerSummaryBean() {
		return new ArrayList<>();
	}
	
	@Bean
	public List<MetroCardDetails> metrocardListBean() {
		return new ArrayList<>();
	}
}
