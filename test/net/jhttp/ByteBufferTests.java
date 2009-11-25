package net.jhttp;

import org.testng.annotations.Test;

import java.nio.ByteBuffer;

public class ByteBufferTests {
    
    @Test(groups={"ignore"})
    public void test() throws Exception {
        byte[] line = "HTTP/1.1 200 OK\n".getBytes("ASCII");
        //            01234567890
        
        ByteBuffer b = ByteBuffer.wrap(line);
        
        //System.out.println("b.position() = " + b.position());
        //System.out.println("b.remaining() = " + b.remaining());
        //System.out.println("b.limit() = " + b.limit());
        //System.out.println("b.capacity() = " + b.capacity());
        
        int pos = 0;
        
        while(b.hasRemaining()) {
            byte c = b.get();
            //System.out.print("c = ");
            //System.out.println((char)c);
            //System.out.println("b.position() = " + b.position());
            
            if (c == 32 || c == 10) {
                ByteBuffer v = b.asReadOnlyBuffer();
                v.position(pos);
                v.limit(b.position() - 1);
                pos = b.position();
                System.out.println("v = '" + Util.ascii(v) + "'");
            }
        }
    }
        
}
