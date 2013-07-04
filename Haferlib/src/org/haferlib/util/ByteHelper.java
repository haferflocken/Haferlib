//Helps do network stuff

package org.haferlib.util;

public class ByteHelper {

	public ByteHelper() {
	}

	public byte bitsToByte(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
		return (byte)((((a)?1:0) << 7) + (((b)?1:0) << 6) + (((c)?1:0) << 5) + (((d)?1:0) << 4) + (((e)?1:0) << 3) + (((f)?1:0) << 2) + (((g)?1:0) << 1) + ((h)?1:0));
	}

	public byte bitsToByte(boolean[] bits) {
		return bitsToByte(bits[0], bits[1], bits[2], bits[3], bits[4], bits[5], bits[6], bits[7]);
	}

	public boolean[] byteToBits(byte b) {
		boolean[] bits = new boolean[8];
		byteIntoBits(b, bits);
		return bits;
	}

	public void byteIntoBits(byte b, boolean[] bits) {
		bits[0] = ((b & 128) == 128);
		bits[1] = ((b & 64) == 64);
		bits[2] = ((b & 32) == 32);
		bits[3] = ((b & 16) == 16);
		bits[4] = ((b & 8) == 8);
		bits[5] = ((b & 4) == 4);
		bits[6] = ((b & 2) == 2);
		bits[7] = ((b & 1) == 1);
	}

	public byte[] intIntoBytes(int num) {
		byte[] bytes = new byte[4];
		intIntoBytes(num, bytes, 0);
		return bytes;
	}

	public void intIntoBytes(int num, byte[] bytes, int startIndex) {
		//System.out.print(num + " into bytes: ");
		bytes[startIndex++] = (byte)((num >> 24) & 0xFF);
		//System.out.print(bytes[startIndex - 1]);
		//System.out.print(", ");
		bytes[startIndex++] = (byte)((num >> 16) & 0xFF);
		//System.out.print(bytes[startIndex - 1]);
		//System.out.print(", ");
		bytes[startIndex++] = (byte)((num >> 8) & 0xFF);
		//System.out.print(bytes[startIndex - 1]);
		//System.out.print(", ");
		bytes[startIndex++] = (byte)((num >> 0) & 0xFF);
		//System.out.println(bytes[startIndex - 1]);
	}

	public int bytesToInt(byte[] bytes, int startIndex) {
		return ((bytes[startIndex++] & 0xFF) << 24) | ((bytes[startIndex++] & 0xFF) << 16) | ((bytes[startIndex++] & 0xFF) << 8) | (bytes[startIndex++] & 0xFF);
	}

	public byte[] longIntoBytes(long num) {
		byte[] bytes = new byte[8];
		longIntoBytes(num, bytes, 0);
		return bytes;
	}

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