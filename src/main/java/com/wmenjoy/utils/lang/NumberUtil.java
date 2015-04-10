package com.wmenjoy.utils.lang;

/***
 * 处理数字相关的工具类
 *
 * @author jinliang.liu
 *
 */
public abstract class NumberUtil {


	public static final String NEGATIVE = "NEGATIVE";

	/**
	 * 处理循环左移位，
	 *
	 * @param id
	 *            ： 待移位的整数
	 * @param shiftNumber
	 *            : 循环移动的位数, 应该小于可移动的位数，否则结果不可知
	 * @param rangeBit
	 *            : 指定固定范围的bit 移位， 应该小于 64位
	 *
	 *
	 *            保留不用的高位为0
	 */
	public static long shiftL(final long id, final int shiftNumber,
			final int rangeBit) {
		return ((id << shiftNumber) | (id >>> (rangeBit - shiftNumber)))
				& LONG_MASK[rangeBit];
	}

	/**
	 * 处理循环右移位，
	 *
	 * @param id
	 *            ： 待移位的整数
	 * @param shiftNumber
	 *            : 循环移动的位数, 应该小于可移动的位数，否则结果不可知
	 * @param rangeBit
	 *            : 指定固定范围的bit 移位， 应该小于 64位
	 *
	 *
	 *            保留不用的高位为0
	 */
	public static long shiftR(final long id, final int shiftNumber,
			final int rangeBit) {
		return ((id >>> shiftNumber) | (id << (rangeBit - shiftNumber)))
				& LONG_MASK[rangeBit];
	}


	/**
	 * 反转指定的bit位
	 *
	 * @param id
	 * @param rangeBit
	 * @return
	 */
	public static long reverse(final long id, final int rangeBit) {
		return Long.reverse(id) >>> (64 - rangeBit);
	}


	/**
	 * 将id的低numOfBit位，按照radix进制，输出numOfOutputChar位， 补0;
	 *
	 * 高位的id不管
	 *
	 * @param id
	 * @param bit
	 * @param radix
	 * @return
	 */
	public static String toUnSignedRadix(final long id, final int numOfBit,
			final int numOfOutputChar, final int radix) {
		if (id < 0) {
			return NEGATIVE;
		}

		final int numberOfBit = (numOfBit < 1) || (numOfBit > 63) ? 63 : numOfBit;
		String resultStr = null;
		resultStr = Long.toString(id & LONG_MASK[numberOfBit], radix);
		final int fillingNum = numOfOutputChar <= resultStr.length() ? 0
				: numOfOutputChar - resultStr.length();
		final StringBuilder sb = new StringBuilder(fillingNum);
		if (fillingNum < FILLING_STR.length) {
			sb.append(FILLING_STR[fillingNum]).append(resultStr);
			return sb.toString();
		}

		final int repeatedNum = fillingNum / (FILLING_STR.length - 1);
		final int remainedNum = fillingNum % (FILLING_STR.length - 1);

		final String fillStr = FILLING_STR[FILLING_STR.length - 1];
		for (int i = 0; i < repeatedNum; i++) {
			sb.append(fillStr);
		}

		sb.append(FILLING_STR[remainedNum]).append(resultStr);
		return sb.toString();

	}


	/**
	 * 填充补零
	 */
	final static String[] FILLING_STR = { "", "0", "00", "000", "0000",
			"00000", "000000", "0000000", "00000000", "000000000", "0000000000" };

	/**
	 * 长整型的mask
	 */
	private static long[] LONG_MASK = { 0, 1L, 0x3L, 0x7L, 0xfL, 0x1fL, 0x3fL,
			0x7fL, 0xffL, 0x1ffL, 0x3ffL, 0x7ffL, 0xfffL, 0x1fffL, 0x3fffL,
			0x7fffL, 0xffffL, 0x1ffffL, 0x3ffffL, 0x7ffffL, 0xfffffL,
			0x1fffffL, 0x3fffffL, 0x7fffffL, 0xffffffL, 0x1ffffffL, 0x3ffffffL,
			0x7ffffffL, 0xfffffffL, 0x1fffffffL, 0x3fffffffL, 0x7fffffffL,
			0xffffffffL, 0x1ffffffffL, 0x3ffffffffL, 0x7ffffffffL,
			0xfffffffffL, 0x1fffffffffL, 0x3fffffffffL, 0x7fffffffffL,
			0xffffffffffL, 0x1ffffffffffL, 0x3ffffffffffL, 0x7ffffffffffL,
			0xfffffffffffL, 0x1fffffffffffL, 0x3fffffffffffL, 0x7fffffffffffL,
			0xffffffffffffL, 0x1ffffffffffffL, 0x3ffffffffffffL,
			0x7ffffffffffffL, 0xfffffffffffffL, 0x1fffffffffffffL,
			0x3fffffffffffffL, 0x7fffffffffffffL, 0xffffffffffffffL,
			0x1ffffffffffffffL, 0x3ffffffffffffffL, 0x7ffffffffffffffL,
			0xfffffffffffffffL, 0x1fffffffffffffffL, 0x3fffffffffffffffL,
			0x7fffffffffffffffL, 0xffffffffffffffffL };

}
