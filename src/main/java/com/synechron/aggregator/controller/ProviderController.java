package com.synechron.aggregator.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONTokener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import com.synechron.aggregator.vo.ProviderDetail;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProviderController {

	@Autowired
	private ProviderService providerService;

	private static RestTemplate restTemplate;

	@GetMapping(path = "/provider/getAllPlans")
	public ResponseEntity<Response> getAllPlans() {
		List<List<ProviderDetail>> mainList = new ArrayList<List<ProviderDetail>>();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		restTemplate = new RestTemplate();
		String url;
		JSONArray jsonArray1 = new JSONArray();
		List<Provider> list = providerService.getAllProviders();
		log.debug("=========== " + list);
		for (Provider temp : list) {
			url = temp.getProviderGetPlanUrl();
			jsonArray1 = restTemplate.exchange(url, HttpMethod.GET, entity, JSONArray.class).getBody();
			log.debug("JSON Array : =====" + jsonArray1.toString());
			try {
				String str = jsonArray1.toString();
				ArrayList<String> arr = new ArrayList<>();
				int pos1 = 0, pos2;
				String str2 = str.substring(1, str.length() - 1);
				int index1 = str2.lastIndexOf("},");
				for (int i = 0; i < str2.length(); i++) {
					if (i <= index1) {
						char x = str2.charAt(i); // }
						char y = str2.charAt(i + 1); // ,
						if (x == '}' && (y == ',' || i + 1 == str2.length())) {
							pos2 = i;
							String sub1 = str2.substring(pos1, pos2 + 1);
							pos1 = i + 2;
							arr.add(sub1);
						}
					}
				}

				String lastStr = str2.substring(index1 + 2, str2.length());
				arr.add(lastStr);
				List<ProviderDetail> subList = getProvidersPlanList(arr, temp.getProviderId());
				mainList.add(subList);
			} catch (Exception ex) {
				log.debug(ex.getMessage());
			}
		}
		return new ResponseEntity<Response>(new Response("List of Plans.", mainList), HttpStatus.BAD_REQUEST);

		// method-2
//		List<Provider> providers = providerService.getAllProviders();
//		for (Provider providerObj : providers) {
//			final String url = providerObj.getProviderGetPlanUrl();
//			log.debug("web service url ======= : " + url);
//			getDataFromUrl(providerObj);
//		}
//
//		if (providers.size() != 0) {
////			log.debug(providers);
//			return new ResponseEntity<Response>(new Response("List of Plans.", providers), HttpStatus.BAD_REQUEST);
//		} else {
//			return new ResponseEntity<Response>(new Response("No provider found.", null), HttpStatus.BAD_REQUEST);
//		}
	}

	public List<ProviderDetail> getProvidersPlanList(ArrayList<String> listdata, int id) {
		List<ProviderDetail> subList = new ArrayList<>();
		for (String temp : listdata) {
			ProviderDetail obj = new ProviderDetail();
			if (temp.charAt(0) != '{') {
				temp = '{' + temp;
			} else if (temp.charAt(temp.length() - 1) != '}') {
				temp = temp + '}';
			}
			obj = extractData(temp, id);
			subList.add(obj);
		}
		return subList;
	}

	public ProviderDetail extractData(String temp, int id) {
		String providerPlanName = "", providerPlanId = "", providerPlanCoverage = "", providerProviderName = "";
		String providerPlanNameValue = "", providerPlanIdValue = "", providerProviderNameValue = "";
		double providerPlanCoverageValue = 0;

		ProviderDetail tempObj = new ProviderDetail();
		Provider p = providerService.getById(id);

		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(p.getProviderResponse());
			JSONObject json2 = (JSONObject) parser.parse(temp);
			for (Object o : json.entrySet()) {
				if (o.toString().contains("planId")) {
					String str = o.toString();
					providerPlanId = str.substring(str.lastIndexOf("=") + 1);
				}

				if (o.toString().contains("providerName")) {
					String str = o.toString();
					providerProviderName = str.substring(str.lastIndexOf("=") + 1);
				}

				if (o.toString().contains("planName")) {
					String str = o.toString();
					providerPlanName = str.substring(str.lastIndexOf("=") + 1);
				}

				if (o.toString().contains("planCoverage")) {
					String str = o.toString();
					providerPlanCoverage = str.substring(str.lastIndexOf("=") + 1);
				}
			}

			for (Object o : json2.entrySet()) {

				if (o.toString().contains(providerPlanId)) {
					String str = o.toString();
					providerPlanIdValue = str.substring(str.lastIndexOf("=") + 1);
				}

				if (o.toString().contains(providerProviderName)) {
					String str = o.toString();
					providerProviderNameValue = str.substring(str.lastIndexOf("=") + 1);
				}

				if (o.toString().contains(providerPlanName)) {
					String str = o.toString();
					providerPlanNameValue = str.substring(str.lastIndexOf("=") + 1);
				}

				if (o.toString().contains(providerPlanCoverage)) {
					String str = o.toString();
					String tempStr = str.substring(str.lastIndexOf("=") + 1);
					providerPlanCoverageValue = Double.parseDouble(tempStr);
				}
			}

			tempObj.setPlanId(providerPlanIdValue);
			tempObj.setProviderName(providerProviderNameValue);
			tempObj.setPlanName(providerPlanNameValue);
			tempObj.setPlanCoverage(providerPlanCoverageValue);
		} catch (Exception e) {
			log.info("" + e.getMessage());
		}
		return tempObj;
	}

	private void getDataFromUrl(Provider providerObj) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		log.debug("Response Data in DB ======= " + providerObj.getProviderResponse());
