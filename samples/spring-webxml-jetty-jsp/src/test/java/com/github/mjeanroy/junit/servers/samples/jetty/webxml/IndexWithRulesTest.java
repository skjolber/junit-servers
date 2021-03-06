/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 <mickael.jeanroy@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.mjeanroy.junit.servers.samples.jetty.webxml;

import com.github.mjeanroy.junit.servers.jetty.EmbeddedJetty;
import com.github.mjeanroy.junit.servers.jetty.EmbeddedJettyConfiguration;
import com.github.mjeanroy.junit.servers.rules.JettyServerRule;

import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexWithRulesTest {

	private static final String PATH = "samples/spring-webxml-jetty-jsp/";

	private static EmbeddedJettyConfiguration configuration = initConfiguration();

	private static EmbeddedJettyConfiguration initConfiguration() {
		try {
			String current = new File(".").getCanonicalPath();
			if (!current.endsWith("/")) {
				current += "/";
			}

			String path = current.endsWith(PATH) ? current : current + PATH;

			return EmbeddedJettyConfiguration.builder()
					.withWebapp(path + "src/main/webapp")
					.withClasspath(path + "target/classes")
					.withParentClasspath(WebAppContext.class, new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							return pathname.getName().startsWith("apache-jstl");
						}
					})
					.build();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static EmbeddedJetty jetty = new EmbeddedJetty(configuration);

	private static RestTemplate restTemplate = new RestTemplate();

	@ClassRule
	public static JettyServerRule serverRule = new JettyServerRule(jetty);

	@Test
	public void it_should_have_an_index() {
		String url = jetty.getUrl();
		String message = restTemplate.getForObject(url, String.class);
		assertThat(message).isNotEmpty().contains("Hello");
	}

}
