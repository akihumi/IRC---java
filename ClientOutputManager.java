import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JTextArea;

// ChatServerからのメッセージを監視しメッセージがあればテキストエリアに出力する
public class ClientOutputManager extends Thread {
    private BufferedReader in;
    private String message;
    private Socket connect;
    private JTextArea text_area;
    public ClientOutputManager(Socket connect, JTextArea text_area){
        this.connect = connect;
        this.text_area = text_area;
        try{
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }
        message = null;
    }
    public void run() {
        try{
            while(true){
                // サーバーからメッセージを取得
                message = in.readLine();
                // messageがnullなら終了
                if(message == null|| message.equals("exit")){
                    text_area.append("通信が途絶えました\n");
                    break;
                }
                text_area.append(message);
                text_area.append("\n");
                // テキストアリアを自動スクロールする
                text_area.setCaretPosition(text_area.getText().length());
            }
        }catch(SocketException e){
            System.out.println("接続を切断します");
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        GUIChatClient.loginButton.setEnabled(true);
    }
}
