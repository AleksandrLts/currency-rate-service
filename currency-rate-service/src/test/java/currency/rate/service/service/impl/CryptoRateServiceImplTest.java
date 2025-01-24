package currency.rate.service.service.impl;

import currency.rate.service.model.CryptoRate;
import currency.rate.service.model.dto.CryptoRateDto;
import currency.rate.service.model.mapper.CryptoRateMapper;
import currency.rate.service.repository.CryptoRateRepository;
import currency.rate.service.web.CurrencyRateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CryptoRateServiceImplTest {

    @Mock
    private CryptoRateRepository cryptoRateRepository;

    @Mock
    private CurrencyRateClient<CryptoRateDto> cryptoRateDtoCurrencyRateClient;

    @Mock
    private CryptoRateMapper cryptoRateMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private CryptoRateServiceImpl cryptoRateService;

    private static final String CRYPTO_URL = "http://localhost:8080/crypto-currency-rates";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(cryptoRateService, "cryptoUrl", CRYPTO_URL);
    }

    @Test
    public void givenRatesFromApi_whenGetCryptoRates_thenReturnsMappedRates() {
        CryptoRateDto cryptoRateDto = mockCryptoRateDto();
        when(cryptoRateDtoCurrencyRateClient.getRates(CRYPTO_URL)).thenReturn(Flux.just(cryptoRateDto));
        when(cryptoRateRepository.upsertCryptoRate(eq("ETH"), any()))
                .thenReturn(Mono.just(mockCryptoRate()));
        when(cryptoRateMapper.toDto(any(CryptoRate.class))).thenReturn(cryptoRateDto);
        when(cryptoRateRepository.findAll()).thenReturn(Flux.empty());
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Flux<CryptoRateDto> result = cryptoRateService.getCryptoRates();

        StepVerifier.create(result)
                .expectNext(cryptoRateDto)
                .verifyComplete();

        verify(cryptoRateDtoCurrencyRateClient, times(1)).getRates(CRYPTO_URL);
        verify(cryptoRateRepository, times(1)).upsertCryptoRate(anyString(), any());
        verify(cryptoRateMapper, times(1)).toDto(any(CryptoRate.class));
    }

    @Test
    public void givenCryptoRateDto_whenSaveOrUpdateRate_thenSavesAndReturnsMappedRate() {
        CryptoRateDto cryptoRateDto = mockCryptoRateDto();
        when(cryptoRateRepository.upsertCryptoRate(eq("ETH"), any()))
                .thenReturn(Mono.just(mockCryptoRate()));
        when(cryptoRateMapper.toDto(any(CryptoRate.class))).thenReturn(cryptoRateDto);

        Mono<CryptoRateDto> result = cryptoRateService.saveOrUpdateRate(cryptoRateDto);

        StepVerifier.create(result)
                .expectNext(cryptoRateDto)
                .verifyComplete();

        verify(cryptoRateRepository, times(1)).upsertCryptoRate(anyString(), any());
        verify(cryptoRateMapper, times(1)).toDto(any(CryptoRate.class));
    }

    @Test
    public void givenCryptoRatesInDb_whenFindAllFromDb_thenReturnsMappedRates() {
        CryptoRate cryptoRate = mockCryptoRate();
        CryptoRateDto cryptoRateDto = mockCryptoRateDto();
        when(cryptoRateRepository.findAll()).thenReturn(Flux.just(cryptoRate));
        when(cryptoRateMapper.toDto(any(CryptoRate.class))).thenReturn(cryptoRateDto);

        Flux<CryptoRateDto> result = cryptoRateService.findAllFromDb();

        StepVerifier.create(result)
                .expectNext(cryptoRateDto)
                .verifyComplete();

        verify(cryptoRateRepository, times(1)).findAll();
        verify(cryptoRateMapper, times(1)).toDto(any(CryptoRate.class));
    }

    @Test
    public void givenEmptyRatesFromApi_whenGetCryptoRates_thenFallbacksToDbRates() {
        CryptoRate cryptoRate = mockCryptoRate();
        CryptoRateDto cryptoRateDto = mockCryptoRateDto();
        when(cryptoRateDtoCurrencyRateClient.getRates(CRYPTO_URL)).thenReturn(Flux.empty());
        when(cryptoRateRepository.findAll()).thenReturn(Flux.just(cryptoRate));
        when(cryptoRateMapper.toDto(any(CryptoRate.class))).thenReturn(cryptoRateDto);
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Flux<CryptoRateDto> result = cryptoRateService.getCryptoRates();

        StepVerifier.create(result)
                .expectNext(cryptoRateDto)
                .verifyComplete();

        verify(cryptoRateDtoCurrencyRateClient, times(1)).getRates(CRYPTO_URL);
        verify(cryptoRateRepository, never()).upsertCryptoRate(anyString(), any());
        verify(cryptoRateRepository, times(1)).findAll();
        verify(cryptoRateMapper, times(1)).toDto(any(CryptoRate.class));
    }

    private CryptoRateDto mockCryptoRateDto() {
        return new CryptoRateDto("ETH", BigDecimal.valueOf(3000.0));
    }

    private CryptoRate mockCryptoRate() {
        return new CryptoRate("ETH", BigDecimal.valueOf(3000.0));
    }
}