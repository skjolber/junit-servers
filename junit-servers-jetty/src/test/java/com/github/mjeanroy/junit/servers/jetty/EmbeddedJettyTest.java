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

package com.github.mjeanroy.junit.servers.jetty;

import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmbeddedJettyTest {

	private EmbeddedJetty jetty;

	@After
	public void tearDown() {
		if (jetty != null) {
			jetty.stop();
		}
	}

	@Test
	public void it_should_start_jetty() {
		jetty = new EmbeddedJetty();
		jetty.start();

		assertThat(jetty.isStarted()).isTrue();
		assertThat(jetty.getPort()).isNotZero();
	}

	@Test
	public void it_should_stop_jetty() {
		jetty = new EmbeddedJetty();
		jetty.start();

		assertThat(jetty.isStarted()).isTrue();
		assertThat(jetty.getPort()).isNotZero();

		jetty.stop();
		assertThat(jetty.isStarted()).isFalse();
		assertThat(jetty.getPort()).isZero();
	}

	@Test
	public void it_should_get_servlet_context() {
		jetty = new EmbeddedJetty();
		jetty.start();
		assertThat(jetty.getServletContext()).isNotNull();
	}

	@Test
	public void it_should_get_original_jetty() {
		jetty = new EmbeddedJetty();
		assertThat(jetty.getDelegate()).isNotNull();
	}
}
