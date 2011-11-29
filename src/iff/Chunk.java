package iff;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.EOFException;

public class Chunk {
    protected DataInputStream stream;
    private String chunkID;
    private int length;
    private int offset;
    
    public Chunk(InputStream f) throws IOException, EOFException {
        this.stream = new DataInputStream(f);
        byte[] buffer = new byte[4];
        this.stream.readFully(buffer);
        this.chunkID = new String(buffer);
        this.length = this.stream.readInt();
        this.offset = 0;
    }
    
    public Chunk(Chunk chunk) throws IOException, EOFException {
        this(chunk.stream);
    }
    
    public String getName() {
        return this.chunkID;
    }
    
    public int getSize() {
        return this.length;
    }
    
    public void close() throws IOException {
        this.skip();
    }
    
    public short[] read() throws IOException {
        return this.read(this.length - this.offset);
    }
    
    public short[] read(int size) throws IOException {
        if(this.offset + size > this.length) {
            throw new EOFException();
        }
        short[] buffer = new short[size];
        for(int i = 0; i < buffer.length; ++i) {
            buffer[i] = (short)this.stream.readUnsignedByte();
        }
        this.offset += size;
        return buffer;
    }
    
    public String readString(int size) throws IOException {
        short[] ubytes = this.read(size);
        byte[] bytes = new byte[ubytes.length];
        for(int i = 0; i < ubytes.length; ++i) bytes[i] = (byte)ubytes[i];
        return new String(bytes);
    }
    
    public String readString() throws IOException {
        return this.readString(this.length - this.offset);
    }
    
    public int readUnsignedShort() throws IOException {
        short[] bytes = this.read(2);
        return (bytes[0] << 8) | bytes[1];
    }
    
    public short readUnsignedByte() throws IOException {
        return this.read(1)[0];
    }
    
    public int[] readUnsignedShorts(int size) throws IOException {
        short[] bytes = this.read(size * 2);
        int[] shorts = new int[size];
        for(int i = 0; i < size; ++i) {
            shorts[i] = (bytes[i*2] << 8) | bytes[i*2+1];
        }
        return shorts;
    }
    
    public void skip() throws IOException {
        this.seek(0, 2);
        if(this.offset % 2 == 1) {
            this.stream.skipBytes(1);
        }
    }
    
    public int tell() {
        return this.offset;
    }
    
    public void seek(int pos) throws IOException {
        this.seek(pos, 0);
    }
    
    public void seek(int pos, int whence) throws IOException {
        if(whence == 0) {
            pos -= this.offset;
        } else if(whence == 2) {
            pos = this.length - pos - this.offset;
        }
        if(pos < 0) {
            throw new IOException("Can't seek backwards");
        }
        this.stream.skipBytes(pos);
        this.offset += pos;
    }
}
