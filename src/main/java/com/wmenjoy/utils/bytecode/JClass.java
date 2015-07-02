package com.wmenjoy.utils.bytecode;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collection;

public class JClass {
	protected String qualifiedName;

	/**
	 * The version number of this release.
	 */
	public static final String version = "3.15.0-GA";

	/**
	 * Prints the version number and the copyright notice.
	 * 
	 * <p>
	 * The following command invokes this method:
	 * 
	 * <ul>
	 * 
	 * <pre>
	 * java -jar javassist.jar
	 * </pre>
	 * 
	 * </ul>
	 */
	public static void main(final String[] args) {
		System.out.println("Javassist version " + JClass.version);
		System.out.println("Copyright (C) 1999-2011 Shigeru Chiba."
				+ " All Rights Reserved.");
	}

	static final String javaLangObject = "java.lang.Object";

	/**
	 * The <code>MClass</code> object representing the <code>boolean</code>
	 * type.
	 */
	public static JClass booleanType;

	/**
	 * The <code>MClass</code> object representing the <code>char</code> type.
	 */
	public static JClass charType;

	/**
	 * The <code>MClass</code> object representing the <code>byte</code> type.
	 */
	public static JClass byteType;

	/**
	 * The <code>MClass</code> object representing the <code>short</code> type.
	 */
	public static JClass shortType;

	/**
	 * The <code>MClass</code> object representing the <code>int</code> type.
	 */
	public static JClass intType;

	/**
	 * The <code>MClass</code> object representing the <code>long</code> type.
	 */
	public static JClass longType;

	/**
	 * The <code>MClass</code> object representing the <code>float</code> type.
	 */
	public static JClass floatType;

	/**
	 * The <code>MClass</code> object representing the <code>double</code> type.
	 */
	public static JClass doubleType;

	/**
	 * The <code>MClass</code> object representing the <code>void</code> type.
	 */
	public static JClass voidType;

	static JClass[] primitiveTypes;

	static {
		primitiveTypes = new JClass[9];

		booleanType = new PrimitiveType("boolean", 'Z', "java.lang.Boolean",
				"booleanValue", "()Z", Opcode.IRETURN, Opcode.T_BOOLEAN, 1);
		primitiveTypes[0] = booleanType;

		charType = new PrimitiveType("char", 'C', "java.lang.Character",
				"charValue", "()C", Opcode.IRETURN, Opcode.T_CHAR, 1);
		primitiveTypes[1] = charType;

		byteType = new PrimitiveType("byte", 'B', "java.lang.Byte",
				"byteValue", "()B", Opcode.IRETURN, Opcode.T_BYTE, 1);
		primitiveTypes[2] = byteType;

		shortType = new PrimitiveType("short", 'S', "java.lang.Short",
				"shortValue", "()S", Opcode.IRETURN, Opcode.T_SHORT, 1);
		primitiveTypes[3] = shortType;

		intType = new PrimitiveType("int", 'I', "java.lang.Integer",
				"intValue", "()I", Opcode.IRETURN, Opcode.T_INT, 1);
		primitiveTypes[4] = intType;

		longType = new PrimitiveType("long", 'J', "java.lang.Long",
				"longValue", "()J", Opcode.LRETURN, Opcode.T_LONG, 2);
		primitiveTypes[5] = longType;

		floatType = new PrimitiveType("float", 'F', "java.lang.Float",
				"floatValue", "()F", Opcode.FRETURN, Opcode.T_FLOAT, 1);
		primitiveTypes[6] = floatType;

		doubleType = new PrimitiveType("double", 'D', "java.lang.Double",
				"doubleValue", "()D", Opcode.DRETURN, Opcode.T_DOUBLE, 2);
		primitiveTypes[7] = doubleType;

		voidType = new PrimitiveType("void", 'V', "java.lang.Void", null, null,
				Opcode.RETURN, 0, 0);
		primitiveTypes[8] = voidType;
	}

	protected JClass(final String name) {
		this.qualifiedName = name;
	}

