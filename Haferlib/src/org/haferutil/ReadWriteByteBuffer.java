//Holds data and such

package org.haferutil;

public class ReadWriteByteBuffer {

	private byte[][] bytes;
	private int readIndex;
	private int writeIndex;

	public ReadWriteByteBuffer() {
		this(10, 10);
	}

	public ReadWriteByteBuffer(int numBuffers, int bufferLength) {
		bytes = new byte[numBuffers][bufferLength];
		readIndex = 0;
		writeIndex = 1;
	}

	public byte[] getReadBuffer() {
		return bytes[readIndex];
	}

	public void doneReading() {
		int nextReadIndex = readIndex;
		while (nextReadIndex == readIndex || nextReadIndex == writeIndex) {
			nextReadIndex++;
			if (!(nextReadIndex < bytes.length))
				nextReadIndex = 0;
		}
		readIndex = nextReadIndex;
	}

	public byte[] getWriteBuffer() {
		return bytes[writeIndex];
	}

	public void doneWriting() {
		int nextWriteIndex = writeIndex;
		while (nextWriteIndex == readIndex || nextWriteIndex == writeIndex) {
			nextWriteIndex++;
			if (!(nextWriteIndex < bytes.length))
				nextWriteIndex = 0;
		}
		writeIndex = nextWriteIndex;
	}
}