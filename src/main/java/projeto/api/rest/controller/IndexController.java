package projeto.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import projeto.api.rest.repository.UsuarioRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired /* se fosse CDI seria @Inject */
	private UsuarioRepository usuarioRepository;

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> lista() {

		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		/*rotina para persistir a informação de usuario_id na hora de salvar na tabela telefone*/
		for(int pos=0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	@PostMapping(value = "/lote", produces = "application/json")
	public ResponseEntity<List<Usuario>> cadastrarLote(@RequestBody List<Usuario> usuarios) {
		for (Usuario usuario : usuarios) {
			/*rotina para persistir a informação de usuario_id na hora de salvar na tabela telefone*/
			for(int pos=0; pos < usuario.getTelefones().size(); pos++) {
				usuario.getTelefones().get(pos).setUsuario(usuario);
			}
			
			usuarioRepository.save(usuario);
		}
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);

	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		/*rotina para persistir a informação de usuario_id na hora de salvar na tabela telefone*/
		for(int pos=0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
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
}
