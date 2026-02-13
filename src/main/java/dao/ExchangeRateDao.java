package dao;

import models.Currency;
import models.ExchangeRate;

public interface ExchangeRateDao extends Dao <ExchangeRate>{
    ExchangeRate findRate(Currency baseCurrency, Currency targetCurrency);
    void update(ExchangeRate exchangeRate);
}
