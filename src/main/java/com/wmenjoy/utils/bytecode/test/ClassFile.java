package com.wmenjoy.utils.bytecode.test;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.wmenjoy.utils.bytecode.AttributeInfo;
import com.wmenjoy.utils.bytecode.ClassMap;
import com.wmenjoy.utils.lang.StringUtils;

/**
 * 深入理解的java的机制，
 * 
 * 要对java的class文件，以及加载过程有所理解
 * 
 * 
 * @author jinliang.liu
 * 
 */
public class ClassFile {
	/**
	 * Class File 的魔幻数字，所有class文件都是这么一个格式
	 */
	private static final int CLASS_FILE_MAGIC = 0xCAFEBABE;
	// 标识的东东， 实际使用起来是没有用处的
	private int magic;
	// u2, 两位无符号整数，为了能够容下，使用int来表示，下面所有的雷同
	int major, minor;
	ConstantPool constPool;
	int accessFlags;
	int thisClass;
	int superClass;
	int[] interfaces;
	FieldInfo[] fields;
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

	final DataInputStream is;

	int readFlag;

	public ClassFile(final String file) {
		if (StringUtils.isBlank(file)) {
			throw new IllegalArgumentException("file Paramter is null");
		}

		// 加上文件名的校验

		try {
			this.is = new DataInputStream(new FileInputStream(file));
		} catch (final FileNotFoundException e) {
			throw new IllegalArgumentException("file is not exist");
		}

		try {
			this.readClassFile(this.is);
		} catch (final IOException e) {
			throw new IllegalArgumentException(
					"file is not a standard class file");
		}
	}

	public ClassFile(final InputStream is) {
		if (is == null) {
			throw new IllegalArgumentException("file Paramter is null");
		}

		// 加上文件名的校验

		this.is = new DataInputStream(is);

		try {
			this.readClassFile(this.is);
		} catch (final IOException e) {
			throw new IllegalArgumentException(
					"file is not a standard class file");
		}
	}

	private void readClassFile(final DataInputStream is) throws IOException {
		// 读写magic number
		this.magic = this.is.readInt();
		if (this.magic != CLASS_FILE_MAGIC) {
			throw new IllegalArgumentException("not a valid maigc number:"
					+ Integer.toHexString(this.magic));
		}

		final int major = this.is.readUnsignedShort();
		final int minor = this.is.readUnsignedShort();

		final int constantPoolCount = this.is.readUnsignedShort();
		final ConstantPool[] cpInfo = new ConstantPool[constantPoolCount];
		for (int i = 0; i < constantPoolCount; i++) {
			cpInfo[i] = new ConstantPool(is);
		}

		final int accessFlag = this.is.readUnsignedShort();
		final int thisClass = this.is.readUnsignedShort();
		final int superClass = this.is.readUnsignedShort();
		final int interfaceCount = this.is.readUnsignedShort();
		final int[] interfaces = new int[interfaceCount];
		for (int i = 0; i < interfaces.length; i++) {
			interfaces[i] = this.is.readUnsignedShort();
		}

		final int fieldsCount = this.is.readUnsignedShort();

		final FieldInfo[] fields = this.readFields(fieldsCount);
		final int methodsCount = this.is.readUnsignedShort();
		final MethodInfo[] methods = this.readMethods(methodsCount);

		final int atrributesCount = this.is.readUnsignedShort();
		final AttributeInfo[] attributes = this.readAttributes(atrributesCount);

	}

	private AttributeInfo[] readAttributes(final int atrributesCount) {
		// TODO Auto-generated method stub
		return null;
	}

	private MethodInfo[] readMethods(final int methodsCount) {
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
