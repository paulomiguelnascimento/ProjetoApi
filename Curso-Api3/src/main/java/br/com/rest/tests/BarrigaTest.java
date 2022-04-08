package br.com.rest.tests;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import br.com.rest.classeAjudaTodosTeste.BaseTest;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;


public class BarrigaTest extends BaseTest {
	
	private String TOKEN;
	
	
	@Before
	
	public void login () {
		
		Map<String, String> login = new HashMap<>();
		login.put("email", "paulo.nascimento@bs2.com.br");
		login.put("senha", "123456");
		
	
		TOKEN = given()
			.body(login)
		
		.when()
			.post("/signin")
		.then()
			.statusCode(200)
			.extract().path("token"); 
		
	}
	
	
	@Test
	
	public void naoDeveAcessarAPISemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
			.log();
		
		
	}
	
	@Test
	
	public void deveIncluirContaComSucesso() {	
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body("{\"nome\": \"conta qualquer 6\" }")
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.log().all();
	

	}
	
	
	@Test
	
	public void deveAlterarContaComSucesso() {
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body("{\"nome\": \"conta alterada paulo2\" }")
		.when()
			.put("/contas/1092609")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("conta alterada paulo2"));
			
			

	}

	
	
	@Test
	
	public void naoDeveInserirContaMesmoNome() {
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body("{\"nome\": \"conta alterada paulo2\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"));
			
			

	}


	

	@Test
	
	public void DeveInserirMovimentacaoSucesso() {
		
		
		Movimentacao mov = getMovimentacaoValida();
		
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body(mov)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201);
			
			

	}
	
	
@Test
	
	public void deveValidarCamposObrigatóriosMovimentacao() {
		
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body("{}")
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(400)
			.body("$", hasSize(8))//nesse comando estou informando ao restassured q deve conter 8 mensagens obrigatórias
			.body("msg", hasItems(
					"Data da Movimentação é obrigatório",
					"Data do pagamento é obrigatório",
					"Descrição é obrigatório",
					"Interessado é obrigatório",
					"Valor é obrigatório",
					"Valor deve ser um número",
					"Conta é obrigatório",
					"Situação é obrigatório"		
					));
			

	}


@Test

public void naoDeveInserirMovimentacaoDataFutura() {
	
	
	Movimentacao mov = getMovimentacaoValida();
	mov.setData_transacao("20/05/2030");
	
	
	given()
	.header("Authorization", "JWT " + TOKEN)
	  .body(mov)
	.when()
		.post("/transacoes")
	.then()
		.log().all()
		.statusCode(400)
		.body("$", hasSize(1))
		.body("msg", hasItem ("Data da Movimentação deve ser menor ou igual à data atual"));
		
		// se vim dentro de um array o retorno da msg de erro conforme abaixo tem que usar HasItem
	
/*	[
	    {
	        "param": "data_transacao",
	        "msg": "Data da Movimentação deve ser menor ou igual à data atual",
	        "value": "20/05/2030"
	    }
	]

*/
	
}

@Test

public void deveCalcularSaldoContas() {
	
	
	given()
	.header("Authorization", "JWT " + TOKEN)
	.when()
		.get("/saldo")
	.then()
		.log().all()
		.statusCode(200)
		.body("find{it.conta_id == 1092609}.saldo", is("333.00"))
		.body("find{it.conta_id == 1092609}.conta", is("conta alterada paulo2"));
		

	
}


@Test

public void deveRemoverMovimentacao() {
	
	
	given()
	.header("Authorization", "JWT " + TOKEN)
	.when()
		.delete("/transacoes/1040296")
	.then()
		.log().all()
		.statusCode(204);
		

	
}



















//esse metodo foi criado pra facilitar a chamada no post acima de transacoes

	private Movimentacao getMovimentacaoValida() {
		
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(1092609);
		mov.setDescricao("Descricao da Movimentacao");
		mov.setEnvolvido("Envolvido na Mov");
		//mov.setInterresado("teste");
		mov.setTipo("Receita");
		mov.setData_transacao("01/01/2018");
		mov.setData_pagamento("03/01/2018");
		mov.setValor(100);
		mov.setStatus(true);
		
		return mov;
	}


	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
