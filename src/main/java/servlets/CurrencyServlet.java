package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.NotFoundException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.JdbcCurrencyDao;
import mappers.ExchangerMapper;
import models.Currency;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private JdbcCurrencyDao currencyDao;
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void init(ServletConfig config) {
        currencyDao = (JdbcCurrencyDao) config.getServletContext().getAttribute("currencyInstance");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String code = path.replaceFirst("/", "").toUpperCase();

        Currency currency = currencyDao.findByCode(code).orElseThrow(() ->
                new NotFoundException(code + " currency not existing in database"));

        resp.setStatus(SC_OK);
        mapper.writeValue(resp.getWriter(), ExchangerMapper.INSTANCE.currencyToDto(currency));
    }
}