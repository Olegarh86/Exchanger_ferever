package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.NotFoundException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import mappers.ExchangerMapper;
import models.Currency;
import models.ExchangeRate;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private JdbcCurrencyDao currencyDao;
    private JdbcExchangeRateDao exchangeRateDao;
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void init(ServletConfig config) {
        currencyDao = (JdbcCurrencyDao) config.getServletContext().getAttribute("currencyInstance");
        exchangeRateDao = (JdbcExchangeRateDao) config.getServletContext().getAttribute("exchangeRateInstance");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String codes = path.replaceFirst("/", "").toUpperCase();
        String baseCode = codes.substring(0, 3);
        String targetCode = codes.substring(3);

        Currency baseCurrency = currencyDao.findByCode(baseCode).orElseThrow(() ->
                new NotFoundException(baseCode + " currency not existing in database"));
        Currency targetCurrency = currencyDao.findByCode(targetCode).orElseThrow(() ->
                new NotFoundException(targetCode + " currency not existing in database"));

        ExchangeRate rate = exchangeRateDao.findRate(baseCurrency, targetCurrency);

        resp.setStatus(SC_OK);
        mapper.writeValue(resp.getWriter(), ExchangerMapper.INSTANCE.exchangeRateToDto(rate));
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        String codes = path.replaceFirst("/", "").toUpperCase();
        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3);
        BufferedReader reader = req.getReader();
        String input = reader.readLine();
        String rateString = input.replace("rate=", "");

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode).orElseThrow(() ->
                new NotFoundException("Base currency with code " + baseCurrencyCode + " not found"));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode).orElseThrow(() ->
                new NotFoundException("Target currency with code " + targetCurrencyCode + " not found"));
        BigDecimal rate = new BigDecimal(rateString);

        exchangeRateDao.update(new ExchangeRate(baseCurrency, targetCurrency, rate));

        resp.setStatus(SC_OK);
        mapper.writeValue(resp.getWriter(),
                ExchangerMapper.INSTANCE.exchangeRateToDto(new ExchangeRate(baseCurrency, targetCurrency, rate)));
    }
}