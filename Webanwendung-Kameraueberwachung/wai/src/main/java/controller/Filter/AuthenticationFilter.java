package controller.Filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import utils.Validator;

@WebFilter(urlPatterns = {"/homepage", "/gallery", "/search", "/getCameras", "/getPictures", "/admin/*", "/view/*", "/assets/*"})
public class AuthenticationFilter implements Filter{
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        

        String path = ((HttpServletRequest) request).getRequestURI();
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String relativePath = path.substring(contextPath.length());
        
        if(relativePath.contains("/view") || relativePath.contains("/assets")){
            ((HttpServletResponse) response).sendRedirect("/wai/login");
            return;
        }
        boolean authenticated = Validator.getInstance().authenticate(((HttpServletRequest) request).getSession(false));

        if(!authenticated){
            ((HttpServletResponse) response).sendRedirect("/wai/login");
            return;
        }
        
        boolean authorized = Validator.getInstance().authorize((User)((HttpServletRequest) request).getSession(false).getAttribute("user"), relativePath, ((HttpServletRequest) request).getMethod());

		if(!authorized){
            ((HttpServletResponse) response).sendRedirect("/wai/login");
            return;
        }

        chain.doFilter(request, response);
    }
    
}