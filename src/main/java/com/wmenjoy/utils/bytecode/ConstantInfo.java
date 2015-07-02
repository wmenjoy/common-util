package com.wmenjoy.utils.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public abstract class ConstantInfo {

	/**
	 * 获取该类型常量对应的tag信息
	 * 
	 * @return
	 */
	public abstract int getTag();

	public String getClassName(final ConstantPool cp) {
		return null;
	}

	public void renameClass(final ConstantPool cp, final String oldName,
			final String newName) {
	}

	public void renameClass(final ConstantPool cp, final Map classnames) {
	}

	public abstract int copy(ConstantPool src, ConstantPool dest, Map classnames);

	// ** classnames is a mapping between JVM names.

	public abstract void write(DataOutputStream out) throws IOException;

	public abstract void print(PrintWriter out);

	void makeHashtable(final ConstantPool cp) {
	} // called after read() finishes in ConstantPool.

	boolean hashCheck(final int a, final int b) {
		return false;
	}

	@Override
	public String toString() {
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		final PrintWriter out = new PrintWriter(bout);
		this.print(out);
		return bout.toString();
	}
}

/*
 * padding following DoubleInfo or LongInfo.
 */
class ConstantInfoPadding extends ConstantInfo {
	@Override
	public int getTag() {
		return 0;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addConstantInfoPadding();
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
	}

	@Override
	public void print(final PrintWriter out) {
		out.println("padding");
	}
}

class ClassInfo extends ConstantInfo {
	static final int tag = 7;
	int name;
	int index;

	public ClassInfo(final int className, final int i) {
		this.name = className;
		this.index = i;
	}

	public ClassInfo(final DataInputStream in, final int i) throws IOException {
		this.name = in.readUnsignedShort();
		this.index = i;
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public String getClassName(final ConstantPool cp) {
		return cp.getUtf8Info(this.name);
	};

	@Override
	public void renameClass(final ConstantPool cp, final String oldName,
			final String newName) {
		final String nameStr = cp.getUtf8Info(this.name);
		if (nameStr.equals(oldName)) {
			this.name = cp.addUtf8Info(newName);
		} else if (nameStr.charAt(0) == '[') {
			final String nameStr2 = Descriptor
					.rename(nameStr, oldName, newName);
			if (nameStr != nameStr2) {
				this.name = cp.addUtf8Info(nameStr2);
			}
		}
	}

	@Override
	public void renameClass(final ConstantPool cp, final Map map) {
		final String oldName = cp.getUtf8Info(this.name);
		if (oldName.charAt(0) == '[') {
			final String newName = Descriptor.rename(oldName, map);
			if (oldName != newName) {
				this.name = cp.addUtf8Info(newName);
			}
		} else {
			final String newName = (String) map.get(oldName);
			if (newName != null && !newName.equals(oldName)) {
				this.name = cp.addUtf8Info(newName);
			}
		}
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		String classname = src.getUtf8Info(this.name);
		if (map != null) {
			final String newname = (String) map.get(classname);
			if (newname != null) {
				classname = newname;
			}
		}

		return dest.addClassInfo(classname);
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeShort(this.name);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("Class #");
		out.println(this.name);
	}

	@Override
	void makeHashtable(final ConstantPool cp) {
		final String name = Descriptor.toJavaName(this.getClassName(cp));
		cp.classes.put(name, this);
	}
}

class NameAndTypeInfo extends ConstantInfo {
	static final int tag = 12;
	int memberName;
	int typeDescriptor;

	public NameAndTypeInfo(final int name, final int type) {
		this.memberName = name;
		this.typeDescriptor = type;
	}

	public NameAndTypeInfo(final DataInputStream in) throws IOException {
		this.memberName = in.readUnsignedShort();
		this.typeDescriptor = in.readUnsignedShort();
	}

	@Override
	boolean hashCheck(final int a, final int b) {
		return a == this.memberName && b == this.typeDescriptor;
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public void renameClass(final ConstantPool cp, final String oldName,
			final String newName) {
		final String type = cp.getUtf8Info(this.typeDescriptor);
		final String type2 = Descriptor.rename(type, oldName, newName);
		if (type != type2) {
			this.typeDescriptor = cp.addUtf8Info(type2);
		}
	}

	@Override
	public void renameClass(final ConstantPool cp, final Map map) {
		final String type = cp.getUtf8Info(this.typeDescriptor);
		final String type2 = Descriptor.rename(type, map);
		if (type != type2) {
			this.typeDescriptor = cp.addUtf8Info(type2);
		}
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		final String mname = src.getUtf8Info(this.memberName);
		String tdesc = src.getUtf8Info(this.typeDescriptor);
		tdesc = Descriptor.rename(tdesc, map);
		return dest.addNameAndTypeInfo(dest.addUtf8Info(mname),
				dest.addUtf8Info(tdesc));
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeShort(this.memberName);
		out.writeShort(this.typeDescriptor);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("NameAndType #");
		out.print(this.memberName);
		out.print(", type #");
		out.println(this.typeDescriptor);
	}
}

abstract class MemberrefInfo extends ConstantInfo {
	int classIndex;
	int nameAndTypeIndex;

