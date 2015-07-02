package com.wmenjoy.utils.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wmenjoy.utils.lang.FileParser;

public class ConstantPool {
	LongVector items;
	int numOfItems;
	HashMap classes;
	HashMap strings;
	ConstantInfo[] ConstantInfoCache;
	int[] ConstantInfoIndexCache;
	int thisClassInfo;

	private static final int CACHE_SIZE = 32;

	/**
	 * A hash function for CACHE_SIZE
	 */
	private static int hashFunc(final int a, final int b) {
		int h = -2128831035;
		final int prime = 16777619;
		h = (h ^ (a & 0xff)) * prime;
		h = (h ^ (b & 0xff)) * prime;

		// changing the hash key size from 32bit to 5bit
		h = (h >> 5) ^ (h & 0x1f);
		return h & 0x1f; // 0..31
	}

	/**
	 * <code>CONSTANT_Class</code>
	 */
	public static final int CONST_Class = ClassInfo.tag;

	/**
	 * <code>CONSTANT_Fieldref</code>
	 */
	public static final int CONST_Fieldref = FieldrefInfo.tag;

	/**
	 * <code>CONSTANT_Methodref</code>
	 */
	public static final int CONST_Methodref = MethodRefInfo.tag;

	/**
	 * <code>CONSTANT_InterfaceMethodref</code>
	 */
	public static final int CONST_InterfaceMethodref = InterfaceMethodRefInfo.tag;

	/**
	 * <code>CONSTANT_String</code>
	 */
	public static final int CONST_String = StringInfo.tag;

	/**
	 * <code>CONSTANT_Integer</code>
	 */
	public static final int CONST_Integer = IntegerInfo.tag;

	/**
	 * <code>CONSTANT_Float</code>
	 */
	public static final int CONST_Float = FloatInfo.tag;

	/**
	 * <code>CONSTANT_Long</code>
	 */
	public static final int CONST_Long = LongInfo.tag;

	/**
	 * <code>CONSTANT_Double</code>
	 */
	public static final int CONST_Double = DoubleInfo.tag;

	/**
	 * <code>CONSTANT_NameAndType</code>
	 */
	public static final int CONST_NameAndType = NameAndTypeInfo.tag;

	/**
	 * <code>CONSTANT_Utf8</code>
	 */
	public static final int CONST_Utf8 = Utf8Info.tag;

	public static final int CONST_MethodHandler = 15;

	public static final int CONST_MethodType = 16;

	public static final int CONST_InvokeDynamic = 18;

	/**
	 * Represents the class using this constant pool table.
	 */
	public static final JClass THIS = null;

	/**
	 * Constructs a constant pool table.
	 * 
	 * @param thisclass
	 *            the name of the class using this constant pool table
	 */
	public ConstantPool(final String thisclass) {
		this.items = new LongVector();
		this.numOfItems = 0;
		this.addItem(null); // index 0 is reserved by the JVM.
		this.classes = new HashMap();
		this.strings = new HashMap();
		this.ConstantInfoCache = new ConstantInfo[CACHE_SIZE];
		this.ConstantInfoIndexCache = new int[CACHE_SIZE];
		this.thisClassInfo = this.addClassInfo(thisclass);
	}

	/**
	 * Constructs a constant pool table from the given byte stream.
	 * 
	 * @param in
	 *            byte stream.
	 */
	public ConstantPool(final DataInputStream in) throws IOException {
		this.classes = new HashMap();
		this.strings = new HashMap();
		this.ConstantInfoCache = new ConstantInfo[CACHE_SIZE];
		this.ConstantInfoIndexCache = new int[CACHE_SIZE];
		this.thisClassInfo = 0;
		/*
		 * read() initializes items and numOfItems, and do addItem(null).
		 */
		this.read(in);
	}

	public ConstantPool(final FileParser parser) {

	}

	void prune() {
		this.classes = new HashMap();
		this.strings = new HashMap();
		this.ConstantInfoCache = new ConstantInfo[CACHE_SIZE];
		this.ConstantInfoIndexCache = new int[CACHE_SIZE];
	}

	/**
	 * Returns the number of entries in this table.
	 */
	public int getSize() {
		return this.numOfItems;
	}

	/**
	 * Returns the name of the class using this constant pool table.
	 */
	public String getClassName() {
		return this.getClassInfo(this.thisClassInfo);
	}

	/**
	 * Returns the index of <code>CONSTANT_Class_info</code> structure
	 * specifying the class using this constant pool table.
	 */
	public int getThisClassInfo() {
		return this.thisClassInfo;
	}

