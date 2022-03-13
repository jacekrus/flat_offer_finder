package flat.offer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MorizonOfferFinder extends AbstractOfferFinder {
	
	public MorizonOfferFinder(WebDriver driver) {
		super(driver);
		elementsToOfferMapper = el -> {
			wait.until(ExpectedConditions.visibilityOf(el));
			actions.moveToElement(el);
			WebElement linkElement = el.findElement(By.xpath(".//a"));
			WebElement dateElement = el.findElement(By.xpath(".//span[contains(@class, 'single-result__category--date')]"));
			String id = getSite().toString() + "-"  + el.getAttribute("data-id");
			String url = linkElement.getAttribute("href");
			LocalDate date = parseDate(dateElement.getText(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			return new Offer(id, url, date);
		};
	}
	
	@Override
	public Site getSite() {
		return Site.MORIZON;
	}

	@Override
	protected Collection<Offer> searchOffers() {
		agreeToCookies();
		setupSearchCriteria();
		List<Offer> offers = new ArrayList<>();
		boolean lendiClosed = false;
		for(String city : cities) {
			setCity(city);
			search();
			if(!lendiClosed) {
				closeLendiOverlay();
				lendiClosed = true;
			}
			sortResultsByDate();
			offers.addAll(getFoundOffers());
			deleteCity();
		}
		return offers.stream().sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())).limit(20).collect(Collectors.toList());
	}

	private void setupSearchCriteria() {
		setFlats();
		setForSale();
		setPrice();
		openMoreOptions();
		setArea();
		setRoomsQuantity();
		setMarket();
	}
	
	private void agreeToCookies() {
		for(int i = 0; i < 5; i++) {
			try {
				wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("qc-cmp2-ui"))));
				break;
			}
			catch(NoSuchElementException e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {}
			}
		}
		WebElement agreeButton = driver.findElement(By.xpath("//button[contains(@class, 'cmp-intro_acceptAll')]"));
		wait.until(ExpectedConditions.elementToBeClickable(agreeButton));
		actions.moveToElement(agreeButton).click().perform();
	}

	private void setFlats() {
		WebElement typeDropbox = driver.findElement(By.xpath("//p[text()='ieszkania']"));
		wait.until(ExpectedConditions.elementToBeClickable(typeDropbox));
		actions.moveToElement(typeDropbox).click().perform();
		WebElement firstListElement = driver.findElement(By.xpath("//li[text()='ieszkania']"));
		wait.until(ExpectedConditions.elementToBeClickable(firstListElement));
		actions.moveToElement(firstListElement).click().perform();
	}

	private void setForSale() {
		WebElement saleDropbox = driver.findElement(By.xpath("//p[contains(text(), 'przeda')]"));
		actions.moveToElement(saleDropbox);
		saleDropbox.click();
		WebElement firstListElement = driver.findElement(By.xpath("//li[contains(text(), 'przeda')]"));
		firstListElement.click();
	}

	private void setPrice() {
		WebElement priceButton = driver.findElement(By.xpath("//p[text()='Dowolna']"));
		priceButton.click();
		WebElement priceInput = driver.findElement(By.id("ps_price_to"));
		priceInput.click();
		priceInput.sendKeys(MAX_PRICE);
	}
	
	private void openMoreOptions() {
		WebElement moreOptions = driver.findElement(By.xpath("//div[@class='moreOptions']"));
		moreOptions.click();
	}

	private void setArea() {
		WebElement minInput = driver.findElement(By.id("ps_living_area_from"));
		wait.until(ExpectedConditions.visibilityOf(minInput));
		actions.moveToElement(minInput);
		minInput.click();
		minInput.sendKeys(MIN_AREA);
		WebElement maxInput = driver.findElement(By.id("ps_living_area_to"));
		maxInput.click();
		maxInput.sendKeys(MAX_AREA);
	}

	private void setRoomsQuantity() {
		WebElement roomsNumberInput = driver.findElement(By.id("ps_number_of_rooms_from"));
		roomsNumberInput.click();
		roomsNumberInput.sendKeys(ROOMS);
	}

	private void setMarket() {
		WebElement secondaryMarketCheckbox = driver.findElement(By.xpath("//label[contains(text(), 'rny')]/preceding-sibling::div"));
		secondaryMarketCheckbox.click();
	}

	private void setCity(String cityName) {
		WebElement cityInput = driver.findElement(By.id("ps_location_text"));
		cityInput.click();
		cityInput.sendKeys(cityName);
	}
	
	private void deleteCity() {
		WebElement clearCity = driver.findElement(By.id("deleteLocation"));
		clearCity.click();
	}

	private void search() {
		WebElement searchButton = driver.findElement(By.name("commit"));
		searchButton.click();
		try {
			wait.until(ExpectedConditions.stalenessOf(searchButton));
		}
		catch(TimeoutException e) {}
	}

	private void closeLendiOverlay() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("lendiOverlay")));
		WebElement lendiOverlay = driver.findElement((By.className("lendiOverlay")));
		WebElement closeButton = lendiOverlay.findElement(By.xpath("./span[contains(@class, 'close')]"));
		wait.until(ExpectedConditions.elementToBeClickable(closeButton));
		closeButton.click();
	}

	private void sortResultsByDate() {
		WebElement sortButton = driver.findElement(By.xpath("//a[text()='Najnowsze']"));
		sortButton.click();
	}

	private Collection<Offer> getFoundOffers() {
		List<WebElement> foundOfferElements = driver.findElements(By.xpath("//section/div[contains(@class, 'row--property-list')]"));
		return foundOfferElements.stream()
				.filter(el -> el.getAttribute("data-id") != null)
				.map(elementsToOfferMapper)
				.limit(20)
				.collect(Collectors.toList());
	}
	
	private LocalDate parseDate(String dateText, DateTimeFormatter formatter) {
		LocalDate date;
		try {
			date = LocalDate.parse(dateText, formatter);
		}
		catch(Exception e) {
			date = LocalDate.now();
		}
		return date;
	}

}
