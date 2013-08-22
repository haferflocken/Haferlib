package org.haferlib.util;

/**
 * A class to convert between arrays of bytes, arrays of booleans, longs, and ints.
 * 
 * @author John Werner
 *
 */

public class ByteHelper {

	/**
	 * Turn 8 booleans into one byte.
	 * 
	 * @param a The first bit.
	 * @param b The second bit.
	 * @param c The third bit.
	 * @param d The fourth bit.
	 * @param e The fifth bit.
	 * @param f The sixth bit.
	 * @param g The seventh bit.
	 * @param h The eighth bit.
	 * @return A byte, in the binary form abcdefgh.
	 */
	public byte bitsToByte(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
		return (byte)((((a)?1:0) << 7) + (((b)?1:0) << 6) + (((c)?1:0) << 5) + (((d)?1:0) << 4) + (((e)?1:0) << 3) + (((f)?1:0) << 2) + (((g)?1:0) << 1) + ((h)?1:0));
	}

	/**
	 * Turn an array of bits into a byte, with the first bit at the given index.
	 * 
	 * @param bits The bits to turn into a byte.
	 * @param startIndex The index to read the array from.
	 * @return A byte made from the given bits.
	 * @throws ArrayIndexOutOfBoundsException if startIndex > bits.length - 8.
	 */
	public byte bitsToByte(boolean[] bits, int startIndex) {
		return bitsToByte(bits[startIndex], bits[startIndex + 1], bits[startIndex + 2], bits[startIndex + 3],
				bits[startIndex + 4], bits[startIndex + 5], bits[startIndex + 6], bits[startIndex + 7]);
	}
	
	/**
	 * Makes an array of booleans representing the bits in a byte.
	 * 
	 * @param b The byte to make into bits.
	 * @return An array of booleans representing the bits in a byte.
	 */
	public boolean[] byteToBits(byte b) {
		boolean[] bits = new boolean[8];
		byteIntoBits(b, bits, 0);
		return bits;
	}
	
	/**
	 * Place the bits of a byte into a boolean array, starting at startIndex.
	 * 
	 * @param b The byte to break down.
	 * @param bits The array to place the bits in.
	 * @param startIndex The index to begin placing the bits in the array.
	 * @throws ArrayIndexOutOfBoundsException if startIndex > bits.length - 8.
	 */
	public void byteIntoBits(byte b, boolean[] bits, int startIndex) {
		bits[startIndex] =		((b & 128) == 128);
		bits[startIndex + 1] =	((b & 64) == 64);
		bits[startIndex + 2] =	((b & 32) == 32);
		bits[startIndex + 3] =	((b & 16) == 16);
		bits[startIndex + 4] =	((b & 8) == 8);
		bits[startIndex + 5] =	((b & 4) == 4);
		bits[startIndex + 6] =	((b & 2) == 2);
		bits[startIndex + 7] =	((b & 1) == 1);
	}

	/**
	 * Convert an int into an array of bytes.
	 * 
	 * @param num The int to break down.
	 * @return An array of four bytes that represent num.
	 */
	public byte[] intIntoBytes(int num) {
		byte[] bytes = new byte[4];
		intIntoBytes(num, bytes, 0);
		return bytes;
	}

	/**
	 * Take an int and break it down into bytes, placing the bytes in the given
	 * array beginning at startIndex.
	 * 
	 * @param num The int to break down.
	 * @param bytes The array to place the bytes in.
	 * @param startIndex The index to begin placing the bytes.
	 * @throws ArrayIndexOutOfBoundsException if startIndex > bytes.length - 4.
	 */
	public void intIntoBytes(int num, byte[] bytes, int startIndex) {
		bytes[startIndex++] = (byte)((num >> 24) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 16) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 8) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 0) & 0xFF);
	}

	/**
	 * Read four bytes from an array, beginning at startIndex, and combine them into an int.
	 * 
	 * @param bytes The array to read from.
	 * @param startIndex Where to begin reading the array.
	 * @return An int constructed from four consecutive bytes in the array.
	 * @throws ArrayIndexOutOfBoundsException if startIndex > bytes.length - 4.
	 */
	public int bytesToInt(byte[] bytes, int startIndex) {
		return ((bytes[startIndex++] & 0xFF) << 24) | ((bytes[startIndex++] & 0xFF) << 16) | ((bytes[startIndex++] & 0xFF) << 8) | (bytes[startIndex++] & 0xFF);
	}

	/**
	 * Convert a long into an array of bytes that is 8 bytes long.
	 * 
	 * @param num The long to make into an array of bytes.
	 * @return An array of bytes representing num.
	 */
	public byte[] longIntoBytes(long num) {
		byte[] bytes = new byte[8];
		longIntoBytes(num, bytes, 0);
		return bytes;
	}

	/**
	 * Break a long into bytes and place them in the given array starting at startIndex.
	 * 
	 * @param num The long to break down.
	 * @param bytes The array to place the long in.
	 * @param startIndex The index at which to begin placing the bytes.
	 * @throws ArrayIndexOutOfBoundsException if startIndex > bytes.length - 8.
	 */
	public void longIntoBytes(long num, byte[] bytes, int startIndex) {
		bytes[startIndex++] = (byte)((num >> 56) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 48) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 40) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 32) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 24) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 16) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 8) & 0xFF);
		bytes[startIndex++] = (byte)((num >> 0) & 0xFF);
	}

	/**
	 * Read eight bytes from an array, beginning at startIndex, and combine them into a long.
	 * 
	 * @param bytes The array to read from.
	 * @param startIndex The index to begin reading from.
	 * @return A long constructed from eight consecutive bytes in the array.
	 * @throws ArrayIndexOutOfBoundsException if startIndex > bytes.length - 8.
	 */
	public long bytesToLong(byte[] bytes, int startIndex) {
		return	((long)(bytes[startIndex++] & 0xFF) << 56) |
				((long)(bytes[startIndex++] & 0xFF) << 48) |
				((long)(bytes[startIndex++] & 0xFF) << 40) |
				((long)(bytes[startIndex++] & 0xFF) << 32) |
				((long)(bytes[startIndex++] & 0xFF) << 24) |
				((long)(bytes[startIndex++] & 0xFF) << 16) |
				((long)(bytes[startIndex++] & 0xFF) << 8) |
				((long)bytes[startIndex++] & 0xFF);
	}
}