	void setThisClassInfo(final int i) {
		this.thisClassInfo = i;
	}

	ConstantInfo getItem(final int n) {
		return this.items.elementAt(n);
	}

	/**
	 * Returns the <code>tag</code> field of the constant pool table entry at
	 * the given index.
	 */
	public int getTag(final int index) {
		return this.getItem(index).getTag();
	}

	/**
	 * Reads <code>CONSTANT_Class_info</code> structure at the given index.
	 * 
	 * @return a fully-qualified class or interface name specified by
	 *         <code>name_index</code>. If the type is an array type, this
	 *         method returns an encoded name like
	 *         <code>[java.lang.Object;</code> (note that the separators are not
	 *         slashes but dots).
	 * @see javassist.ClassPool#getCtClass(String)
	 */
	public String getClassInfo(final int index) {
		final ClassInfo c = (ClassInfo) this.getItem(index);
		if (c == null) {
			return null;
		} else {
			return Descriptor.toJavaName(this.getUtf8Info(c.name));
		}
	}

	/**
	 * Reads <code>CONSTANT_Class_info</code> structure at the given index.
	 * 
	 * @return the descriptor of the type specified by <code>name_index</code>.
	 * @see javassist.ClassPool#getCtClass(String)
	 * @since 3.15
	 */
	public String getClassInfoByDescriptor(final int index) {
		final ClassInfo c = (ClassInfo) this.getItem(index);
		if (c == null) {
			return null;
		} else {
			final String className = this.getUtf8Info(c.name);
			if (className.charAt(0) == '[') {
				return className;
			} else {
				return Descriptor.of(className);
			}
		}
	}

	/**
	 * Reads the <code>name_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure at the given index.
	 */
	public int getNameAndTypeName(final int index) {
		final NameAndTypeInfo ntinfo = (NameAndTypeInfo) this.getItem(index);
		return ntinfo.memberName;
	}

