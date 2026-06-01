# Documentação de Infraestrutura (Docker)

Este documento descreve a infraestrutura baseada em containers da aplicação **Aluno Online**, desenhada com foco em segurança, performance e escalabilidade.

## Destaques da Arquitetura

Nossa infraestrutura foi construída seguindo as melhores práticas de mercado (DevOps):
*   **Multi-Stage Build:** Separação entre ambiente de compilação (Maven) e ambiente de execução (JRE Alpine) para garantir imagens leves (apenas o `.jar`) e seguras.
*   **Security First:** Execução da aplicação com usuário não-root.
*   **Orquestração Resiliente:** Uso de `depends_on` e `healthchecks` garantem que a API só inicie após o banco de dados estar 100% pronto para receber conexões.
*   **Monitoramento:** Integração com Spring Boot Actuator para verificação de saúde contínua da API.
*   **Otimização de Cache:** O `Dockerfile` é estruturado para aproveitar o cache do Docker, evitando o download repetitivo das dependências do Maven.

---

## Pré-requisitos

Para rodar este projeto, você precisará de:
*   [Docker](https://docs.docker.com/get-docker/) instalado.
*   [Docker Compose](https://docs.docker.com/compose/install/) instalado.

---

## Como Iniciar o Projeto

### 1. Configurando as Variáveis de Ambiente
O projeto usa variáveis de ambiente para esconder credenciais sensíveis.
Crie um arquivo chamado `.env` na raiz do projeto e adicione as seguintes variáveis:

```env
DB_NAME=aluno_online
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
