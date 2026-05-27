package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Disciplina;
import br.com.alunoonline.api.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class DisciplinaService {

    @Autowired
    DisciplinaRepository disciplinaRepository;

    // CREATE - salvar nova disciplina
    public void criarDisciplina(Disciplina disciplina) {
        disciplinaRepository.save(disciplina);
    }

    // READ - listar todos os Disciplina
    public List<Disciplina> listarTodasDisciplinas() {
        return disciplinaRepository.findAll();
    }

    // READ - listar disciplina por id
    public Optional<Disciplina> buscarDisciplinaPorId(Long id) {
        return disciplinaRepository.findById(id);
    }

    // Update - Atualizar por Id
    public void atualizarDisciplinaPorId(Long id, Disciplina disciplina) {
        Optional<Disciplina> disciplinaDoBanco = buscarDisciplinaPorId(id);

        if (disciplinaDoBanco.isEmpty()){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,  "Disciplina não encontrada");
        }

        Disciplina disciplinaParaEditar = disciplinaDoBanco.get();
        disciplinaParaEditar.setNome(disciplina.getNome());
        disciplinaParaEditar.setCargaHoraria(disciplina.getCargaHoraria());

        disciplinaRepository.save(disciplinaParaEditar);
    }

    // DELETE - Deletar Disciplina por Id
    public void deletarDisciplinaPorId(Long id) {
        if(!disciplinaRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Disciplina não encontrada");
        }

        disciplinaRepository.deleteById(id);
    }
}
