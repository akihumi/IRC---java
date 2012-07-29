import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// ChatServerと通信するためのチャットクライアント
public class ChatClient {
    private static ClientOutputManager timeLine;
    private String address, port;
    private JTextField text_field;
    private JTextArea text_area;
    private String user_name;
    private Socket connect;
    private PrintWriter out;

    public ChatClient(Socket connect, JTextField text_field, JTextArea text_area){
        this.text_field = text_field;
        this.text_area = text_area;
        this.connect = connect;
        // ClientOutputManagerを起動
        timeLine = new ClientOutputManager(connect, text_area);
        timeLine.start();
        try{
            out = new PrintWriter(new OutputStreamWriter(connect.getOutputStream()));
            user_name = "";
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void setName(String name){
        user_name = name;
    }
    // テキストフィールドの内容をサーバーに送信
    public void sendLineMessage(){
        try{
            StringBuilder sendMessage = new StringBuilder(user_name);
            sendMessage.append(" >");
            sendMessage.append(text_field.getText());
            text_field.setText("");
            out.println(sendMessage.toString());
            out.flush();
        }catch(NullPointerException e){
            System.out.println("ぬるぽ");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
