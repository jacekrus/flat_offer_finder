package flat.offer;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractOfferFinder implements OfferFinder {

	public static final String MAX_PRICE = "230000";
	public static final String MIN_AREA = "35";
	public static final String MAX_AREA = "55";
	public static final String ROOMS = "2";

	protected final WebDriver driver;
	protected WebDriverWait wait;
	protected Actions actions;
	protected Site site;
	protected Function<WebElement, Offer> elementsToOfferMapper;
	protected String[] cities = { "Radzionków", "Piekary Œl¹skie", "Bytom", "Tarnowskie Góry" };

	protected AbstractOfferFinder(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, 25);
		actions = new Actions(driver);
	}

	@Override
	public Collection<Pair<String, List<Offer>>> getOffers() {
		openPage(getSite().getUrl());
		maximizeWindow();
		return searchOffers();
	}

	protected abstract Collection<Pair<String, List<Offer>>> searchOffers();

	private void openPage(String url) {
		driver.get(url);
	}

	private void maximizeWindow() {
		driver.manage().window().maximize();
	}

}