	/**
	 * Reads the <code>descriptor_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure at the given index.
	 */
	public int getNameAndTypeDescriptor(final int index) {
		final NameAndTypeInfo ntinfo = (NameAndTypeInfo) this.getItem(index);
		return ntinfo.typeDescriptor;
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_Fieldref_info</code>, <code>CONSTANT_Methodref_info</code>
	 * , or <code>CONSTANT_Interfaceref_info</code>, structure at the given
	 * index.
	 * 
	 * @since 3.6
	 */
	public int getMemberClass(final int index) {
		final MemberrefInfo minfo = (MemberrefInfo) this.getItem(index);
		return minfo.classIndex;
	}

	/**
	 * Reads the <code>name_and_type_index</code> field of the
	 * <code>CONSTANT_Fieldref_info</code>, <code>CONSTANT_Methodref_info</code>
	 * , or <code>CONSTANT_Interfaceref_info</code>, structure at the given
	 * index.
	 * 
	 * @since 3.6
	 */
	public int getMemberNameAndType(final int index) {
		final MemberrefInfo minfo = (MemberrefInfo) this.getItem(index);
		return minfo.nameAndTypeIndex;
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_Fieldref_info</code> structure at the given index.
	 */
	public int getFieldrefClass(final int index) {
		final FieldrefInfo finfo = (FieldrefInfo) this.getItem(index);
		return finfo.classIndex;
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_Fieldref_info</code> structure at the given index.
	 * 
	 * @return the name of the class at that <code>class_index</code>.
	 */
	public String getFieldrefClassName(final int index) {
		final FieldrefInfo f = (FieldrefInfo) this.getItem(index);
		if (f == null) {
			return null;
		} else {
			return this.getClassInfo(f.classIndex);
		}
	}

	/**
	 * Reads the <code>name_and_type_index</code> field of the
	 * <code>CONSTANT_Fieldref_info</code> structure at the given index.
	 */
	public int getFieldrefNameAndType(final int index) {
		final FieldrefInfo finfo = (FieldrefInfo) this.getItem(index);
		return finfo.nameAndTypeIndex;
	}

	/**
	 * Reads the <code>name_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure indirectly specified by
	 * the given index.
	 * 
	 * @param index
	 *            an index to a <code>CONSTANT_Fieldref_info</code>.
	 * @return the name of the field.
	 */
	public String getFieldrefName(final int index) {
		final FieldrefInfo f = (FieldrefInfo) this.getItem(index);
		if (f == null) {
			return null;
		} else {
			final NameAndTypeInfo n = (NameAndTypeInfo) this
					.getItem(f.nameAndTypeIndex);
			if (n == null) {
				return null;
			} else {
				return this.getUtf8Info(n.memberName);
			}
		}
	}

	/**
	 * Reads the <code>descriptor_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure indirectly specified by
	 * the given index.
	 * 
	 * @param index
	 *            an index to a <code>CONSTANT_Fieldref_info</code>.
	 * @return the type descriptor of the field.
	 */
	public String getFieldrefType(final int index) {
		final FieldrefInfo f = (FieldrefInfo) this.getItem(index);
		if (f == null) {
			return null;
		} else {
			final NameAndTypeInfo n = (NameAndTypeInfo) this
					.getItem(f.nameAndTypeIndex);
			if (n == null) {
				return null;
			} else {
				return this.getUtf8Info(n.typeDescriptor);
			}
		}
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_Methodref_info</code> structure at the given index.
	 */
	public int getMethodrefClass(final int index) {
		final MethodRefInfo minfo = (MethodRefInfo) this.getItem(index);
		return minfo.classIndex;
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_Methodref_info</code> structure at the given index.
	 * 
	 * @return the name of the class at that <code>class_index</code>.
	 */
	public String getMethodrefClassName(final int index) {
		final MethodRefInfo minfo = (MethodRefInfo) this.getItem(index);
		if (minfo == null) {
			return null;
		} else {
			return this.getClassInfo(minfo.classIndex);
		}
	}

	/**
	 * Reads the <code>name_and_type_index</code> field of the
	 * <code>CONSTANT_Methodref_info</code> structure at the given index.
	 */
	public int getMethodrefNameAndType(final int index) {
		final MethodRefInfo minfo = (MethodRefInfo) this.getItem(index);
		return minfo.nameAndTypeIndex;
	}

	/**
	 * Reads the <code>name_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure indirectly specified by
	 * the given index.
	 * 
	 * @param index
	 *            an index to a <code>CONSTANT_Methodref_info</code>.
	 * @return the name of the method.
	 */
	public String getMethodrefName(final int index) {
		final MethodRefInfo minfo = (MethodRefInfo) this.getItem(index);
		if (minfo == null) {
			return null;
		} else {
			final NameAndTypeInfo n = (NameAndTypeInfo) this
					.getItem(minfo.nameAndTypeIndex);
			if (n == null) {
				return null;
			} else {
				return this.getUtf8Info(n.memberName);
			}
		}
	}

	/**
	 * Reads the <code>descriptor_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure indirectly specified by
	 * the given index.
	 * 
	 * @param index
	 *            an index to a <code>CONSTANT_Methodref_info</code>.
	 * @return the descriptor of the method.
	 */
	public String getMethodrefType(final int index) {
		final MethodRefInfo minfo = (MethodRefInfo) this.getItem(index);
		if (minfo == null) {
			return null;
		} else {
			final NameAndTypeInfo n = (NameAndTypeInfo) this
					.getItem(minfo.nameAndTypeIndex);
			if (n == null) {
				return null;
			} else {
				return this.getUtf8Info(n.typeDescriptor);
			}
		}
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_InterfaceMethodref_info</code> structure at the given
	 * index.
	 */
	public int getInterfaceMethodrefClass(final int index) {
		final InterfaceMethodRefInfo minfo = (InterfaceMethodRefInfo) this
				.getItem(index);
		return minfo.classIndex;
	}

	/**
	 * Reads the <code>class_index</code> field of the
	 * <code>CONSTANT_InterfaceMethodref_info</code> structure at the given
	 * index.
	 * 
	 * @return the name of the class at that <code>class_index</code>.
	 */
	public String getInterfaceMethodrefClassName(final int index) {
		final InterfaceMethodRefInfo minfo = (InterfaceMethodRefInfo) this
				.getItem(index);
		return this.getClassInfo(minfo.classIndex);
	}

	/**
	 * Reads the <code>name_and_type_index</code> field of the
	 * <code>CONSTANT_InterfaceMethodref_info</code> structure at the given
	 * index.
	 */
	public int getInterfaceMethodrefNameAndType(final int index) {
		final InterfaceMethodRefInfo minfo = (InterfaceMethodRefInfo) this
				.getItem(index);
		return minfo.nameAndTypeIndex;
	}

	/**
	 * Reads the <code>name_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure indirectly specified by
	 * the given index.
	 * 
	 * @param index
	 *            an index to a <code>CONSTANT_InterfaceMethodref_info</code>.
	 * @return the name of the method.
	 */
	public String getInterfaceMethodrefName(final int index) {
		final InterfaceMethodRefInfo minfo = (InterfaceMethodRefInfo) this
				.getItem(index);
		if (minfo == null) {
			return null;
		} else {
			final NameAndTypeInfo n = (NameAndTypeInfo) this
					.getItem(minfo.nameAndTypeIndex);
			if (n == null) {
				return null;
			} else {
				return this.getUtf8Info(n.memberName);
			}
		}
	}

	/**
	 * Reads the <code>descriptor_index</code> field of the
	 * <code>CONSTANT_NameAndType_info</code> structure indirectly specified by
	 * the given index.
	 * 
	 * @param index
	 *            an index to a <code>CONSTANT_InterfaceMethodref_info</code>.
	 * @return the descriptor of the method.
	 */
	public String getInterfaceMethodrefType(final int index) {
		final InterfaceMethodRefInfo minfo = (InterfaceMethodRefInfo) this
				.getItem(index);
		if (minfo == null) {
			return null;
		} else {
			final NameAndTypeInfo n = (NameAndTypeInfo) this
					.getItem(minfo.nameAndTypeIndex);
			if (n == null) {
				return null;
			} else {
				return this.getUtf8Info(n.typeDescriptor);
			}
		}
	}

	/**
	 * Reads <code>CONSTANT_Integer_info</code>, <code>_Float_info</code>,
	 * <code>_Long_info</code>, <code>_Double_info</code>, or
	 * <code>_String_info</code> structure. These are used with the LDC
	 * instruction.
	 * 
	 * @return a <code>String</code> value or a wrapped primitive-type value.
	 */
	public Object getLdcValue(final int index) {
		final ConstantInfo ConstantInfo = this.getItem(index);
		Object value = null;
		if (ConstantInfo instanceof StringInfo) {
			value = this.getStringInfo(index);
		} else if (ConstantInfo instanceof FloatInfo) {
			value = new Float(this.getFloatInfo(index));
		} else if (ConstantInfo instanceof IntegerInfo) {
			value = new Integer(this.getIntegerInfo(index));
		} else if (ConstantInfo instanceof LongInfo) {
			value = new Long(this.getLongInfo(index));
		} else if (ConstantInfo instanceof DoubleInfo) {
			value = new Double(this.getDoubleInfo(index));
		} else {
			value = null;
		}

		return value;
	}

	/**
	 * Reads <code>CONSTANT_Integer_info</code> structure at the given index.
	 * 
	 * @return the value specified by this entry.
	 */
	public int getIntegerInfo(final int index) {
		final IntegerInfo i = (IntegerInfo) this.getItem(index);
		return i.value;
	}

	/**
	 * Reads <code>CONSTANT_Float_info</code> structure at the given index.
	 * 
	 * @return the value specified by this entry.
	 */
	public float getFloatInfo(final int index) {
		final FloatInfo i = (FloatInfo) this.getItem(index);
		return i.value;
	}

	/**
	 * Reads <code>CONSTANT_Long_info</code> structure at the given index.
	 * 
	 * @return the value specified by this entry.
	 */
	public long getLongInfo(final int index) {
		final LongInfo i = (LongInfo) this.getItem(index);
		return i.value;
	}

	/**
	 * Reads <code>CONSTANT_Double_info</code> structure at the given index.
	 * 
	 * @return the value specified by this entry.
	 */
	public double getDoubleInfo(final int index) {
		final DoubleInfo i = (DoubleInfo) this.getItem(index);
		return i.value;
	}

	/**
	 * Reads <code>CONSTANT_String_info</code> structure at the given index.
	 * 
	 * @return the string specified by <code>string_index</code>.
	 */
	public String getStringInfo(final int index) {
		final StringInfo si = (StringInfo) this.getItem(index);
		return this.getUtf8Info(si.string);
	}

	/**
	 * Reads <code>CONSTANT_utf8_info</code> structure at the given index.
	 * 
	 * @return the string specified by this entry.
	 */
	public String getUtf8Info(final int index) {
		final Utf8Info utf = (Utf8Info) this.getItem(index);
		return utf.string;
	}

	/**
	 * Determines whether <code>CONSTANT_Methodref_info</code> structure at the
	 * given index represents the constructor of the given class.
	 * 
	 * @return the <code>descriptor_index</code> specifying the type descriptor
	 *         of the that constructor. If it is not that constructor,
	 *         <code>isConstructor()</code> returns 0.
	 */
	public int isConstructor(final String classname, final int index) {
		return this.isMember(classname, MethodInfo.nameInit, index);
	}

	/**
	 * Determines whether <code>CONSTANT_Methodref_info</code>,
	 * <code>CONSTANT_Fieldref_info</code>, or
	 * <code>CONSTANT_InterfaceMethodref_info</code> structure at the given
	 * index represents the member with the specified name and declaring class.
	 * 
	 * @param classname
	 *            the class declaring the member
	 * @param membername
	 *            the member name
	 * @param index
	 *            the index into the constant pool table
	 * 
	 * @return the <code>descriptor_index</code> specifying the type descriptor
	 *         of that member. If it is not that member, <code>isMember()</code>
	 *         returns 0.
	 */
	public int isMember(final String classname, final String membername,
			final int index) {
		final MemberrefInfo minfo = (MemberrefInfo) this.getItem(index);
		if (this.getClassInfo(minfo.classIndex).equals(classname)) {
			final NameAndTypeInfo ntinfo = (NameAndTypeInfo) this
					.getItem(minfo.nameAndTypeIndex);
			if (this.getUtf8Info(ntinfo.memberName).equals(membername)) {
				return ntinfo.typeDescriptor;
			}
		}

		return 0; // false
	}

	/**
	 * Determines whether <code>CONSTANT_Methodref_info</code>,
	 * <code>CONSTANT_Fieldref_info</code>, or
	 * <code>CONSTANT_InterfaceMethodref_info</code> structure at the given
	 * index has the name and the descriptor given as the arguments.
	 * 
	 * @param membername
	 *            the member name
	 * @param desc
	 *            the descriptor of the member.
	 * @param index
	 *            the index into the constant pool table
	 * 
	 * @return the name of the target class specified by the
	 *         <code>..._info</code> structure at <code>index</code>. Otherwise,
	 *         null if that structure does not match the given member name and
	 *         descriptor.
	 */
	public String eqMember(final String membername, final String desc,
			final int index) {
		final MemberrefInfo minfo = (MemberrefInfo) this.getItem(index);
		final NameAndTypeInfo ntinfo = (NameAndTypeInfo) this
				.getItem(minfo.nameAndTypeIndex);
		if (this.getUtf8Info(ntinfo.memberName).equals(membername)
				&& this.getUtf8Info(ntinfo.typeDescriptor).equals(desc)) {
			return this.getClassInfo(minfo.classIndex);
		} else {
			return null; // false
		}
	}

	private int addItem(final ConstantInfo info) {
		this.items.addElement(info);
		return this.numOfItems++;
	}

	/**
	 * Copies the n-th item in this ConstantInfo object into the destination
	 * ConstantInfo object. The class names that the item refers to are renamed
	 * according to the given map.
	 * 
	 * @param n
	 *            the <i>n</i>-th item
	 * @param dest
	 *            destination constant pool table
	 * @param classnames
	 *            the map or null.
	 * @return the index of the copied item into the destination ClassPool.
	 */
	public int copy(final int n, final ConstantPool dest, final Map classnames) {
		if (n == 0) {
			return 0;
		}

		final ConstantInfo info = this.getItem(n);
		return info.copy(this, dest, classnames);
	}

	int addConstantInfoPadding() {
		return this.addItem(new ConstantInfoPadding());
	}

	/**
	 * Adds a new <code>CONSTANT_Class_info</code> structure.
	 * 
	 * <p>
	 * This also adds a <code>CONSTANT_Utf8_info</code> structure for storing
	 * the class name.
	 * 
	 * @return the index of the added entry.
	 */
	public int addClassInfo(final JClass c) {
		if (c == THIS) {
			return this.thisClassInfo;
		} else if (!c.isArray()) {
			return this.addClassInfo(c.getName());
		} else {
			// an array type is recorded in the hashtable with
			// the key "[L<classname>;" instead of "<classname>".
			//
			// note: toJvmName(toJvmName(c)) is equal to toJvmName(c).

			return this.addClassInfo(Descriptor.toJvmName(c));
		}
	}

	/**
	 * Adds a new <code>CONSTANT_Class_info</code> structure.
	 * 
	 * <p>
	 * This also adds a <code>CONSTANT_Utf8_info</code> structure for storing
	 * the class name.
	 * 
	 * @param qname
	 *            a fully-qualified class name (or the JVM-internal
	 *            representation of that name).
	 * @return the index of the added entry.
	 */
	public int addClassInfo(final String qname) {
		ClassInfo info = (ClassInfo) this.classes.get(qname);
		if (info != null) {
			return info.index;
		} else {
			final int utf8 = this.addUtf8Info(Descriptor.toJvmName(qname));
			info = new ClassInfo(utf8, this.numOfItems);
			this.classes.put(qname, info);
			return this.addItem(info);
		}
	}

	/**
	 * Adds a new <code>CONSTANT_NameAndType_info</code> structure.
	 * 
	 * <p>
	 * This also adds <code>CONSTANT_Utf8_info</code> structures.
	 * 
	 * @param name
	 *            <code>name_index</code>
	 * @param type
	 *            <code>descriptor_index</code>
	 * @return the index of the added entry.
	 */
	public int addNameAndTypeInfo(final String name, final String type) {
		return this.addNameAndTypeInfo(this.addUtf8Info(name),
				this.addUtf8Info(type));
	}

	/**
	 * Adds a new <code>CONSTANT_NameAndType_info</code> structure.
	 * 
	 * @param name
	 *            <code>name_index</code>
	 * @param type
	 *            <code>descriptor_index</code>
	 * @return the index of the added entry.
	 */
	public int addNameAndTypeInfo(final int name, final int type) {
		final int h = hashFunc(name, type);
		final ConstantInfo ci = this.ConstantInfoCache[h];
		if (ci != null && ci instanceof NameAndTypeInfo
				&& ci.hashCheck(name, type)) {
			return this.ConstantInfoIndexCache[h];
		} else {
			final NameAndTypeInfo item = new NameAndTypeInfo(name, type);
			this.ConstantInfoCache[h] = item;
			final int i = this.addItem(item);
			this.ConstantInfoIndexCache[h] = i;
			return i;
		}
	}

	/**
	 * Adds a new <code>CONSTANT_Fieldref_info</code> structure.
	 * 
	 * <p>
	 * This also adds a new <code>CONSTANT_NameAndType_info</code> structure.
	 * 
	 * @param classInfo
	 *            <code>class_index</code>
	 * @param name
	 *            <code>name_index</code> of
	 *            <code>CONSTANT_NameAndType_info</code>.
	 * @param type
	 *            <code>descriptor_index</code> of
	 *            <code>CONSTANT_NameAndType_info</code>.
	 * @return the index of the added entry.
	 */
	public int addFieldrefInfo(final int classInfo, final String name,
			final String type) {
		final int nt = this.addNameAndTypeInfo(name, type);
		return this.addFieldrefInfo(classInfo, nt);
	}

	/**
	 * Adds a new <code>CONSTANT_Fieldref_info</code> structure.
	 * 
	 * @param classInfo
	 *            <code>class_index</code>
	 * @param nameAndTypeInfo
	 *            <code>name_and_type_index</code>.
	 * @return the index of the added entry.
	 */
	public int addFieldrefInfo(final int classInfo, final int nameAndTypeInfo) {
		final int h = hashFunc(classInfo, nameAndTypeInfo);
		final ConstantInfo ci = this.ConstantInfoCache[h];
		if (ci != null && ci instanceof FieldrefInfo
				&& ci.hashCheck(classInfo, nameAndTypeInfo)) {
			return this.ConstantInfoIndexCache[h];
		} else {
			final FieldrefInfo item = new FieldrefInfo(classInfo,
					nameAndTypeInfo);
			this.ConstantInfoCache[h] = item;
			final int i = this.addItem(item);
			this.ConstantInfoIndexCache[h] = i;
			return i;
		}
	}

	/**
	 * Adds a new <code>CONSTANT_Methodref_info</code> structure.
	 * 
	 * <p>
	 * This also adds a new <code>CONSTANT_NameAndType_info</code> structure.
	 * 
	 * @param classInfo
	 *            <code>class_index</code>
	 * @param name
	 *            <code>name_index</code> of
	 *            <code>CONSTANT_NameAndType_info</code>.
	 * @param type
	 *            <code>descriptor_index</code> of
	 *            <code>CONSTANT_NameAndType_info</code>.
	 * @return the index of the added entry.
	 */
	public int addMethodrefInfo(final int classInfo, final String name,
			final String type) {
		final int nt = this.addNameAndTypeInfo(name, type);
		return this.addMethodrefInfo(classInfo, nt);
	}

	/**
	 * Adds a new <code>CONSTANT_Methodref_info</code> structure.
	 * 
	 * @param classInfo
	 *            <code>class_index</code>
	 * @param nameAndTypeInfo
	 *            <code>name_and_type_index</code>.
	 * @return the index of the added entry.
	 */
	public int addMethodrefInfo(final int classInfo, final int nameAndTypeInfo) {
		final int h = hashFunc(classInfo, nameAndTypeInfo);
		final ConstantInfo ci = this.ConstantInfoCache[h];
		if (ci != null && ci instanceof MethodRefInfo
				&& ci.hashCheck(classInfo, nameAndTypeInfo)) {
			return this.ConstantInfoIndexCache[h];
		} else {
			final MethodRefInfo item = new MethodRefInfo(classInfo,
					nameAndTypeInfo);
			this.ConstantInfoCache[h] = item;
			final int i = this.addItem(item);
			this.ConstantInfoIndexCache[h] = i;
			return i;
		}
	}

	/**
	 * Adds a new <code>CONSTANT_InterfaceMethodref_info</code> structure.
	 * 
	 * <p>
	 * This also adds a new <code>CONSTANT_NameAndType_info</code> structure.
	 * 
	 * @param classInfo
	 *            <code>class_index</code>
	 * @param name
	 *            <code>name_index</code> of
	 *            <code>CONSTANT_NameAndType_info</code>.
	 * @param type
	 *            <code>descriptor_index</code> of
	 *            <code>CONSTANT_NameAndType_info</code>.
	 * @return the index of the added entry.
	 */
	public int addInterfaceMethodrefInfo(final int classInfo,
			final String name, final String type) {
		final int nt = this.addNameAndTypeInfo(name, type);
		return this.addInterfaceMethodrefInfo(classInfo, nt);
	}

	/**
	 * Adds a new <code>CONSTANT_InterfaceMethodref_info</code> structure.
	 * 
	 * @param classInfo
	 *            <code>class_index</code>
	 * @param nameAndTypeInfo
	 *            <code>name_and_type_index</code>.
	 * @return the index of the added entry.
	 */
	public int addInterfaceMethodrefInfo(final int classInfo,
			final int nameAndTypeInfo) {
		final int h = hashFunc(classInfo, nameAndTypeInfo);
		final ConstantInfo ci = this.ConstantInfoCache[h];
		if (ci != null && ci instanceof InterfaceMethodRefInfo
				&& ci.hashCheck(classInfo, nameAndTypeInfo)) {
			return this.ConstantInfoIndexCache[h];
		} else {
			final InterfaceMethodRefInfo item = new InterfaceMethodRefInfo(
					classInfo, nameAndTypeInfo);
			this.ConstantInfoCache[h] = item;
			final int i = this.addItem(item);
			this.ConstantInfoIndexCache[h] = i;
			return i;
		}
	}

	/**
	 * Adds a new <code>CONSTANT_String_info</code> structure.
	 * 
	 * <p>
	 * This also adds a new <code>CONSTANT_Utf8_info</code> structure.
	 * 
	 * @return the index of the added entry.
	 */
	public int addStringInfo(final String str) {
		return this.addItem(new StringInfo(this.addUtf8Info(str)));
	}

	/**
	 * Adds a new <code>CONSTANT_Integer_info</code> structure.
	 * 
	 * @return the index of the added entry.
	 */
	public int addIntegerInfo(final int i) {
		return this.addItem(new IntegerInfo(i));
	}

	/**
	 * Adds a new <code>CONSTANT_Float_info</code> structure.
	 * 
	 * @return the index of the added entry.
	 */
	public int addFloatInfo(final float f) {
		return this.addItem(new FloatInfo(f));
	}

	/**
	 * Adds a new <code>CONSTANT_Long_info</code> structure.
	 * 
	 * @return the index of the added entry.
	 */
	public int addLongInfo(final long l) {
		final int i = this.addItem(new LongInfo(l));
		this.addItem(new ConstantInfoPadding());
		return i;
	}

	/**
	 * Adds a new <code>CONSTANT_Double_info</code> structure.
	 * 
	 * @return the index of the added entry.
	 */
	public int addDoubleInfo(final double d) {
		final int i = this.addItem(new DoubleInfo(d));
		this.addItem(new ConstantInfoPadding());
		return i;
	}

	/**
	 * Adds a new <code>CONSTANT_Utf8_info</code> structure.
	 * 
	 * <p>
	 * If the given utf8 string has been already recorded in the table, then
	 * this method does not add a new entry to avoid adding a duplicated entry.
	 * Instead, it returns the index of the entry already recorded.
	 * 
	 * @return the index of the added entry.
	 */
	public int addUtf8Info(final String utf8) {
		Utf8Info info = (Utf8Info) this.strings.get(utf8);
		if (info != null) {
			return info.index;
		} else {
			info = new Utf8Info(utf8, this.numOfItems);
			this.strings.put(utf8, info);
			return this.addItem(info);
		}
	}

	/**
	 * Get all the class names.
	 * 
	 * @return a set of class names
	 */
	public Set getClassNames() {
		final HashSet result = new HashSet();
		final LongVector v = this.items;
		final int size = this.numOfItems;
		for (int i = 1; i < size; ++i) {
			final String className = v.elementAt(i).getClassName(this);
			if (className != null) {
				result.add(className);
			}
		}
		return result;
	}

	/**
	 * Replaces all occurrences of a class name.
	 * 
	 * @param oldName
	 *            the replaced name (JVM-internal representation).
	 * @param newName
	 *            the substituted name (JVM-internal representation).
	 */
	public void renameClass(final String oldName, final String newName) {
		final LongVector v = this.items;
		final int size = this.numOfItems;
		this.classes = new HashMap(this.classes.size() * 2);
		for (int i = 1; i < size; ++i) {
			final ConstantInfo ci = v.elementAt(i);
			ci.renameClass(this, oldName, newName);
			ci.makeHashtable(this);
		}
	}

	/**
	 * Replaces all occurrences of class names.
	 * 
	 * @param classnames
	 *            specifies pairs of replaced and substituted name.
	 */
	public void renameClass(final Map classnames) {
		final LongVector v = this.items;
		final int size = this.numOfItems;
		this.classes = new HashMap(this.classes.size() * 2);
		for (int i = 1; i < size; ++i) {
			final ConstantInfo ci = v.elementAt(i);
			ci.renameClass(this, classnames);
			ci.makeHashtable(this);
		}
	}

	private void read(final DataInputStream in) throws IOException {
		int n = in.readUnsignedShort();

		this.items = new LongVector(n);
		this.numOfItems = 0;
		this.addItem(null); // index 0 is reserved by the JVM.

		while (--n > 0) { // index 0 is reserved by JVM
			final int tag = this.readOne(in);
			if ((tag == LongInfo.tag) || (tag == DoubleInfo.tag)) {
				this.addItem(new ConstantInfoPadding());
				--n;
			}
		}

		int i = 1;
		while (true) {
			final ConstantInfo info = this.items.elementAt(i++);
			if (info == null) {
				break;
			} else {
				info.makeHashtable(this);
			}
		}
	}

	private int readOne(final DataInputStream in) throws IOException {
		ConstantInfo info;
		final int tag = in.readUnsignedByte();
		switch (tag) {
		case Utf8Info.tag: // 1
			info = new Utf8Info(in, this.numOfItems);
			this.strings.put(((Utf8Info) info).string, info);
			break;
		case IntegerInfo.tag: // 3
			info = new IntegerInfo(in);
			break;
		case FloatInfo.tag: // 4
			info = new FloatInfo(in);
			break;
		case LongInfo.tag: // 5
			info = new LongInfo(in);
			break;
		case DoubleInfo.tag: // 6
			info = new DoubleInfo(in);
			break;
		case ClassInfo.tag: // 7
			info = new ClassInfo(in, this.numOfItems);
			// classes.put(<classname>, info);
			break;
		case StringInfo.tag: // 8
			info = new StringInfo(in);
			break;
		case FieldrefInfo.tag: // 9
			info = new FieldrefInfo(in);
			break;
		case MethodRefInfo.tag: // 10
			info = new MethodRefInfo(in);
			break;
		case InterfaceMethodRefInfo.tag: // 11
			info = new InterfaceMethodRefInfo(in);
			break;
		case NameAndTypeInfo.tag: // 12
			info = new NameAndTypeInfo(in);
			break;
		default:
			throw new IOException("invalid constant type: " + tag);
		}

		this.addItem(info);
		return tag;
	}

	/**
	 * Writes the contents of the constant pool table.
	 */
	public void write(final DataOutputStream out) throws IOException {
		out.writeShort(this.numOfItems);
		final LongVector v = this.items;
		final int size = this.numOfItems;
		for (int i = 1; i < size; ++i) {
			v.elementAt(i).write(out);
		}
	}

	/**
	 * Prints the contents of the constant pool table.
	 */
	public void print() {
		this.print(new PrintWriter(System.out, true));
	}

	/**
	 * Prints the contents of the constant pool table.
	 */
	public void print(final PrintWriter out) {
		final int size = this.numOfItems;
		for (int i = 1; i < size; ++i) {
			out.print(i);
			out.print(" ");
			this.items.elementAt(i).print(out);
		}
	}
}
