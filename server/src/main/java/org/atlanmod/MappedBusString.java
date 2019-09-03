package org.atlanmod;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

import java.nio.charset.Charset;

/**
 * Serializable Bus message
 */
public class MappedBusString implements MappedBusMessage {
    private byte[] string;
    private int length;

    /**
     *
     * @param length the maximum length of the string the message will contain
     */
    public MappedBusString(int length) {
        this.length = length;
        string = new byte[length];
    }

    /**
     *
     * @param string the string the message contains
     * @param length the max length of the string
     */
    public MappedBusString(String string, int length) {
        this.string = new byte[length];
        byte[] bytes = string.getBytes(Charset.defaultCharset());

        for (int i = 0; i < bytes.length; i++) {
            this.string[i] = bytes[i];
        }

        this.length = length;
    }

    @Override
    public void write(MemoryMappedFile mem, long pos) {
        for (int i = 0; i < length; i++) {
            mem.putByte(pos+i, string[i]);
        }
    }

    @Override
    public void read(MemoryMappedFile mem, long pos) {
        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = mem.getByte(pos + i);
        }
        string = bytes;
    }

    @Override
    public int type() {
        return 0;
    }

    /**
     * Returns a {@link String} contained in the MappedBus after parsing the corresponding byte array
     * @return a {@link String}
     */
    public String getString() {
        int firstZeroByteIndex = length;
        int i = 0;

        while (firstZeroByteIndex == length) {
            if (string[i] == 0) {
                firstZeroByteIndex = i;
            }
            i++;
        }
        return new String(string, 0, firstZeroByteIndex, Charset.defaultCharset());
    }
}