//		Response Data in DB ======= 
//		{
//		"planId": "planId","planName": "planName","providerName": "insuranceProviderName","planCoverage":"planCoverage"
//		}

		ResponseEntity<Response> result = restTemplate.exchange(providerObj.getProviderGetPlanUrl(), HttpMethod.GET,
				entity, Response.class);
//		log.debug();
//		log.debug("Providers Plans JSON Array Result ===== " + result.getBody().getData());
//		Providers Plans JSON Array Result ===== 
//		[
//		{planId=1, planName=Max Silver Plan, insuranceProviderName=Max Life Insurance, planType=Life, planPremiumAmount=60000.0, planCoverage=1000000.0, age=21, city=Indore, isActive=1}, 
//		{planId=2, planName=Max Super Crore, insuranceProviderName=Max Life Insurance, planType=Life, planPremiumAmount=120000.0, planCoverage=1.0E7, age=25, city=Ujjain, isActive=1}
//		]

//		JSONArray jsonArray1 = restTemplate.exchange(providerObj.getProviderGetPlanUrl(), HttpMethod.GET, entity, JSONArray.class).getBody();
//		log.debug();
//		log.debug("JSON Array : ===== " + result.getBody().getData());
//		JSON Array : =====
//		[
//		{"planId":1,"planName":"Max Silver Plan","insuranceProviderName":"Max Life Insurance","planType":"Life","planPremiumAmount":60000.0,"planCoverage":1000000.0,"age":21,"city":"Indore","isActive":1},
//		{"planId":2,"planName":"Max Super Crore","insuranceProviderName":"Max Life Insurance","planType":"Life","planPremiumAmount":120000.0,"planCoverage":1.0E7,"age":25,"city":"Ujjain","isActive":1}
//		]

		extractListData(providerObj.getProviderResponse(), result.getBody().getData());

	}

	private void extractListData(String strResponse, Object jsonArrayData) {
		// TODO Auto-generated method stub
		String s1 = strResponse.substring(strResponse.indexOf("{") + 1, strResponse.indexOf("}"));

		String[] strArray = s1.split(",");
//		log.debug("String array========= " + Arrays.toString(strArray));
//		"planId": "planId", "planName": "planName", "providerName": "providerName", "planCoverage":"planCover"

		for (String entries : strArray) {
			String str2 = entries.substring(entries.indexOf(":")).trim();
			String strValue = str2.substring(str2.indexOf("\"") + 1, str2.lastIndexOf("\""));
			log.debug("final key ========= " + strValue);

		}

		log.debug("==============  Now For Json Array ==================");
		String strJsonArray = jsonArrayData.toString();

		String strNew = strJsonArray.substring(1, strJsonArray.length() - 1);
		log.debug("json array to object ====== " + strNew);
//		json array to object ====== 
//		{planId=1, planName=Max Silver Plan, insuranceProviderName=Max Life Insurance, planType=Life, planPremiumAmount=60000.0, planCoverage=1000000.0, age=21, city=Indore, isActive=1},
//		{planId=2, planName=Max Super Crore, insuranceProviderName=Max Life Insurance, planType=Life, planPremiumAmount=120000.0, planCoverage=1.0E7, age=25, city=Ujjain, isActive=1}

		String[] strDataJsonArray = strNew.split("},");
		log.debug("String Data Json Array========= " + Arrays.toString(strDataJsonArray));

	}

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
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			ResponseEntity<Response> result = restTemplate.exchange(url, HttpMethod.GET, entity, Response.class);
			return result;
		} else {
			return new ResponseEntity<Response>(new Response("No provider found.", null), HttpStatus.BAD_REQUEST);
		}
	}
}
