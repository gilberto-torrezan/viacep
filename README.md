# ViaCEP Client

Cliente Java (SE, Android e GWT) para os webservices da ViaCEP.

## ViaCEP

ViaCEP é um webservice gratuito e de alto desempenho para consultar Códigos de Endereçamento Postal (CEP) do Brasil. Sobre o serviço: http://viacep.com.br. 

## ViaCEP Client

O projeto ViaCEP Client disponibiliza uma API amigável para o acesso aos webservices da ViaCEP utilizando Java. Funciona tanto em Java Standard Edition (servidores e clientes desktop) quanto clientes Android e GWT. Requer Java 7 (e no caso do GWT, SDK 2.7.0).

## Exemplos

### Java SE e Android

Consulta por CEP:

```java
ViaCEPClient client = new ViaCEPClient();
ViaCEPEndereco endereco = client.getEndereco("20930-040");
System.out.prinln(endereco.getLocalidade()); //Rio de Janeiro

endereco = client.getEndereco(" 01311000 ");
System.out.prinln(endereco.getLocalidade()); //São Paulo
```

Note que o cliente se responsabiliza por enviar o formato correto do CEP, desde que ele possua 8 dígitos.

Consulta por UF, localidade e logradouro:

```java
ViaCEPClient client = new ViaCEPClient();
List<ViaCEPEndereco> enderecos = client.getEnderecos("SP", "São Paulo", "Avenida Paulista");
```

### GWT

Consulta por CEP:

```java
ViaCEPGWTClient client = new ViaCEPGWTClient();
client.getEndereco("20930-040", new MethodCallback<ViaCEPEndereco>(){
	@Override
	public void onSuccess(Method method, ViaCEPEndereco response) {
		GWT.log(response.getLocalidade()); //Rio de Janeiro
	}
	
	@Override
	public void onFailure(Method method, Throwable exception) {
		GWT.log("Erro: " + exception, exception);
	}
});
```

Consulta por UF, localidade e logradouro:

```java
ViaCEPGWTClient client = new ViaCEPGWTClient();
client.getEnderecos("SP", "São Paulo", "Avenida Paulista", new MethodCallback<List<ViaCEPEndereco>>(){
	@Override
	public void onSuccess(Method method, List<ViaCEPEndereco> response) {
		//response
	}
	
	@Override
	public void onFailure(Method method, Throwable exception) {
		GWT.log("Erro: " + exception, exception);
	}
});
```

Os web services são chamados utilizando CORS ao invés de JSONP.

## Setup

### Adicione o ViaCEP Client no classpath do seu projeto 

Usando Apache Maven:

```xml
<dependency>
	<groupId>com.github.gilberto-torrezan</groupId>
	<artifactId>viacep</artifactId>
	<version>1.0.0</version>
</dependency>
```
Você pode baixar o jar diretamente do [The Central Repository](http://search.maven.org/#search|gav|1|g%3A%22com.github.gilberto-torrezan%22%20AND%20a%3A%22viacep%22) também. Fique atento as dependências:

* Para GWT, é necessário importar o [RestyGWT](https://resty-gwt.github.io/) (o projeto usa v.2.0.3)
* Para Java SE, é necessário importar o [Jackson jr](https://github.com/FasterXML/jackson-jr) (o projeto usa v.2.5.3)

### Módulo GWT

Para usar o client GWT, adicione o ViaCEP Client no seu project.gwt.xml:

```xml
<inherits name="com.github.gilbertotorrezan.viacep.viacep"/>
```

## Javadoc

Você pode ver o javadoc desse projeto no javadoc.io:

[http://www.javadoc.io/doc/com.github.gilberto-torrezan/viacep](http://www.javadoc.io/doc/com.github.gilberto-torrezan/viacep)
	
## Licença

Esse projeto é licenciado sob os termos da The MIT License (MIT).