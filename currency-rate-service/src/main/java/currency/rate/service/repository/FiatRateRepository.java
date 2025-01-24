package currency.rate.service.repository;

import currency.rate.service.model.FiatRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface FiatRateRepository extends R2dbcRepository<FiatRate, Long> {

    @Query("""
        INSERT INTO fiat_rates (currency, rate) 
        VALUES (:currency, :rate)
        ON CONFLICT (currency) 
        DO UPDATE SET rate = :rate
        RETURNING *
        """)
    Mono<FiatRate> upsertFiatRate(@Param("currency") String currency, @Param("rate") BigDecimal rate);

}
