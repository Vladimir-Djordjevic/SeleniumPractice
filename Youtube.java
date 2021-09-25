package youtube;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Youtube {

	public static void main(String[] args) throws Exception {
		// DOMACI:
//		Pomocu jave i selenijuma
//		1) otici na sajt youtube
//		2) u search-u pronaci Rick Astley i pustiti pesmu Never gonna give you up
//		// voditi racuna da ako postoje reklame da ih preskocite
//		3) Nakon sto je pustena pesma, iz liste sa desne strane (watch next) pustiti drugi predlozen video

		System.setProperty("webdriver.chrome.driver", "driver-lib\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		WebDriverWait wdwait = new WebDriverWait(driver, 13);
		driver.navigate().to("https:youtube.com");

		WebElement searchField = wdwait.until(ExpectedConditions.elementToBeClickable(By.name("search_query")));
		searchField.clear();
		Thread.sleep(1000); // mora pokusao sam svaki wdwait ali preskoci nekad i obrise tekst
		searchField.sendKeys("Rick Astley Never gonna give you up");
		// cekaj da upise tekst onda enter
		wdwait.until(
				ExpectedConditions.textToBePresentInElementValue(searchField, "Rick Astley Never gonna give you up"));
		searchField.sendKeys(Keys.ENTER);

		// kada se pojavi video klikni
		WebElement clickSong = wdwait.until(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"video-title\"]/yt-formatted-string")));
		clickSong.click();

		// Naslov pesme da dokazemo da je to ta pesma
		String naslovPesme = wdwait
				.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath("//*[@id=\"container\"]/h1/yt-formatted-string")))
				.getText();
		System.out.println(naslovPesme);
		if (naslovPesme.equals("Rick Astley - Never Gonna Give You Up (Official Music Video)")) {
			System.out.println("Prava pesma!!!");
		} else {
			System.out.println("Neka losa pesma :( ");
		}
		// Assert dokaz
		Assert.assertEquals(naslovPesme, "Rick Astley - Never Gonna Give You Up (Official Music Video)");

		// Izbrisi sve cookies i refresh, tako da reklame prestanu
		deleteCookies(driver, wdwait);

		// Mora refresh koji ima gore i mora barem 1 sec da se saceka
		// da bi lista bila ispravna , jer je youtube sulud

		// Izaberi iz liste sa strane drugu pesmu sa indexom 1
		wdwait.until(ExpectedConditions
				.elementToBeClickable((By.cssSelector(".yt-simple-endpoint.inline-block.style-scope.ytd-thumbnail"))));
		List<WebElement> lista = driver
				.findElements(By.cssSelector(".yt-simple-endpoint.inline-block.style-scope.ytd-thumbnail"));
		wdwait.until(ExpectedConditions.elementToBeClickable(lista.get(1)));
		lista.get(1).click();

		// Izbrisi sve cookies i refresuj, tako reklame prestanu i za drugi video
		deleteCookies(driver, wdwait);

		// metoda za addskip button jer hoce nekad da se pojavi ovde
		addSkip(driver, wdwait);

		// novi naslov uzimamo da uporedimo sa starom pesmom
		// mora da se saceka par sekundi
		wdwait.until(ExpectedConditions
				.presenceOfElementLocated((By.xpath("//*[@id=\"container\"]/h1/yt-formatted-string"))));
		String naslovPesme2 = driver.findElement(By.xpath("//*[@id=\"container\"]/h1/yt-formatted-string")).getText();
		System.out.println(naslovPesme2);
		if (!naslovPesme2.equals(naslovPesme)) {
			System.out.println("Prebacio je pesmu!!!");
		} else {
			System.out.println("Ostala ista pesma :(");
		}
		// I assert dokaz da pesme su se prebacile
		Assert.assertNotEquals(naslovPesme2, naslovPesme);

	}

	// staticka funkcija za skipovanje addova adskip button
	// program puca ako nema addova zato mora try and catch
	public static void addSkip(WebDriver driver, WebDriverWait wdwait) {
		try {
			WebElement addSkip = wdwait.until(
					ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ytp-ad-skip-button.ytp-button")));
			addSkip.click();
			System.out.println("Skipovali smo addove !");

		} catch (Exception TimeoutException) {
			System.out.println("Nema addova !");
		}

	}

	// metoda za delete all adds i klikne na play dugme
	public static void deleteCookies(WebDriver driver, WebDriverWait wdwait) throws InterruptedException {
		driver.manage().deleteAllCookies();
		Thread.sleep(1000);
		driver.navigate().refresh();
		// ponovi klikni na pesmu
		WebElement bigPlayButton = wdwait
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ytp-large-play-button.ytp-button")));
		bigPlayButton.click();
	}

}
