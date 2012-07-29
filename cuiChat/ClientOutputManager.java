import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

// ChatServerからのメッセージを監視してメッセージがあればコンソールに出力する
public class ClientOutputManager extends Thread {
    private boolean flag;
    private String message;
    private BufferedReader in;
    private Socket connect;
    public ClientOutputManager(Socket connect){
        this.connect = connect;
        try{
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }
        message = null;
        flag = false;
    }
    public boolean isShutdown(){
        return flag;
    }
    public void run() {
        try{
            while(true){
                message = in.readLine();
                // messageがnullかexitなら終了
                if(message == null || message.equals("exit")){
                    flag = true;
                    System.out.println("通信が途絶えました");
                    break;
                }
                System.out.println(message);
            }
        }catch(SocketException e){
            System.out.println("接続を切断します");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
