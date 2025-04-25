package org.ntnu.idatt2106.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/** Configuration class for Cross-Origin Resource Sharing (CORS) settings.
 * This class defines the allowed origins, HTTP methods, headers, and credential
 * requirements for cross-origin requests to the marketplace API.*
 * @author Erlend
 * @version 1.0
 * @since 1.0
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Configures CORS mappings for the application.
     * Allows requests from the specified origin with defined HTTP methods,
     * permits all headers, and enables credentials for cross-origin requests.*
     * @param registry the CORS registry to configure
     * @since 1.0
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // Allow only this origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true);
    }
}
