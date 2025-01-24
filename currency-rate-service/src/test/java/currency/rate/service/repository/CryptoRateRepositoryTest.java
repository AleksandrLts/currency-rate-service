package currency.rate.service.repository;

import currency.rate.service.model.CryptoRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class CryptoRateRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private CryptoRateRepository cryptoRateRepository;
    private static final String CURRENCY = "BTC";
    private static final BigDecimal INITIAL_RATE = BigDecimal.valueOf(50000.45);

    @Test
    void connectionEstablished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void givenCryptoRate_whenUpsert_thenSave() {
        Mono<CryptoRate> result = cryptoRateRepository.upsertCryptoRate(CURRENCY, INITIAL_RATE);

        StepVerifier.create(result)
                .assertNext(cryptoRate -> {
                    assertThat(cryptoRate.getCurrency()).isEqualTo(CURRENCY);
                    assertThat(cryptoRate.getRate().stripTrailingZeros()).isEqualTo(INITIAL_RATE);
                })
                .verifyComplete();
    }

    @Test
    void givenExistCurrency_whenUpsertSameCurrency_thenUpdateExist() {
        cryptoRateRepository.upsertCryptoRate(CURRENCY, INITIAL_RATE).block();

        BigDecimal updatedRate = BigDecimal.valueOf(55000.45);
        Mono<CryptoRate> result = cryptoRateRepository.upsertCryptoRate(CURRENCY, updatedRate);

        StepVerifier.create(result)
                .assertNext(cryptoRate -> {
                    assertThat(cryptoRate.getCurrency()).isEqualTo(CURRENCY);
                    assertThat(cryptoRate.getRate().stripTrailingZeros()).isEqualTo(updatedRate);
                })
                .verifyComplete();
    }
}