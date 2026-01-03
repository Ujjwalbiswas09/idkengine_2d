package engine.internal.util;

import android.os.ParcelFileDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class IOUtils {
    public static void transfer(InputStream inp, OutputStream out){
        try {

            byte[] tmp = new byte[1024];
            int i = 0;
            while ((i = inp.read(tmp)) > -1) {
                out.write(tmp,0,i);
            }
            inp.close();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static byte[] readAllBytes(InputStream ins){
        ByteArrayOutputStream bos= new ByteArrayOutputStream();
        byte[] array = new byte[1024];
        int i=0;
        try {
            while ((i = ins.read(array)) != -1) {
                bos.write(array,0,i);
            }
            ins.close();
            bos.flush();
            array = bos.toByteArray();
            bos.close();
            return array;
        }catch (Exception e){

        }
        return null;
    }
    public static class PipeStream{

        private final ParcelFileDescriptor myDescriptor;

        private PipeStream(ParcelFileDescriptor descriptor){
            myDescriptor = descriptor;
        }
        public InputStream getInputStreamA(){
            return new ParcelFileDescriptor.AutoCloseInputStream(myDescriptor);
        }
        public OutputStream getOutputStream(){
            return new ParcelFileDescriptor.AutoCloseOutputStream(myDescriptor);
        }

    }
    public static class Pipes{

        private PipeStream a;
        private PipeStream b;
        private ParcelFileDescriptor[] ar;
        private Pipes(ParcelFileDescriptor[] arr){
            a = new PipeStream(arr[0]);
            b = new PipeStream(arr[1]);
            ar =arr;
        }
        public PipeStream getStartPoint(){
            return a;
        }
        public PipeStream getEndPoint(){
            return b;
        }
        public void close(){
            for (ParcelFileDescriptor s : ar){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
