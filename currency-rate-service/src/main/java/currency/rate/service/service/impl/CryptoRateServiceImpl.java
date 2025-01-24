package currency.rate.service.service.impl;

import currency.rate.service.model.dto.CryptoRateDto;
import currency.rate.service.model.mapper.CryptoRateMapper;
import currency.rate.service.repository.CryptoRateRepository;
import currency.rate.service.service.CryptoRateService;
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
public class CryptoRateServiceImpl implements CryptoRateService {

    private final CryptoRateRepository cryptoRateRepository;
    private final CurrencyRateClient<CryptoRateDto> cryptoRateDtoCurrencyRateClient;
    private final CryptoRateMapper cryptoRateMapper;
    private final TransactionalOperator transactionalOperator;

    @Value("${crypto.api.url}")
    private String cryptoUrl;

    @Override
    public Flux<CryptoRateDto> getCryptoRates() {
        return cryptoRateDtoCurrencyRateClient
                .getRates(cryptoUrl)
                .flatMap(this::saveOrUpdateRate)
                .switchIfEmpty(transactionalOperator.transactional(findAllFromDb()));
    }

    @Override
    @Transactional
    public Mono<CryptoRateDto> saveOrUpdateRate(CryptoRateDto cryptoRateDto) {
        return cryptoRateRepository.upsertCryptoRate(cryptoRateDto.getCurrency(), cryptoRateDto.getRate())
                .doOnNext(rate -> log.debug("Crypto rate updated: {}", rate))
                .map(cryptoRateMapper::toDto);
    }

    @Override
    public Flux<CryptoRateDto> findAllFromDb() {
        return cryptoRateRepository.findAll()
                .doOnNext(rate -> log.debug("Got crypto rates from db: {}", rate))
                .map(cryptoRateMapper::toDto);

    }

}
