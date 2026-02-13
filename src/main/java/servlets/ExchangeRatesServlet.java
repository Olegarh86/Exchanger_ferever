package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateResponseDto;
import exceptions.NotFoundException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import mappers.ExchangerMapper;
import models.Currency;
import models.ExchangeRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private JdbcCurrencyDao currencyDao;
    private JdbcExchangeRateDao exchangeRateDao;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) {
        currencyDao = (JdbcCurrencyDao) config.getServletContext().getAttribute("currencyInstance");
        exchangeRateDao = (JdbcExchangeRateDao) config.getServletContext().getAttribute("exchangeRateInstance");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> allRates = exchangeRateDao.findAll();

        resp.setStatus(SC_OK);
        mapper.writeValue(resp.getWriter(), ExchangerMapper.INSTANCE.exchangeRateToDtoList(allRates));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode").toUpperCase();
        String targetCurrencyCode = req.getParameter("targetCurrencyCode").toUpperCase();
        String rateString = req.getParameter("rate");

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode).orElseThrow(() ->
                new NotFoundException("Base currency with code " + baseCurrencyCode + " not found"));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode).orElseThrow(() ->
                new NotFoundException("Target currency with code " + targetCurrencyCode + " not found"));
        BigDecimal rate = new BigDecimal(rateString);

        Integer id = exchangeRateDao.save(new ExchangeRate(baseCurrency, targetCurrency, rate));

        resp.setStatus(SC_CREATED);
        mapper.writeValue(resp.getWriter(), new ExchangeRateResponseDto(id, baseCurrency, targetCurrency, rate));
    }
}
