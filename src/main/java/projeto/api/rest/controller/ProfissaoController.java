package projeto.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projeto.api.rest.model.Profissao;
import projeto.api.rest.repository.ProfissaoRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/profissao")
public class ProfissaoController {
	@Autowired
	private ProfissaoRepository profissaoRepository;

	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Profissao>> carregaLista() {

		List<Profissao> profissao = profissaoRepository.findAll();

		return new ResponseEntity<List<Profissao>>(profissao, HttpStatus.OK);
	}
}
