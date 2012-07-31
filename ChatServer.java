import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {
    public static final int LISTEN_PORT = 9000;
    private static Set<ChatServerWorker> workers;
    private static ServerSocket server;
    private static QueueManager queue;
    private static ServerCommand command;
    private static String user_name;
    /**
     * メインメソッド(スレッド)です。
     * 各種サーバー起動準備を整えて、ソケットをオープンし
     * 接続要求街になります
     */
    public static void main(String...args) {
        // TODO: worker スレッドを管理するコレクションを用意
        workers = new HashSet<ChatServerWorker>();
        // TODO: キューマネージャーを起動
        queue = new QueueManager(workers);
        queue.start();
        // コマンド受け付けスレッド起動
        command = new ServerCommand(workers);
        command.start();
        // TODO: ServerSocket をオープン
        try{
            server = new ServerSocket(LISTEN_PORT);
            while (true) {
                // TODO: 接続要求があったら、ワーカースレッドを生成
                Socket connect = server.accept();
                ChatServerWorker worker = new ChatServerWorker(connect, queue);
                synchronized (workers) {
                    // TODO: ワーカースレッドをコレクションに追加
                    workers.add(worker);
                }
                // TODO: ワーカースレッドを起動
                worker.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * ワーカースレッドをコレクションから削除するためのクラスメソッド
     *
     * @param ChatServerWorker
     * @return boolean 成功した場合にはtrue
     */
    public static boolean remove(ChatServerWorker worker) {
        synchronized (workers) {
            return workers.remove(worker);
        }
    }
    /**
     * キューを管理するネストクラスです。
     * Threadクラスを継承していますので、独立したスレッドとして動作します。
     */
    public static class QueueManager extends Thread {
        private BlockingQueue<String> queue;
        private Set<ChatServerWorker> workers;
        /**
         * メッセージキューを生成し、いつでもメッセージ送信要求を
         * 受け付けられる準備をします。
         *
         * ワーカースレッドを通してデータを送信するため、
         * Setコレクションへの参照が必要です。
         *
         * @param Set ワーカースレッドを管理しているコレクション
         */
        public QueueManager(Set<ChatServerWorker> workers) {
            queue = new LinkedBlockingQueue<String>();
            this.workers = workers;
        }
        /**
         * コレクション(キュー)のofferメソッドのラッパーです。
         *
         * @param CharSequence メッセージ
         */
        public boolean offer(CharSequence message) {
            synchronized (workers) {
                return queue.offer(message.toString());
            }
        }
        /**
         * QueueManager のメイン処理です
         */
        public void run() {
            while (true) {
                // TODO:キューからメッセージを1件取得
                String message = null;
                try{
                    message = queue.take();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                // TODO: 同期処理をしながら、各ワーカースレッドを通して
                //       メッセージを送信するループ処理をする。
                for(ChatServerWorker worker : workers){
                    worker.send(message);
                }
            }
        }
    }
}
