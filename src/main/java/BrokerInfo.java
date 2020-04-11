import java.io.Serializable;
import java.util.List;

/* List<String> -> informations of each broker
       Position 0->name of broker
       Position 1->ip of broker
       Position 2->value of port of broker
       Position 3->value of boolean isAlive of broker
       After update method in Broker's class List<String> is updated with a new element in
       position 4-> value of hash of broker
     */

public class BrokerInfo implements Serializable {
    private List<String> broker;
    private List<ArtistName> relatedArtists;

    public BrokerInfo(){}

    public BrokerInfo(List<String> broker, List<ArtistName> relatedArtists){
        this.broker = broker;
        this.relatedArtists = relatedArtists;
    }

    public void setBrokerInfo(List<String> broker){this.broker = broker;}

    public List<String> getBrokerInfo(){return this.broker;}

    public void setRelatedArtists(List<ArtistName> relatedArtists) {
        this.relatedArtists = relatedArtists;
    }

    public List<ArtistName> getRelatedArtists() {
        return relatedArtists;
    }

}
