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

package com.github.mjeanroy.junit.servers.runner;

import com.github.mjeanroy.junit.servers.rules.HandlersRule;
import com.github.mjeanroy.junit.servers.rules.ServerRule;
import com.github.mjeanroy.junit.servers.servers.EmbeddedServer;
import com.github.mjeanroy.junit.servers.servers.configuration.AbstractConfiguration;
import org.junit.rules.TestRule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static com.github.mjeanroy.junit.servers.annotations.handlers.ConfigurationAnnotationHandler.newConfigurationAnnotationHandler;
import static com.github.mjeanroy.junit.servers.annotations.handlers.ServerAnnotationHandler.newServerAnnotationHandler;
import static com.github.mjeanroy.junit.servers.servers.utils.Servers.instantiate;

/**
 * Runner that will start and stop embedded server
 * before tests.
 * This runner will also add some custom rules to
 * inject data to test classes.
 */
public class JunitServerRunner extends BlockJUnit4ClassRunner {

	/**
	 * Embedded server defined before and after tests.
	 */
	private final EmbeddedServer server;

	/**
	 * Server configuration.
	 */
	private final AbstractConfiguration configuration;

	/**
	 * Create runner.
	 *
	 * @param klass Running class.
	 * @throws InitializationError
	 */
	public JunitServerRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.server = instantiate(klass);
		this.configuration = this.server.getConfiguration();
	}

	@Override
	protected List<TestRule> classRules() {
		List<TestRule> classRules = super.classRules();

		ServerRule classRule = new ServerRule(server);
		classRules.add(classRule);

		return classRules;
	}

	@Override
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> testRules = super.getTestRules(target);

		HandlersRule rule = new HandlersRule(target,
				newServerAnnotationHandler(server),
				newConfigurationAnnotationHandler(configuration)
		);

		testRules.add(rule);

		return testRules;
	}
}