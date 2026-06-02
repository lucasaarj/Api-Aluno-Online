package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Professor;
import br.com.alunoonline.api.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    @Autowired
    ProfessorRepository professorRepository;

    // CREATE - salvar novo Professor
    public void criarProfessor(Professor professor) {
        professorRepository.save(professor);
    }

    // READ - listar todos os professores
    public List<Professor> listarTodosProfessores() {
        return professorRepository.findAll();
    }

    // READ - listar professor por id
    public Optional<Professor> buscarProfessorPorId(Long id) {
        return professorRepository.findById(id);
    }

    // Update - Atualizar por Id
    public void atualizarProfessorPorId(Long id, Professor professor) {
        Optional<Professor> professorDoBanco = buscarProfessorPorId(id);

        if (professorDoBanco.isEmpty()){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,  "Professor não encontrado");
        }

        Professor professorParaEditar = professorDoBanco.get();
        professorParaEditar.setNome(professor.getNome());
        professorParaEditar.setCpf(professor.getCpf());
        professorParaEditar.setEmail(professor.getEmail());

        professorRepository.save(professorParaEditar);
    }

    // DELETE - Deletar professor por Id
    public void deletarProfessorPorId(Long id) {
        if(!professorRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Professor não encontrado");
        }

        professorRepository.deleteById(id);
    }
}
