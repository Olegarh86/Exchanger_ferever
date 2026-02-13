package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyResponseDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.JdbcCurrencyDao;
import mappers.ExchangerMapper;
import models.Currency;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;


@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private JdbcCurrencyDao currencyDao;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) {
        currencyDao = (JdbcCurrencyDao) config.getServletContext().getAttribute("currencyInstance");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> all = currencyDao.findAll();

        resp.setStatus(SC_OK);
        mapper.writeValue(resp.getWriter(), ExchangerMapper.INSTANCE.currencyToDtoList(all));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code").toUpperCase();
        String sign = req.getParameter("sign");

        Integer id = currencyDao.save(new Currency(name, code, sign));

        resp.setStatus(SC_CREATED);
        mapper.writeValue(resp.getWriter(), new CurrencyResponseDto(id, name, code, sign));
    }
}
