package currency.rate.service.web;

import currency.rate.service.model.dto.CryptoRateDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@AllArgsConstructor
public class CryptoCurrencyRateClient implements CurrencyRateClient<CryptoRateDto> {

    private WebClient webClient;

    @Override
    public Flux<CryptoRateDto> getRates(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(CryptoRateDto.class)
                .onErrorResume(e -> {
                    log.debug("Crypto currency rate client got exception: {}", e.getMessage());
                    return Flux.empty();
                });
    }

}
