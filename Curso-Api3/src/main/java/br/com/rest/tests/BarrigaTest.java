package br.com.rest.tests;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.rest.classeAjudaTodosTeste.BaseTest;
import br.com.rest.utils.DataUtils;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)  /// isso aqui serve para ordenar a ordem do teste

public class BarrigaTest extends BaseTest {
	
	private String TOKEN;
	
	private static String CONTA_NAME = "Conta " + System.nanoTime(); //deixando como static para não zerar a variavel
																	 //para deixar o nome diferente utiliza + System.nanoTime();
	private static Integer CONTA_ID;
	
	private static Integer MOV_ID;
	
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
	
	public void t01_naoDeveAcessarAPISemToken() {
		
		given()
		.when()
			.get("/contas")
		.then()
			.statusCode(401)
			.log();
		
		
	}
	
	@Test
	
	public void t02_deveIncluirContaComSucesso() {	
		
		CONTA_ID = given() //AQUI INSERO MINHA VARIAVEL CRIADA PARA POVOAR MEU ID
		.header("Authorization", "JWT " + TOKEN)
		  .body("{\"nome\": \""+CONTA_NAME+"\" }") //USAR O MAP
		.when()
			.post("/contas")
		.then()
			.statusCode(201)
			.log().all()
			.extract().path("id"); // nesse ponto estou extraindo o ID e armazendo na variavel CONTA_ID criada logo acima
	

	}
	
	
	@Test
	
	public void t03_deveAlterarContaComSucesso() {
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body("{\"nome\": \""+CONTA_NAME+"alterada\" }")
		  .pathParam("id", "CONTA_ID") // aqui estou usando o id capturado na inserção
		.when()
			.put("/contas/{id}") //passando o {id}
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", is("conta alterada paulo2"));
			
			

	}

	
	
	@Test
	
	public void t04_naoDeveInserirContaMesmoNome() {
		
		given()
		.header("Authorization", "JWT " + TOKEN)
		  .body("{\"nome\": \""+CONTA_NAME+ "alterada\" }")
		.when()
			.post("/contas")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Já existe uma conta com esse nome!"));
			
			

	}


	

	@Test
	
	public void t05_DeveInserirMovimentacaoSucesso() {
		
		
		Movimentacao mov = getMovimentacaoValida();
		
		
		MOV_ID = given()
		.header("Authorization", "JWT " + TOKEN)
		  .body(mov)
		.when()
			.post("/transacoes")
		.then()
			.log().all()
			.statusCode(201)
			.extract().path("id");
			
			

	}
	
	
@Test
	
	public void t06_deveValidarCamposObrigatóriosMovimentacao() {
		
		
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

public void t07_naoDeveInserirMovimentacaoDataFutura() {
	
	
	Movimentacao mov = getMovimentacaoValida();
	mov.setData_transacao(DataUtils.getDataDiferencaDias(2)); //aqui eu informo que a data sempre será data atual mas duas pra frente
	
	
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

public void t08_naoDeveRemoverContaComMovimentacao() {
	
	
	given()
	.header("Authorization", "JWT " + TOKEN)
	.pathParam("id", CONTA_ID)
	.when()
		.delete("contas/{id}")
	.then()
		.log().all()
		.statusCode(500)
		.body("constraint", is("transacoes_conta_id_foreign"));
		

	
}





@Test

public void t09_deveCalcularSaldoContas() {
	
	
	given()
	.header("Authorization", "JWT " + TOKEN)
	.when()
		.get("/saldo")
	.then()
		.log().all()
		.statusCode(200)
		.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("333.00"))
		.body("find{it.conta_id == 1092609}.conta", is("conta alterada paulo2"));
		

	
}


@Test

public void t10_deveRemoverMovimentacao() {
	
	
	given()
	.header("Authorization", "JWT " + TOKEN)
	.pathParam("id", MOV_ID)
	.when()
		.delete("/transacoes/{id}")
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


	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//teste ph
	
	
	
	
	
	
	
	
	
	
	
	

}
 