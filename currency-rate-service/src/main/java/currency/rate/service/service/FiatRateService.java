package currency.rate.service.service;

import currency.rate.service.model.dto.FiatRateDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FiatRateService {

    Flux<FiatRateDto> getFiatRates();

    Flux<FiatRateDto> findAllFromDb();

    Mono<FiatRateDto> saveOrUpdateRate(FiatRateDto fiatRateDto);

}
