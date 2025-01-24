package currency.rate.service.controller;

import currency.rate.service.model.dto.CryptoRateDto;
import currency.rate.service.model.dto.CurrencyRateResponseDto;
import currency.rate.service.model.dto.FiatRateDto;
import currency.rate.service.service.CryptoRateService;
import currency.rate.service.service.FiatRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/currency-rates")
public class CurrencyRateController {

    private final CryptoRateService cryptoRateService;
    private final FiatRateService fiatRateService;

    @GetMapping
    public Mono<ResponseEntity<CurrencyRateResponseDto>> getCurrencyRates() {
        return Mono.zip(
                        cryptoRateService.getCryptoRates().collectList(),
                        fiatRateService.getFiatRates().collectList()
                )
                .map(tuple -> {
                    List<CryptoRateDto> cryptoRates = tuple.getT1();
                    List<FiatRateDto> fiatRates = tuple.getT2();
                    return CurrencyRateResponseDto.builder()
                            .cryptoRates(cryptoRates)
                            .fiatRates(fiatRates)
                            .build();
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