	public MemberrefInfo(final int cindex, final int ntindex) {
		this.classIndex = cindex;
		this.nameAndTypeIndex = ntindex;
	}

	public MemberrefInfo(final DataInputStream in) throws IOException {
		this.classIndex = in.readUnsignedShort();
		this.nameAndTypeIndex = in.readUnsignedShort();
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		final int classIndex2 = src.getItem(this.classIndex).copy(src, dest,
				map);
		final int ntIndex2 = src.getItem(this.nameAndTypeIndex).copy(src, dest,
				map);
		return this.copy2(dest, classIndex2, ntIndex2);
	}

	@Override
	boolean hashCheck(final int a, final int b) {
		return a == this.classIndex && b == this.nameAndTypeIndex;
	}

	abstract protected int copy2(ConstantPool dest, int cindex, int ntindex);

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(this.getTag());
		out.writeShort(this.classIndex);
		out.writeShort(this.nameAndTypeIndex);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print(this.getTagName() + " #");
		out.print(this.classIndex);
		out.print(", name&type #");
		out.println(this.nameAndTypeIndex);
	}

	public abstract String getTagName();
}

class FieldrefInfo extends MemberrefInfo {
	static final int tag = 9;

	public FieldrefInfo(final int cindex, final int ntindex) {
		super(cindex, ntindex);
	}

	public FieldrefInfo(final DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public String getTagName() {
		return "Field";
	}

	@Override
	protected int copy2(final ConstantPool dest, final int cindex,
			final int ntindex) {
		return dest.addFieldrefInfo(cindex, ntindex);
	}
}

class MethodRefInfo extends MemberrefInfo {
	static final int tag = 10;

	public MethodRefInfo(final int cindex, final int ntindex) {
		super(cindex, ntindex);
	}

	public MethodRefInfo(final DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public String getTagName() {
		return "Method";
	}

	@Override
	protected int copy2(final ConstantPool dest, final int cindex,
			final int ntindex) {
		return dest.addMethodrefInfo(cindex, ntindex);
	}
}

class InterfaceMethodRefInfo extends MemberrefInfo {
	static final int tag = 11;

	public InterfaceMethodRefInfo(final int cindex, final int ntindex) {
		super(cindex, ntindex);
	}

	public InterfaceMethodRefInfo(final DataInputStream in) throws IOException {
		super(in);
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public String getTagName() {
		return "Interface";
	}

	@Override
	protected int copy2(final ConstantPool dest, final int cindex,
			final int ntindex) {
		return dest.addInterfaceMethodrefInfo(cindex, ntindex);
	}
}

class StringInfo extends ConstantInfo {
	static final int tag = 8;
	int string;

	public StringInfo(final int str) {
		this.string = str;
	}

	public StringInfo(final DataInputStream in) throws IOException {
		this.string = in.readUnsignedShort();
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addStringInfo(src.getUtf8Info(this.string));
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeShort(this.string);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("String #");
		out.println(this.string);
	}
}

class IntegerInfo extends ConstantInfo {
	static final int tag = 3;
	int value;

	public IntegerInfo(final int i) {
		this.value = i;
	}

	public IntegerInfo(final DataInputStream in) throws IOException {
		this.value = in.readInt();
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addIntegerInfo(this.value);
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeInt(this.value);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("Integer ");
		out.println(this.value);
	}
}

class FloatInfo extends ConstantInfo {
	static final int tag = 4;
	float value;

	public FloatInfo(final float f) {
		this.value = f;
	}

	public FloatInfo(final DataInputStream in) throws IOException {
		this.value = in.readFloat();
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addFloatInfo(this.value);
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeFloat(this.value);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("Float ");
		out.println(this.value);
	}
}

class LongInfo extends ConstantInfo {
	static final int tag = 5;
	long value;

	public LongInfo(final long l) {
		this.value = l;
	}

	public LongInfo(final DataInputStream in) throws IOException {
		this.value = in.readLong();
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addLongInfo(this.value);
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeLong(this.value);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("Long ");
		out.println(this.value);
	}
}

class DoubleInfo extends ConstantInfo {
	static final int tag = 6;
	double value;

	public DoubleInfo(final double d) {
		this.value = d;
	}

	public DoubleInfo(final DataInputStream in) throws IOException {
		this.value = in.readDouble();
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addDoubleInfo(this.value);
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeDouble(this.value);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("Double ");
		out.println(this.value);
	}
}

class Utf8Info extends ConstantInfo {
	static final int tag = 1;
	String string;
	int index;

	public Utf8Info(final String utf8, final int i) {
		this.string = utf8;
		this.index = i;
	}

	public Utf8Info(final DataInputStream in, final int i) throws IOException {
		this.string = in.readUTF();
		this.index = i;
	}

	@Override
	public int getTag() {
		return tag;
	}

	@Override
	public int copy(final ConstantPool src, final ConstantPool dest,
			final Map map) {
		return dest.addUtf8Info(this.string);
	}

	@Override
	public void write(final DataOutputStream out) throws IOException {
		out.writeByte(tag);
		out.writeUTF(this.string);
	}

	@Override
	public void print(final PrintWriter out) {
		out.print("UTF8 \"");
		out.print(this.string);
		out.println("\"");
	}
}
