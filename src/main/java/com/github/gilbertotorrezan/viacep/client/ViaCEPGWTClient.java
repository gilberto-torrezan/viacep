/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Gilberto Torrezan Filho
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package com.github.gilbertotorrezan.viacep.client;

import java.util.List;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import com.google.gwt.core.client.GWT;

/**
 * Classe de acesso aos web services da ViaCEP para GWT.
 * Utiliza RestyGWT para a comunicação e serialização de objetos Java.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 * @see https://resty-gwt.github.io/
 * @see http://viacep.com.br
 */
public class ViaCEPGWTClient {
	
	protected ViaCEPGWTService service;
	
	/**
	 * Construtor padrão.
	 */
	public ViaCEPGWTClient() {
		service = GWT.create(ViaCEPGWTService.class);
	}
	
	/**
	 * Construtor que permite que seja setado um {@link ViaCEPGWTService} customizado.
	 */
	public ViaCEPGWTClient(ViaCEPGWTService service) {
		this.service = service;
	}
	
	void blah(){
		getEndereco("as", new MethodCallback<ViaCEPEndereco>() {
			@Override
			public void onSuccess(Method method, ViaCEPEndereco response) {
				GWT.log(response.getLocalidade());
			}
			
			@Override
			public void onFailure(Method method, Throwable exception) {
				GWT.log("Erro: " + exception, exception);
			}
		});
	}
	
	/**
	 * Executa a consulta de endereço a partir de um CEP.
	 * 
	 * @param cep CEP da localidade onde se quer consultar o endereço. Precisa ter 8 dígitos - a formatação é feita pelo cliente.
	 * CEPs válidos (que contém 8 dígitos): "20930-040", "abc0 1311000xy z", "20930 040". CEPs inválidos (que não contém 8 dígitos): "00000", "abc", "123456789"
	 * 
	 * @param callback O retorno da chamada ao webservice. Erros de conexão são tratados no callback.
	 * 
	 * @throws IllegalArgumentException para CEPs que não possuam 8 dígitos.
	 */
	public void getEndereco(String cep, MethodCallback<ViaCEPEndereco> callback){
		char[] chars = cep.toCharArray();
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i< chars.length; i++){
			if (Character.isDigit(chars[i])){
				builder.append(chars[i]);
			}
		}
		cep = builder.toString();
		
		if (cep.length() != 8){
			throw new IllegalArgumentException("CEP inválido - deve conter 8 dígitos: " + cep);
		}
		
		ViaCEPGWTService service = getService();
		service.getEndereco(cep, callback);
	}
	
	/**
	 * Executa a consulta de endereços a partir da UF, localidade e logradouro
	 * 
	 * @param uf Unidade Federativa. Precisa ter 2 caracteres.
	 * @param localidade Localidade (p.e. município). Precisa ter ao menos 3 caracteres.
	 * @param logradouro Logradouro (p.e. rua, avenida, estrada). Precisa ter ao menos 3 caracteres.
	 * 
	 * @param callback O retorno da chamada ao webservice. Erros de conexão são tratados no callback.
	 * 
	 * @throws IllegalArgumentException para localidades e logradouros com tamanho menor do que 3 caracteres.
	 */
	public void getEnderecos(String uf, String localidade, String logradouro, MethodCallback<List<ViaCEPEndereco>> callback){
		if (uf == null || uf.length() != 2){
			throw new IllegalArgumentException("UF inválida - deve conter 2 caracteres: " + uf);
		}
		if (localidade == null || localidade.length() < 3){
			throw new IllegalArgumentException("Localidade inválida - deve conter pelo menos 3 caracteres: " + localidade);
		}
		if (logradouro == null || logradouro.length() < 3){
			throw new IllegalArgumentException("Logradouro inválido - deve conter pelo menos 3 caracteres: " + logradouro);
		}
		
		ViaCEPGWTService service = getService();
		service.getEnderecos(uf, localidade, logradouro, callback);
	}
	
	/**
	 * Retorna o {@link ViaCEPGWTService} utilizado por esse client.
	 */
	public ViaCEPGWTService getService() {
		return service;
	}
	
	/**
	 * Seta um {@link ViaCEPGWTService} customizado para uso por esse client.
	 */
	public void setService(ViaCEPGWTService service) {
		this.service = service;
	}

}
