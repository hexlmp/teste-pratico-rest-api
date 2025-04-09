## Teste prático do edital 02/2025/SEPLAG (Analista de TI - Perfil Junior, Pleno e Sênior)

### Dados da inscrição
Nome: Luciano Machado Pereira
URL do projeto no GitHub: https://github.com/hexlmp/teste-pratico-rest-api

### Requisitos
- Docker-compose instalado e funcionando no ambiente.
- Java 17 instalado e funcionando no ambiente.

## Serviços executados no docker-compose.yml

#### LDAP (1.0.0)
Utilizado para armazenar usuários e grupos.

#### Keycloak (26.1.4)
Para o gerenciamento de identidade e acesso. Utilizado em conjunto com o LDAP para questões de autorização e autenticação. 
Ele gera um Access Token após a autenticação contento os grupos aos quais o usuário pertence.

#### PostgreSQL (latest)
Armazenar os dados do modelo relacional proposto.

#### Min. IO (latest)
Armazenar as fotos da pessoa, conforme proposto no teste.

## Montar imangens dos containers

No diretório *teste-pratico-rest-api/docker*, executar os comandos:

#### Montar imagens dos containers
    time DOCKER_BUILDKIT=1 docker-compose up -d --remove-orphans

#### Executar os containers
    docker-compose start

## Acessar a aplicação

Após o comando docker-compose start, se tudo correr bem, a aplicação estará disponível nos seguintes endereços e portas:
#### Keycloak Admin
endereço:  http://localhost:8080
usuário: admin
senha: admin1234

#### Aplicação teste-pratico-rest-api
endereço: http://localhost:8081
 
|Usuário|Senha|
|--|--|
|user|user|
|lucianopereira|admin1234|
|seplag|admin|



