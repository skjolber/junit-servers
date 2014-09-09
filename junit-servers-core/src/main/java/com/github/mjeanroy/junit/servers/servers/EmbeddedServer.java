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

import javax.servlet.ServletContext;

/**
 * Specification of embedded server.
 * An embedded server:
 * - Can be started, stopped or restarted.
 * - Must provide port that can be used to query resources.
 */
public interface EmbeddedServer {

	/**
	 * Start embedded server.
	 * If server is already started, this method do nothing.
	 */
	void start();

	/**
	 * Stop embedded server.
	 * If server is already stopped, this method do nothing.
	 */
	void stop();

	/** Restart embedded server. */
	void restart();

	/**
	 * Check if embedded server is started.
	 * @return True if embedded server is started, false otherwise.
	 */
	boolean isStarted();

	/**
	 * Get port used by embedded server.
	 * @return Port.
	 */
	int getPort();

	/**
	 * Get server context path.
	 * @return Server context path.
	 */
	String getPath();

	/**
	 * Get URL to query embedded server.
	 * @return URL.
	 */
	String getUrl();

	/**
	 * Get servlet context used within container.
	 * If container is not a servlet container, this method should return null.
	 *
	 * @return Servlet Context from container.
	 */
	ServletContext getServletContext();
}
