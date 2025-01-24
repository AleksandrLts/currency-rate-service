package currency.rate.service.controller;

import currency.rate.service.model.dto.CryptoRateDto;
import currency.rate.service.model.dto.FiatRateDto;
import currency.rate.service.service.impl.CryptoRateServiceImpl;
import currency.rate.service.service.impl.FiatRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyRateControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CryptoRateServiceImpl cryptoRateService;

    @MockBean
    private FiatRateServiceImpl fiatRateService;

    @BeforeEach
    public void setup() {
        mockCryptoRateService();
        mockFiatRateService();
    }

    private void mockCryptoRateService() {
        when(cryptoRateService.getCryptoRates())
                .thenReturn(Flux.just(new CryptoRateDto("BTC", BigDecimal.valueOf(50000))));
    }

    private void mockFiatRateService() {
        when(fiatRateService.getFiatRates())
                .thenReturn(Flux.just(new FiatRateDto("USD", BigDecimal.valueOf(1.0))));
    }

    @Test
    public void givenValidServices_whenGetCurrencyRates_thenReturnsExpectedResponse() {
        webTestClient.get()
                .uri("/currency-rates")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.crypto_rates").isNotEmpty()
                .jsonPath("$.crypto_rates[0].currency").isEqualTo("BTC")
                .jsonPath("$.crypto_rates[0].rate").isEqualTo(50000)
                .jsonPath("$.fiat_rates").isNotEmpty()
                .jsonPath("$.fiat_rates[0].currency").isEqualTo("USD")
                .jsonPath("$.fiat_rates[0].rate").isEqualTo(1.0);
    }

    @Test
    public void givenEmptyCryptoRates_whenGetCurrencyRates_thenReturnsOnlyFiatRates() {
        when(cryptoRateService.getCryptoRates()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/currency-rates")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.crypto_rates").isEmpty()
                .jsonPath("$.fiat_rates").isNotEmpty()
                .jsonPath("$.fiat_rates[0].currency").isEqualTo("USD")
                .jsonPath("$.fiat_rates[0].rate").isEqualTo(1.0);
    }

    @Test
    public void givenEmptyFiatRates_whenGetCurrencyRates_thenReturnsOnlyCryptoRates() {
        when(fiatRateService.getFiatRates()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/currency-rates")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody()
                .jsonPath("$.crypto_rates").isNotEmpty()
                .jsonPath("$.crypto_rates[0].currency").isEqualTo("BTC")
                .jsonPath("$.crypto_rates[0].rate").isEqualTo(50000)
                .jsonPath("$.fiat_rates").isEmpty();
    }
}