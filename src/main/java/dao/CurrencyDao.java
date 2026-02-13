package dao;

import models.Currency;

import java.util.Optional;

public interface CurrencyDao extends Dao <Currency>{
    Optional<Currency> findByCode(String code);
}
