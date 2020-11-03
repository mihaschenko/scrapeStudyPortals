import com.DataBase;
import com.driver.ChromeDriverDecorator;
import com.studyportals.ScrapeStudyPortals;
import com.studyportals.StudyPortalsData;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StartScrape {
    static Logger log;
    static ThreadPoolExecutor executorService;
    static BufferedWriter finalStatus;
    static BufferedWriter failLinks;

    public static void main(String[] args) throws IOException
    {
        log = Logger.getLogger(StartScrape.class.getName());
        try(FileInputStream fileRead = new FileInputStream("properties\\log.properties"))
        { LogManager.getLogManager().readConfiguration(fileRead); }

        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        // path to chrome driver
        System.setProperty("webdriver.chrome.driver", "driver\\chrome.exe");

        try
        {
            finalStatus = new BufferedWriter(new FileWriter("final.txt", true));
            failLinks = new BufferedWriter(new FileWriter("failLinks.txt", true));
        }
        catch (IOException e) { log.log(Level.SEVERE, "", e); }

        // file with links to discipline pages
        List<String> links = Files.readAllLines(Paths.get("links.txt"));

        for(String l : links)
            executorService.execute(new ScrapeData(l));
    }

    private static void saveFinalPage(String subDiscipline, int page)
    {
        synchronized (finalStatus)
        {
            try
            {
                finalStatus.write(subDiscipline + " | " + page + "\n");
                finalStatus.flush();
            }
            catch (IOException e) {log.log(Level.SEVERE, subDiscipline + " | " + page + "\n", e);}
        }
    }

    private static int failLinksCount = 0;
    private static int failLinksIndex = 0;
    private static void saveFailLink(String link)
    {
        synchronized (failLinks)
        {
            try
            {
                failLinks.write(link + "\n");
                failLinksCount++;
            }
            catch (IOException e) {log.log(Level.SEVERE, "FAIL LINK: " + link + "\n", e);}

            if(failLinksCount >= 5000)
            {
                failLinksCount = 0;
                failLinksIndex++;
                try
                {
                    failLinks.close();
                    failLinks = new BufferedWriter(new FileWriter("failLinks" + failLinksIndex + ".txt", true));
                }
                catch (IOException e) { log.log(Level.SEVERE, "", e); }
            }
        }
    }

    public static ChromeDriverDecorator getNewDriver()
    {
        // set your path to chrome browser
        ChromeDriverDecorator driver = ChromeDriverDecorator.createChromeDriverDecorator("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"
                , false, null);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        return driver;
    }

    private static class ScrapeData implements Runnable
    {
        private final String subCategory;
        private int page;

        public ScrapeData(String subCategory) { this(subCategory, 0); }

        public ScrapeData(String subCategory, int page) 
        {
            this.subCategory = subCategory;
            this.page = page;
        }

        @Override
        public void run()
        {
            int fail = 0; // fail counter
            final int maxFail = 30;
            RemoteWebDriver driver = getNewDriver();
            StudyPortalsData data;
            DataBase.Status status;

            while (fail < maxFail) {
                try
                {
                    driver.navigate().to(subCategory + "&start=" + page);
                    ScrapeStudyPortals.changeCurrencyToUSD(driver);
                    break;
                }
                catch (Exception e) {log.log(Level.SEVERE, "", e); fail += 5;}
            }

            while(fail < maxFail) 
            {
                // get links to program page
                List<String> links = null;
                try
                {
                    driver.navigate().to(subCategory + "&start=" + page);
                    log.info("SCRAPE LINKS on page: \n\t" + subCategory + "&start=" + page);
                    links = ScrapeStudyPortals.scrapeLinksToUniversityPage(driver);
                }
                catch (Exception e) 
                {
                    log.log(Level.SEVERE, "", e);
                    fail += 3;
                }

               	// follow the links and get the data
                if(links != null && links.size() > 0) 
                {
                    for (String l : links) 
                    {
                        log.info("Scrape page: " + l);
                        try
                        {
                        	// check if the program is exists in database
                            if(DataBase.isExistUniversityByLink(l))
                            {
                                log.info(l + " %%% This page is exist in DB %%%");
                                continue;
                            }

                            driver.navigate().to(l);
                            data = ScrapeStudyPortals.scrapeAllDataJSoup(driver);

                            // save the data
                            status = DataBase.insertStudyPortalsData(data); 
                            if (status != DataBase.Status.SUCCESSFUL) 
                            {
                                if(status != DataBase.Status.UNIVERSITY_ALREADY_IN_BD) 
                                {
                                	log.warning(subCategory + "\n\t[" + status + "]");
                                    saveFailLink(l + " [" + status + "]");
                                	fail++;
                                }
                                else if(fail > 0)
                                    fail--;
                            } 
                            else if (fail > 0)
                                fail--;
                        } 
                        catch (Exception e) 
                        {
                            log.log(Level.WARNING, "", e);
                            saveFailLink(l + " [" + e.getMessage() + "]");
                            fail += 1;
                        }
                    }
                    page += 10;
                }
                else 
                {
                    log.warning(subCategory + "\n\t<<<<<<<<<< Links don't exist >>>>>>>>>>");
                    fail += 5;
                }
            }

            driver.quit();
            // write last page down
            saveFinalPage(subCategory, page);
        }
    }
}
