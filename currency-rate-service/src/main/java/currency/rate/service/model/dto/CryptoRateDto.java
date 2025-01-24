package currency.rate.service.model.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CryptoRateDto {

    @JsonSetter("name")
    private String currency;

    @JsonSetter("value")
    private BigDecimal rate;

    @JsonGetter("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonGetter("rate")
    public BigDecimal getRate() {
        return rate;
    }

}
