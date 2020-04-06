package projeto.api.rest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import projeto.api.rest.model.Telefone;

@Repository
public interface TelefoneRepository extends CrudRepository<Telefone, Long>{
	
	

}
