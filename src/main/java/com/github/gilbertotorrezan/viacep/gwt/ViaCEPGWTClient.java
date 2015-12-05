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
package com.github.gilbertotorrezan.viacep.gwt;

import java.util.List;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.ServiceRoots;

import com.github.gilbertotorrezan.viacep.shared.ViaCEPConstants;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;
import com.google.gwt.core.client.GWT;

/**
 * Classe de acesso aos web services da ViaCEP para GWT.
 * Utiliza RestyGWT para a comunicação e serialização de objetos Java.
 * 
 * As chamadas utilizam CORS, e por padrão utilizam HTTPS. Para utilizar HTTP, ou
 * outro host, utilize:
 * 
 * <pre>
 * <code>
 * ServiceRoots.add(ViaCEPGWTService.SERVICE_ROOT_KEY, "http://" + ViaCEPConstants.SERVICE_HOST);
 * </code>
 * </pre>
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 * @see https://resty-gwt.github.io/
 * @see http://viacep.com.br
 */
public class ViaCEPGWTClient {
	
	static {
		if (ServiceRoots.get(ViaCEPGWTService.SERVICE_ROOT_KEY) == null){
			ServiceRoots.add(ViaCEPGWTService.SERVICE_ROOT_KEY, "https://" + ViaCEPConstants.SERVICE_HOST);			
		}
	}
	
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
	
	/**
	 * Executa a consulta de endereço a partir de um CEP.
	 * 
	 * @param cep CEP da localidade onde se quer consultar o endereço. Precisa ter 8 dígitos - a formatação é feita pelo cliente.
	 * CEPs válidos (que contém 8 dígitos): "20930-040", "abc0 1311000xy z", "20930 040". CEPs inválidos (que não contém 8 dígitos): "00000", "abc", "123456789"
	 * 
	 * @param callback O retorno da chamada ao webservice. Erros de validação de campos e de conexão são tratados no callback.
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
			callback.onFailure(null, new IllegalArgumentException("CEP inválido - deve conter 8 dígitos: " + cep));
			return;
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
	 * @param callback O retorno da chamada ao webservice. Erros de validação de campos e de conexão são tratados no callback.
	 */
	public void getEnderecos(String uf, String localidade, String logradouro, final MethodCallback<List<ViaCEPEndereco>> callback){
		if (uf == null || uf.length() != 2){
			callback.onFailure(null, new IllegalArgumentException("UF inválida - deve conter 2 caracteres: " + uf));
			return;
		}
		if (localidade == null || localidade.length() < 3){
			callback.onFailure(null, new IllegalArgumentException("Localidade inválida - deve conter pelo menos 3 caracteres: " + localidade));
			return;
		}
		if (logradouro == null || logradouro.length() < 3){
			callback.onFailure(null, new IllegalArgumentException("Logradouro inválido - deve conter pelo menos 3 caracteres: " + logradouro));
			return;
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
