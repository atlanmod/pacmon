package org.atlanmod;

import io.mappedbus.MappedBusMessage;
import io.mappedbus.MemoryMappedFile;

public class MappedBusFloat implements MappedBusMessage {

    private float value;

    public MappedBusFloat() {
    }

    public MappedBusFloat(float value) {
        this.value = value;
    }

    @Override
    public void write(MemoryMappedFile mem, long pos) {
        mem.putInt(pos, Float.floatToIntBits(value));
    }

    @Override
    public void read(MemoryMappedFile mem, long pos) {
        value = Float.intBitsToFloat(mem.getInt(pos));
    }

    @Override
    public int type() {
        return 0;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
