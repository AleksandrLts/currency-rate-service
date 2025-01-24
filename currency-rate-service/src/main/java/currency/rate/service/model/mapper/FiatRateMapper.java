package currency.rate.service.model.mapper;

import currency.rate.service.model.FiatRate;
import currency.rate.service.model.dto.FiatRateDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FiatRateMapper {
    FiatRateDto toDto(FiatRate fiatRate);

    @InheritInverseConfiguration
    FiatRate toEntity(FiatRateDto fiatRateDto);

}
