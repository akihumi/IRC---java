import java.net.Socket;
import java.io.IOException;
public class ChatSocket extends Socket{
    private String user_name;
    public ChatSocket(){
        super();
    }
    public ChatSocket(String address, int port) throws IOException {
        super(address, port);
    }
    public void setUserName(String name){
        user_name = name;
    }
    public String getUserName(){
        return user_name;
    }
}
