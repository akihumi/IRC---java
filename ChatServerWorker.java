import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatServerWorker extends Thread {
    private Socket connect;
    private PrintWriter out;
    private BufferedReader in;
    private Calendar cal;
    private ChatServer.QueueManager qm;
    private Long id;
    private InetAddress ip;
    private String user_name;

    /**
     * 接続済みSocketとQueueManagerを使用してインスタンスを生成します。
     *
     * @param Socket 接続済みソケット
     * @param ChatServer.QueueManager キューマネージャー
     * @throws IOException I/Oストリームの生成に失敗すると投げられます
     */
    public ChatServerWorker(Socket connect, ChatServer.QueueManager qm)
        throws IOException
    {
        this.qm = qm;
        this.connect = connect;
        
        // TODO: 各種フィールドの初期化もここでおこなっておくこと。
        out = new PrintWriter(new OutputStreamWriter(connect.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
    }
    /**
     * ワーカースレッドのメイン処理です。
     * メイン処理では、クライアントから送信されてくるであろう
     * 投稿内容を待ち続けます。
     *
     * 投稿内容が届くとキューマネージャーへその内容を登録します。
     */
    public void run() {
        // クライアントのipとidを取得
        id = Thread.currentThread().getId();
        ip = connect.getInetAddress();
        try {
            while (true) {
                // TODO: 投稿内容の読み取り(場合によってはブロック)
                String message = null;
                message = in.readLine();
                // ユーザーネームを抜き取る
                try{
                    user_name = message.split("[\\s+]+")[0];
                }catch(NullPointerException ne){
                    break;
                }
                // TODO: もし、内容が null または "exit" ならば
                //       そのタイミングで終了です。
                if(message == null || message.equals("exit") || user_name == null){
                    break;
                }
                // TODO: タイムスタンプを YYYY-MM-DD HH:MM:SS.ms 形式で生成
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                int ms = (cal.get(Calendar.MILLISECOND) / 10);
                StringBuffer str = new StringBuffer();
                SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.");
                str.append(time.format(date));
                if(ms < 10) str.append(0);
                str.append(ms);
                str.append(" ");
                str.append(message);
                // TODO: タイムスタンプ文字列と投稿内容を合成した文字列を
                //       キューマネージャーへ登録
                qm.offer(str);
                // //ダミー
                // if (true) {
                //     break;
                // }
            }
            // TODO: メインスレッドに自身の管理をやめるよう通知
            ChatServer.remove(this);
            // 各種リソースの開放
            // 行儀のいい人はこれも
            out.close();
            in.close();
            // 通常はSocketのcloseだけでOK
            connect.close();
        } catch (IOException ioe) {
            System.err.println("worker: " + ioe);
            ioe.printStackTrace();
            return;
        }
    }
    /**
     * クライアントにメッセージを渡します。
     *
     * @param String 送信メッセージ
     */
    public void send(String message) {
        out.println(message);
        out.flush();
    }
    // このスレッドのIDを文字列で返す
    public String getWorkerId(){
        return id.toString();
    }
    // スレッドのIDとipアドレスを文字列で返す
    public String getConnect(){
        StringBuilder str = new StringBuilder();
        str.append("UserName:");
        str.append(user_name);
        str.append(" IP:");
        str.append(ip);
        str.append(" ID:");
        str.append(getWorkerId());
        return str.toString();
    }
}
