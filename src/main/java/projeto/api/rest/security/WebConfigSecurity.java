package projeto.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import projeto.api.rest.service.ImplementacaoUserDetailsService;

// essa classe mapeia url endereços, autoriza ou bloqueia acessos a url

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override //Configura as solicitacoes de acesso do Http
	protected void configure(HttpSecurity http) throws Exception {

		/*Ativando a protecao contra usuario que nao estao validados por TOKEN*/
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		
		/*Ativando a permissÃ£o para acesso a pagina incial do sistema EX: sistema.com.br/index*/
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		
		
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		
		/*URL de Logout - Redireciona apÃ³s o user deslogar do sistema*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*Maperia URL de Logout e insvalida o usuario*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtra requisicoes de login para autenticacao*/
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), 
									UsernamePasswordAuthenticationFilter.class)
		
		/*Filtra demais requisicoes paa verificar a presenca do TOKEN JWT no HEADER HTTP*/
		.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	
	}
	
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

	/*Service que ira consultar o usuario no banco de dados*/	
	auth.userDetailsService(implementacaoUserDetailsService)
	
	/*Padrao de codigo de senha*/
	.passwordEncoder(new BCryptPasswordEncoder());
	
	}
}
