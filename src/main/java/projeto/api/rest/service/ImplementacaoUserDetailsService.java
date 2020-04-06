package projeto.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Usuario usuario = usuarioRepository.findUserByLogin(username);

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario " + username + " nao encontrado");
		}
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.isEnabled(), true, true, true,
				usuario.getAuthorities());
	}

	public void insereAcessoPadrao(Long usuario_id) {
		/* descobre quala constraint de restricao */
		String constraint = usuarioRepository.consultaConstraintRole();
		
		/* remove a constraint de restricao */
		if (constraint != null) {

			jdbcTemplate.execute(" alter table usuarios_role drop constraint " + constraint);
		}
		
		/* insere o acesso padrao ao usuario informado */
		usuarioRepository.insereAcessoRolePadrao(usuario_id);

	}

}
