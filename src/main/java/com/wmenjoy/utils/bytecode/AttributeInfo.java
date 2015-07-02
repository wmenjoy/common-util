package com.wmenjoy.utils.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.sun.org.apache.bcel.internal.classfile.StackMap;

public class AttributeInfo {

	protected ConstantPool constPool;
	int name;
	byte[] info;

	protected AttributeInfo(final ConstantPool cp, final int attrname,
			final byte[] attrinfo) {
		this.constPool = cp;
		this.name = attrname;
		this.info = attrinfo;
	}

	protected AttributeInfo(final ConstantPool cp, final String attrname) {
		this(cp, attrname, (byte[]) null);
	}

	/**
	 * Constructs an <code>attribute_info</code> structure.
	 * 
	 * @param cp
	 *            constant pool table
	 * @param attrname
	 *            attribute name
	 * @param attrinfo
	 *            <code>info</code> field of <code>attribute_info</code>
	 *            structure.
	 */
	public AttributeInfo(final ConstantPool cp, final String attrname,
			final byte[] attrinfo) {
		this(cp, cp.addUtf8Info(attrname), attrinfo);
	}

	protected AttributeInfo(final ConstantPool cp, final int n,
			final DataInputStream in) throws IOException {
		this.constPool = cp;
		this.name = n;
		final int len = in.readInt();
		this.info = new byte[len];
		if (len > 0) {
			in.readFully(this.info);
		}
	}

	static AttributeInfo read(final ConstantPool cp, final DataInputStream in)
			throws IOException {
		final int name = in.readUnsignedShort();
		final String nameStr = cp.getUtf8Info(name);
		if (nameStr.charAt(0) < 'L') {
			if (nameStr.equals(AnnotationDefaultAttribute.tag)) {
				return new AnnotationDefaultAttribute(cp, name, in);
			} else if (nameStr.equals(CodeAttribute.tag)) {
				return new CodeAttribute(cp, name, in);
			} else if (nameStr.equals(ConstantAttribute.tag)) {
				return new ConstantAttribute(cp, name, in);
			} else if (nameStr.equals(DeprecatedAttribute.tag)) {
				return new DeprecatedAttribute(cp, name, in);
			} else if (nameStr.equals(EnclosingMethodAttribute.tag)) {
				return new EnclosingMethodAttribute(cp, name, in);
			} else if (nameStr.equals(ExceptionsAttribute.tag)) {
				return new ExceptionsAttribute(cp, name, in);
			} else if (nameStr.equals(InnerClassesAttribute.tag)) {
				return new InnerClassesAttribute(cp, name, in);
			}
		} else {
			/*
			 * Note that the names of Annotations attributes begin with 'R'.
			 */
			if (nameStr.equals(LineNumberAttribute.tag)) {
				return new LineNumberAttribute(cp, name, in);
			} else if (nameStr.equals(LocalVariableAttribute.tag)) {
				return new LocalVariableAttribute(cp, name, in);
			} else if (nameStr.equals(LocalVariableTypeAttribute.tag)) {
				return new LocalVariableTypeAttribute(cp, name, in);
			} else if (nameStr.equals(AnnotationsAttribute.visibleTag)
					|| nameStr.equals(AnnotationsAttribute.invisibleTag)) {
				// RuntimeVisibleAnnotations or RuntimeInvisibleAnnotations
				return new AnnotationsAttribute(cp, name, in);
			} else if (nameStr.equals(ParameterAnnotationsAttribute.visibleTag)
					|| nameStr
							.equals(ParameterAnnotationsAttribute.invisibleTag)) {
				return new ParameterAnnotationsAttribute(cp, name, in);
			} else if (nameStr.equals(SignatureAttribute.tag)) {
				return new SignatureAttribute(cp, name, in);
			} else if (nameStr.equals(SourceFileAttribute.tag)) {
				return new SourceFileAttribute(cp, name, in);
			} else if (nameStr.equals(SyntheticAttribute.tag)) {
				return new SyntheticAttribute(cp, name, in);
			} else if (nameStr.equals(StackMap.tag)) {
				return new StackMap(cp, name, in);
			} else if (nameStr.equals(StackMapTable.tag)) {
				return new StackMapTable(cp, name, in);
			}
		}

		return new AttributeInfo(cp, name, in);
	}

	/**
	 * Returns an attribute name.
	 */
	public String getName() {
		return this.constPool.getUtf8Info(this.name);
	}

	/**
	 * Returns a constant pool table.
	 */
	public ConstPool getConstPool() {
		return this.constPool;
	}

