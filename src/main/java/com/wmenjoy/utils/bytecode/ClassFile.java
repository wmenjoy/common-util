package com.wmenjoy.utils.bytecode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.wmenjoy.utils.lang.FileParser;
import com.wmenjoy.utils.lang.StringUtils;

public class ClassFile {
	/**
	 * Class File 的魔幻数字，所有class文件都是这么一个格式
	 */
	private static final int CLASS_FILE_MAGIC = 0xCAFEBABE;

	int magic;
	int major, minor;
	ConstantPool constPool;
	int accessFlags;
	int thisClass;
	int superClass;
	int[] interfaces;
	ArrayList fields;
	ArrayList methods;
	ArrayList attributes;
	String thisclassname; // not JVM-internal name
	String[] cachedInterfaces;
	String cachedSuperclass;

	/**
	 * The major version number of class files for JDK 1.1.
	 */
	public static final int JAVA_1 = 45;

	/**
	 * The major version number of class files for JDK 1.2.
	 */
	public static final int JAVA_2 = 46;

	/**
	 * The major version number of class files for JDK 1.3.
	 */
	public static final int JAVA_3 = 47;

	/**
	 * The major version number of class files for JDK 1.4.
	 */
	public static final int JAVA_4 = 48;

	/**
	 * The major version number of class files for JDK 1.5.
	 */
	public static final int JAVA_5 = 49;

	/**
	 * The major version number of class files for JDK 1.6.
	 */
	public static final int JAVA_6 = 50;

	/**
	 * The major version number of class files for JDK 1.7.
	 */
	public static final int JAVA_7 = 51;

	/**
	 * The major version number of class files created from scratch. The default
	 * value is 47 (JDK 1.3) or 49 (JDK 1.5) if the JVM supports
	 * <code>java.lang.StringBuilder</code>.
	 */
	public static int MAJOR_VERSION = JAVA_3;

	final FileParser parser;

	public ClassFile(final String file) {
		if (StringUtils.isBlank(file)) {
			throw new IllegalArgumentException("file Paramter is null");
		}

		// 加上文件名的校验

		try {
			this.parser = new FileParser(file);
		} catch (final FileNotFoundException e) {
			throw new IllegalArgumentException("file is not exist");
		}

		try {
			this.initClassFile();
		} catch (final IOException e) {
			throw new IllegalArgumentException(
					"file is not a standard class file");
		}
	}

	private void initClassFile() throws IOException {
		// 读写magic number
		final int magic = this.parser.getInt();
		if (magic != CLASS_FILE_MAGIC) {
			throw new IllegalArgumentException(
					"file is not a standard class file");
		}

		final int major = this.parser.getShort();
		final int minor = this.parser.getShort();

		final int constantPoolCount = this.parser.getShort();
		final ConstantPool[] cpInfo = this.readConstantPool();

		final int accessFlag = this.parser.getShort();
		final int thisClass = this.parser.getShort();
		final int superClass = this.parser.getShort();
		final int interfaceCount = this.parser.getShort();
		final short[] interfaces = this.parser.getShortArray(interfaceCount);
		final int fieldsCount = this.parser.getShort();
		final FieldInfo[] fields = this.readFields(fieldsCount);
		final short methodsCount = this.parser.getShort();
		final MethodInfo[] methods = this.readMethods(methodsCount);

		final int atrributesCount = this.parser.getShort();
		final AttributeInfo[] attributes = this.readAttributes(atrributesCount);

	}

	private AttributeInfo[] readAttributes(final int atrributesCount) {
		// TODO Auto-generated method stub
		return null;
	}

	private MethodInfo[] readMethods(final short methodsCount) {
		// TODO Auto-generated method stub
		return null;
	}

	private FieldInfo[] readFields(final int fieldsCount) {
		// TODO Auto-generated method stub
		return null;
	}

	private ConstantPool[] readConstantPool() {
		// TODO Auto-generated method stub
		return null;
	}

	public void getRefClasses(final ClassMap cm) {

	}

	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(final String[] args) {
	}
}
