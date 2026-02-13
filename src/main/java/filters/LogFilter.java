package filters;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;

@WebFilter(urlPatterns = "/*")
public class LogFilter extends HttpFilter {
}
