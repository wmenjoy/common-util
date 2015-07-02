package com.wmenjoy.utils.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnnotationDefaultAttribute extends AttributeInfo {

	protected AnnotationDefaultAttribute(final ConstantPool cp,
			final int attrname, final byte[] attrinfo) {
		super(cp, attrname, attrinfo);
		// TODO Auto-generated constructor stub
	}

	/**
	 * The name of the <code>RuntimeVisibleAnnotations</code> attribute.
	 */
	public static final String visibleTag = "RuntimeVisibleAnnotations";

	/**
	 * The name of the <code>RuntimeInvisibleAnnotations</code> attribute.
	 */
	public static final String invisibleTag = "RuntimeInvisibleAnnotations";

	/**
	 * Constructs a <code>Runtime(In)VisibleAnnotations_attribute</code>.
	 * 
	 * @param cp
	 *            constant pool
	 * @param attrname
	 *            attribute name (<code>visibleTag</code> or
	 *            <code>invisibleTag</code>).
	 * @param info
	 *            the contents of this attribute. It does not include
	 *            <code>attribute_name_index</code> or
	 *            <code>attribute_length</code>.
	 */
	public AnnotationsAttribute(final ConstantPool cp, final String attrname,
			final byte[] info) {
		super(cp, attrname, info);
	}

	/**
	 * Constructs an empty <code>Runtime(In)VisibleAnnotations_attribute</code>.
	 * A new annotation can be later added to the created attribute by
	 * <code>setAnnotations()</code>.
	 * 
	 * @param cp
	 *            constant pool
	 * @param attrname
	 *            attribute name (<code>visibleTag</code> or
	 *            <code>invisibleTag</code>).
	 * @see #setAnnotations(Annotation[])
	 */
	public AnnotationsAttribute(final ConstantPool cp, final String attrname) {
		this(cp, attrname, new byte[] { 0, 0 });
	}

	/**
	 * @param n
	 *            the attribute name.
	 */
	AnnotationsAttribute(final ConstantPool cp, final int n,
			final DataInputStream in) throws IOException {
		super(cp, n, in);
	}

	/**
	 * Returns <code>num_annotations</code>.
	 */
	public int numAnnotations() {
		return ByteArray.readU16bit(this.info, 0);
	}

	/**
	 * Copies this attribute and returns a new copy.
	 */
	public AttributeInfo copy(final ConstantPool newCp, final Map classnames) {
		final Copier copier = new Copier(this.info, this.ConstantPool, newCp,
				classnames);
		try {
			copier.annotationArray();
			return new AnnotationsAttribute(newCp, this.getName(),
					copier.close());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parses the annotations and returns a data structure representing the
	 * annotation with the specified type. See also
	 * <code>getAnnotations()</code> as to the returned data structure.
	 * 
	 * @param type
	 *            the annotation type.
	 * @return null if the specified annotation type is not included.
	 * @see #getAnnotations()
	 */
	public Annotation getAnnotation(final String type) {
		final Annotation[] annotations = this.getAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].getTypeName().equals(type)) {
				return annotations[i];
			}
		}

		return null;
	}

	/**
	 * Adds an annotation. If there is an annotation with the same type, it is
	 * removed before the new annotation is added.
	 * 
	 * @param annotation
	 *            the added annotation.
	 */
	public void addAnnotation(final Annotation annotation) {
		final String type = annotation.getTypeName();
		final Annotation[] annotations = this.getAnnotations();
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].getTypeName().equals(type)) {
				annotations[i] = annotation;
				this.setAnnotations(annotations);
				return;
			}
		}

		final Annotation[] newlist = new Annotation[annotations.length + 1];
		System.arraycopy(annotations, 0, newlist, 0, annotations.length);
		newlist[annotations.length] = annotation;
		this.setAnnotations(newlist);
	}

	/**
	 * Parses the annotations and returns a data structure representing that
	 * parsed annotations. Note that changes of the node values of the returned
	 * tree are not reflected on the annotations represented by this object
	 * unless the tree is copied back to this object by
	 * <code>setAnnotations()</code>.
	 * 
	 * @see #setAnnotations(Annotation[])
	 */
	public Annotation[] getAnnotations() {
		try {
			return new Parser(this.info, this.ConstantPool).parseAnnotations();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Changes the annotations represented by this object according to the given
	 * array of <code>Annotation</code> objects.
	 * 
	 * @param annotations
	 *            the data structure representing the new annotations.
	 */
	public void setAnnotations(final Annotation[] annotations) {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final AnnotationsWriter writer = new AnnotationsWriter(output,
				this.ConstantPool);
		try {
			final int n = annotations.length;
			writer.numAnnotations(n);
			for (int i = 0; i < n; ++i) {
				annotations[i].write(writer);
			}

			writer.close();
		} catch (final IOException e) {
			throw new RuntimeException(e); // should never reach here.
		}

		this.set(output.toByteArray());
	}

	/**
	 * Changes the annotations. A call to this method is equivalent to:
	 * <ul>
	 * 
	 * <pre>
	 * setAnnotations(new Annotation[] { annotation })
	 * </pre>
	 * 
	 * </ul>
	 * 
	 * @param annotation
	 *            the data structure representing the new annotation.
	 */
	public void setAnnotation(final Annotation annotation) {
		this.setAnnotations(new Annotation[] { annotation });
	}

	/**
	 * @param oldname
	 *            a JVM class name.
	 * @param newname
	 *            a JVM class name.
	 */
	@Override
	void renameClass(final String oldname, final String newname) {
		final HashMap map = new HashMap();
		map.put(oldname, newname);
		this.renameClass(map);
	}

	@Override
	void renameClass(final Map classnames) {
		final Renamer renamer = new Renamer(this.info, this.getConstantPool(),
				classnames);
		try {
			renamer.annotationArray();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	void getRefClasses(final Map classnames) {
		this.renameClass(classnames);
	}

	/**
	 * Returns a string representation of this object.
	 */
	@Override
	public String toString() {
		final Annotation[] a = this.getAnnotations();
		final StringBuilder sbuf = new StringBuilder();
		int i = 0;
		while (i < a.length) {
			sbuf.append(a[i++].toString());
			if (i != a.length) {
				sbuf.append(", ");
			}
		}

		return sbuf.toString();
	}

	static class Walker {
		byte[] info;

		Walker(final byte[] attrInfo) {
			this.info = attrInfo;
		}

		final void parameters() throws Exception {
			final int numParam = this.info[0] & 0xff;
			this.parameters(numParam, 1);
		}

		void parameters(final int numParam, int pos) throws Exception {
			for (int i = 0; i < numParam; ++i) {
				pos = this.annotationArray(pos);
			}
		}

		final void annotationArray() throws Exception {
			this.annotationArray(0);
		}

		final int annotationArray(final int pos) throws Exception {
			final int num = ByteArray.readU16bit(this.info, pos);
			return this.annotationArray(pos + 2, num);
		}

		int annotationArray(int pos, final int num) throws Exception {
			for (int i = 0; i < num; ++i) {
				pos = this.annotation(pos);
			}

			return pos;
		}

		final int annotation(final int pos) throws Exception {
			final int type = ByteArray.readU16bit(this.info, pos);
			final int numPairs = ByteArray.readU16bit(this.info, pos + 2);
			return this.annotation(pos + 4, type, numPairs);
		}

		int annotation(int pos, final int type, final int numPairs)
				throws Exception {
			for (int j = 0; j < numPairs; ++j) {
				pos = this.memberValuePair(pos);
			}

			return pos;
		}

		final int memberValuePair(final int pos) throws Exception {
			final int nameIndex = ByteArray.readU16bit(this.info, pos);
			return this.memberValuePair(pos + 2, nameIndex);
		}

		int memberValuePair(final int pos, final int nameIndex)
				throws Exception {
			return this.memberValue(pos);
		}

		final int memberValue(final int pos) throws Exception {
			final int tag = this.info[pos] & 0xff;
			if (tag == 'e') {
				final int typeNameIndex = ByteArray.readU16bit(this.info,
						pos + 1);
				final int constNameIndex = ByteArray.readU16bit(this.info,
						pos + 3);
				this.enumMemberValue(pos, typeNameIndex, constNameIndex);
				return pos + 5;
			} else if (tag == 'c') {
				final int index = ByteArray.readU16bit(this.info, pos + 1);
				this.classMemberValue(pos, index);
				return pos + 3;
			} else if (tag == '@') {
				return this.annotationMemberValue(pos + 1);
			} else if (tag == '[') {
				final int num = ByteArray.readU16bit(this.info, pos + 1);
				return this.arrayMemberValue(pos + 3, num);
			} else { // primitive types or String.
				final int index = ByteArray.readU16bit(this.info, pos + 1);
				this.constValueMember(tag, index);
				return pos + 3;
			}
		}

		void constValueMember(final int tag, final int index) throws Exception {
		}

		void enumMemberValue(final int pos, final int typeNameIndex,
				final int constNameIndex) throws Exception {
		}

		void classMemberValue(final int pos, final int index) throws Exception {
		}

		int annotationMemberValue(final int pos) throws Exception {
			return this.annotation(pos);
		}

		int arrayMemberValue(int pos, final int num) throws Exception {
			for (int i = 0; i < num; ++i) {
				pos = this.memberValue(pos);
			}

			return pos;
		}
	}

	static class Renamer extends Walker {
		ConstantPool cpool;
		Map classnames;

		/**
		 * Constructs a renamer. It renames some class names into the new names
		 * specified by <code>map</code>.
		 * 
		 * @param info
		 *            the annotations attribute.
		 * @param cp
		 *            the constant pool.
		 * @param map
		 *            pairs of replaced and substituted class names. It can be
		 *            null.
		 */
		Renamer(final byte[] info, final ConstantPool cp, final Map map) {
			super(info);
			this.cpool = cp;
			this.classnames = map;
		}

		@Override
		int annotation(final int pos, final int type, final int numPairs)
				throws Exception {
			this.renameType(pos - 4, type);
			return super.annotation(pos, type, numPairs);
		}

		@Override
		void enumMemberValue(final int pos, final int typeNameIndex,
				final int constNameIndex) throws Exception {
			this.renameType(pos + 1, typeNameIndex);
			super.enumMemberValue(pos, typeNameIndex, constNameIndex);
		}

		@Override
		void classMemberValue(final int pos, final int index) throws Exception {
			this.renameType(pos + 1, index);
			super.classMemberValue(pos, index);
		}

		private void renameType(final int pos, final int index) {
			final String name = this.cpool.getUtf8Info(index);
			final String newName = Descriptor.rename(name, this.classnames);
			if (!name.equals(newName)) {
				final int index2 = this.cpool.addUtf8Info(newName);
				ByteArray.write16bit(index2, this.info, pos);
			}
		}
	}

	static class Copier extends Walker {
		ByteArrayOutputStream output;
		AnnotationsWriter writer;
		ConstantPool srcPool, destPool;
		Map classnames;

		/**
		 * Constructs a copier. This copier renames some class names into the
		 * new names specified by <code>map</code> when it copies an annotation
		 * attribute.
		 * 
		 * @param info
		 *            the source attribute.
		 * @param src
		 *            the constant pool of the source class.
		 * @param dest
		 *            the constant pool of the destination class.
		 * @param map
		 *            pairs of replaced and substituted class names. It can be
		 *            null.
		 */
		Copier(final byte[] info, final ConstantPool src,
				final ConstantPool dest, final Map map) {
			super(info);
			this.output = new ByteArrayOutputStream();
			this.writer = new AnnotationsWriter(this.output, dest);
			this.srcPool = src;
			this.destPool = dest;
			this.classnames = map;
		}

		byte[] close() throws IOException {
			this.writer.close();
			return this.output.toByteArray();
		}

		@Override
		void parameters(final int numParam, final int pos) throws Exception {
			this.writer.numParameters(numParam);
			super.parameters(numParam, pos);
		}

		@Override
		int annotationArray(final int pos, final int num) throws Exception {
			this.writer.numAnnotations(num);
			return super.annotationArray(pos, num);
		}

		@Override
		int annotation(final int pos, final int type, final int numPairs)
				throws Exception {
			this.writer.annotation(this.copyType(type), numPairs);
			return super.annotation(pos, type, numPairs);
		}

		@Override
		int memberValuePair(final int pos, final int nameIndex)
				throws Exception {
			this.writer.memberValuePair(this.copy(nameIndex));
			return super.memberValuePair(pos, nameIndex);
		}

		@Override
		void constValueMember(final int tag, final int index) throws Exception {
			this.writer.constValueIndex(tag, this.copy(index));
			super.constValueMember(tag, index);
		}

		@Override
		void enumMemberValue(final int pos, final int typeNameIndex,
				final int constNameIndex) throws Exception {
			this.writer.enumConstValue(this.copyType(typeNameIndex),
					this.copy(constNameIndex));
			super.enumMemberValue(pos, typeNameIndex, constNameIndex);
		}

		@Override
		void classMemberValue(final int pos, final int index) throws Exception {
			this.writer.classInfoIndex(this.copyType(index));
			super.classMemberValue(pos, index);
		}

		@Override
		int annotationMemberValue(final int pos) throws Exception {
			this.writer.annotationValue();
			return super.annotationMemberValue(pos);
		}

		@Override
		int arrayMemberValue(final int pos, final int num) throws Exception {
			this.writer.arrayValue(num);
			return super.arrayMemberValue(pos, num);
		}

		/**
		 * Copies a constant pool entry into the destination constant pool and
		 * returns the index of the copied entry.
		 * 
		 * @param srcIndex
		 *            the index of the copied entry into the source constant
		 *            pool.
		 * @return the index of the copied item into the destination constant
		 *         pool.
		 */
		int copy(final int srcIndex) {
			return this.srcPool.copy(srcIndex, this.destPool, this.classnames);
		}

		/**
		 * Copies a constant pool entry into the destination constant pool and
		 * returns the index of the copied entry. That entry must be a Utf8Info
		 * representing a class name in the L<class name>; form.
		 * 
		 * @param srcIndex
		 *            the index of the copied entry into the source constant
		 *            pool.
		 * @return the index of the copied item into the destination constant
		 *         pool.
		 */
		int copyType(final int srcIndex) {
			final String name = this.srcPool.getUtf8Info(srcIndex);
			final String newName = Descriptor.rename(name, this.classnames);
			return this.destPool.addUtf8Info(newName);
		}
	}

	static class Parser extends Walker {
		ConstantPool pool;
		Annotation[][] allParams; // all parameters
		Annotation[] allAnno; // all annotations
		Annotation currentAnno; // current annotation
		MemberValue currentMember; // current member

		/**
		 * Constructs a parser. This parser constructs a parse tree of the
		 * annotations.
		 * 
		 * @param info
		 *            the attribute.
		 * @param src
		 *            the constant pool.
		 */
		Parser(final byte[] info, final ConstantPool cp) {
			super(info);
			this.pool = cp;
		}

		Annotation[][] parseParameters() throws Exception {
			this.parameters();
			return this.allParams;
		}

		Annotation[] parseAnnotations() throws Exception {
			this.annotationArray();
			return this.allAnno;
		}

		MemberValue parseMemberValue() throws Exception {
			this.memberValue(0);
			return this.currentMember;
		}

		@Override
		void parameters(final int numParam, int pos) throws Exception {
			final Annotation[][] params = new Annotation[numParam][];
			for (int i = 0; i < numParam; ++i) {
				pos = this.annotationArray(pos);
				params[i] = this.allAnno;
			}

			this.allParams = params;
		}

		@Override
		int annotationArray(int pos, final int num) throws Exception {
			final Annotation[] array = new Annotation[num];
			for (int i = 0; i < num; ++i) {
				pos = this.annotation(pos);
				array[i] = this.currentAnno;
			}

			this.allAnno = array;
			return pos;
		}

		@Override
		int annotation(final int pos, final int type, final int numPairs)
				throws Exception {
			this.currentAnno = new Annotation(type, this.pool);
			return super.annotation(pos, type, numPairs);
		}

		@Override
		int memberValuePair(int pos, final int nameIndex) throws Exception {
			pos = super.memberValuePair(pos, nameIndex);
			this.currentAnno.addMemberValue(nameIndex, this.currentMember);
			return pos;
		}

		@Override
		void constValueMember(final int tag, final int index) throws Exception {
			MemberValue m;
			final ConstantPool cp = this.pool;
			switch (tag) {
			case 'B':
				m = new ByteMemberValue(index, cp);
				break;
			case 'C':
				m = new CharMemberValue(index, cp);
				break;
			case 'D':
				m = new DoubleMemberValue(index, cp);
				break;
			case 'F':
				m = new FloatMemberValue(index, cp);
				break;
			case 'I':
				m = new IntegerMemberValue(index, cp);
				break;
			case 'J':
				m = new LongMemberValue(index, cp);
				break;
			case 'S':
				m = new ShortMemberValue(index, cp);
				break;
			case 'Z':
				m = new BooleanMemberValue(index, cp);
				break;
			case 's':
				m = new StringMemberValue(index, cp);
				break;
			default:
				throw new RuntimeException("unknown tag:" + tag);
			}

			this.currentMember = m;
			super.constValueMember(tag, index);
		}

		@Override
		void enumMemberValue(final int pos, final int typeNameIndex,
				final int constNameIndex) throws Exception {
			this.currentMember = new EnumMemberValue(typeNameIndex,
					constNameIndex, this.pool);
			super.enumMemberValue(pos, typeNameIndex, constNameIndex);
		}

		@Override
		void classMemberValue(final int pos, final int index) throws Exception {
			this.currentMember = new ClassMemberValue(index, this.pool);
			super.classMemberValue(pos, index);
		}

		@Override
		int annotationMemberValue(int pos) throws Exception {
			final Annotation anno = this.currentAnno;
			pos = super.annotationMemberValue(pos);
			this.currentMember = new AnnotationMemberValue(this.currentAnno,
					this.pool);
			this.currentAnno = anno;
			return pos;
		}

		@Override
		int arrayMemberValue(int pos, final int num) throws Exception {
			final ArrayMemberValue amv = new ArrayMemberValue(this.pool);
			final MemberValue[] elements = new MemberValue[num];
			for (int i = 0; i < num; ++i) {
				pos = this.memberValue(pos);
				elements[i] = this.currentMember;
			}

			amv.setValue(elements);
			this.currentMember = amv;
			return pos;
		}
	}

}
