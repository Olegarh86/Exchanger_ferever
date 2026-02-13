package service;

import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRequestDto;
import dto.ExchangeResponseDto;
import exceptions.NotFoundException;
import models.Currency;
import models.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Exchange {
    private final JdbcCurrencyDao currencyDao;
    private final JdbcExchangeRateDao exchangeRateDao;

    public Exchange(JdbcCurrencyDao jdbcCurrencyDao, JdbcExchangeRateDao jdbcExchangeRateDao) {
        currencyDao = jdbcCurrencyDao;
        exchangeRateDao = jdbcExchangeRateDao;
    }

    public ExchangeResponseDto convert(ExchangeRequestDto exchangeRequestDto) {
        Currency baseCurrency = currencyDao.findByCode(exchangeRequestDto.baseCode()).orElseThrow(() ->
                new NotFoundException("Currency " + exchangeRequestDto.baseCode() + " not found in database"));
        Currency targetCurrency = currencyDao.findByCode(exchangeRequestDto.targetCode()).orElseThrow(() ->
                new NotFoundException("Currency " + exchangeRequestDto.targetCode() + " not found in database"));
        BigDecimal amount = new BigDecimal(exchangeRequestDto.amount());

        BigDecimal rate = findDirectRate(baseCurrency, targetCurrency);

        if (rate == null) {
            rate = findReverseRate(baseCurrency, targetCurrency);

            if (rate == null) {
                rate = findRateThroughUsd(baseCurrency, targetCurrency);
            } else {
                throw new NotFoundException("Exchange rate not found in database");
            }
        }
        BigDecimal convertedAmount = amount.multiply(rate);

        return new ExchangeResponseDto(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }

    private BigDecimal findDirectRate(Currency baseCurrency, Currency targetCurrency) {
        ExchangeRate exchangeRate = exchangeRateDao.findRate(baseCurrency, targetCurrency);
        if (exchangeRate == null) {
            return null;
        }
        return exchangeRate.rate();
    }

    private BigDecimal findReverseRate(Currency baseCurrency, Currency targetCurrency) {
        ExchangeRate exchangeRate = exchangeRateDao.findRate(targetCurrency, baseCurrency);
        if (exchangeRate.rate() == null) {
            return null;
        }
        return new BigDecimal(1).divide(exchangeRate.rate(), RoundingMode.HALF_EVEN);
    }

    private BigDecimal findRateThroughUsd(Currency baseCurrency, Currency targetCurrency) {
        ExchangeRate exchangeRate = exchangeRateDao.findRate(new Currency(1, "", "", ""), baseCurrency);
        ExchangeRate exchangeRateSecond = exchangeRateDao.findRate(new Currency(1, "", "", ""), targetCurrency);

        if (exchangeRate.rate() == null || exchangeRateSecond.rate() == null) {
            return null;
        }
        return exchangeRateSecond.rate().divide(exchangeRate.rate(),  RoundingMode.HALF_EVEN);
    }
}
