package currency.rate.service.model.mapper;

import currency.rate.service.model.CryptoRate;
import currency.rate.service.model.dto.CryptoRateDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CryptoRateMapper {
    CryptoRateDto toDto(CryptoRate cryptoRate);

    @InheritInverseConfiguration
    CryptoRate toEntity(CryptoRateDto cryptoRateDto);

}
