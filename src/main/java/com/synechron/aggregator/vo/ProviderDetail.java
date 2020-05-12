package com.synechron.aggregator.vo;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class ProviderDetail {

	private String planId;
	private String planName;
	private String providerName;
//	private String planPremiumAmount;
	private double planCoverage;
}
