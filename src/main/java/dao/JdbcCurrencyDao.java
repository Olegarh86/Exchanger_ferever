package dao;

import exceptions.AlreadyExistException;
import exceptions.NotFoundException;
import utils.DataSource;
import models.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JdbcCurrencyDao implements CurrencyDao {
    private static final JdbcCurrencyDao INSTANCE;
    private static final String FIND_ALL = """
            SELECT *
            FROM currencies
            """;
    private static final String FIND_BY_CODE = FIND_ALL + """
            WHERE code = ?;
            """;
    private static final String SAVE = """
            INSERT INTO currencies (code, fullName, sign)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    static {
        INSTANCE = new JdbcCurrencyDao();
    }

    private JdbcCurrencyDao() {
    }

    public static JdbcCurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_CODE);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new NotFoundException(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Integer save(Currency currency) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SAVE);

            statement.setString(1, currency.code());
            statement.setString(2 , currency.name());
            statement.setString(3 , currency.sign());

            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new AlreadyExistException(e.getMessage());
        }
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("id"),
                resultSet.getString("fullName"),
                resultSet.getString("code"),
                resultSet.getString("sign"));
    }
}
