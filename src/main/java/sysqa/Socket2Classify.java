package sysqa;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class Socket2Classify {

    public static String getClassify(String question){

        String filePath = "/home/paladnix/resources/server.properties";
        Properties prop = new Properties();
        String classify = null;
        String host;
        int port;
        try {
            FileInputStream in = new FileInputStream(filePath);
            prop.load(in);
            port = Integer.parseInt(prop.getProperty("port"));
            host = prop.getProperty("host");
        } catch (Exception e){
            port = 9001;
            host = "127.0.0.1";
        }
        try {
            Socket socket = new Socket(host, port);

            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(out);
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            writer.println(question);
            writer.flush();
            socket.shutdownOutput();
            classify = br.readLine();
            br.close();
            in.close();
            writer.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classify;
    }
}