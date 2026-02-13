package mappers;

import dto.ExchangeRateResponseDto;
import models.ExchangeRate;
import org.mapstruct.factory.Mappers;
import dto.CurrencyResponseDto;
import models.Currency;

import java.util.List;

@org.mapstruct.Mapper
public interface ExchangerMapper {
    ExchangerMapper INSTANCE = Mappers.getMapper(ExchangerMapper.class);

    CurrencyResponseDto currencyToDto(Currency currency);
    List<CurrencyResponseDto> currencyToDtoList(List<Currency> currencies);
    ExchangeRateResponseDto exchangeRateToDto(ExchangeRate exchangeRate);
    List<ExchangeRateResponseDto> exchangeRateToDtoList(List<ExchangeRate> exchangeRates);
}
