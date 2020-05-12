package com.synechron.aggregator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_providers")
@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class Provider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int providerId;

	@NotBlank(message = "Provider name must not be empty.")
	@Size(min = 2, message = "Provider name should be at least 2 character long.")
	@NotNull(message = "Provider name must not be null.")
	private String providerName;

	@NotBlank(message = "Provider plan url must not be empty.")
	@Size(min = 2, message = "Provider plan url should be at least 2 character long.")
	@NotNull(message = "Provider plan url must not be null.")
	private String providerGetPlanUrl;

	@NotBlank(message = "Provider response format must not be empty.")
	@Size(min = 2, message = "Provider response format should be at least 2 character long.")
	@NotNull(message = "Provider response format must not be null.")
	private String providerResponseFormat;

	@NotBlank(message = "Provider response must not be empty.")
	@Size(min = 2, message = "Provider response should be at least 2 character long.")
	@NotNull(message = "Provider response must not be null.")
	private String providerResponse;

}
