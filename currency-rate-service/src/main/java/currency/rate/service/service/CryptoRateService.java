package currency.rate.service.service;

import currency.rate.service.model.dto.CryptoRateDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CryptoRateService {

    Flux<CryptoRateDto> getCryptoRates();

    Flux<CryptoRateDto> findAllFromDb();

    Mono<CryptoRateDto> saveOrUpdateRate(CryptoRateDto cryptoRateDto);

}
