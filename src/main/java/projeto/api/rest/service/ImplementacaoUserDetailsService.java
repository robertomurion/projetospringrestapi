package projeto.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.UsuarioRepository;


public class ImplementacaoUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findUserByLogin(username);

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario " + username + " nao encontrado");
		}
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.isEnabled(), true, true, true,
				usuario.getAuthorities());
	}

}
