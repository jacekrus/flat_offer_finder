package flat.offer;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

	private static final Logger LOG = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
		scheduledExecutor.scheduleAtFixedRate(Main::scanOffers, 0, 15, TimeUnit.MINUTES);
	}

	private static void scanOffers() {
		ChromeDriver webDriver = new ChromeDriver();
		try {
			List<OfferFinder> finders = List.of(new OtoDomOfferFinder(webDriver), new MorizonOfferFinder(webDriver));
			OfferEvaluator offerEvaluator = new OfferEvaluator(new OfferFileStorageHandler(), new MailSender());
			for (OfferFinder finder : finders) {
				LOG.info("Starting to look for new offers on " + finder.getSite().toString() + "...");
				Collection<Offer> offers = finder.getOffers();
				offerEvaluator.evaluateFoundOffers(finder.getSite(), offers);
			}
			LOG.info("Finishing execution...");
		}
		catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
		finally {
			webDriver.close();
		}
	}

}
