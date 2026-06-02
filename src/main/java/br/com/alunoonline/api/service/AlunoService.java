package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Aluno;
import br.com.alunoonline.api.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    @Autowired
    AlunoRepository alunoRepository;

    // CREATE - salvar novo alunoRepository
    public void criarAluno(Aluno aluno) {
        alunoRepository.save(aluno);
    }

    // READ - listar todos os alunos
    public List<Aluno> listarTodosAlunos() {
        return alunoRepository.findAll();
    }

    // READ - listar aluno por id
    public Optional<Aluno> buscarAlunoPorId(Long id) {
        return alunoRepository.findById(id);
    }

    // Update - Atualizar por Id
    public void atualizarAlunoPorId(Long id, Aluno aluno) {
        Optional<Aluno> alunoDoBanco = buscarAlunoPorId(id);

        if (alunoDoBanco.isEmpty()){
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,  "Aluno não encontrado");
        }

        Aluno alunoParaEditar = alunoDoBanco.get();
        alunoParaEditar.setNome(aluno.getNome());
        alunoParaEditar.setCpf(aluno.getCpf());
        alunoParaEditar.setEmail(aluno.getEmail());

        alunoRepository.save(alunoParaEditar);
    }

    // DELETE - Deletar aluno por Id
    public void deletarAlunoPorId(Long id) {
        if(!alunoRepository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Aluno não encontrado");
        }

        alunoRepository.deleteById(id);
    }
}
