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
    private String user_name, before_user_name;
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
    // 初回起動や名前が変更された時によばれる
    public void loginMessage(String name, boolean flag){
        try{
            StringBuilder sendMessage = new StringBuilder();
            if(flag){
                sendMessage.append(name);
                sendMessage.append(" さんがログインしました。");
            }else{
                sendMessage.append(name);
                sendMessage.append(" に ");
                sendMessage.append(before_user_name);
                sendMessage.append(" さんのユーザー名が変更されます");
            }
            out.println(sendMessage.toString());
            out.flush();
            // 前のuser_nameを記憶しておく
            before_user_name = name;
        }catch(NullPointerException e){
            System.out.println("ぬるぽ");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
