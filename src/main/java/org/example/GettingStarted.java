package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.dto.JobItem;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class GettingStarted {

    private static final Logger LOG = Logger.getLogger(GettingStarted.class.getName());

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.104.com.tw/jobs/main/");
        driver.getTitle();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));

        // 首頁關鍵字
        WebElement mainPageKeyword = driver.findElement(By.className("form-control"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        mainPageKeyword.sendKeys("Java 工程師");
        submitButton.click();

        // 出勤制度
        WebElement attendence = driver.findElement(By.cssSelector("li[data-value='1']"));
        attendence.click();
        By afterCNY = By.xpath("//span[@class='js-filter-select-list']/label[input[@value='0']]");
        WebElement afterFebruary = driver.findElement(afterCNY);
        afterFebruary.click();

        // 薪資條件
        WebElement salary = driver.findElement(By.cssSelector("li[data-value='2']"));
        salary.click();
        By atLeastFiftyThousands = By.xpath("//span[@class='js-salary-list']/label[input[@value='50000']]");
        WebElement fiftyThousand = driver.findElement(atLeastFiftyThousands);
        fiftyThousand.click();
        salary.click(); // 收合

        // 全職筆數
        By fulltimeLocator = By.xpath("//li[@data-value='1']/span[@class='js-txt']");
        WebElement fulltimeTotalAmount = driver.findElement(fulltimeLocator);
        String fullTimeAmt = fulltimeTotalAmount.getText().replaceAll("[^0-9]", "");
        LOG.info("How many items: " + fullTimeAmt);
        fulltimeTotalAmount.click();

        // 結果共幾頁
        By selectLocator = By.className("js-paging-select");
        WebElement selectElement = driver.findElement(selectLocator);
        Select select = new Select(selectElement);
        int numberOfOptions = select.getOptions().size();
        LOG.info("How many pages: " + numberOfOptions);

        // 拉到最下面
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < numberOfOptions; i++) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            Thread.sleep(700);
        }


        WebElement result = driver.findElement(By.id("js-job-list"));
        List<WebElement> jobLists = result.findElements(By.cssSelector("article.js-job-item"));
        List<JobItem> jobList = new ArrayList<>();
        for (WebElement jobItem : jobLists) {

            String jobName = jobItem.getAttribute("data-job-name");
            if (!jobName.toUpperCase().contains("JAVA") || !jobName.contains("工程師")) {
                continue;
            }
            String custName = jobItem.getAttribute("data-cust-name");
            String indcatDesc = jobItem.getAttribute("data-indcat-desc");

            jobList.add(JobItem.builder()
                    .dateUpdated("")
                    .jobListIntro("")
                    .jobItemInfo("")
                    .jobItemTag("")
                    .jobLink("")
                    .jobName(jobName)
                    .custName(custName)
                    .industryDesc(indcatDesc)
                    .build());
        }
        LOG.info("About to quit web driver-----------" + jobList.size());
        driver.quit();

        exportToCSV(jobList, "/Users/wysiwyz/Desktop/selenium.csv");
    }

    private static void exportToCSV(List<JobItem> jobList, String filePath) {
        LOG.info("START----------exportToCSV");
        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(JobItem.getHeaders()))
        ) {
            for (JobItem jobItem : jobList) {

                csvPrinter.printRecord(
                        getFieldValues(jobItem)
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            LOG.info("[IO Exception occured]" + e.getMessage());
        }
    }

    private static Object[] getFieldValues(JobItem jobItem) {
        Field[] fields = JobItem.class.getDeclaredFields();
        return Arrays.stream(fields)
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(jobItem);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Error accessing field " + field.getName(), e);
                    }
                })
                .toArray();
    }
}