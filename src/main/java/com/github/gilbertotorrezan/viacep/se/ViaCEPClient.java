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
package com.github.gilbertotorrezan.viacep.se;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.jr.ob.JSON;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPConstants;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;

/**
 * Classe de acesso aos web services da ViaCEP para Java SE e Android.
 * Utiliza Jackson-jr para a serialização de JSON para objetos Java.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 * @see https://github.com/FasterXML/jackson-jr
 * @see http://viacep.com.br
 */
public class ViaCEPClient {
	
	protected boolean usingHTTPS = false;
	protected JSON service;
	
	/**
	 * Construtor padrão.
	 */
	public ViaCEPClient(){
		service = JSON.std;
	}
	
	/**
	 * Construtor que permite que seja setado um {@link JSON} customizado.
	 */
	public ViaCEPClient(JSON service){
		this.service = service;
	}

	/**
	 * Executa a consulta de endereço a partir de um CEP.
	 * 
	 * @param cep CEP da localidade onde se quer consultar o endereço. Precisa ter 8 dígitos - a formatação é feita pelo cliente.
	 * CEPs válidos (que contém 8 dígitos): "20930-040", "abc0 1311000xy z", "20930 040". CEPs inválidos (que não contém 8 dígitos): "00000", "abc", "123456789"
	 * 
	 * @return O endereço encontrado para o CEP, ou <code>null</code> caso não tenha sido encontrado.
	 * @throws IOException em casos de erro de conexão.
	 * @throws IllegalArgumentException para CEPs que não possuam 8 dígitos.
	 */
	public ViaCEPEndereco getEndereco(String cep) throws IOException {
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
		
		String urlString = getHost() + cep + "/json/";
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			ViaCEPEndereco obj = getService().beanFrom(ViaCEPEndereco.class, in);
			if (obj == null || obj.getCep() == null){
				return null;
			}
			return obj;
		}
		finally {
			urlConnection.disconnect();
		}
	}
	
	/**
	 * Executa a consulta de endereços a partir da UF, localidade e logradouro
	 * 
	 * @param uf Unidade Federativa. Precisa ter 2 caracteres.
	 * @param localidade Localidade (p.e. município). Precisa ter ao menos 3 caracteres.
	 * @param logradouro Logradouro (p.e. rua, avenida, estrada). Precisa ter ao menos 3 caracteres.
	 * 
	 * @return Os endereços encontrado para os dados enviados, nunca <code>null</code>. Caso não sejam encontrados endereços, uma lista vazia é retornada.
	 * @throws IOException em casos de erro de conexão.
	 * @throws IllegalArgumentException para localidades e logradouros com tamanho menor do que 3 caracteres.
	 */
	public List<ViaCEPEndereco> getEnderecos(String uf, String localidade, String logradouro) throws IOException {
		if (uf == null || uf.length() != 2){
			throw new IllegalArgumentException("UF inválida - deve conter 2 caracteres: " + uf);
		}
		if (localidade == null || localidade.length() < 3){
			throw new IllegalArgumentException("Localidade inválida - deve conter pelo menos 3 caracteres: " + localidade);
		}
		if (logradouro == null || logradouro.length() < 3){
			throw new IllegalArgumentException("Logradouro inválido - deve conter pelo menos 3 caracteres: " + logradouro);
		}
		
		String urlString = getHost() + uf + "/" + localidade + "/" + logradouro + "/json/";
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			List<ViaCEPEndereco> obj = getService().listOfFrom(ViaCEPEndereco.class, in);
			return obj;
		}
		finally {
			urlConnection.disconnect();
		}
	}
	
	/**
	 * Método interno que retorna o host dos webservices da ViaCEP. Por padrão é "http://viacep.com.br/ws/".
	 */
	protected String getHost(){
		String host = (isUsingHTTPS() ? "https://" : "http://") + ViaCEPConstants.SERVICE_HOST;
		return host;
	}

	/**
	 * Retorna se o client está utilizando HTTP ou HTTPS para consultar os web services. Por padrão utiliza HTTP.
	 */
	public boolean isUsingHTTPS() {
		return usingHTTPS;
	}

	/**
	 *	Seta se o client deve utilizar HTTPS para consultar os web services. Por padrão é <code>false</code>. 
	 */
	public void setUsingHTTPS(boolean usingHTTPS) {
		this.usingHTTPS = usingHTTPS;
	}

	/**
	 * Retorna o {@link JSON} utilizado na desserialização de objetos.
	 */
	public JSON getService() {
		return service;
	}

	/**
	 * Seta um {@link JSON} customizado para a desserialização de objetos.
	 */
	public void setService(JSON service) {
		this.service = service;
	}
}
