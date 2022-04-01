package flat.offer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

public class OfferEvaluator {
	
	private static final Logger LOG = Logger.getLogger(OfferEvaluator.class);
	private final OfferFileStorageHandler offerFileStorage;
	private final MailSender mailSender;
	
	public OfferEvaluator(OfferFileStorageHandler offerFileStorage, MailSender mailSender) {
		this.offerFileStorage = offerFileStorage;
		this.mailSender = mailSender;
	}

	public void evaluateFoundOffers(Site site, Collection<Pair<String, List<Offer>>> foundOffers) throws IOException, OfferFinderException {
		Collection<Offer> allNewOffers = new ArrayList<>();
		for(var offersCityPair : foundOffers) {
			String offersFileId = offersCityPair.getKey() + "-" + site;
			List<Offer> offers = offersCityPair.getValue();
			Collection<String> alreadyKnownOfferIds = offerFileStorage.getOfferIdsForFileId(offersFileId);

			if(alreadyKnownOfferIds.isEmpty()) {
				LOG.info("Offers file withId: " + offersFileId + " not found. Creating new file.");
				offerFileStorage.createOrUpdateOffersFileForId(offersFileId, offers);
			}
			else {
				Collection<Offer> newlyAddedOffers = findNewlyAddedOffers(offers, alreadyKnownOfferIds);
				if(!newlyAddedOffers.isEmpty()) {
					allNewOffers.addAll(newlyAddedOffers);
					LOG.info(newlyAddedOffers.size() + " new offer(s) found for " + offersFileId + " updating offers file.");
					offerFileStorage.createOrUpdateOffersFileForId(offersFileId, offers);
				}
				else {
					LOG.info("No new offers found for " + offersFileId + ".");
				}
			}
		}
		sendEmailIfNeeded(allNewOffers);
	}

	private void sendEmailIfNeeded(Collection<Offer> newOffers) throws OfferFinderException {
		if(!newOffers.isEmpty()) {
			LOG.info("Sending email with offers...");
			mailSender.sendEmail(newOffers);
		}
	}

	private Collection<Offer> findNewlyAddedOffers(Collection<Offer> offers, Collection<String> alreadyKnownIds) {
		return offers.stream().filter(offer -> !alreadyKnownIds.contains(offer.getId())).collect(Collectors.toList());
	}

}
