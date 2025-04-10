## Teste prático do edital 02/2025/SEPLAG (Analista de TI - Sênior)
### Dados da inscrição

Nome: Luciano Machado Pereira

URL do projeto no GitHub: https://github.com/hexlmp/teste-pratico-rest-api/tree/develop

### Requisitos
- Docker-compose instalado e funcionando no ambiente.
- Java 17 instalado e funcionando no ambiente.
- Ubuntu/Debian

## Serviços executados no docker-compose.yml

#### LDAP (1.0.0)
Utilizado para armazenar usuários e grupos. 
Obs: São criados três usuário ao montar o container: user, lucianopereira, seplag.

#### Keycloak (26.1.4)
Para o gerenciamento de identidade e acesso. Utilizado em conjunto com o LDAP para questões de autorização e autenticação. 
Ele gera um Access Token após a autenticação, contento os grupos aos quais o usuário pertence.

Obs: é criado o **realm seplag-mt** ao montar o container. O realm está configurado para gerar access_token que expiram em 5 minutos. 

O back-end tem um filtro que adiciona no cabeçalho HTTP os atributos *X-AccessToken-Principal* e *X-AccessToken-Expire*, que são, respectivamente, o nome do usuário e tempo restante de sessão.

O Front-end por meio desses atributos, mostra o tempo restante de sessão e, quando restar menos de 30 segundos, aparecerá um botão de renovar sessão.

#### PostgreSQL (latest)
Armazenar os dados do modelo relacional proposto.
Obs: são criadas as tabelas conforme o modelo em edital e inseridos alguns registros na tabela pessoa e cidade.

#### Min. IO (latest)
Armazenar as fotos da pessoa, conforme proposto em edital.

## Montar imagens dos containers

Criar e executar os containers:

*Obs: Para o funcionamento correto dos serviços, é feita adição no /etc/hosts ( "127.0.0.1    keycloak" e "127.0.0.1    minio")* 

    sh Build.sh 

Executar containers já criados:

    sh Start.sh 

Parar de containers:

    sh Stop.sh 

Destruir de containers:

    sh Destroy.sh 


## Acessar a aplicação

A aplicação está *dockerizada*, no momento do Build é criado o container **app_seplag** e é baixado o *.jar diretamente do GitHub *(pasta pública)*  https://raw.githubusercontent.com/hexlmp/teste-pratico-rest-api/refs/heads/develop/deploy/teste-pratico-rest-api.jar 
Após o Build.sh *(criação de container e execução)* ou Start.sh *( execução de containers já existentes)*, se tudo correr bem, a aplicação estará disponível na porta 8081, e também os seguintes serviços:

#### Aplicação teste-pratico-rest-api
endereço: http://localhost:8081
 
|Usuário LDAP|Senha|Rule|
|--|--|--|
|user|user|USER|
|lucianopereira|admin1234|Admin|
|seplag|admin|Admin|

#### Keycloak Admin
endereço:  http://localhost:8080
usuário: admin
senha: admin1234

#### Min. IO Admin
endereço:  http://localhost:9001
usuário: admin
senha: admin1234

#### LDAP
endereço:  http://localhost:10389
simple authentication
Bind DN: uid=admin,ou=system
senha: secret


 


