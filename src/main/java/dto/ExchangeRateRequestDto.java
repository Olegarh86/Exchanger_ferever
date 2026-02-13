package dto;

import models.Currency;

import java.math.BigDecimal;

public record ExchangeRateRequestDto(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
}
