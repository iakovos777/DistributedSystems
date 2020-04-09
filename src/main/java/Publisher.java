import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

//TODO use registeredBrokers
public class Publisher extends Node{
    //private List<ArtistName> relatedArtists;
    //eite private HashMap<String, HashMap<String, Queue<MusicFile>>> listOfSongs;
    //key artistname->values hashmap with key song title and value a queue of chunks
    private HashMap<ArtistName, HashMap<String, Queue<MusicFile>>> listOfSongs;
    private HashMap<String[], Socket> registeredBrokers;
    private HashMap<String[], List<ArtistName>> listOfBrokersRelatedArtists;
    private List<String[]> listOfBrokers;
    private String keys;
    private String publisherIp;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerSocket serverSocket;


    public Publisher(){super();}

    public Publisher(String name, String ip, int port, String keys){
        super(name,ip,port);
        this.keys = keys;
        this.listOfSongs = new HashMap<>();
        this.registeredBrokers = new HashMap<>();
        this.listOfSongs = new HashMap<>();
        this.listOfBrokersRelatedArtists = new HashMap<>();
        this.listOfBrokers = new ArrayList<>();
        this.init();
        this.connect();
        this.waitRequest();
    }


    //read mp3 files and split them to chunks and also read brokers.json
    private void init(){
        try {
            InetAddress ia = InetAddress.getLocalHost();
            this.setPublisherIp(ia.getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.listOfBrokers = Utility.readBrokers();
        this.listOfSongs = Utility.readSongs(this.getKeys());
    }

    private void connect(){
        List<ArtistName> artistNames = new ArrayList<>(this.getListOfSongs().keySet());
        String[] publisher = new String[3];
        publisher[0] = this.getName();
        publisher[1] = this.getIp();
        publisher[2] = Integer.toString(this.getPort());
        //TODO maybe do it with thread?
        for(String[] broker : this.getListOfBrokers()) {
            super.connect(broker[1], Integer.parseInt(broker[2]));
            try {
                this.getOutputStream().writeObject(publisher);
                this.getOutputStream().flush();
                this.getOutputStream().writeObject(artistNames);
                this.getOutputStream().flush();
                String[] b = (String[])this.getInputStream().readObject();
                List<ArtistName> rA = (List<ArtistName>) this.getInputStream().readObject();
                this.getListOfBrokersRelatedArtists().put(b,rA);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                super.disconnect();
            }
        }
    }

    private void waitRequest() {
        try {
            serverSocket = new ServerSocket(this.getPort());
            while (true) {
                try {
                    Socket s = serverSocket.accept();
                    this.out = new ObjectOutputStream(s.getOutputStream());
                    this.in = new ObjectInputStream(s.getInputStream());
                    Thread job = new Thread(() ->
                    {
                        handleRequest(s,this.out,this.in);

                    });
                    job.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    //break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void handleRequest(Socket s, ObjectOutputStream output, ObjectInputStream input) {
        try {
            boolean songExist = false;
            ArtistName artist = (ArtistName) input.readObject();
            String song = (String) input.readObject();
            ArtistName index1 = null;
            String index2 = null;
            //TODO CHECK WHY INFINITIVE LOOP IF SONG IS WRONG
            for(ArtistName a : this.getListOfSongs().keySet()){
                if(a.getArtistName().equalsIgnoreCase(artist.getArtistName())){
                    System.out.println(1);
                    for(String str : this.getListOfSongs().get(a).keySet()){
                        if(str.equalsIgnoreCase(song)){
                            System.out.println(2);
                            index1 = a;
                            index2 = str;
                            songExist = true;
                            break;
                        }

                    }
                }
                if(songExist)
                    break;
            }
            //call push
            if(songExist){
                System.out.println(3);
                Queue<MusicFile> tem_queue = new LinkedList<>(this.getListOfSongs().get(index1).get(index2));
                /*Queue<Value> values_queue = new LinkedList<Value>();
                for(MusicFile m : tem_queue){
                    Value val = new Value(m);
                    values_queue.add(val);
                }*/
                do{
                    System.out.println(4);
                    Value v = push(index1,tem_queue);
                    output.writeObject(v);
                    artist =(ArtistName) input.readObject();
                    song = (String) input.readObject();
                }while(artist!=null && song!=null);
            }
            //notifyFailure
            else{
                notifyFailure(output);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Broker hashTopic(ArtistName artistName);
    //TODO THREAD SLEEP
    private Value push(ArtistName artistName,  Queue<MusicFile> queue){
        Value value = null;
        MusicFile mF = queue.poll();
        if(mF!=null) {
            value = new Value(mF);
            System.out.println(5);
        }
        return value;
    }

    public void notifyFailure(ObjectOutputStream output){
        Value v = new Value();
        v.setFailure(true);
        try{
            output.writeObject(v);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setListOfSongs(HashMap<ArtistName, HashMap<String, Queue<MusicFile>>> listOfSongs){
        this.listOfSongs = listOfSongs;
    }

    public HashMap<ArtistName, HashMap<String, Queue<MusicFile>>> getListOfSongs(){
        return this.listOfSongs;
    }

    public void setRegisteredBrokers(HashMap<String[], Socket> registeredBrokers){
        this.registeredBrokers = registeredBrokers;
    }

    public HashMap<String[], Socket> getRegisteredBrokers() {
        return this.registeredBrokers;
    }

    public void setListOfBrokers(List<String[]> listOfBrokers){this.listOfBrokers = listOfBrokers;}

    public List<String[]> getListOfBrokers() {
        return this.listOfBrokers;
    }

    public void setListOfBrokersRelatedArtists(HashMap<String[], List<ArtistName>> listOfBrokersRelatedArtists) {
        this.listOfBrokersRelatedArtists = listOfBrokersRelatedArtists;
    }

    public HashMap<String[], List<ArtistName>> getListOfBrokersRelatedArtists() {
        return this.listOfBrokersRelatedArtists;
    }

    public void setPublisherIp(String ip){
        this.publisherIp = ip;
    }

    public String getPublisherIp(){return this.publisherIp;}

    public String getKeys(){return this.keys;}

    public void setKeys(String keys){this.keys = keys;}

    //MAIN
    public static void main(String args[]){
        //may auto-generate name and it is not needed to be given by keyboard
        /*args[0]->name
          args[1]->IP of publisher
          args[2]->Port of publisher
          args[3]->publisher's keys that he is responsible for them
         */
        new Publisher(args[0],args[1],Integer.parseInt(args[2]),args[3]);
    }
}
