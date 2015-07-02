/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wmenjoy.utils.compiler.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wmenjoy.utils.compiler.Compiler;
import com.wmenjoy.utils.lang.reflect.ClassUtil;

/**
 * Abstract compiler. (SPI, Prototype, ThreadSafe)
 * 
 * @author william.liangf
 */
public abstract class AbstractCompiler implements Compiler {

	private static final Pattern PACKAGE_PATTERN = Pattern
			.compile("package\\s+([$_a-zA-Z][$_a-zA-Z0-9\\.]*);");

	private static final Pattern CLASS_PATTERN = Pattern
			.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*\\{");

	@Override
	public Class<?> compile(final String code, final ClassLoader classLoader) {
		final String realCode = code.trim();
		Matcher matcher = PACKAGE_PATTERN.matcher(code);
		String pkg;
		if (matcher.find()) {
			pkg = matcher.group(1);
		} else {
			pkg = "";
		}
		matcher = CLASS_PATTERN.matcher(realCode);
		String cls;
		if (matcher.find()) {
			cls = matcher.group(1);
		} else {
			throw new IllegalArgumentException("No such class name in " + code);
		}
		final String className = pkg != null && pkg.length() > 0 ? pkg + "."
				+ cls : cls;
		try {
			return Class.forName(className, true,
					ClassUtil.getCallerClassLoader(this.getClass()));
		} catch (final ClassNotFoundException e) {
			if (!code.endsWith("}")) {
				throw new IllegalStateException(
						"The java code not endsWith \"}\", code: \n" + code
								+ "\n");
			}
			try {
				return this.doCompile(className, realCode);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Throwable t) {
				throw new IllegalStateException(
						"Failed to compile class, cause: " + t.getMessage()
								+ ", class: " + className + ", code: \n" + code
								+ "\n, stack: " + ClassUtils.toString(t));
			}
		}
	}

	protected abstract Class<?> doCompile(String name, String source)
			throws Throwable;

}
