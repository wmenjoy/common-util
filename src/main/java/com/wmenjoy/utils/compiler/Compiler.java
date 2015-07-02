package com.wmenjoy.utils.compiler;

public interface Compiler {

	/**
	 * Compile java source code.
	 * 
	 * @param code
	 *            Java source code
	 * @param classLoader
	 *            TODO
	 * @return Compiled class
	 */
	Class<?> compile(String code, ClassLoader classLoader);
}
