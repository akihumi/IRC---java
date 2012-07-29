import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.ConnectException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GUIChatClient extends JFrame implements ActionListener{

    public static JButton loginButton;
    private JButton sendButton, renameButton;
    private static ChatClient chat;
    private static GUIChatClient gChat;
    private static LoginDialog dialog;
    private static Socket connect;
    private static String address, port;
    private static JTextArea text_area;
    private static JTextField text_field;
    // コンストラクタいろんな初期化
    public GUIChatClient(){
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        // 各コンポーネントの生成
        text_field = new JTextField(25);
        sendButton = new JButton("send");
        renameButton = new JButton("rename");
        loginButton = new JButton("login");
        text_area = new JTextArea(40, 40);
        JScrollPane scroll_area = new JScrollPane(text_area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        // 各コンポーネントにリスナーを登録
        text_field.addActionListener(this);
        sendButton.addActionListener(this);
        renameButton.addActionListener(this);
        loginButton.addActionListener(this);
        // カーソルをボタンの上においた時のヒントを表示
        sendButton.setToolTipText("メッセージを送信します");
        renameButton.setToolTipText("名前を変更できます");
        loginButton.setToolTipText("サーバーから追い出された時に再度ログインします");
        // 初期は押せない状態
        loginButton.setEnabled(false);
        // テキストエリアを編集できないようにする
        text_area.setEditable(false);
        // コンポーネントをpanelに追加
        panel.add(text_field);
        panel.add(sendButton);
        panel.add(renameButton);
        panel.add(loginButton);
        // コンポーネントをgChatに追加
        add(panel, "North");
        add(scroll_area, "Center");
    }
    // メインの処理
    public static void main(String...args){
        try{
            address = args[0];
            port = args[1];
            gChat = new GUIChatClient();
            gChat.setVisible(true);
            // gChat.setSize(500, 500);
            gChat.pack();
            // ダイアログを生成
            dialog = new LoginDialog(gChat);
            dialog.setVisible(true);
            connect = new Socket(address, Integer.parseInt(port));
            chat = new ChatClient(connect, text_field, text_area);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("use: java ChatClient [SERVERNAME] [PORT]");
        }catch(ConnectException e){
            text_area.append("サーバーに接続できませんでした。\n");
            text_area.append("アプリケーションを終了します。");
            // ちょっと待つ
            try{Thread.sleep(1500);}catch(InterruptedException err){}
            System.exit(0);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    // ボタンが押された時の処理
    public void actionPerformed(ActionEvent event){
        // 名前を変更、ダイヤログを呼び出す
        if(event.getActionCommand().equals("rename")){
            dialog.setVisible(true);
        }else if(event.getActionCommand().equals("login")){
            // サーバーからはじかれた後に再ログイン
            try{
                connect = new Socket(address, Integer.parseInt(port));
            }catch(IOException e){e.printStackTrace();}
            chat = new ChatClient(connect, text_field, text_area);
            loginButton.setEnabled(false);
        }
        if(text_field.getText().equals("")){
            // 文字入力欄になにも入ってなかったらなにもしない
        }else{
            chat.setName(gChat.getTitle());
            chat.sendLineMessage();
        }
    }
    //ダイアログクラスを継承した ユーザーネームを入力する内部クラス
    static class LoginDialog extends JDialog implements ActionListener{
        JTextField input_name;
        JFrame owner;
        LoginDialog(JFrame owner){
            super(owner, "ユーザー名を入力して下さい", true);
            this.owner = owner;
            this.setResizable(false);
            this.setAlwaysOnTop(true);
            setLayout(new FlowLayout());
            input_name = new JTextField(25);
            JButton ok = new JButton("ok");
            // リスナーを登録
            input_name.addActionListener(this);
            ok.addActionListener(this);
            // ダイアログにコンポーネントを追加
            add(input_name);
            add(ok);
            this.pack();
        }
        public void actionPerformed(ActionEvent e){
            String name = input_name.getText();
            if(name.equals("")){
                name = "名無しの名無さん";
            }
            owner.setTitle(name);
            setVisible(false);
        }
    }
}

