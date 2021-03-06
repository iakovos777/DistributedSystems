/*
    IAKOVOS EVDAIMON 3130059
    NIKOS KOULOS 3150079
    STEFANOS PAVLOPOULOS 3130168
    GIANNIS IPSILANTIS 3130215
 */

import java.io.*;
import java.net.InetAddress;
import java.net.*;
import java.util.*;

public class Node{
    private List<BrokerInfo> brokers;
    private String name, ip;
    private int port;
    private Socket requestSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Node(){
        this.brokers = new ArrayList<>();
    }

    //constructor which is used by Broker and Publisher
    public Node(String name, int port){
        this.name = name;
        this.port = port;
        this.brokers = new ArrayList<>();
    }

    //constructor which is used by Consumer
    public Node(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.brokers = new ArrayList<>();
    }

    /* method that is used by Broker in order to be informed about other brokers of system
       to inform his ip address and to initialize the list of brokers which contains BrokerInfo objects
     */
    public void init(int port){
        try {
            InetAddress ia = InetAddress.getLocalHost();
            this.setIp(ia.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //read json file with information of brokers
        List<String[]> list = Utility.readBrokers();
        //generates BrokerInfo objects and stores them in a list
        //Each BrokerInfo object concerns informations about a broker(name,ip,port,isAlive,hash and list of related artists)
        List<BrokerInfo> broks = new ArrayList<>();
        for(String[] l :list){
            List<String> brokerInfo = new ArrayList<>();
            List<ArtistName> artists = new ArrayList<>();
            for(int i=0; i<l.length;i++){
                brokerInfo.add(l[i]);
            }
            BrokerInfo broker = new BrokerInfo(brokerInfo,artists);
            broks.add(broker);
        }
        this.setBrokers(broks);

    }


    //method which is used mainly in consumer
    public void connect(String ip, int port){
        try
        {
            this.requestSocket = new Socket(ip, port);
            // obtaining input and out streams
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            this.out.writeObject(this.getClass().getSimpleName());
            this.out.flush();
        }catch(Exception e){
            e.printStackTrace();
            disconnect();
        }
    }

    //method to close socket, outputstreams and inputstreams
    public void disconnect(){
        System.out.println("Closing this connection : " + this.requestSocket);
        try {
            this.requestSocket.close();
            System.out.println("Connection closed");
        } catch (Exception e) {
            System.err.println("Failed to disconnect");
            e.printStackTrace();
        }
        try {
            this.out.close();
            this.in.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void disconnect(Socket s, ObjectInputStream in, ObjectOutputStream out){
        try{
            if(s!=null) {
                System.out.println("Closing this connection : " + s);
                s.close();
            }
            if(in!=null) {
                in.close();
            }
            if(out!=null) {
                out.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public List<BrokerInfo> getBrokers(){
        return this.brokers;
    }

    public void setBrokers(List<BrokerInfo> brokers){
        this.brokers = brokers;
    }

    public int getPort() {
        return this.port;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int p) {
        this.port = p;
    }

    public  void setName(String name){this.name=name;}

    public String getName(){return this.name;}

    public void setSocket(Socket requestSocket){
        this.requestSocket = requestSocket;
    }

    public void setOutputStream(ObjectOutputStream out){
        this.out = out;
    }

    public void setInputStream(ObjectInputStream in){
        this.in = in;
    }

    public Socket getSocket(){
        return this.requestSocket;
    }

    public ObjectOutputStream getOutputStream(){
        return this.out ;
    }

    public ObjectInputStream getInputStream(){
        return this.in;
    }

}
