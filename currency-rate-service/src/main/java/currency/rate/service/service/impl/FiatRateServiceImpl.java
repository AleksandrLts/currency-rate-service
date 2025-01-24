package currency.rate.service.service.impl;

import currency.rate.service.model.dto.FiatRateDto;
import currency.rate.service.model.mapper.FiatRateMapper;
import currency.rate.service.repository.FiatRateRepository;
import currency.rate.service.service.FiatRateService;
import currency.rate.service.web.CurrencyRateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiatRateServiceImpl implements FiatRateService {

    private final FiatRateRepository fiatRateRepository;
    private final CurrencyRateClient<FiatRateDto> fiatRateDtoCurrencyRateClient;
    private final FiatRateMapper fiatRateMapper;
    private final TransactionalOperator transactionalOperator;

    @Value("${fiat.api.url}")
    private String fiatUrl;

    @Override
    public Flux<FiatRateDto> getFiatRates() {
        return fiatRateDtoCurrencyRateClient
                .getRates(fiatUrl)
                .flatMap(this::saveOrUpdateRate)
                .switchIfEmpty(transactionalOperator.transactional(findAllFromDb()));
    }

    @Override
    @Transactional
    public Mono<FiatRateDto> saveOrUpdateRate(FiatRateDto fiatRateDto) {
        return fiatRateRepository.upsertFiatRate(fiatRateDto.getCurrency(), fiatRateDto.getRate())
                .doOnNext(rate -> log.debug("Fiat rate updated: {}", rate))
                .map(fiatRateMapper::toDto);
    }

    @Override
    public Flux<FiatRateDto> findAllFromDb() {
        return fiatRateRepository.findAll()
                .doOnNext(rate -> log.debug("Got fiat rates from db: {}", rate))
                .map(fiatRateMapper::toDto);

    }

}
