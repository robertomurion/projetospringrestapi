package projeto.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import projeto.api.rest.ApplicationContextLoad;
import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.UsuarioRepository;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/*
	 * @Autowired private UsuarioRepository usuarioRepository;
	 */

	/*Tem de validade do Token 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/*Uma senha unica para compor a autenticacao e ajudar na seguranca*/
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	/*Prefixo padrÃ£o de Token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";

	/* gerando token de autenticacao e adicionando ao cabecalho de resposta http */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		/*Montagem do Token*/
		String JWT = Jwts.builder() /*Chama o gerador de Token*/
				        .setSubject(username) /*Adicona o usuario*/
				        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiraÃ§Ã£o*/
				        .signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*CompactaÃ§Ã£o e algoritmos de geraÃ§Ã£o de senha*/
		
		/*Junta token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT; /*Bearer 87878we8we787w8e78w78e78w7e87w*/
		
		/*Liberando resposta para porta diferente*/
		liberacao(response);
		
		/*Adiciona no cabecalho http*/
		response.addHeader(HEADER_STRING, token); /*Authorization: Bearer 87878we8we787w8e78w78e78w7e87w*/
		
		/*Escreve token como responsta no corpo http*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
	}

	private void liberacao(HttpServletResponse response) {
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}

	/* retorna o usuario validado com token ou caso nao seja valido retorna null */

	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		/* Pega o token enviado no cabecalho Http */
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			
			/*Faz a validacao do token do usuario na requisicao*/
			String user = Jwts.parser().setSigningKey(SECRET) /*Bearer 87878we8we787w8e78w78e78w7e87w*/
								.parseClaimsJws(token.replace(TOKEN_PREFIX, "")) /*87878we8we787w8e78w78e78w7e87w*/
								.getBody().getSubject(); /*Joao Silva*/
			if (user != null) {
				
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						        .getBean(UsuarioRepository.class).findUserByLogin(user);
				
				if (usuario != null) {
					
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), 
							usuario.getSenha(),
							usuario.getAuthorities());
					
				}
			}
			
		}
		
		/*Liberando resposta para porta diferente*/
		liberacao(response);
		
		return null; /*Nao autorizado*/
		
	}
}