	/**
	 * Returns the length of this <code>attribute_info</code> structure. The
	 * returned value is <code>attribute_length + 6</code>.
	 */
	public int length() {
		return this.info.length + 6;
	}

	/**
	 * Returns the <code>info</code> field of this <code>attribute_info</code>
	 * structure.
	 * 
	 * <p>
	 * This method is not available if the object is an instance of
	 * <code>CodeAttribute</code>.
	 */
	public byte[] get() {
		return this.info;
	}

	/**
	 * Sets the <code>info</code> field of this <code>attribute_info</code>
	 * structure.
	 * 
	 * <p>
	 * This method is not available if the object is an instance of
	 * <code>CodeAttribute</code>.
	 */
	public void set(final byte[] newinfo) {
		this.info = newinfo;
	}

	/**
	 * Makes a copy. Class names are replaced according to the given
	 * <code>Map</code> object.
	 * 
	 * @param newCp
	 *            the constant pool table used by the new copy.
	 * @param classnames
	 *            pairs of replaced and substituted class names.
	 */
	public AttributeInfo copy(final ConstPool newCp, final Map classnames) {
		final int s = this.info.length;
		final byte[] srcInfo = this.info;
		final byte[] newInfo = new byte[s];
		for (int i = 0; i < s; ++i) {
			newInfo[i] = srcInfo[i];
		}

		return new AttributeInfo(newCp, this.getName(), newInfo);
	}

	void write(final DataOutputStream out) throws IOException {
		out.writeShort(this.name);
		out.writeInt(this.info.length);
		if (this.info.length > 0) {
			out.write(this.info);
		}
	}

	static int getLength(final ArrayList list) {
		int size = 0;
		final int n = list.size();
		for (int i = 0; i < n; ++i) {
			final AttributeInfo attr = (AttributeInfo) list.get(i);
			size += attr.length();
		}

		return size;
	}

	static AttributeInfo lookup(final ArrayList list, final String name) {
		if (list == null) {
			return null;
		}

		final ListIterator iterator = list.listIterator();
		while (iterator.hasNext()) {
			final AttributeInfo ai = (AttributeInfo) iterator.next();
			if (ai.getName().equals(name)) {
				return ai;
			}
		}

		return null; // no such attribute
	}

	static synchronized void remove(final ArrayList list, final String name) {
		if (list == null) {
			return;
		}

		final ListIterator iterator = list.listIterator();
		while (iterator.hasNext()) {
			final AttributeInfo ai = (AttributeInfo) iterator.next();
			if (ai.getName().equals(name)) {
				iterator.remove();
			}
		}
	}

	static void writeAll(final ArrayList list, final DataOutputStream out)
			throws IOException {
		if (list == null) {
			return;
		}

		final int n = list.size();
		for (int i = 0; i < n; ++i) {
			final AttributeInfo attr = (AttributeInfo) list.get(i);
			attr.write(out);
		}
	}

	static ArrayList copyAll(final ArrayList list, final ConstPool cp) {
		if (list == null) {
			return null;
		}

		final ArrayList newList = new ArrayList();
		final int n = list.size();
		for (int i = 0; i < n; ++i) {
			final AttributeInfo attr = (AttributeInfo) list.get(i);
			newList.add(attr.copy(cp, null));
		}

		return newList;
	}

	/*
	 * The following two methods are used to implement ClassFile.renameClass().
	 * Only CodeAttribute, LocalVariableAttribute, AnnotationsAttribute, and
	 * SignatureAttribute override these methods.
	 */
	void renameClass(final String oldname, final String newname) {
	}

	void renameClass(final Map classnames) {
	}

	static void renameClass(final List attributes, final String oldname,
			final String newname) {
		final Iterator iterator = attributes.iterator();
		while (iterator.hasNext()) {
			final AttributeInfo ai = (AttributeInfo) iterator.next();
			ai.renameClass(oldname, newname);
		}
	}

	static void renameClass(final List attributes, final Map classnames) {
		final Iterator iterator = attributes.iterator();
		while (iterator.hasNext()) {
			final AttributeInfo ai = (AttributeInfo) iterator.next();
			ai.renameClass(classnames);
		}
	}

	void getRefClasses(final Map classnames) {
	}

	static void getRefClasses(final List attributes, final Map classnames) {
		final Iterator iterator = attributes.iterator();
		while (iterator.hasNext()) {
			final AttributeInfo ai = (AttributeInfo) iterator.next();
			ai.getRefClasses(classnames);
		}
	}
}
