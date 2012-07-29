import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

// server側の入力コマンドを実行します
public class ServerCommand extends Thread {
    private Set<ChatServerWorker> workers;
    private HashMap<String, ChatServerWorker> connectList;
    enum Command {
        LIST, KICK
            }
    public ServerCommand(Set<ChatServerWorker> workers){
        this.workers = workers;
        connectList = new HashMap<String, ChatServerWorker>();
    }
    public void run(){
        while(true){
            String input = null;
                // C-d がおされたときに終了サせる
            try{
                input = new Scanner(System.in).nextLine();
            }catch(NoSuchElementException e){
                System.exit(0);
            }
            // 何も入力せずサヴァ？(enter)、を押すと使い方を表示
            if(input.equals("")){
                System.out.print("command: list, kick\nlist:show connected user\nkick [USERID]... : reject connected user\n");
                continue;
            }
            // 入力したコマンドを保持する変数
            LinkedList<String> command  = new LinkedList<String>();
            for(String s : input.split("[\\s+]+")){
                command.add(s);
            }
            // IDとスレッドを関連づけする
            for(ChatServerWorker worker : workers){
                connectList.put(worker.getWorkerId(), worker);
            }
            try{
                // 入力されたコマンドを判別する
                switch(Command.valueOf(command.remove().toUpperCase())){
                case LIST:
                    if(workers.isEmpty()){
                        System.out.println("Not User Connected");
                    }else{
                        for(ChatServerWorker worker : workers){
                            System.out.println(worker.getConnect());
                        }
                    }
                    break;
                case KICK:
                    if(command.isEmpty()){
                        System.out.println("usage: kick ID1, ID2, ...");
                    }else{
                        String errID = null;
                        try{
                            for(String id : command){
                                ChatServerWorker worker = connectList.remove(id);
                                errID = id;
                                worker.send("サーバーから接続拒否ですm9(^Д^)ﾌﾟｷﾞｬｰ");
                                worker.send("exit");
                                ChatServer.remove(worker);
                            }
                        }catch(NullPointerException e){
                            System.out.print("ID:");
                            System.out.print(errID);
                            System.out.println(" is unknown.");
                        }
                    }
                    break;
                }
            }catch(IllegalArgumentException e){
                System.out.println("this command not found.\ncommand:list, kick");
            }catch(NoSuchElementException e){}
        }
    }
}
