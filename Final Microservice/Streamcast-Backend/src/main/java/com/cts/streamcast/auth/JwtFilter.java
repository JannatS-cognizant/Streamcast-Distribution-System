package com.cts.streamcast.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cts.streamcast.entity.User;
import com.cts.streamcast.repository.UserRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final UserRepository userRepo;
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
    	
    	
    	String path=request.getRequestURI();
    	if(path.startsWith("/auth"))
    	{
    		filterChain.doFilter(request, response);
    		return;
    	}
    	

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) 
        	{
        		filterChain.doFilter(request, response);
        		return;
        	}  
        
        String token = authHeader.substring(7);
        
            try {
            	
                String email = jwtUtil.extractEmail(token);
                
                User user=userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
                String role=user.getRole().getName().toUpperCase().replace(" ","_");
                
                
                //System.out.println("Authenticated User: " + email); 
                UsernamePasswordAuthenticationToken auth= new UsernamePasswordAuthenticationToken(email,null,List.of(new SimpleGrantedAuthority("ROLE_"+role)));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        

        filterChain.doFilter(request, response);
    }
}
