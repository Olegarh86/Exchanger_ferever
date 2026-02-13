package dto;

import models.Currency;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(Integer id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
}
