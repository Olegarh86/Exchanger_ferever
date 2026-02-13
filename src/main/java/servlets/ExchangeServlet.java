package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRequestDto;
import dto.ExchangeResponseDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import mappers.ExchangerMapper;
import service.Exchange;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private JdbcCurrencyDao currencyDao;
    private JdbcExchangeRateDao exchangeRateDao;
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyDao = (JdbcCurrencyDao) config.getServletContext().getAttribute("currencyInstance");
        exchangeRateDao = (JdbcExchangeRateDao) config.getServletContext().getAttribute("exchangeRateInstance");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        String stringAmount = req.getParameter("amount");

        Exchange exchange = new Exchange(currencyDao, exchangeRateDao);
        ExchangeResponseDto result = exchange.convert(new ExchangeRequestDto(from, to, stringAmount));

        resp.setStatus(SC_OK);
        mapper.writeValue(resp.getWriter(), result);
    }
}
