import java.io.Console;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// ChatServerと通信するためのチャットクライアント
public class ChatClient {
    private static ClientOutputManager timeLine;

    public static void main(String...args) {
        try{
            Socket connect = new Socket(args[0], Integer.parseInt(args[1]));
            Scanner stdIn = new Scanner(System.in);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(connect.getOutputStream()));
            Console cons = System.console();
            // ClientOutputManagerを起動
            timeLine = new ClientOutputManager(connect);
            timeLine.start();
            System.out.print("ユーザー名を入力してください:");
            String name = null;
            name = stdIn.nextLine();
            if(name.equals("")){
                name = "ななしのはなしさん";
            }
            // ループの処理。ユーザーからの入力をサーバーに送信する
            while(true){
                // tmp変数はユーザーからの入力をコンソールに出力しない
                // ための一時的な処理。
                char[] tmp = cons.readPassword();
                // サーバーが落ちたら接続を切る
                if(timeLine.isShutdown()){
                    break;
                }else{
                    try{
                        StringBuilder sendMessage = new StringBuilder(name);
                        String send = null;
                        System.out.print(">");
                        // System.outオブジェクトをロックしてユーザーが入力中は
                        // サーバーからのメッセージを出力しない
                        synchronized(System.out){
                            send = stdIn.nextLine();
                        }
                        if(send.equals("exit") || send == null){
                            break;
                        }
                        sendMessage.append(" >");
                        sendMessage.append(send);
                        out.println(sendMessage.toString());
                        out.flush();
                    }catch(NullPointerException e){
                        break;
                    }
                }
            }
            // 接続終了の後処理
            out.close();
            stdIn.close();
            connect.close();
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("use: java ChatClient [SERVERNAME] [PORT]");
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
