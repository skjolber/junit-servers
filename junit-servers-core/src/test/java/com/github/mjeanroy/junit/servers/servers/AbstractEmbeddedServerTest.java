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

package com.github.mjeanroy.junit.servers.servers;

import org.junit.Test;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AbstractEmbeddedServerTest {

	private TestServer server = new TestServer();

	@Test
	public void it_should_not_be_started() {
		assertThat(server.isStarted()).isFalse();
	}

	@Test
	public void it_should_have_default_configuration() {
		assertThat(server.path).isEqualTo("/");
		assertThat(server.webapp).isEqualTo("src/main/webapp");
		assertThat(server.port).isZero();
	}

	@Test
	public void it_should_have_custom_configuration() {
		String path = "/foo";
		int port = 8080;
		String webapp = "/foo/bar";
		AbstractEmbeddedServerConfiguration configuration = new EmbeddedConfiguration()
				.withPath(path)
				.withPort(port)
				.withWebapp(webapp);

		server = new TestServer(configuration);

		assertThat(server.path).isEqualTo(path);
		assertThat(server.webapp).isEqualTo(webapp);
		assertThat(server.port).isEqualTo(port);
	}

	@Test
	public void it_should_start_server() {
		assertThat(server.doStart).isZero();

		server.start();

		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStart).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_not_start_server_twice() {
		assertThat(server.doStart).isZero();

		server.start();
		server.start();

		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStart).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_stop_server() {
		assertThat(server.doStop).isZero();
		assertThat(server.isStarted()).isFalse();

		server.start();
		assertThat(server.isStarted()).isTrue();

		server.stop();

		assertThat(server.isStarted()).isFalse();
		assertThat(server.doStop).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_not_stop_server_twice() {
		assertThat(server.doStop).isZero();
		assertThat(server.isStarted()).isFalse();

		server.start();
		assertThat(server.isStarted()).isTrue();

		server.stop();
		server.stop();

		assertThat(server.isStarted()).isFalse();
		assertThat(server.doStop).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_restart_server() {
		assertThat(server.doStop).isZero();
		assertThat(server.doStart).isZero();
		assertThat(server.isStarted()).isFalse();

		server.start();
		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStart).isNotZero().isEqualTo(1);

		server.restart();

		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStop).isNotZero().isEqualTo(1);
		assertThat(server.doStart).isNotZero().isEqualTo(2);
	}

	@Test
	public void it_should_block_until_server_is_started() throws Exception {
		assertThat(server.doStart).isZero();
		assertThat(server.isStarted()).isFalse();

		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal1 = new CountDownLatch(1);
		CountDownLatch doneSignal2 = new CountDownLatch(1);

		Thread th1 = new Thread(new StartWorker(server, startSignal, doneSignal1));
		Thread th2 = new Thread(new StartWorker(server, startSignal, doneSignal2));

		th1.start();
		th2.start();

		assertThat(server.doStart).isZero();
		assertThat(server.isStarted()).isFalse();

		startSignal.countDown();

		doneSignal2.await();
		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStart).isNotZero().isEqualTo(1);

		doneSignal1.await();
		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStart).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_block_until_server_is_stopped() throws Exception {
		assertThat(server.doStart).isZero();
		assertThat(server.isStarted()).isFalse();

		server.start();
		assertThat(server.isStarted()).isTrue();
		assertThat(server.doStart).isNotZero().isEqualTo(1);

		CountDownLatch stopSignal = new CountDownLatch(1);
		CountDownLatch doneSignal1 = new CountDownLatch(1);
		CountDownLatch doneSignal2 = new CountDownLatch(1);

		Thread th1 = new Thread(new StopWorker(server, stopSignal, doneSignal1));
		Thread th2 = new Thread(new StopWorker(server, stopSignal, doneSignal2));

		th1.start();
		th2.start();

		assertThat(server.doStop).isZero();
		assertThat(server.isStarted()).isTrue();

		stopSignal.countDown();

		doneSignal2.await();
		assertThat(server.isStarted()).isFalse();
		assertThat(server.doStop).isNotZero().isEqualTo(1);

		doneSignal1.await();
		assertThat(server.isStarted()).isFalse();
		assertThat(server.doStop).isNotZero().isEqualTo(1);
	}

	@Test
	public void it_should_set_environment_properties() {
		String name1 = "foo";
		String oldValue1 = "foo";
		String newValue1 = "bar";
		System.setProperty(name1, oldValue1);

		String name2 = "foo1";
		String newValue2 = "bar1";

		final Map<String, String> properties = new HashMap<String, String>();
		properties.put(name1, newValue1);
		properties.put(name2, newValue2);
		server = new TestServer(new AbstractEmbeddedServerConfiguration() {
			@Override
			public Map<String, String> getEnvProperties() {
				return properties;
			}
		});

		server.start();

		assertThat(System.getProperty(name1))
				.isNotNull()
				.isEqualTo(newValue1);

		assertThat(System.getProperty(name2))
				.isNotNull()
				.isEqualTo(newValue2);

		server.stop();

		assertThat(System.getProperty(name1))
				.isNotNull()
				.isEqualTo(oldValue1);

		assertThat(System.getProperty(name2))
				.isNull();

		System.clearProperty(name1);
		System.clearProperty(name2);
	}

	@Test
	public void it_should_execute_hook() {
		final Hook hook = mock(Hook.class);

		server = new TestServer(new AbstractEmbeddedServerConfiguration() {
			@Override
			public List<Hook> getHooks() {
				return asList(hook);
			}
		});

		server.start();

		verify(hook).pre(server);
		verify(hook).onStarted(server, server.getServletContext());
		verify(hook, never()).post(server);

		server.stop();

		verify(hook).post(server);
		verify(hook, times(1)).pre(server);
		verify(hook, times(1)).pre(server);
	}

	@Test
	public void it_should_get_path() {
		String path = server.getPath();
		assertThat(path).isEqualTo("/");
	}

	@Test
	public void it_should_get_custom_path() {
		TestServer server = new TestServer(new AbstractEmbeddedServerConfiguration() {
			@Override
			public String getPath() {
				return "/foo";
			}
		});

		String path = server.getPath();
		assertThat(path).isEqualTo("/foo");
	}

	@Test
	public void it_should_get_url() {
		String url = server.getUrl();
		assertThat(url).isEqualTo("http://localhost:0/");
	}

	@Test
	public void it_should_get_url_with_custom_path() {
		TestServer server = new TestServer(new AbstractEmbeddedServerConfiguration() {
			@Override
			public String getPath() {
				return "/foo";
			}
		});

		String url = server.getUrl();

		assertThat(url).isEqualTo("http://localhost:0/foo");
	}

	@Test
	public void it_should_get_url_with_custom_path_pre_pending_slash() {
		TestServer server = new TestServer(new AbstractEmbeddedServerConfiguration() {
			@Override
			public String getPath() {
				return "foo";
			}
		});

		String url = server.getUrl();

		assertThat(url).isEqualTo("http://localhost:0/foo");
	}

	private static class TestServer extends AbstractEmbeddedServer {

		public int doStart = 0;

		public int doStop = 0;

		public ServletContext servletContext;

		public TestServer() {
			super(new EmbeddedConfiguration());
			servletContext = mock(ServletContext.class);
		}

		public TestServer(AbstractEmbeddedServerConfiguration configuration) {
			super(configuration);
			servletContext = mock(ServletContext.class);
		}

		@Override
		protected void doStart() {
			doStart++;
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		protected void doStop() {
			doStop++;

			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public int getPort() {
			return 0;
		}

		@Override
		public ServletContext getServletContext() {
			return isStarted() ? servletContext : null;
		}
	}

	private static class EmbeddedConfiguration extends AbstractEmbeddedServerConfiguration<EmbeddedConfiguration> {

	}

	private static class StartWorker implements Runnable {
		private final TestServer server;
		private final CountDownLatch startSignal;
		private final CountDownLatch doneSignal;

		public StartWorker(TestServer server, CountDownLatch startSignal, CountDownLatch doneSignal) {
			this.server = server;
			this.startSignal = startSignal;
			this.doneSignal = doneSignal;
		}

		@Override
		public void run() {
			try {
				startSignal.await();
				server.start();
				doneSignal.countDown();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private static class StopWorker implements Runnable {
		private final TestServer server;
		private final CountDownLatch startSignal;
		private final CountDownLatch doneSignal;

		public StopWorker(TestServer server, CountDownLatch startSignal, CountDownLatch doneSignal) {
			this.server = server;
			this.startSignal = startSignal;
			this.doneSignal = doneSignal;
		}

		@Override
		public void run() {
			try {
				startSignal.await();
				server.stop();
				doneSignal.countDown();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}
