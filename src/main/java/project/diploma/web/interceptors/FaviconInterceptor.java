package project.diploma.web.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class FaviconInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String link = "https://res.cloudinary.com/martgeorgiev-cloud/image/upload/v1624289854/logo/logo4_ddimem.jpg";
                //https://res.cloudinary.com/martgeorgiev-cloud/image/upload/v1624289473/logo/logo3_vaph7p.jpg
        if (modelAndView != null) {
            modelAndView.addObject("favicon", link);
        }
    }
}