package flat.offer;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class OtoDomOfferFinder extends AbstractOfferFinder {
	
	public OtoDomOfferFinder(WebDriver driver) {
		super(driver);
		elementsToOfferMapper = el -> {
			wait.until(ExpectedConditions.visibilityOf(el));
			actions.moveToElement(el);
			String id = getSite().toString() + "-" + el.getAttribute("data-item-id");
			String url = el.getAttribute("data-url");
			boolean isPromo = el.getAttribute("data-featured-name").equals("promo_top_ads");
			return new Offer(id, url, isPromo);
		};
	}

	@Override
	public Site getSite() {
		return Site.OTODOM;
	}
	
	@Override
	protected Collection<Offer> searchOffers() {
		setupSearchCriteria();
		search();
		sortResultsByDate();
		return getFoundOffers();
	}

	private void setupSearchCriteria() {
		setFlats();
		setForSale();
		setPrice();
		setArea();
		setRoomsQunatity();
		setMarket();
		setCities();
	}

	private void setFlats() {
		WebElement typeDropbox = driver.findElement(By.id("downshift-1-toggle-button"));
		wait.until(ExpectedConditions.elementToBeClickable(typeDropbox));
		typeDropbox.click();
		WebElement firstListElement = driver.findElement(By.id("downshift-1-item-0"));
		firstListElement.click();
	}

	private void setForSale() {
		WebElement saleDropbox = driver.findElement(By.id("downshift-2-toggle-button"));
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

	private void setRoomsQunatity() {
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

	private void sortResultsByDate() {
		WebElement sortMenu = driver.findElement(By.id("sortMenu"));
		wait.until(ExpectedConditions.visibilityOf(sortMenu));
		sortMenu.click();
		WebElement sortCriteriaDate = driver.findElement(By.id("sort_date_adding_newest"));
		sortCriteriaDate.click();
	}

	private Collection<Offer> getFoundOffers() {
		wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.xpath("//article"))));
		List<WebElement> foundOffers = driver.findElements(By.xpath("//article"));
		return foundOffers.stream()
						  .map(elementsToOfferMapper)
						  .filter(Predicate.not(Offer::isPromo))
						  .limit(20)
						  .collect(Collectors.toList());
	}

}
