package filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.ExceptionDto;
import exceptions.AlreadyExistException;
import exceptions.BadRequestException;
import exceptions.NotFoundException;

import java.io.IOException;

@WebFilter(value = "/*")
public class ExceptionHandlerFilter extends HttpFilter {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (BadRequestException br) {
            writeResponseException(res, br, 400);
        } catch (NotFoundException nf) {
            writeResponseException(res, nf, 404);
        } catch (AlreadyExistException ae) {
            writeResponseException(res, ae, 409);
        } catch (Exception e) {
            writeResponseException(res, e, 500);
        }
    }

    private void writeResponseException(HttpServletResponse res, Exception e, int statusCode) throws IOException {
        res.setStatus(statusCode);
        mapper.writeValue(res.getWriter(), new ExceptionDto(e.getMessage()));
    }
}
