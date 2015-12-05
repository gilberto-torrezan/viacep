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
package com.github.gilbertotorrezan.viacep.server;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.gilbertotorrezan.viacep.se.ViaCEPClient;
import com.github.gilbertotorrezan.viacep.shared.ViaCEPEndereco;

/**
 * Testes unitários dos métodos da classe {@link ViaCEPClient}.
 * 
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
@RunWith(JUnit4.class)
public class ViaCEPClientTest {
	
	@Test
	public void testGetEnderecoValido() throws Exception {
		ViaCEPClient client = new ViaCEPClient();
		ViaCEPEndereco endereco = client.getEndereco("20930-040");
		Assert.assertTrue(endereco.getLocalidade().equalsIgnoreCase("Rio de Janeiro"));
		Assert.assertTrue(endereco.getLogradouro().toLowerCase().contains("avenida brasil"));
		
		endereco = client.getEndereco("abc01311000xyz");
		Assert.assertTrue(endereco.getLocalidade().equalsIgnoreCase("São Paulo"));
		Assert.assertTrue(endereco.getLogradouro().toLowerCase().contains("avenida paulista"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetEnderecoInvalido() throws Exception {
		ViaCEPClient client = new ViaCEPClient();
		client.getEndereco("2093004");
	}
	
	@Test
	public void testGetEnderecoInexistente() throws Exception {
		ViaCEPClient client = new ViaCEPClient();
		Assert.assertNull(client.getEndereco("99999999"));
	}
	
	@Test
	public void testGetEnderecosValidos() throws Exception {
		ViaCEPClient client = new ViaCEPClient();
		List<ViaCEPEndereco> enderecos = client.getEnderecos("RJ", "Rio de Janeiro", "Avenida Brasil");
		Assert.assertFalse(enderecos.isEmpty());
		
		enderecos = client.getEnderecos("SP", "São Paulo", "Avenida Paulista");
		Assert.assertFalse(enderecos.isEmpty());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetEnderecosInvalidos() throws Exception {
		ViaCEPClient client = new ViaCEPClient();
		client.getEnderecos("asd", "a", "b");
	}
	
	@Test
	public void testGetEnderecosInexistentes() throws Exception {
		ViaCEPClient client = new ViaCEPClient();
		List<ViaCEPEndereco> enderecos = client.getEnderecos("AC", "Terra do Nunca", "Casa do Peter Pan");
		Assert.assertTrue(enderecos.isEmpty());
	}

}
