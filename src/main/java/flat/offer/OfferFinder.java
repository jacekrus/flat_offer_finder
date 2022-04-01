package flat.offer;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface OfferFinder {

	/**
	 * @return Collection of pairs: Name of city - List of offers
	 */
	Collection<Pair<String, List<Offer>>> getOffers() throws IOException;
	
	Site getSite();
	
}
