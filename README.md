# Minha aplicação para monitoramento de água

![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)



## Sobre o que é??

Essa é a nossa aplicação back-end em SpringBoot para medir a qualidade da água de qualquer corpo hidríco.

Ela será capaz de medir as águas do rio Tietê e do rio Ghandi, com o único empecilho sendo o hardware para tal, uma vez que o ph, turbidez, baixo oxigênio dissolvido, radiação, egoísmo, apatia, competição, preconceito, miséria dentre outras falhas - tão presentes em nossa sociedade e nesses rios - não permitem aparelho algum chegar perto, sem antes ser completamente destruído...

Entretanto, é em tal dicotomia que se pode afimar: mesmo em um rio sujo, há nele água pura. Permitindo-me citar Voltaire, em sua obra "Autobiografia de Babuc escrita por ele mesmo":

- "Você condenaria uma estátua com acabamento de ouro apenas porque o resto é feito de barro?".

E já que há algo tão puro, bonito e essencial a vida - como a água nestes rios sujos - há, partindo de nós, a necessidade de medir e tratá-la de suas impurezas. 

E é *sobre isso* esse software!! Mais do que apenas medir a água, é mostrar que há bom em tudo, que talvez a natureza da água sempre foi ser doce, mas que devido ao seu percurso histórico e ao local em que se encontra, dá-se como suja.

Neste caso, esses percursos d'agua - superando o meio físico prevísivel e adentrando o imaginário - nos permite olhar para um reflexo além do nosso rosto - a de nossa história - que até o momento é marcada por uma luta de classes aparentemente sem fim, contudo, lembre-se caro leitor:

Tal como um presunçoso observador, no centro de São Paulo, cometeria um grave engano ao, precocemente, determinar que a natrueza das impurezas das águas presentes em sua frente é inato ao rio que as transporta - que suas águas nunca foram e nunca serão limpas - cometeríamos um engano ainda maior ao afirmar o mesmo sobre nossa sociedade. 

Concluindo humildemente minha pequena documentação, o ponto em que observamos a sociedade atualmente é apenas um ponto ínfimo de sua história, muito longe de conseguirmos assertar que

"a história da humanidade chegou ao fim" ou/e que o atual estado das coisas reflete a natureza humana em sua plenitude.

## Implementação do RabbitMq (o que entendi)

Primeiramente, deve-se compreender que o RabbitMQ é um sistema aparte, que liga duas aplicações, uma como receptor e outra como consumidor: tudo de modo assíncrono. Isso é importante quando está lidando com microsserviços e dados colossais (mais uma justificativa para ser um sistema aparte)

E já que é um software (message broker) aparte, devemos instalar ele, ou melhor, colocar em um container para ser utilizado. Por isso a importância de adicioná-lo ao docker compose

Pelo visto existem muitos protocolos para fazer a comunicação entre dois serviços, vamos focar em implementar apenas o AMQP 0-9-1

Para não ficar repetitivo e superficial, ao falar que existe "o serviço que produz" e o "serviço que recebe", tem-se as terminologias e conceitos do próprio RabbitMQ, sendo eles:

- produtor - aquele que produz as mensagens a serem mandadas;

- consumidor - aquele que recebe as mensagens produzidas;

- Fila (queue) - é onde as mensagens ficam para ser consumidas;

- Conexão - é em realção a conexão TCP entre a aplicação e o RabbitMq;

- Canal - as conexões *leves* que compartilham de uma única conexão TCP. Quando alguém diz "publicou" ou "consumiu" uma mensagem, saiba que isso é feito em um canal;

- Troca (exchange) - recebe as mensagens dos produtores e adicionam elas às filas. importante: uma fila deve estar conectado a pelo menos um *exchange*;

- Bindings - regras que o *exchange* usa para rotear as mensagens

- routing key - chave que decide como rotear as mensagens para as filas;

- Users - dá para se conectar ao RabbitMQ com um usuário e senha (olouco O.o, deve ser tipo um painel) eles podem ter permissões para ler, escrever e configurar privilégios dentro de uma instância. Usuários podem receber permissões apenas para hosts virtuais específicos.

- Vhost ou virtual hosts - permitem o agrupamento e separação de recursos. Igual coloquei antes, dá para dar permissões aos de usuários a virtual hosts especificos.

Para mais detalhes, leêm essa doc: https://medium.com/cwan-engineering/rabbitmq-concepts-and-best-practices-aa3c699d6f08
 (foi onde peguei essas infos)

 Em relação a como instalar com o docker compose: 
 https://hub.docker.com/_/rabbitmq
 e
 https://medium.com/@kaloyanmanev/how-to-run-rabbitmq-in-docker-compose-e5baccc3e644


