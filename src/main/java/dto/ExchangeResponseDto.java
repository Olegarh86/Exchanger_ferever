package dto;

import models.Currency;

import java.math.BigDecimal;

public record ExchangeResponseDto(Currency baseCurrency, Currency TargetCurrency, BigDecimal rate, BigDecimal amount,
                                  BigDecimal convertedAmount) {
}
