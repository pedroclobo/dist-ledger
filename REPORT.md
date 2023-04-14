# Coerência Fraca
A _gossip architecture_ implementada previne dois tipos de anomalias
tipicamente presentes em soluções com coerência fraca:

- **Leituras Incoerentes pelo mesmo Cliente**: Este problema é contornado
  atribuindo um _vector clock_ ao _frontend_ de cada cliente. Ao receber um
  pedido de leitura, a réplica apenas devolve a resposta caso o conjunto de
  _updates_ da réplica contenha o conjunto de _updates_ observados pelo
  cliente.
- **Violação da Causalidade entre Operações**: Este problema é resolvido
  fazendo a distinção entre operações estáveis e instáveis. Uma operação apenas
  se torna estável e é executada quando todas as dependências causais se
  encontram satisfeitas.

Contudo, a coerência fraca não garante que todas as réplicas apliquem as
operações pela mesma ordem. Considere-se a situação em que uma operação de
transferência falha numa das réplicas pois a conta destino não existe. Uma
operação de _gossip_ para uma réplica onde a conta já exista faz com que a
operação de transferência se execute, levando a uma situação de incoerência
entre réplicas.

# Algoritmo
## Leituras
Um pedido de leitura contém a informação da operação a executar e um _vector
clock_ `prevTS`.

- Ao receber um destes pedidos, a réplica verifica se `prevTS` <= `valueTS`.
	- Caso a condição se verifique:
		- A réplica retorna o valor ao cliente, juntamente com o `valueTS`.
	- Caso contrário:
		- O cliente recebe um erro.

## Escritas
Um pedido de escrita contém a especificação da operação a realizar e o `prevTS`
do cliente.

- Quando a réplica recebe um pedido feito pelo cliente:
	- Incrementa a entrada correspondente do seu `replicaTS` em uma unidade.
	- Cria-se um _vector clock_, `operationTS`, idêntico a `prevTS`.
	- Realiza-se um _merge_ no `operationTS` no índice correspondente à replica
	  em relação a `replicaTS`.
	- Retorna-se o novo _timestamp_, `operationTS`, ao cliente.
	- Cria-se a operação, que guarda os valores de `prevTS` e `operationTS`.
	- Adiciona-se a operação à _ledger_.
	- Caso `valueTS` >= `operationTS`:
		- Marca-se a operação como estável.
		- Executa-se a operação.
		- Dá-se o _merge_ do `valueTS` com `operationTS`.

## Propagação de Modificações
Cada réplica contacta outra, enviando o _ledger_, para que estas se
sincronizem. Isto é conseguido através do comando `gossip <emissor> <destino>`
do Administrador.

Uma mensagem de _gossip_ contém o _ledger_ e o `replicaTS` da réplica emissora.
Ao receber uma mensagem de _gossip_ a réplica realiza as seguintes tarefas:

- Por cada operação recebida:
	- Descarta a operação caso este já se encontra na _ledger_.
	- Caso `replicaTS` < `operationTS`:
		- Adiciona-se a operação à _ledger_.
		- Caso `valueTS` >= `operationTS`:
			- Marca-se a operação como estável.
			- Executa-se a operação.
			- Dá-se o _merge_ do `valueTS` com `operationTS`.
		- Dá-se o _merge_ entre o `replicaTS`'s das réplicas destino e emissora.
		- Recalcula-se quais as operações estáveis. Para cada operação:
			- Caso `valueTS` >= `prevTS` e a operação seja instável:
				- Marca-se a operação como estável.
				- Executa-se a operação.
				- Dá-se o _merge_ do `valueTS` com `operationTS`.

# Possíveis Otimizações
- Não enviar toda a _ledger_ em pedidos de _gossip_.