	/**
	 * Converts the object to a string.
	 */
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer(this.getClass().getName());
		buf.append("@");
		buf.append(Integer.toHexString(this.hashCode()));
		buf.append("[");
		this.extendToString(buf);
		buf.append("]");
		return buf.toString();
	}

	/**
	 * Implemented in subclasses to add to the {@link #toString()} result.
	 * Subclasses should put a space before each token added to the buffer.
	 */
	protected void extendToString(final StringBuffer buffer) {
		buffer.append(this.getName());
	}

	/**
	 * Returns a <code>ClassPool</code> for this class.
	 */
	public ClassPool getClassPool() {
		return null;
	}

	/**
	 * Returns a class file for this class.
	 * 
	 * <p>
	 * This method is not available if <code>isFrozen()</code> is true.
	 */
	public ClassFile getClassFile() {
		this.checkModify();
		return this.getClassFile2();
	}

	/**
	 * Returns a class file for this class (read only). Normal applications do
	 * not need calling this method. Use <code>getClassFile()</code>.
	 * 
	 * <p>
	 * The <code>ClassFile</code> object obtained by this method is read only.
	 * Changes to this object might not be reflected on a class file generated
	 * by <code>toBytecode()</code>, <code>toClass()</code>, etc.
	 * 
	 * <p>
	 * This method is available even if <code>isFrozen()</code> is true.
	 * However, if the class is frozen, it might be also pruned.
	 * 
	 * @see JClass#getClassFile()
	 * @see JClass#isFrozen()
	 * @see JClass#prune()
	 */
	public ClassFile getClassFile2() {
		return null;
	}

	/**
	 * Returns the uniform resource locator (URL) of the class file.
	 */
	public URL getURL() throws NotFoundException {
		throw new NotFoundException(this.getName());
	}

	/**
	 * Returns true if the definition of the class has been modified.
	 */
	public boolean isModified() {
		return false;
	}

	/**
	 * Returns true if the class has been loaded or written out and thus it
	 * cannot be modified any more.
	 * 
	 * @see #defrost()
	 * @see #detach()
	 */
	public boolean isFrozen() {
		return true;
	}

	/**
	 * Makes the class frozen.
	 * 
	 * @see #isFrozen()
	 * @see #defrost()
	 * @since 3.6
	 */
	public void freeze() {
	}

	/*
	 * Note: this method is overridden by MClassType
	 */
	void checkModify() throws RuntimeException {
		if (this.isFrozen()) {
			throw new RuntimeException(this.getName() + " class is frozen");
		}

		// isModified() must return true after this method is invoked.
	}

	/**
	 * Defrosts the class so that the class can be modified again.
	 * 
	 * <p>
	 * To avoid changes that will be never reflected, the class is frozen to be
	 * unmodifiable if it is loaded or written out. This method should be called
	 * only in a case that the class will be reloaded or written out later
	 * again.
	 * 
	 * <p>
	 * If <code>defrost()</code> will be called later, pruning must be
	 * disallowed in advance.
	 * 
	 * @see #isFrozen()
	 * @see #stopPruning(boolean)
	 * @see #detach()
	 */
	public void defrost() {
		throw new RuntimeException("cannot defrost " + this.getName());
	}

	/**
	 * Returns <code>true</code> if this object represents a primitive Java
	 * type: boolean, byte, char, short, int, long, float, double, or void.
	 */
	public boolean isPrimitive() {
		return false;
	}

	/**
	 * Returns <code>true</code> if this object represents an array type.
	 */
	public boolean isArray() {
		return false;
	}

	/**
	 * If this object represents an array, this method returns the component
	 * type of the array. Otherwise, it returns <code>null</code>.
	 */
	public JClass getComponentType() throws NotFoundException {
		return null;
	}

	/**
	 * Returns <code>true</code> if this class extends or implements
	 * <code>clazz</code>. It also returns <code>true</code> if this class is
	 * the same as <code>clazz</code>.
	 */
	public boolean subtypeOf(final JClass clazz) throws NotFoundException {
		return this == clazz || this.getName().equals(clazz.getName());
	}

	/**
	 * Obtains the fully-qualified name of the class.
	 */
	public String getName() {
		return this.qualifiedName;
	}

	/**
	 * Obtains the not-qualified class name.
	 */
	public final String getSimpleName() {
		final String qname = this.qualifiedName;
		final int index = qname.lastIndexOf('.');
		if (index < 0) {
			return qname;
		} else {
			return qname.substring(index + 1);
		}
	}

	/**
	 * Obtains the package name. It may be <code>null</code>.
	 */
	public final String getPackageName() {
		final String qname = this.qualifiedName;
		final int index = qname.lastIndexOf('.');
		if (index < 0) {
			return null;
		} else {
			return qname.substring(0, index);
		}
	}

	/**
	 * Sets the class name
	 * 
	 * @param name
	 *            fully-qualified name
	 */
	public void setName(final String name) {
		this.checkModify();
		if (name != null) {
			this.qualifiedName = name;
		}
	}

	/**
	 * Substitutes <code>newName</code> for all occurrences of a class name
	 * <code>oldName</code> in the class file.
	 * 
	 * @param oldName
	 *            replaced class name
	 * @param newName
	 *            substituted class name
	 */
	public void replaceClassName(final String oldName, final String newName) {
		this.checkModify();
	}

	/**
	 * Changes class names appearing in the class file according to the given
	 * <code>map</code>.
	 * 
	 * <p>
	 * All the class names appearing in the class file are tested with
	 * <code>map</code> to determine whether each class name is replaced or not.
	 * Thus this method can be used for collecting all the class names in the
	 * class file. To do that, first define a subclass of <code>ClassMap</code>
	 * so that <code>get()</code> records all the given parameters. Then, make
	 * an instance of that subclass as an empty hash-table. Finally, pass that
	 * instance to this method. After this method finishes, that instance would
	 * contain all the class names appearing in the class file.
	 * 
	 * @param map
	 *            the hashtable associating replaced class names with
	 *            substituted names.
	 */
	public void replaceClassName(final ClassMap map) {
		this.checkModify();
	}

	/**
	 * Returns a collection of the names of all the classes referenced in this
	 * class. That collection includes the name of this class.
	 * 
	 * <p>
	 * This method may return <code>null</code>.
	 * 
	 * @return a <code>Collection&lt;String&gt;</code> object.
	 */
	public synchronized Collection getRefClasses() {
		final ClassFile cf = this.getClassFile2();
		if (cf != null) {
			final ClassMap cm = new ClassMap() {
				public void put(final String oldname, final String newname) {
					put0(oldname, newname);
				}

				public Object get(final Object jvmClassName) {
					final String n = toJavaName((String) jvmClassName);
					put0(n, n);
					return null;
				}

				public void fix(final String name) {
				}
			};
			cf.getRefClasses(cm);
			return cm.values();
		} else {
			return null;
		}
	}

	/**
	 * Determines whether this object represents a class or an interface. It
	 * returns <code>true</code> if this object represents an interface.
	 */
	public boolean isInterface() {
		return false;
	}

	/**
	 * Determines whether this object represents an annotation type. It returns
	 * <code>true</code> if this object represents an annotation type.
	 * 
	 * @since 3.2
	 */
	public boolean isAnnotation() {
		return false;
	}

	/**
	 * Determines whether this object represents an enum. It returns
	 * <code>true</code> if this object represents an enum.
	 * 
	 * @since 3.2
	 */
	public boolean isEnum() {
		return false;
	}

	/**
	 * Returns the modifiers for this class, encoded in an integer. For
	 * decoding, use <code>javassist.Modifier</code>.
	 * 
	 * <p>
	 * If the class is a static nested class (a.k.a. static inner class), the
	 * returned modifiers include <code>Modifier.STATIC</code>.
	 * 
	 * @see Modifier
	 */
	public int getModifiers() {
		return 0;
	}

	/**
	 * Returns true if the class has the specified annotation class.
	 * 
	 * @param clz
	 *            the annotation class.
	 * @return <code>true</code> if the annotation is found, otherwise
	 *         <code>false</code>.
	 * @since 3.11
	 */
	public boolean hasAnnotation(final Class clz) {
		return false;
	}

	/**
	 * Returns the annotation if the class has the specified annotation class.
	 * For example, if an annotation <code>@Author</code> is associated with
	 * this class, an <code>Author</code> object is returned. The member values
	 * can be obtained by calling methods on the <code>Author</code> object.
	 * 
	 * @param clz
	 *            the annotation class.
	 * @return the annotation if found, otherwise <code>null</code>.
	 * @since 3.11
	 */
	public Object getAnnotation(final Class clz) throws ClassNotFoundException {
		return null;
	}

	/**
	 * Returns the annotations associated with this class. For example, if an
	 * annotation <code>@Author</code> is associated with this class, the
	 * returned array contains an <code>Author</code> object. The member values
	 * can be obtained by calling methods on the <code>Author</code> object.
	 * 
	 * @return an array of annotation-type objects.
	 * @see CtMember#getAnnotations()
	 * @since 3.1
	 */
	public Object[] getAnnotations() throws ClassNotFoundException {
		return new Object[0];
	}

	/**
	 * Returns the annotations associated with this class. This method is
	 * equivalent to <code>getAnnotations()</code> except that, if any
	 * annotations are not on the classpath, they are not included in the
	 * returned array.
	 * 
	 * @return an array of annotation-type objects.
	 * @see #getAnnotations()
	 * @see CtMember#getAvailableAnnotations()
	 * @since 3.3
	 */
	public Object[] getAvailableAnnotations() {
		return new Object[0];
	}

	/**
	 * Returns an array of nested classes declared in the class. Nested classes
	 * are inner classes, anonymous classes, local classes, and static nested
	 * classes. This simply calls <code>getNestedClasses()</code>.
	 * 
	 * @see #getNestedClasses()
	 * @since 3.15
	 */
	public JClass[] getDeclaredClasses() throws NotFoundException {
		return this.getNestedClasses();
	}

	/**
	 * Returns an array of nested classes declared in the class. Nested classes
	 * are inner classes, anonymous classes, local classes, and static nested
	 * classes.
	 * 
	 * @since 3.2
	 */
	public JClass[] getNestedClasses() throws NotFoundException {
		return new JClass[0];
	}

	/**
	 * Sets the modifiers.
	 * 
	 * <p>
	 * If the class is a nested class, this method also modifies the class
	 * declaring that nested class (i.e. the enclosing class is modified).
	 * 
	 * @param mod
	 *            modifiers encoded by <code>javassist.Modifier</code>
	 * @see Modifier
	 */
	public void setModifiers(final int mod) {
		this.checkModify();
	}

	/**
	 * Determines whether the class directly or indirectly extends the given
	 * class. If this class extends a class A and the class A extends a class B,
	 * then subclassof(B) returns true.
	 * 
	 * <p>
	 * This method returns true if the given class is identical to the class
	 * represented by this object.
	 */
	public boolean subclassOf(final JClass superclass) {
		return false;
	}

	/**
	 * Obtains the class object representing the superclass of the class. It
	 * returns null if this object represents the <code>java.lang.Object</code>
	 * class and thus it does not have the super class.
	 * 
	 * <p>
	 * If this object represents an interface, this method always returns the
	 * <code>java.lang.Object</code> class. To obtain the super interfaces
	 * extended by that interface, call <code>getInterfaces()</code>.
	 */
	public JClass getSuperclass() throws NotFoundException {
		return null;
	}

	/**
	 * Changes a super class unless this object represents an interface. The new
	 * super class must be compatible with the old one; for example, it should
	 * inherit from the old super class.
	 * 
	 * <p>
	 * If this object represents an interface, this method is equivalent to
	 * <code>addInterface()</code>; it appends <code>clazz</code> to the list of
	 * the super interfaces extended by that interface. Note that an interface
	 * can extend multiple super interfaces.
	 * 
	 * @see #replaceClassName(String, String)
	 * @see #replaceClassName(ClassMap)
	 */
	public void setSuperclass(final JClass clazz) throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Obtains the class objects representing the interfaces implemented by the
	 * class or, if this object represents an interface, the interfaces extended
	 * by that interface.
	 */
	public JClass[] getInterfaces() throws NotFoundException {
		return new JClass[0];
	}

	/**
	 * Sets implemented interfaces. If this object represents an interface, this
	 * method sets the interfaces extended by that interface.
	 * 
	 * @param list
	 *            a list of the <code>MClass</code> objects representing
	 *            interfaces, or <code>null</code> if the class implements no
	 *            interfaces.
	 */
	public void setInterfaces(final JClass[] list) {
		this.checkModify();
	}

	/**
	 * Adds an interface.
	 * 
	 * @param anInterface
	 *            the added interface.
	 */
	public void addInterface(final JClass anInterface) {
		this.checkModify();
	}

	/**
	 * If this class is a member class or interface of another class, then the
	 * class enclosing this class is returned.
	 * 
	 * @return null if this class is a top-level class or an anonymous class.
	 */
	public JClass getDeclaringClass() throws NotFoundException {
		return null;
	}

	/**
	 * Returns the immediately enclosing method of this class. This method works
	 * only with JDK 1.5 or later.
	 * 
	 * @return null if this class is not a local class or an anonymous class.
	 */
	public JMethod getEnclosingMethod() throws NotFoundException {
		return null;
	}

	/**
	 * Makes a new public nested class. If this method is called, the
	 * <code>MClass</code>, which encloses the nested class, is modified since a
	 * class file includes a list of nested classes.
	 * 
	 * <p>
	 * The current implementation only supports a static nested class.
	 * <code>isStatic</code> must be true.
	 * 
	 * @param name
	 *            the simple name of the nested class.
	 * @param isStatic
	 *            true if the nested class is static.
	 */
	public JClass makeNestedClass(final String name, final boolean isStatic) {
		throw new RuntimeException(this.getName() + " is not a class");
	}

	/**
	 * Returns an array containing <code>JField</code> objects representing all
	 * the non-private fields of the class. That array includes non-private
	 * fields inherited from the superclasses.
	 */
	public JField[] getFields() {
		return new JField[0];
	}

	/**
	 * Returns the field with the specified name. The returned field may be a
	 * private field declared in a super class or interface.
	 */
	public JField getField(final String name) throws NotFoundException {
		return this.getField(name, null);
	}

	/**
	 * Returns the field with the specified name and type. The returned field
	 * may be a private field declared in a super class or interface. Unlike
	 * Java, the JVM allows a class to have multiple fields with the same name
	 * but different types.
	 * 
	 * @param name
	 *            the field name.
	 * @param desc
	 *            the type descriptor of the field. It is available by
	 *            {@link JField#getSignature()}.
	 * @see JField#getSignature()
	 */
	public JField getField(final String name, final String desc)
			throws NotFoundException {
		throw new NotFoundException(name);
	}

	/**
	 * @return null if the specified field is not found.
	 */
	JField getField2(final String name, final String desc) {
		return null;
	}

	/**
	 * Gets all the fields declared in the class. The inherited fields are not
	 * included.
	 * 
	 * <p>
	 * Note: the result does not include inherited fields.
	 */
	public JField[] getDeclaredFields() {
		return new JField[0];
	}

	/**
	 * Retrieves the field with the specified name among the fields declared in
	 * the class.
	 * 
	 * <p>
	 * Note: this method does not search the super classes.
	 */
	public JField getDeclaredField(final String name) throws NotFoundException {
		throw new NotFoundException(name);
	}

	/**
	 * Retrieves the field with the specified name and type among the fields
	 * declared in the class. Unlike Java, the JVM allows a class to have
	 * multiple fields with the same name but different types.
	 * 
	 * <p>
	 * Note: this method does not search the super classes.
	 * 
	 * @param name
	 *            the field name.
	 * @param desc
	 *            the type descriptor of the field. It is available by
	 *            {@link JField#getSignature()}.
	 * @see JField#getSignature()
	 */
	public JField getDeclaredField(final String name, final String desc)
			throws NotFoundException {
		throw new NotFoundException(name);
	}

	/**
	 * Gets all the constructors and methods declared in the class.
	 */
	public JBehavior[] getDeclaredBehaviors() {
		return new JBehavior[0];
	}

	/**
	 * Returns an array containing <code>JConstructor</code> objects
	 * representing all the non-private constructors of the class.
	 */
	public JConstructor[] getConstructors() {
		return new JConstructor[0];
	}

	/**
	 * Returns the constructor with the given signature, which is represented by
	 * a character string called method descriptor. For details of the method
	 * descriptor, see the JVM specification or
	 * <code>javassist.bytecode.Descriptor</code>.
	 * 
	 * @param desc
	 *            method descriptor
	 * @see javassist.bytecode.Descriptor
	 */
	public JConstructor getConstructor(final String desc)
			throws NotFoundException {
		throw new NotFoundException("no such constructor");
	}

	/**
	 * Gets all the constructors declared in the class.
	 * 
	 * @see javassist.JConstructor
	 */
	public JConstructor[] getDeclaredConstructors() {
		return new JConstructor[0];
	}

	/**
	 * Returns a constructor receiving the specified parameters.
	 * 
	 * @param params
	 *            parameter types.
	 */
	public JConstructor getDeclaredConstructor(final JClass[] params)
			throws NotFoundException {
		final String desc = Descriptor.ofConstructor(params);
		return this.getConstructor(desc);
	}

	/**
	 * Gets the class initializer (static constructor) declared in the class.
	 * This method returns <code>null</code> if no class initializer is not
	 * declared.
	 * 
	 * @see #makeClassInitializer()
	 * @see javassist.JConstructor
	 */
	public JConstructor getClassInitializer() {
		return null;
	}

	/**
	 * Returns an array containing <code>JMethod</code> objects representing all
	 * the non-private methods of the class. That array includes non-private
	 * methods inherited from the superclasses.
	 */
	public JMethod[] getMethods() {
		return new JMethod[0];
	}

	/**
	 * Returns the method with the given name and signature. The returned method
	 * may be declared in a super class. The method signature is represented by
	 * a character string called method descriptor, which is defined in the JVM
	 * specification.
	 * 
	 * @param name
	 *            method name
	 * @param desc
	 *            method descriptor
	 * @see CtBehavior#getSignature()
	 * @see javassist.bytecode.Descriptor
	 */
	public JMethod getMethod(final String name, final String desc)
			throws NotFoundException {
		throw new NotFoundException(name);
	}

	/**
	 * Gets all methods declared in the class. The inherited methods are not
	 * included.
	 * 
	 * @see javassist.JMethod
	 */
	public JMethod[] getDeclaredMethods() {
		return new JMethod[0];
	}

	/**
	 * Retrieves the method with the specified name and parameter types among
	 * the methods declared in the class.
	 * 
	 * <p>
	 * Note: this method does not search the superclasses.
	 * 
	 * @param name
	 *            method name
	 * @param params
	 *            parameter types
	 * @see javassist.JMethod
	 */
	public JMethod getDeclaredMethod(final String name, final JClass[] params)
			throws NotFoundException {
		throw new NotFoundException(name);
	}

	/**
	 * Retrieves the method with the specified name among the methods declared
	 * in the class. If there are multiple methods with the specified name, then
	 * this method returns one of them.
	 * 
	 * <p>
	 * Note: this method does not search the superclasses.
	 * 
	 * @see javassist.JMethod
	 */
	public JMethod getDeclaredMethod(final String name)
			throws NotFoundException {
		throw new NotFoundException(name);
	}

	/**
	 * Makes an empty class initializer (static constructor). If the class
	 * already includes a class initializer, this method returns it.
	 * 
	 * @see #getClassInitializer()
	 */
	public JConstructor makeClassInitializer() throws CannotCompileException {
		throw new CannotCompileException("not a class");
	}

	/**
	 * Adds a constructor. To add a class initializer (static constructor), call
	 * <code>makeClassInitializer()</code>.
	 * 
	 * @see #makeClassInitializer()
	 */
	public void addConstructor(final JConstructor c)
			throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Removes a constructor declared in this class.
	 * 
	 * @param c
	 *            removed constructor.
	 * @throws NotFoundException
	 *             if the constructor is not found.
	 */
	public void removeConstructor(final JConstructor c)
			throws NotFoundException {
		this.checkModify();
	}

	/**
	 * Adds a method.
	 */
	public void addMethod(final JMethod m) throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Removes a method declared in this class.
	 * 
	 * @param m
	 *            removed method.
	 * @throws NotFoundException
	 *             if the method is not found.
	 */
	public void removeMethod(final JMethod m) throws NotFoundException {
		this.checkModify();
	}

	/**
	 * Adds a field.
	 * 
	 * <p>
	 * The <code>JField</code> belonging to another <code>MClass</code> cannot
	 * be directly added to this class. Only a field created for this class can
	 * be added.
	 * 
	 * @see javassist.JField#JField(JField,JClass)
	 */
	public void addField(final JField f) throws CannotCompileException {
		this.addField(f, (JField.Initializer) null);
	}

	/**
	 * Adds a field with an initial value.
	 * 
	 * <p>
	 * The <code>JField</code> belonging to another <code>MClass</code> cannot
	 * be directly added to this class. Only a field created for this class can
	 * be added.
	 * 
	 * <p>
	 * The initial value is given as an expression written in Java. Any regular
	 * Java expression can be used for specifying the initial value. The
	 * followings are examples.
	 * 
	 * <ul>
	 * 
	 * <pre>
	 * cc.addField(f, "0")               // the initial value is 0.
	 * cc.addField(f, "i + 1")           // i + 1.
	 * cc.addField(f, "new Point()");    // a Point object.
	 * </pre>
	 * 
	 * </ul>
	 * 
	 * <p>
	 * Here, the type of variable <code>cc</code> is <code>MClass</code>. The
	 * type of <code>f</code> is <code>JField</code>.
	 * 
	 * <p>
	 * Note: do not change the modifier of the field (in particular, do not add
	 * or remove <code>static</code> to/from the modifier) after it is added to
	 * the class by <code>addField()</code>.
	 * 
	 * @param init
	 *            an expression for the initial value.
	 * 
	 * @see javassist.JField.Initializer#byExpr(String)
	 * @see javassist.JField#JField(JField,JClass)
	 */
	public void addField(final JField f, final String init)
			throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Adds a field with an initial value.
	 * 
	 * <p>
	 * The <code>JField</code> belonging to another <code>MClass</code> cannot
	 * be directly added to this class. Only a field created for this class can
	 * be added.
	 * 
	 * <p>
	 * For example,
	 * 
	 * <ul>
	 * 
	 * <pre>
	 * MClass cc = ...;
	 * addField(new JField(MClass.intType, "i", cc),
	 *          JField.Initializer.constant(1));
	 * </pre>
	 * 
	 * </ul>
	 * 
	 * <p>
	 * This code adds an <code>int</code> field named "i". The initial value of
	 * this field is 1.
	 * 
	 * @param init
	 *            specifies the initial value of the field.
	 * 
	 * @see javassist.JField#JField(JField,JClass)
	 */
	public void addField(final JField f, final JField.Initializer init)
			throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Removes a field declared in this class.
	 * 
	 * @param f
	 *            removed field.
	 * @throws NotFoundException
	 *             if the field is not found.
	 */
	public void removeField(final JField f) throws NotFoundException {
		this.checkModify();
	}

	/**
	 * Obtains an attribute with the given name. If that attribute is not found
	 * in the class file, this method returns null.
	 * 
	 * <p>
	 * This is a convenient method mainly for obtaining a user-defined
	 * attribute. For dealing with attributes, see the
	 * <code>javassist.bytecode</code> package. For example, the following
	 * expression returns all the attributes of a class file.
	 * 
	 * <ul>
	 * 
	 * <pre>
	 * getClassFile().getAttributes()
	 * </pre>
	 * 
	 * </ul>
	 * 
	 * @param name
	 *            attribute name
	 * @see javassist.bytecode.AttributeInfo
	 */
	public byte[] getAttribute(final String name) {
		return null;
	}

	/**
	 * Adds a named attribute. An arbitrary data (smaller than 64Kb) can be
	 * saved in the class file. Some attribute name are reserved by the JVM. The
	 * attributes with the non-reserved names are ignored when a class file is
	 * loaded into the JVM. If there is already an attribute with the same name,
	 * this method substitutes the new one for it.
	 * 
	 * <p>
	 * This is a convenient method mainly for adding a user-defined attribute.
	 * For dealing with attributes, see the <code>javassist.bytecode</code>
	 * package. For example, the following expression adds an attribute
	 * <code>info</code> to a class file.
	 * 
	 * <ul>
	 * 
	 * <pre>
	 * getClassFile().addAttribute(info)
	 * </pre>
	 * 
	 * </ul>
	 * 
	 * @param name
	 *            attribute name
	 * @param data
	 *            attribute value
	 * @see javassist.bytecode.AttributeInfo
	 */
	public void setAttribute(final String name, final byte[] data) {
		this.checkModify();
	}

	/**
	 * Applies the given converter to all methods and constructors declared in
	 * the class. This method calls <code>instrument()</code> on every
	 * <code>JMethod</code> and <code>JConstructor</code> object in the class.
	 * 
	 * @param converter
	 *            specifies how to modify.
	 */
	public void instrument(final CodeConverter converter)
			throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Modifies the bodies of all methods and constructors declared in the
	 * class. This method calls <code>instrument()</code> on every
	 * <code>JMethod</code> and <code>JConstructor</code> object in the class.
	 * 
	 * @param editor
	 *            specifies how to modify.
	 */
	public void instrument(final ExprEditor editor)
			throws CannotCompileException {
		this.checkModify();
	}

	/**
	 * Converts this class to a <code>java.lang.Class</code> object. Once this
	 * method is called, further modifications are not allowed any more. To load
	 * the class, this method uses the context class loader of the current
	 * thread. If the program is running on some application server, the context
	 * class loader might be inappropriate to load the class.
	 * 
	 * <p>
	 * This method is provided for convenience. If you need more complex
	 * functionality, you should write your own class loader.
	 * 
	 * <p>
	 * Note: this method calls <code>toClass()</code> in <code>ClassPool</code>.
	 * 
	 * <p>
	 * <b>Warining:</b> A Class object returned by this method may not work with
	 * a security manager or a signed jar file because a protection domain is
	 * not specified.
	 * 
	 * @see #toClass(java.lang.ClassLoader,ProtectionDomain)
	 * @see ClassPool#toClass(JClass)
	 */
	public Class toClass() throws CannotCompileException {
		return this.getClassPool().toClass(this);
	}

	/**
	 * Converts this class to a <code>java.lang.Class</code> object. Once this
	 * method is called, further modifications are not allowed any more.
	 * 
	 * <p>
	 * The class file represented by this <code>MClass</code> is loaded by the
	 * given class loader to construct a <code>java.lang.Class</code> object.
	 * Since a private method on the class loader is invoked through the
	 * reflection API, the caller must have permissions to do that.
	 * 
	 * <p>
	 * An easy way to obtain <code>ProtectionDomain</code> object is to call
	 * <code>getProtectionDomain()</code> in <code>java.lang.Class</code>. It
	 * returns the domain that the class belongs to.
	 * 
	 * <p>
	 * This method is provided for convenience. If you need more complex
	 * functionality, you should write your own class loader.
	 * 
	 * <p>
	 * Note: this method calls <code>toClass()</code> in <code>ClassPool</code>.
	 * 
	 * @param loader
	 *            the class loader used to load this class. If it is null, the
	 *            class loader returned by {@link ClassPool#getClassLoader()} is
	 *            used.
	 * @param domain
	 *            the protection domain that the class belongs to. If it is
	 *            null, the default domain created by
	 *            <code>java.lang.ClassLoader</code> is used.
	 * @see ClassPool#toClass(JClass,java.lang.ClassLoader)
	 * @since 3.3
	 */
	public Class toClass(ClassLoader loader, final ProtectionDomain domain)
			throws CannotCompileException {
		final ClassPool cp = this.getClassPool();
		if (loader == null) {
			loader = cp.getClassLoader();
		}

		return cp.toClass(this, loader, domain);
	}

	/**
	 * Converts this class to a <code>java.lang.Class</code> object.
	 * 
	 * <p>
	 * <b>Warining:</b> A Class object returned by this method may not work with
	 * a security manager or a signed jar file because a protection domain is
	 * not specified.
	 * 
	 * @deprecated Replaced by {@link #toClass(ClassLoader,ProtectionDomain)}
	 */
	@Deprecated
	public final Class toClass(final ClassLoader loader)
			throws CannotCompileException {
		return this.getClassPool().toClass(this, loader);
	}

	/**
	 * Removes this <code>MClass</code> object from the <code>ClassPool</code>.
	 * After this method is called, any method cannot be called on the removed
	 * <code>MClass</code> object.
	 * 
	 * <p>
	 * If <code>get()</code> in <code>ClassPool</code> is called with the name
	 * of the removed method, the <code>ClassPool</code> will read the class
	 * file again and constructs another <code>MClass</code> object representing
	 * the same class.
	 */
	public void detach() {
		final ClassPool cp = this.getClassPool();
		final JClass obj = cp.removeCached(this.getName());
		if (obj != this) {
			cp.cacheMClass(this.getName(), obj, false);
		}
	}

	/**
	 * Disallows (or allows) automatically pruning this <code>MClass</code>
	 * object.
	 * 
	 * <p>
	 * Javassist can automatically prune a <code>MClass</code> object when
	 * <code>toBytecode()</code> (or <code>toClass()</code>,
	 * <code>writeFile()</code>) is called. Since a <code>ClassPool</code> holds
	 * all instances of <code>MClass</code> even after <code>toBytecode()</code>
	 * (or <code>toClass()</code>, <code>writeFile()</code>) is called, pruning
	 * may significantly save memory consumption.
	 * 
	 * <p>
	 * If <code>ClassPool.doPruning</code> is true, the automatic pruning is on
	 * by default. Otherwise, it is off. The default value of
	 * <code>ClassPool.doPruning</code> is false.
	 * 
	 * @param stop
	 *            disallow pruning if true. Otherwise, allow.
	 * @return the previous status of pruning. true if pruning is already
	 *         stopped.
	 * 
	 * @see #detach()
	 * @see #prune()
	 * @see ClassPool#doPruning
	 */
	public boolean stopPruning(final boolean stop) {
		return true;
	}

	/**
	 * Discards unnecessary attributes, in particular,
	 * <code>CodeAttribute</code>s (method bodies) of the class, to minimize the
	 * memory footprint. After calling this method, the class is read only. It
	 * cannot be modified any more. Furthermore, <code>toBytecode()</code>,
	 * <code>writeFile()</code>, <code>toClass()</code>, or
	 * <code>instrument()</code> cannot be called. However, the method names and
	 * signatures in the class etc. are still accessible.
	 * 
	 * <p>
	 * <code>toBytecode()</code>, <code>writeFile()</code>, and
	 * <code>toClass()</code> internally call this method if automatic pruning
	 * is on.
	 * 
	 * <p>
	 * According to some experiments, pruning does not really reduce memory
	 * consumption. Only about 20%. Since pruning takes time, it might not pay
	 * off. So the automatic pruning is off by default.
	 * 
	 * @see #stopPruning(boolean)
	 * @see #detach()
	 * @see ClassPool#doPruning
	 * 
	 * @see #toBytecode()
	 * @see #toClass()
	 * @see #writeFile()
	 * @see #instrument(CodeConverter)
	 * @see #instrument(ExprEditor)
	 */
	public void prune() {
	}

	/*
	 * Called by get() in ClassPool. MClassType overrides this method.
	 */
	void incGetCounter() {
	}

	/**
	 * If this method is called, the class file will be rebuilt when it is
	 * finally generated by <code>toBytecode()</code> and
	 * <code>writeFile()</code>. For a performance reason, the symbol table of
	 * the class file may contain unused entries, for example, after a method or
	 * a filed is deleted. This method removes those unused entries. This
	 * removal will minimize the size of the class file.
	 * 
	 * @since 3.8.1
	 */
	public void rebuildClassFile() {
	}

	/**
	 * Converts this class to a class file. Once this method is called, further
	 * modifications are not possible any more.
	 * 
	 * @return the contents of the class file.
	 */
	public byte[] toBytecode() throws IOException, CannotCompileException {
		final ByteArrayOutputStream barray = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(barray);
		try {
			this.toBytecode(out);
		} finally {
			out.close();
		}

		return barray.toByteArray();
	}

	/**
	 * Writes a class file represented by this <code>MClass</code> object in the
	 * current directory. Once this method is called, further modifications are
	 * not possible any more.
	 * 
	 * @see #debugWriteFile()
	 */
	public void writeFile() throws NotFoundException, IOException,
			CannotCompileException {
		this.writeFile(".");
	}

	/**
	 * Writes a class file represented by this <code>MClass</code> object on a
	 * local disk. Once this method is called, further modifications are not
	 * possible any more.
	 * 
	 * @param directoryName
	 *            it must end without a directory separator.
	 * @see #debugWriteFile(String)
	 */
	public void writeFile(final String directoryName)
			throws CannotCompileException, IOException {
		final String classname = this.getName();
		final String filename = directoryName + File.separatorChar
				+ classname.replace('.', File.separatorChar) + ".class";
		final int pos = filename.lastIndexOf(File.separatorChar);
		if (pos > 0) {
			final String dir = filename.substring(0, pos);
			if (!dir.equals(".")) {
				new File(dir).mkdirs();
			}
		}

		final DataOutputStream out = new DataOutputStream(
				new BufferedOutputStream(new DelayedFileOutputStream(filename)));
		try {
			this.toBytecode(out);
		} finally {
			out.close();
		}
	}

	/**
	 * Writes a class file as <code>writeFile()</code> does although this method
	 * does not prune or freeze the class after writing the class file. Note
	 * that, once <code>writeFile()</code> or <code>toBytecode()</code> is
	 * called, it cannot be called again since the class is pruned and frozen.
	 * This method would be useful for debugging.
	 */
	public void debugWriteFile() {
		this.debugWriteFile(".");
	}

	/**
	 * Writes a class file as <code>writeFile()</code> does although this method
	 * does not prune or freeze the class after writing the class file. Note
	 * that, once <code>writeFile()</code> or <code>toBytecode()</code> is
	 * called, it cannot be called again since the class is pruned and frozen.
	 * This method would be useful for debugging.
	 * 
	 * @param directoryName
	 *            it must end without a directory separator.
	 */
	public void debugWriteFile(final String directoryName) {
		try {
			final boolean p = this.stopPruning(true);
			this.writeFile(directoryName);
			this.defrost();
			this.stopPruning(p);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	static class DelayedFileOutputStream extends OutputStream {
		private FileOutputStream file;
		private String filename;

		DelayedFileOutputStream(final String name) {
			this.file = null;
			this.filename = name;
		}

		private void init() throws IOException {
			if (this.file == null) {
				this.file = new FileOutputStream(this.filename);
			}
		}

		@Override
		public void write(final int b) throws IOException {
			this.init();
			this.file.write(b);
		}

		@Override
		public void write(final byte[] b) throws IOException {
			this.init();
			this.file.write(b);
		}

		@Override
		public void write(final byte[] b, final int off, final int len)
				throws IOException {
			this.init();
			this.file.write(b, off, len);

		}

		@Override
		public void flush() throws IOException {
			this.init();
			this.file.flush();
		}

		@Override
		public void close() throws IOException {
			this.init();
			this.file.close();
		}
	}

	/**
	 * Converts this class to a class file. Once this method is called, further
	 * modifications are not possible any more.
	 * 
	 * <p>
	 * This method dose not close the output stream in the end.
	 * 
	 * @param out
	 *            the output stream that a class file is written to.
	 */
	public void toBytecode(final DataOutputStream out)
			throws CannotCompileException, IOException {
		throw new CannotCompileException("not a class");
	}

	/**
	 * Makes a unique member name. This method guarantees that the returned name
	 * is not used as a prefix of any methods or fields visible in this class.
	 * If the returned name is XYZ, then any method or field names in this class
	 * do not start with XYZ.
	 * 
	 * @param prefix
	 *            the prefix of the member name.
	 */
	public String makeUniqueName(final String prefix) {
		throw new RuntimeException("not available in " + this.getName());
	}

	/*
	 * Invoked from ClassPool#compress(). This method is overridden by
	 * MClassType.
	 */
	void compress() {
	}
}
