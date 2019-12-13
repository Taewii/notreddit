package notreddit.web.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import notreddit.constants.ErrorMessages;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.util.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class VoteRateLimitingInterceptor extends HandlerInterceptorAdapter {

    private static final boolean ENABLED = true;
    private static final int CALLS_PER_SECOND = 1;

    private Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // check if limiting is enabled & the current path matches some of the "vote" paths
        // & if the method is POST
        if (!ENABLED || !request.getRequestURI().contains("vote") || !"POST".equals(request.getMethod())) {
            return true;
        }

        Principal client = request.getUserPrincipal();
        if (client == null) {
            return true;
        }

        String clientId = client.getName();
        RateLimiter limiter = getRateLimiter(clientId);
        boolean allowRequest = limiter.tryAcquire();

        if (!allowRequest) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ApiResponse apiResponse = new ApiResponse(false, ErrorMessages.TOO_MANY_REQUESTS);
            PrintWriter out = response.getWriter();
            out.print(mapper.writeValueAsString(apiResponse));
            out.flush();
        }

        response.addHeader("X-RateLimit-Limit", String.valueOf(CALLS_PER_SECOND));
        return allowRequest;
    }

    private RateLimiter getRateLimiter(String clientId) {
        if (limiters.containsKey(clientId)) {
            return limiters.get(clientId);
        } else {
            synchronized (clientId.intern()) {
                // double-checked locking to avoid multiple-reinitialization
                if (limiters.containsKey(clientId)) {
                    return limiters.get(clientId);
                }

                RateLimiter rateLimiter = RateLimiter.create(CALLS_PER_SECOND, TimeUnit.SECONDS);
                limiters.put(clientId, rateLimiter);

                return rateLimiter;
            }
        }
    }
}