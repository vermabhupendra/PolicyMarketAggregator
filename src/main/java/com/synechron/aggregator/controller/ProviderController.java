package com.synechron.aggregator.controller;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.synechron.aggregator.model.Provider;
import com.synechron.aggregator.service.ProviderService;
import com.synechron.aggregator.util.Response;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProviderController {

	@Autowired
	private ProviderService providerService;

	/**
	 * Method to register insurance providers
	 * 
	 * @param provider
	 * @return
	 */
	@PostMapping(path = "/provider/register")
	public ResponseEntity<Response> registerProviders(@Valid @RequestBody Provider provider) {

		Provider providerObj = providerService.registerProvider(provider);
		if (providerObj != null) {
			return new ResponseEntity<Response>(new Response("Provider Registered Successfully", null),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Response>(new Response("Failed, Please try again.", null),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Method to get plans of any specific health provider
	 * 
	 * @param providerName
	 * @return
	 */
	@GetMapping(path = "/provider/getAllPlans/{providerName}")
	public ResponseEntity<Response> getAllPlansOfProvider(@PathVariable("providerName") String providerName) {

		Provider providerObj = providerService.checkProviderExists(providerName);
		if (providerObj != null) {

			final String url = providerObj.getProviderGetPlanUrl();
			log.debug(" Get plan rest web service name ======= : " + url);
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

			ResponseEntity<Response> result = restTemplate.exchange(url, HttpMethod.GET, entity, Response.class);

			log.debug(" Result returned by api ======= : " + result);
			return result;

		} else {
			return new ResponseEntity<Response>(new Response("No provider found.", null), HttpStatus.BAD_REQUEST);
		}
	}
}
