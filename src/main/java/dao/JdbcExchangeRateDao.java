package dao;

import exceptions.AlreadyExistException;
import exceptions.NotFoundException;
import utils.DataSource;
import models.Currency;
import models.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcExchangeRateDao implements ExchangeRateDao {
    private static final JdbcExchangeRateDao INSTANCE;
    private static final String FIND_ALL = """
            SELECT er.id, baseCurrencyId, targetCurrencyId, rate,
                   b.id baseId, b.code baseCode, b.fullName baseName, b.sign baseSign,
                   t.id targetId, t.code targetCode, t.fullName targetName, t.sign targetSign
            FROM exchangeRates er
            JOIN currencies b on b.id = er.baseCurrencyId
            JOIN currencies t on t.id = er.targetCurrencyId
            """;
    private static final String SAVE = """
            INSERT INTO exchangeRates (baseCurrencyId, targetCurrencyId, rate)
            VALUES (?, ?, ?)
            RETURNING id
            """;
    private static final String FIND_RATE = FIND_ALL + """
            WHERE baseCurrencyId = ? and targetCurrencyId = ?
            """;
    private static final String UPDATE_RATE = """
            UPDATE exchangeRates
            SET rate = ?
            WHERE baseCurrencyId = ? AND targetCurrencyId = ?
            RETURNING *
            """;

    static {
        INSTANCE = new JdbcExchangeRateDao();
    }

    private JdbcExchangeRateDao() {
    }

    public static JdbcExchangeRateDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Integer save(ExchangeRate exchangeRate) {
        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(SAVE);
            statement.setLong(1, exchangeRate.baseCurrency().id());
            statement.setLong(2, exchangeRate.targetCurrency().id());
            statement.setBigDecimal(3, exchangeRate.rate());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    @Override
    public ExchangeRate findRate(Currency baseCurrency, Currency targetCurrency) {
        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(FIND_RATE);
            statement.setInt(1, baseCurrency.id());
            statement.setInt(2, targetCurrency.id());
            ResultSet resultSet = statement.executeQuery();
            return buildExchangeRate(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) {
        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(UPDATE_RATE);
            statement.setBigDecimal(1, exchangeRate.rate());
            statement.setInt(2, exchangeRate.baseCurrency().id());
            statement.setInt(3, exchangeRate.targetCurrency().id());
            statement.execute();
        } catch (SQLException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();

        try (Connection connection = DataSource.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(FIND_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),

                new Currency(
                        resultSet.getInt("baseId"),
                        resultSet.getString("baseName"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseSign")),

                new Currency(resultSet.getInt("targetId"),
                        resultSet.getString("targetName"),
                        resultSet.getString("targetCode"),
                        resultSet.getString("targetSign")),

                resultSet.getBigDecimal("rate"));
    }
}
