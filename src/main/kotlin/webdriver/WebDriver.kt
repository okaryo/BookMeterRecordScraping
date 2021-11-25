package webdriver

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

class WebDriver {
    private val options: ChromeOptions
    private val driver: ChromeDriver

    init {
        System.setProperty("webdriver.chrome.driver", "./chromedriver")
        options = ChromeOptions().apply { addArguments("--headless") }
        driver = ChromeDriver(options)
    }

    fun getPageSource(url: String): String {
        driver.get(url)
        return driver.pageSource
    }

    fun close() {
        driver.close()
    }
}
