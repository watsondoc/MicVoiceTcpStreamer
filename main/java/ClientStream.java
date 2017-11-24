import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.sound.sampled.*;

public class ClientStream{

    public ClientStream() throws IOException{
        isl.runListener();
    }

    private IncomingSoundListener isl = new IncomingSoundListener();
    AudioFormat format = getAudioFormat();
    InputStream is;
    Socket client;
    String serverName = "10.0.9.222";
    int port=3345;
    boolean inVoice = true;
    boolean outVoice = true;

    private AudioFormat getAudioFormat(){
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }

    class IncomingSoundListener {
        public void runListener(){
            try{
                System.out.println("Connecting to server:"+serverName+" Port:"+port);
                client = new Socket(serverName,port);
                System.out.println("Connected to: "+client.getRemoteSocketAddress());
                System.out.println("Listening for incoming audio.");
                System.out.println("Listening from mic.");
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class,format);
                TargetDataLine mic = (TargetDataLine) AudioSystem.getLine(micInfo);
                mic.open(format);
                System.out.println("Mic open.");
                byte tmpBuff[] = new byte[mic.getBufferSize()/5];
                mic.start();
                while(outVoice) {
                    System.out.println("Reading from mic.");
                    int count = mic.read(tmpBuff,0,tmpBuff.length);
                    if (count > 0){
                        System.out.println("Writing buffer to server.");
                        out.write(tmpBuff, 0, count);
                    }
                }
                mic.drain();
                mic.close();
                System.out.println("Stopped listening from mic.");

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public static void main(String [] args) throws IOException{
        new ClientStream();
    }
}
