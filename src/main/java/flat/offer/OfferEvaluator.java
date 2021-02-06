package flat.offer;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class OfferEvaluator {
	
	private static final Logger LOG = Logger.getLogger(OfferEvaluator.class);
	private final OfferFileStorageHandler offerFileStorage;
	private final MailSender mailSender;
	
	public OfferEvaluator(OfferFileStorageHandler offerFileStorage, MailSender mailSender) {
		this.offerFileStorage = offerFileStorage;
		this.mailSender = mailSender;
	}

	public void evaluateFoundOffers(Site site, Collection<Offer> offers) throws IOException, OfferFinderException {
		Collection<String> alreadyKnownOfferIds = offerFileStorage.getOfferIdsForSite(site);
		if(alreadyKnownOfferIds.isEmpty()) {
			LOG.info("Offers file for site: " + site.toString() + " not found. Creating new file.");
			offerFileStorage.createOrUpdateOffersFileForSite(site, offers);
		}
		else {
			Collection<Offer> newlyAddedOffers = findNewlyAddedOffers(offers, alreadyKnownOfferIds);
			if(!newlyAddedOffers.isEmpty()) {
				LOG.info(newlyAddedOffers.size() + " new offer(s) found, updating offers file.");
				offerFileStorage.createOrUpdateOffersFileForSite(site, offers);
				LOG.info("Sending email with offers...");
				mailSender.sendEmail(newlyAddedOffers);
			}
			else {
				LOG.info("No new offers found.");
			}
		}
	}
	
	private Collection<Offer> findNewlyAddedOffers(Collection<Offer> offers, Collection<String> alreadyKnownIds) {
		return offers.stream().filter(offer -> !alreadyKnownIds.contains(offer.getId())).collect(Collectors.toList());
	}

}
