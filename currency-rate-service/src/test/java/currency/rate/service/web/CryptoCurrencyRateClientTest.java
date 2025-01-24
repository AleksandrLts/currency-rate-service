package currency.rate.service.web;

import currency.rate.service.model.dto.CryptoRateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CryptoCurrencyRateClientTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CryptoCurrencyRateClient cryptoCurrencyRateClient;

    private static final String URL = "http://localhost:8080/crypto-currency-rates";

    @BeforeEach
    public void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(URL)).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void givenValidResponse_whenGetRates_thenReturnsCryptoRateDtoFlux() {
        CryptoRateDto cryptoRateDto = new CryptoRateDto("BTC", BigDecimal.valueOf(50000));

        when(responseSpec.bodyToFlux(CryptoRateDto.class)).thenReturn(Flux.just(cryptoRateDto));

        Flux<CryptoRateDto> result = cryptoCurrencyRateClient.getRates(URL);

        StepVerifier.create(result)
                .expectNext(cryptoRateDto)
                .verifyComplete();

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpecMock, times(1)).uri(URL);
        verify(requestHeadersUriSpecMock, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToFlux(CryptoRateDto.class);
    }

    @Test
    public void givenErrorResponse_whenGetRates_thenReturnsEmptyFlux() {
        when(responseSpec.bodyToFlux(CryptoRateDto.class))
                .thenReturn(Flux.error(new RuntimeException()));

        Flux<CryptoRateDto> result = cryptoCurrencyRateClient.getRates(URL);

        StepVerifier.create(result)
                .verifyComplete();

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpecMock, times(1)).uri(URL);
        verify(requestHeadersUriSpecMock, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToFlux(CryptoRateDto.class);
    }

}