package com.wmenjoy.utils.bytecode;

final class LongVector {
	static final int ASIZE = 128;
	static final int ABITS = 7; // ASIZE = 2^ABITS
	static final int VSIZE = 8;
	private ConstantInfo[][] objects;
	private int elements;

	public LongVector() {
		this.objects = new ConstantInfo[VSIZE][];
		this.elements = 0;
	}

	public LongVector(final int initialSize) {
		final int vsize = ((initialSize >> ABITS) & ~(VSIZE - 1)) + VSIZE;
		this.objects = new ConstantInfo[vsize][];
		this.elements = 0;
	}

	public int size() {
		return this.elements;
	}

	public int capacity() {
		return this.objects.length * ASIZE;
	}

	public ConstantInfo elementAt(final int i) {
		if (i < 0 || this.elements <= i) {
			return null;
		}

		return this.objects[i >> ABITS][i & (ASIZE - 1)];
	}

	public void addElement(final ConstantInfo value) {
		final int nth = this.elements >> ABITS;
		final int offset = this.elements & (ASIZE - 1);
		final int len = this.objects.length;
		if (nth >= len) {
			final ConstantInfo[][] newObj = new ConstantInfo[len + VSIZE][];
			System.arraycopy(this.objects, 0, newObj, 0, len);
			this.objects = newObj;
		}

		if (this.objects[nth] == null) {
			this.objects[nth] = new ConstantInfo[ASIZE];
		}

		this.objects[nth][offset] = value;
		this.elements++;
	}
}