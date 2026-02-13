package contextListener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;


@WebListener
public class ServletContextListener implements jakarta.servlet.ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("currencyInstance", JdbcCurrencyDao.getInstance());
        servletContext.setAttribute("exchangeRateInstance", JdbcExchangeRateDao.getInstance());
    }
}
