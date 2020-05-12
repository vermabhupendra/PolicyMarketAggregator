package com.synechron.aggregator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.synechron.aggregator.model.Provider;
import com.synechron.aggregator.repository.ProviderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProviderService {

	@Autowired
	public ProviderRepository providerRepository;

	public Provider registerProvider(Provider provider) {
		return providerRepository.saveAndFlush(provider);
	}

	public Provider checkProviderExists(String providerName) {
		log.debug("Provider Name ====== :" + providerName);
		Optional<Provider> providerObj = providerRepository.findByProviderName(providerName);
		return providerObj.isPresent() ? providerObj.get() : null;
	}

	public List<Provider> getAllProviders() {
		return providerRepository.findAll();
	}

	public Provider getById(int id) {
		return providerRepository.getOne(id);
	}
}
