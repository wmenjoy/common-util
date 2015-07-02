package com.wmenjoy.utils.bytecode;

import java.lang.reflect.Modifier;

public class PrimitiveType extends JClass {
	private char descriptor;
	private String wrapperName;
	private String getMethodName;
	private String mDescriptor;
	private int returnOp;
	private int arrayType;
	private int dataSize;

	PrimitiveType(final String name, final char desc, final String wrapper,
			final String methodName, final String mDesc, final int opcode,
			final int atype, final int size) {
		super(name);
		this.descriptor = desc;
		this.wrapperName = wrapper;
		this.getMethodName = methodName;
		this.mDescriptor = mDesc;
		this.returnOp = opcode;
		this.arrayType = atype;
		this.dataSize = size;
	}

	/**
	 * Returns <code>true</code> if this object represents a primitive Java
	 * type: boolean, byte, char, short, int, long, float, double, or void.
	 */
	@Override
	public boolean isPrimitive() {
		return true;
	}

	/**
	 * Returns the modifiers for this type. For decoding, use
	 * <code>javassist.Modifier</code>.
	 * 
	 * @see Modifier
	 */
	@Override
	public int getModifiers() {
		return Modifier.PUBLIC | Modifier.FINAL;
	}

	/**
	 * Returns the descriptor representing this type. For example, if the type
	 * is int, then the descriptor is I.
	 */
	public char getDescriptor() {
		return this.descriptor;
	}

	/**
	 * Returns the name of the wrapper class. For example, if the type is int,
	 * then the wrapper class is <code>java.lang.Integer</code>.
	 */
	public String getWrapperName() {
		return this.wrapperName;
	}

	/**
	 * Returns the name of the method for retrieving the value from the wrapper
	 * object. For example, if the type is int, then the method name is
	 * <code>intValue</code>.
	 */
	public String getGetMethodName() {
		return this.getMethodName;
	}

	/**
	 * Returns the descriptor of the method for retrieving the value from the
	 * wrapper object. For example, if the type is int, then the method
	 * descriptor is <code>()I</code>.
	 */
	public String getGetMethodDescriptor() {
		return this.mDescriptor;
	}

	/**
	 * Returns the opcode for returning a value of the type. For example, if the
	 * type is int, then the returned opcode is
	 * <code>javassit.bytecode.Opcode.IRETURN</code>.
	 */
	public int getReturnOp() {
		return this.returnOp;
	}

	/**
	 * Returns the array-type code representing the type. It is used for the
	 * newarray instruction. For example, if the type is int, then this method
	 * returns <code>javassit.bytecode.Opcode.T_INT</code>.
	 */
	public int getArrayType() {
		return this.arrayType;
	}

	/**
	 * Returns the data size of the primitive type. If the type is long or
	 * double, this method returns 2. Otherwise, it returns 1.
	 */
	public int getDataSize() {
		return this.dataSize;
	}
}
