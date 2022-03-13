package flat.offer;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.thoughtworks.selenium.webdriven.commands.Close;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class OtoDomOfferFinder extends AbstractOfferFinder {
	
	public OtoDomOfferFinder(WebDriver driver) {
		super(driver);
		elementsToOfferMapper = el -> {
			wait.until(ExpectedConditions.visibilityOf(el));
			actions.moveToElement(el);
			String url = el.getAttribute("href");
			String id = url.substring(url.lastIndexOf("-"));
			return new Offer(id, url, false);
		};
	}

	@Override
	public Site getSite() {
		return Site.OTODOM;
	}
	
	@Override
	protected Collection<Offer> searchOffers() {
		agreeToCookies();
		setupSearchCriteria();
		search();
		discardInfoPopup();
		sortResultsByDate();
		return getFoundOffers();
	}

	private void agreeToCookies() {
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("onetrust-accept-btn-handler"))));
		WebElement agreeButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
		wait.until(ExpectedConditions.elementToBeClickable(agreeButton));
		agreeButton.click();
	}

	private void setupSearchCriteria() {
		setFlats();
		setForSale();
		setPrice();
		setArea();
		setRoomsQuantity();
		setMarket();
		setCities();
	}

	private void setFlats() {
		WebElement typeDropbox = driver.findElement(By.id("downshift-1-toggle-button"));
		wait.until(ExpectedConditions.elementToBeClickable(typeDropbox));
		actions.moveToElement(typeDropbox);
		typeDropbox.click();
		WebElement firstListElement = driver.findElement(By.id("downshift-1-item-0"));
		firstListElement.click();
	}

	private void setForSale() {
		WebElement saleDropbox = driver.findElement(By.id("downshift-2-toggle-button"));
		actions.moveToElement(saleDropbox);
		saleDropbox.click();
		WebElement firstListElement = driver.findElement(By.id("downshift-2-item-0"));
		firstListElement.click();
	}

	private void setPrice() {
		WebElement priceButton = driver.findElement(By.xpath("//button[@aria-label='Cena']"));
		priceButton.click();
		WebElement priceInput = driver.findElement(By.id("downshift-2-input"));
		priceInput.click();
		priceInput.sendKeys(MAX_PRICE);
	}

	private void setArea() {
		WebElement areaButton = driver.findElement(By.xpath("//button[@aria-label='Powierzchnia']"));
		areaButton.click();
		WebElement minInput = driver.findElement(By.id("downshift-3-input"));
		minInput.click();
		minInput.sendKeys(MIN_AREA);
		WebElement maxInput = driver.findElement(By.id("downshift-4-input"));
		maxInput.click();
		maxInput.sendKeys(MAX_AREA);
	}

	private void setRoomsQuantity() {
		WebElement roomsButton = driver.findElement(By.xpath("//button[@aria-label='Liczba pokoi']"));
		roomsButton.click();
		WebElement twoRoomsOption = driver.findElement(By.xpath("//p[text()='" + ROOMS + "']"));
		twoRoomsOption.click();
	}

	private void setMarket() {
		WebElement marketButton = driver.findElement(By.xpath("//button[@aria-label='Rynek']"));
		marketButton.click();
		WebElement secondaryMarketRadio = driver.findElement(By.xpath("//label[contains(text(), 'Wtórny')]"));
		secondaryMarketRadio.click();
	}

	private void setCities() {
		WebElement citiesDropBox = driver.findElement(By.id("downshift-0-label"));
		citiesDropBox.click();
		WebElement silesiaInput = driver.findElement(By.xpath("//div[text()='œl¹skie']"));
		wait.until(ExpectedConditions.visibilityOf(silesiaInput));
		actions.moveToElement(silesiaInput);
		silesiaInput.click();

		for(String city : cities) {
			WebElement cityCheckBox = driver.findElement(By.xpath("//div[contains(text(), '"+ city +"')]/preceding-sibling::span"));
			wait.until(ExpectedConditions.visibilityOf(cityCheckBox));
			actions.moveToElement(cityCheckBox);
			cityCheckBox.click();
			if(driver.findElements(By.xpath("//div[text()='œl¹skie']")).isEmpty()) {
				citiesDropBox.click();
			}
		}
	}

	private void search() {
		WebElement searchButton = driver.findElement(By.xpath("//button[@type='submit']"));
		searchButton.click();
	}


	private void discardInfoPopup() {
		WebElement closeButton = driver.findElement(By.cssSelector("[aria-label='Zamknij']"));
		closeButton.click();
	}

	private void sortResultsByDate() {
		WebElement sortCriteriaDate = driver.findElement(By.xpath("//button[contains(text(), 'najnowsze')]"));
		sortCriteriaDate.click();
	}

	private Collection<Offer> getFoundOffers() {
		List<WebElement> offersLists = driver.findElements(By.cssSelector("div[data-cy='search.listing']"));
		List<WebElement> foundOffers = offersLists.get(1).findElements(By.xpath("./ul/li/a"));
		return foundOffers.stream()
						  .map(elementsToOfferMapper)
						  .filter(Predicate.not(Offer::isPromo))
						  .limit(20)
						  .collect(Collectors.toList());
	}

}
