package currency.rate.service.web;

import currency.rate.service.model.dto.FiatRateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class FiatCurrencyRateApiClient implements CurrencyRateClient<FiatRateDto> {

    private final WebClient webClient;

    public FiatCurrencyRateApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Value("${fiat.api.key}")
    private String key;

    @Override
    public Flux<FiatRateDto> getRates(String url) {
        return webClient.get()
                .uri(url)
                .header("X-API-KEY", key)
                .retrieve()
                .bodyToFlux(FiatRateDto.class)
                .onErrorResume(e -> {
                    log.debug("Fiat currency rate client got exception: {}", e.getMessage());
                    return Flux.empty();
                });
    }

}
