package currency.rate.service.service.impl;

import currency.rate.service.model.FiatRate;
import currency.rate.service.model.dto.FiatRateDto;
import currency.rate.service.model.mapper.FiatRateMapper;
import currency.rate.service.repository.FiatRateRepository;
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

class FiatRateServiceImplTest {

    @Mock
    private FiatRateRepository fiatRateRepository;

    @Mock
    private CurrencyRateClient<FiatRateDto> fiatRateDtoCurrencyRateClient;

    @Mock
    private FiatRateMapper fiatRateMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private FiatRateServiceImpl fiatRateService;

    private static final String FIAT_URL = "http://localhost:8080/fiat-currency-rates";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fiatRateService, "fiatUrl", FIAT_URL);
    }

    @Test
    public void givenRatesFromApi_whenGetFiatRates_thenReturnsMappedRates() {
        FiatRateDto fiatRateDto = new FiatRateDto("EUR", BigDecimal.valueOf(3.4));
        when(fiatRateDtoCurrencyRateClient.getRates(FIAT_URL)).thenReturn(Flux.just(fiatRateDto));
        when(fiatRateRepository.upsertFiatRate(eq("EUR"), any()))
                .thenReturn(Mono.just(new FiatRate("EUR", BigDecimal.valueOf(3.4))));
        when(fiatRateMapper.toDto(any(FiatRate.class))).thenReturn(fiatRateDto);
        when(fiatRateRepository.findAll()).thenReturn(Flux.empty());
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Flux<FiatRateDto> result = fiatRateService.getFiatRates();

        StepVerifier.create(result)
                .expectNext(fiatRateDto)
                .verifyComplete();

        verify(fiatRateDtoCurrencyRateClient, times(1)).getRates(FIAT_URL);
        verify(fiatRateRepository, times(1)).upsertFiatRate(anyString(), any());
        verify(fiatRateMapper, times(1)).toDto(any(FiatRate.class));

    }

    @Test
    public void givenFiatRateDto_whenSaveOrUpdateRate_thenSavesAndReturnsMappedRate() {
        FiatRateDto fiatRateDto = new FiatRateDto("EUR", BigDecimal.valueOf(3.4));
        when(fiatRateRepository.upsertFiatRate(eq("EUR"), any()))
                .thenReturn(Mono.just(new FiatRate("EUR", BigDecimal.valueOf(3.4))));
        when(fiatRateMapper.toDto(any(FiatRate.class))).thenReturn(fiatRateDto);

        Mono<FiatRateDto> result = fiatRateService.saveOrUpdateRate(fiatRateDto);

        StepVerifier.create(result)
                .expectNext(fiatRateDto)
                .verifyComplete();

        verify(fiatRateRepository, times(1)).upsertFiatRate(anyString(), any());
        verify(fiatRateMapper, times(1)).toDto(any(FiatRate.class));
    }

    @Test
    public void givenFiatRatesInDb_whenFindAllFromDb_thenReturnsMappedRates() {
        FiatRate fiatRate = new FiatRate("EUR", BigDecimal.valueOf(3.4));
        FiatRateDto fiatRateDto = new FiatRateDto("EUR", BigDecimal.valueOf(3.4));
        when(fiatRateRepository.findAll()).thenReturn(Flux.just(fiatRate));
        when(fiatRateMapper.toDto(any(FiatRate.class))).thenReturn(fiatRateDto);

        Flux<FiatRateDto> result = fiatRateService.findAllFromDb();

        StepVerifier.create(result)
                .expectNext(fiatRateDto)
                .verifyComplete();

        verify(fiatRateRepository, times(1)).findAll();
        verify(fiatRateMapper, times(1)).toDto(any(FiatRate.class));
    }

    @Test
    public void givenEmptyRatesFromApi_whenGetFiatRates_thenFallbacksToDbRates() {
        FiatRate fiatRate = new FiatRate("USD", BigDecimal.valueOf(1.2));
        FiatRateDto fiatRateDto = new FiatRateDto("USD", BigDecimal.valueOf(1.2));
        when(fiatRateDtoCurrencyRateClient.getRates(FIAT_URL)).thenReturn(Flux.empty());
        when(fiatRateRepository.findAll()).thenReturn(Flux.just(fiatRate));
        when(fiatRateMapper.toDto(any(FiatRate.class))).thenReturn(fiatRateDto);
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Flux<FiatRateDto> result = fiatRateService.getFiatRates();

        StepVerifier.create(result)
                .expectNext(fiatRateDto)
                .verifyComplete();

        verify(fiatRateDtoCurrencyRateClient, times(1)).getRates(FIAT_URL);
        verify(fiatRateRepository, never()).upsertFiatRate(anyString(), any());
        verify(fiatRateRepository, times(1)).findAll();
        verify(fiatRateMapper, times(1)).toDto(any(FiatRate.class));
    }

}