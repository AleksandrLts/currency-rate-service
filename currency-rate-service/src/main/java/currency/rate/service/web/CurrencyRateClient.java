package currency.rate.service.web;

import reactor.core.publisher.Flux;

public interface CurrencyRateClient<T> {

    Flux<T> getRates(String url);

}
