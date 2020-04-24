package com.synechron.aggregator.repositor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.synechron.aggregator.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {

	Optional<Provider> findByProviderName(String providerName);

}
