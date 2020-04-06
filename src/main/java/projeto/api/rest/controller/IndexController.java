package projeto.api.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projeto.api.rest.model.Usuario;
import projeto.api.rest.repository.TelefoneRepository;
import projeto.api.rest.repository.UsuarioRepository;
import projeto.api.rest.service.ImplementacaoUserDetailsService;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired /* se fosse CDI seria @Inject */
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	@CachePut("cachelista")
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<Page<Usuario>> lista() {

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@CachePut("cachelista")
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuarioPage(@PathVariable(value = "pagina") Integer pagina) {

		PageRequest pageRequest = PageRequest.of(pagina, 5, Sort.by("nome"));

		Page<Usuario> page = usuarioRepository.findAll(pageRequest);

		return new ResponseEntity<Page<Usuario>>(page, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid Usuario usuario) {

		/*
		 * rotina para persistir a informação de usuario_id na hora de salvar na tabela
		 * telefone
		 */
		if (usuario.getTelefones() != null) {
			for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
				usuario.getTelefones().get(pos).setUsuario(usuario);
			}
		}
		String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhacriptografada);

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	@PostMapping(value = "/lote", produces = "application/json")
	public ResponseEntity<List<Usuario>> cadastrarLote(@RequestBody List<Usuario> usuarios) {
		for (Usuario usuario : usuarios) {
			/*
			 * rotina para persistir a informação de usuario_id na hora de salvar na tabela
			 * telefone
			 */
			if (usuario.getTelefones() != null) {
				for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
					usuario.getTelefones().get(pos).setUsuario(usuario);
				}
			}
			usuarioRepository.save(usuario);
		}
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);

	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		/*
		 * rotina para persistir a informação de usuario_id na hora de salvar na tabela
		 * telefone
		 */
		if (usuario.getTelefones() != null) {
			for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
				usuario.getTelefones().get(pos).setUsuario(usuario);
			}
		}
		Usuario userTemp = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemp.getSenha().equals(usuario.getSenha())) {
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}

		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	@DeleteMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<List<Usuario>> apagar(@PathVariable(value = "id") Long id) {
		usuarioRepository.deleteById(id);
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")

	public ResponseEntity<Page<Usuario>> consultaNome(@PathVariable(value = "nome") String nome) {

		PageRequest pageRequest = null;
		Page<Usuario> page = null;
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			page = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			page = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		// List<Usuario> lista =
		// usuarioRepository.findUserByNameContainingIgnoreCase(nome);

		return new ResponseEntity<Page<Usuario>>(page, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> consultaNomePage(@PathVariable(value = "nome") String nome,
			@PathVariable(value = "page") Integer pagina) {

		PageRequest pageRequest = null;
		Page<Usuario> page = null;
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(pagina, 5, Sort.by("nome"));
			page = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(pagina, 5, Sort.by("nome"));
			page = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}
		// List<Usuario> lista =
		// usuarioRepository.findUserByNameContainingIgnoreCase(nome);

		return new ResponseEntity<Page<Usuario>>(page, HttpStatus.OK);
	}

	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable(value = "id") Long telefone_id) {
		telefoneRepository.deleteById(telefone_id);
		return "ok";
	}
}
