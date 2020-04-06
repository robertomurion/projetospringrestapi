package projeto.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {"projeto.api.rest.model"})
@ComponentScan(basePackages = {"projeto.*"})
@EnableJpaRepositories(basePackages = "projeto.api.rest.repository")
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class ProjetospringrestapiApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(ProjetospringrestapiApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("123"));
	}
	
	
	/*mapeamento global que reflete em todo o sistema*/
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/usuario/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		
		registry.addMapping("/profissao/**")
		.allowedMethods("*")
		.allowedOrigins("*");
	}
}
