package flat.offer;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractOfferFinder implements OfferFinder {

	public static final String MAX_PRICE = "230000";
	public static final String MIN_AREA = "35";
	public static final String MAX_AREA = "52";
	public static final String ROOMS = "2";

	protected final WebDriver driver;
	protected WebDriverWait wait;
	protected Actions actions;
	protected Site site;
	protected Function<WebElement, Offer> elementsToOfferMapper;
	protected String[] cities = { "Katowice", "Gliwice", "Tarnowskie Góry" };

	protected AbstractOfferFinder(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 10);
		actions = new Actions(driver);
	}

	@Override
	public Collection<Offer> getOffers() throws IOException {
		openPage(getSite().getUrl());
		maximizeWindow();
		return searchOffers();
	}

	protected abstract Collection<Offer> searchOffers();

	private void openPage(String url) {
		driver.get(url);
	}

	private void maximizeWindow() {
		driver.manage().window().maximize();
	}

}
