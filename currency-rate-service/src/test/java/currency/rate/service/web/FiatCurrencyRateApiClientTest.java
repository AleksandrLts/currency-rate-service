package currency.rate.service.web;

import currency.rate.service.model.dto.FiatRateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FiatCurrencyRateApiClientTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private FiatCurrencyRateApiClient fiatCurrencyRateApiClient;

    private static final String URL = "http://localhost:8080/fiat-currency-rates";
    private static final String HEADER_NAME = "X-API-KEY";
    private static final String HEADER_VALUE = "test-api-key";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(fiatCurrencyRateApiClient, "key", HEADER_VALUE);
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(URL)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(HEADER_NAME, HEADER_VALUE)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void givenValidResponse_whenGetRates_thenReturnsFiatRateDtoFlux() {
        FiatRateDto fiatRateDto = new FiatRateDto("USD", BigDecimal.valueOf(1.0));

        when(responseSpec.bodyToFlux(FiatRateDto.class)).thenReturn(Flux.just(fiatRateDto));

        Flux<FiatRateDto> result = fiatCurrencyRateApiClient.getRates(URL);

        StepVerifier.create(result)
                .expectNext(fiatRateDto)
                .verifyComplete();

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpecMock, times(1)).uri(URL);
        verify(requestHeadersSpecMock, times(1)).header(HEADER_NAME, HEADER_VALUE);
        verify(requestHeadersSpecMock, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToFlux(FiatRateDto.class);
    }

    @Test
    public void givenErrorResponse_whenGetRates_thenReturnsEmptyFlux() {
        when(responseSpec.bodyToFlux(FiatRateDto.class))
                .thenReturn(Flux.error(new RuntimeException()));

        Flux<FiatRateDto> result = fiatCurrencyRateApiClient.getRates(URL);

        StepVerifier.create(result)
                .verifyComplete();

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpecMock, times(1)).uri(URL);
        verify(requestHeadersSpecMock, times(1)).header(HEADER_NAME, HEADER_VALUE);
        verify(requestHeadersSpecMock, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToFlux(FiatRateDto.class);
    }
}