package models;

import java.math.BigDecimal;

public record ExchangeRate(Integer id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this(0, baseCurrency, targetCurrency, rate);
    }
}
