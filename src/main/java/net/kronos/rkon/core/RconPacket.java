package net.kronos.rkon.core;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.MalformedPacketException;

public class RconPacket {

	public static final int SERVERDATA_EXECCOMMAND = 2;
	public static final int SERVERDATA_AUTH = 3;

	private int requestId;
	private int type;
	private byte[] payload;

	private RconPacket(int requestId, int type, byte[] payload) {
		this.requestId = requestId;
		this.type = type;
		this.payload = payload;
	}

	public int getRequestId() {
		return requestId;
	}

	public int getType() {
		return type;
	}

	public byte[] getPayload() {
		return payload;
	}


	protected static RconPacket send(Rcon rcon, int type, byte[] payload) throws IOException {
		try {
			RconPacket.write(rcon.getSocket().getOutputStream(), rcon.getRequestId(), type, payload);
		} catch (SocketException se) {
			// Close the socket if something happens
			rcon.getSocket().close();

			// Rethrow the exception
			throw se;
		}

		return RconPacket.read(rcon.getSocket().getInputStream());
	}


	private static void write(OutputStream out, int requestId, int type, byte[] payload) throws IOException {
		int bodyLength = RconPacket.getBodyLength(payload.length);
		int packetLength = RconPacket.getPacketLength(bodyLength);

		ByteBuffer buffer = ByteBuffer.allocate(packetLength);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		buffer.putInt(bodyLength);
		buffer.putInt(requestId);
		buffer.putInt(type);
		buffer.put(payload);

		// Null bytes terminators
		buffer.put((byte) 0);
		buffer.put((byte) 0);

		// Woosh!
		out.write(buffer.array());
		out.flush();
	}

	/**
	 *
	 * 读取传入的rcon数据包
	 *
	 *
	 *
	 * 要读取的InputStream中的@param
	 *
	 * @返回读取的RconPacket
	 *
	 *
	 *
	 * @抛出IOException
	 *
	 * @抛出格式错误的包异常
	 *
	 */
	private static RconPacket read(InputStream in) throws IOException {
		// Header is 3 4-bytes ints
		byte[] header = new byte[4 * 3];

		// Read the 3 ints
		in.read(header);

		try {
			// Use a bytebuffer in little endian to read the first 3 ints
			ByteBuffer buffer = ByteBuffer.wrap(header);
			buffer.order(ByteOrder.LITTLE_ENDIAN);

			int length = buffer.getInt();
			int requestId = buffer.getInt();
			int type = buffer.getInt();

			// Payload size can be computed now that we have its length
			byte[] payload = new byte[length - 4 - 4 - 2];

			DataInputStream dis = new DataInputStream(in);

			// Read the full payload
			dis.readFully(payload);

			// Read the null bytes
			dis.read(new byte[2]);

			return new RconPacket(requestId, type, payload);
		} catch (BufferUnderflowException | EOFException e) {
			throw new MalformedPacketException("Cannot read the whole packet");
		}
	}

	private static int getPacketLength(int bodyLength) {
		// 4 bytes for length + x bytes for body length
		return 4 + bodyLength;
	}

	private static int getBodyLength(int payloadLength) {
		// 4 bytes for requestId, 4 bytes for type, x bytes for payload, 2 bytes for two
		// null bytes
		return 4 + 4 + payloadLength + 2;
	}

}
