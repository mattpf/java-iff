package iff;
import java.util.ArrayList;

public class OutputChunk {
    private ArrayList<short[]> segments;
    private String chunkID;
    private int length;
    
    public OutputChunk(String chunkID) {
        this.chunkID = chunkID;
        this.length = 0;
        this.segments = new ArrayList<short[]>();
    }
    
    public OutputChunk() {
        this("FORM");
    }
    
    public void append(String str) {
        short[] bytes = new short[str.length()];
        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (short)str.charAt(i);
        }
        this.append(bytes);
    }
    
    public void append(short[] bytes) {
        this.length += bytes.length;
        this.segments.add(bytes);
    }
    
    public void append(OutputChunk chunk) {
        this.append(chunk.getShorts());
    }
    
    public void append(short unsignedByte) {
        short[] segment = { unsignedByte };
        this.append(segment);
    }
    
    public void append(int unsignedShort) {
        short[] segment = { (short)((unsignedShort >>> 8) & 0xFF), (short)(unsignedShort & 0xFF) };
        this.append(segment);
    }
    
    public void append(int[] unsignedShorts) {
        for(int ushort : unsignedShorts)
            this.append(ushort);
    }
    
    public short[] getShorts() {
        int output_length = this.length + 8;
        // Word-aligned for compatibility with the Motorola 68000 (yes, really)
        if(output_length % 2 == 1) {
            output_length++;
        }
        short[] ubytes = new short[output_length];
        ubytes[0] = (short)chunkID.charAt(0);
        ubytes[1] = (short)chunkID.charAt(1);
        ubytes[2] = (short)chunkID.charAt(2);
        ubytes[3] = (short)chunkID.charAt(3);
        ubytes[4] = (short)((this.length >>> 24) & 0xFF);
        ubytes[5] = (short)((this.length >>> 16) & 0xFF);
        ubytes[6] = (short)((this.length >>> 8) & 0xFF);
        ubytes[7] = (short)(this.length & 0xFF);
        int pointer = 8;
        for(short[] segment : this.segments) {
            System.arraycopy(segment, 0, ubytes, pointer, segment.length);
            pointer += segment.length;
        }
        return ubytes;
    }
    
    public byte[] getBytes() {
        short[] ubytes = this.getShorts();
        byte[] bytes = new byte[ubytes.length];
        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)(ubytes[i] & 0xFF);
        }
        
        return bytes;
    }
    
    public int getLength() {
        return this.length;
    }
}
