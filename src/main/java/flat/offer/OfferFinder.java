package flat.offer;

import java.io.IOException;
import java.util.Collection;

public interface OfferFinder {
	
	Collection<Offer> getOffers() throws IOException;
	
	Site getSite();
	
}
