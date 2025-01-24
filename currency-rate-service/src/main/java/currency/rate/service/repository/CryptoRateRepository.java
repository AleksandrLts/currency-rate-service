package currency.rate.service.repository;

import currency.rate.service.model.CryptoRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface CryptoRateRepository extends R2dbcRepository<CryptoRate, Long> {

    @Query("""
        INSERT INTO crypto_rates (currency, rate) 
        VALUES (:currency, :rate)
        ON CONFLICT (currency) 
        DO UPDATE SET rate = :rate
        RETURNING *
        """)
    Mono<CryptoRate> upsertCryptoRate(@Param("currency") String currency, @Param("rate") BigDecimal rate);

}
