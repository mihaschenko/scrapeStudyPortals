package com.studyportals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrapeStudyPortals
{
    public static List<String> scrapeLinksToUniversityPage(RemoteWebDriver driver)
    {
        if(driver == null)
            throw new NullPointerException("RemoteWebDriver = null");

        List<WebElement> webElements = driver.findElementsByCssSelector("#StudySearchResultsStudies a.StudyCard");
        ArrayList<String> result = new ArrayList<>();

        if(webElements.size() > 0)
        {
            for(WebElement webElement : webElements)
            {
                String href = webElement.getAttribute("href");
                result.add(href);
            }

            return result;
        }
        return null;
    }

    /* ********************************************************************* */

    public static StudyPortalsData scrapeAllDataJSoup(RemoteWebDriver driver)
    {
        if(driver == null)
            throw new NullPointerException("RemoteWebDriver = null");

        StudyPortalsData result = new StudyPortalsData(driver.getCurrentUrl());
        Document html = Jsoup.parse(driver.getPageSource());

        String programWebsite = scrapeProgramWebsite(html);
        if(programWebsite != null) 
        {
            driver.navigate().to(programWebsite);
            result.setProgramWebsite(driver.getCurrentUrl());
        }

        String[] arr = scrapeProgramNameAndDegreeType(html).split(",");
        if(arr.length >= 2) 
        {
            if(arr.length == 2)
                result.setProgramName(arr[0].trim());
            else
                result.setProgramName(String.join(",", Arrays.copyOfRange(arr, 0, arr.length-1)).trim());
            result.setDegreeType(arr[arr.length-1].trim());
        }

        result.setDisciplines(scrapeDisciplines(html));

        Duration duration = scrapeDuration(html);
        if(duration != null)
        {
            result.setDuration(duration.duration);
            result.setDurationMonth(duration.months);
        }

        result.setAttendance(scrapeAttendance(html));
        result.setFormat(scrapeFormat(html));
        result.setUniversityName(scrapeUniversityName(html));
        result.setLocation(scrapeLocation(html));
        result.setDeadline(scrapeDeadline(html));
        result.setLinkToImage(scrapeLinkToImage(html));
        result.setTuition(scrapeTuition(html));

        result.setProgrammeOutline(replaceLinks(scrapeProgrammeOutline(html), result.getProgramWebsite()));
        result.setKeyFacts(replaceLinks(scrapeKeyFacts(html), result.getProgramWebsite()));
        result.setOverview(replaceLinks(scrapeOverview(html), result.getProgramWebsite()));
        result.setAdmissionRequirements(replaceLinks(scrapeAdmissionRequirements(html), result.getProgramWebsite()));
        result.setFeesAndFunding(replaceLinks(scrapeFeesAndFunding(html), result.getProgramWebsite()));

        return result;
    }

    public static StudyPortalsData scrapeAllDataJSoupMaster(RemoteWebDriver driver) {
        if(driver == null)
            throw new NullPointerException("RemoteWebDriver = null");

        StudyPortalsData result = new StudyPortalsData(driver.getCurrentUrl());
        Document html = Jsoup.parse(driver.getPageSource());

        String programWebsite = scrapeProgramWebsiteMaster(html);
        if(programWebsite != null)
        {
            driver.navigate().to(programWebsite);
            result.setProgramWebsite(driver.getCurrentUrl());
        }

        String[] arr = scrapeProgramNameAndDegreeTypeMaster(html).split(",");
        if(arr.length >= 2)
        {
            if(arr.length == 2)
                result.setProgramName(arr[0].trim());
            else
                result.setProgramName(String.join(",", Arrays.copyOfRange(arr, 0, arr.length-1)).trim());
            result.setDegreeType(arr[arr.length-1].trim());
        }

        result.setDisciplines(scrapeDisciplinesMaster(html));

        Duration duration = scrapeDurationMaster(html);
        if(duration != null)
        {
            result.setDuration(duration.duration);
            result.setDurationMonth(duration.months);
        }

        result.setAttendance(scrapeAttendanceMaster(html));
        result.setFormat(scrapeFormatMaster(html));
        result.setUniversityName(scrapeUniversityNameMaster(html));
        result.setLocation(scrapeLocationMaster(html));
        result.setDeadline(scrapeDeadlineMaster(html));
        result.setLinkToImage(scrapeLinkToImageMaster(html));
        result.setTuition(scrapeTuitionMaster(html));

        result.setProgrammeOutline(replaceLinks(scrapeProgrammeOutlineMaster(html), result.getProgramWebsite()));
        result.setKeyFacts(replaceLinks(scrapeKeyFactsMaster(html), result.getProgramWebsite()));
        result.setOverview(replaceLinks(scrapeOverviewMaster(html), result.getProgramWebsite()));
        result.setAdmissionRequirements(replaceLinks(scrapeAdmissionRequirementsMaster(html), result.getProgramWebsite()));
        result.setFeesAndFunding(replaceLinks(scrapeFeesAndFundingMaster(html), result.getProgramWebsite()));

        return result;
    }

    /* ********************************************************************* */

    public static void changeCurrencyToUSD(RemoteWebDriver driver)
    {
        if(driver == null)
            throw new NullPointerException("RemoteWebDriver = null");

        driver.navigate().to("https://www.bachelorsportal.com");

        List<WebElement> webElements = driver.findElementsByCssSelector("#FooterCurrency .DriverButton");
        if(webElements.size() > 0)
        {
            driver.executeScript("arguments[0].click();", webElements.get(0));

            webElements = driver.findElementsByCssSelector("#CurrencyDropDown");
            if(webElements.size() > 0)
            {
                Select select = new Select(webElements.get(0));

                select.selectByValue("USD");
            }
        }
    }

    public static void changeCurrencyToUSDMaster(RemoteWebDriver driver)
    {
        if(driver == null)
            throw new NullPointerException("RemoteWebDriver = null");

        driver.navigate().to("https://www.mastersportal.com");

        List<WebElement> webElements = driver.findElementsByCssSelector("#FooterCurrency .DriverButton");
        if(webElements.size() > 0)
        {
            driver.executeScript("arguments[0].click();", webElements.get(0));

            webElements = driver.findElementsByCssSelector("#CurrencyDropDown");
            if(webElements.size() > 0)
            {
                Select select = new Select(webElements.get(0));

                select.selectByValue("USD");
            }
        }
    }

    /* ********************************************************************* */

    public static List<String> scrapeAttendance(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("div.FactData");
        if(elements.size() > 0)
        {
            ArrayList<String> result = new ArrayList<>();
            for(Element element : elements)
            {
                String[] attendance = element.text().split(",");
                if(attendance.length > 0)
                {
                    for(String str : attendance)
                    {
                        str = str.trim();
                        if(str.equalsIgnoreCase("Online") || str.equalsIgnoreCase("Blended") || str.equalsIgnoreCase("On Campus"))
                            result.add(str);
                    }
                }
            }
            return result;
        }
        return null;
    }

    public static List<String> scrapeAttendanceMaster(Document html) {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("#StudyKeyFacts .FactItem div");
        if(elements.size() > 0)
        {
            ArrayList<String> result = new ArrayList<>();
            for(Element element : elements)
            {
                String[] attendance = element.text().split(",");
                if(attendance.length > 0)
                {
                    for(String str : attendance)
                    {
                        str = str.trim();
                        if(str.equalsIgnoreCase("Online") || str.equalsIgnoreCase("Blended") || str.equalsIgnoreCase("On Campus"))
                            result.add(str);
                    }
                }
            }
            return result;
        }
        return null;
    }

    /* ********************************************************************* */

    public static List<String> scrapeFormat(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("span.FactData");
        if(elements.size() > 0)
        {
            ArrayList<String> result = new ArrayList<>();
            for (Element element : elements)
            {
                String text = element.text().trim();
                if(text.length() < 10)
                    result.add(text);
            }

            return result;
        }
        return null;
    }

    public static List<String> scrapeFormatMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("ul.DurationList .FactListItem .FactListTitle");
        if(elements.size() > 0)
        {
            ArrayList<String> result = new ArrayList<>();
            for (Element element : elements)
                result.add(element.text().trim());

            return result;
        }
        return null;
    }

    /* ********************************************************************* */

    public static List<String> scrapeDisciplines(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select(".DisciplinesLinks li a");

        if(elements.size() > 0)
        {
            ArrayList<String> result = new ArrayList<>();
            for(Element webElement : elements)
                result.add(webElement.attr("title").trim());
            return result;
        }
        return null;
    }

    public static List<String> scrapeDisciplinesMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("article.Disciplines a");

        if(elements.size() > 0)
        {
            ArrayList<String> result = new ArrayList<>();
            for(Element webElement : elements)
                result.add(webElement.attr("title").trim());
            return result;
        }
        return null;
    }

    /* ********************************************************************* */

    public static String scrapeFeesAndFunding(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        StringBuilder result = new StringBuilder();

        html.select(".ExplanationContainer").remove();
        html.select(".ImportantNote").remove();

        String value = scrapeElementHtml("#TuitionFeeContainer", html);
        if(value != null)
            result.append(value).append("\n");
        value = scrapeElementHtml("#LivingCostContainer", html);
        if(value != null)
        {
            /* удаляем ненужные ссылки */
            value = value.replaceAll("<a .+?>", "");
            value = value.replaceAll("</a>", "");
            result.append(value).append("\n");
        }
        value = scrapeElementHtml("#FundingContainer", html);
        if(value != null)
            result.append(value);

        if(!result.toString().equals(""))
            return deleteDataAttributes(result.toString());
        return null;
    }

    public static String scrapeFeesAndFundingMaster(Document html) {
        return scrapeFeesAndFunding(html);
    }

    /* ********************************************************************* */

    public static String scrapeAdmissionRequirements(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        StringBuilder result = new StringBuilder();
        String value = scrapeElementHtml("#LanguageRequirementsSegmentedControl", html);

        if(value != null)
        {
            Pattern pattern = Pattern.compile("(data-[a-z_-]+)=\".+?\"");
            Matcher matcher = pattern.matcher(value);
            while(matcher.find())
            {
                if(!matcher.group(1).equals("data-segment-id"))
                    value = value.replaceAll(matcher.group(1) + "=\".+?\"", "");
            }
            value = value.replaceAll("style=\".+?\"", "");

            result.append(value).append("\n");
        }

        value = scrapeElementHtml("#AcademicRequirements", html);
        if(value != null)
            result.append(value).append("\n");

        value = deleteDataAttributes(scrapeElementHtml("#StudyRequirement", html));
        if(value != null)
            result.append(value).append("\n");

        if(!result.toString().equals(""))
            return result.toString();
        return null;
    }

    public static String scrapeAdmissionRequirementsMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return removeImg(scrapeElementHtml("#AdmissionRequirements", html));
    }

    /* ********************************************************************* */

    public static String scrapeTuition(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select(".QFTuition span[data-target]");
        if(elements.size() > 0)
        {
            for(Element element : elements)
            {
                boolean tuitionIsHidden = false;
                String[] classes = element.attr("class").split(" ");
                if(classes.length > 1)
                {
                    for(String c : classes)
                    {
                        if(c.equals("Hidden"))
                        {
                            tuitionIsHidden = true;
                            break;
                        }
                    }
                }

                if(!tuitionIsHidden)
                {
                    String value = element.text();
                    value = value.replaceFirst("Tuition Fee", "");
                    return value;
                }
            }
        }
        return null;
    }

    public static String scrapeTuitionMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select(".QuickFact .TuitionFeeContainer[data-target]");
        if(elements.size() > 0)
        {
            for(Element element : elements)
            {
                boolean tuitionIsHidden = false;
                String[] classes = element.attr("class").split(" ");
                if(classes.length > 1)
                {
                    for(String c : classes)
                    {
                        if(c.equals("Hidden"))
                        {
                            tuitionIsHidden = true;
                            break;
                        }
                    }
                }

                if(!tuitionIsHidden)
                {
                    String value = element.text();
                    value = value.replaceFirst("Tuition Fee", "");
                    return value;
                }
            }
        }
        return null;
    }

    /* ********************************************************************* */

    public static Duration scrapeDuration(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select(".QFDetails .js-duration");
        if(elements.size() > 0)
        {
            String duration = elements.get(0).text().trim();
            String months = elements.get(0).attr("data-duration");
            Duration result;
            if(months != null && !duration.equals("Unknown"))
                result = new Duration(duration, Integer.parseInt(months));
            else if(!duration.equals("Unknown"))
                result = new Duration(duration, 0);
            else
                return null;
            return result;
        }
        return null;
    }

    public static Duration scrapeDurationMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("div.QuickFact span.js-duration");
        if(elements.size() > 0)
        {
            String duration = elements.get(0).text();
            String months = elements.get(0).attr("data-duration");
            Duration result;
            if(months != null)
                result = new Duration(duration, Integer.parseInt(months));
            else
                result = new Duration(duration, 0);
            return result;
        }
        return null;
    }

    private static class Duration
    {
        public final String duration;
        public final int months;

        public Duration(String duration, int months)
        {
            this.duration = duration;
            this.months = months;
        }
    }

    /* ********************************************************************* */

    public static String scrapeUniversityName(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("section.OrganisationDetails .OrganisationTitle span");

        if(elements.size() > 0)
        {
            if(elements.size() > 1)
            {
                ArrayList<String> l = new ArrayList<>();
                for(Element element : elements)
                    l.add(element.text().trim());

                return String.join(", ", l);
            }
            else
                return elements.get(0).text();
        }
        return null;
    }

    public static String scrapeUniversityNameMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("span.NameLocation .Name");

        if(elements.size() > 0)
        {
            if(elements.size() > 1)
            {
                ArrayList<String> l = new ArrayList<>();
                for(Element element : elements)
                    l.add(element.text().trim());

                return String.join(", ", l);
            }
            else
                return elements.get(0).text();
        }
        return null;
    }

    /* ********************************************************************* */

    public static String scrapeLocation(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("#StudyOrganisationLocation span.OrganisationCity");

        if(elements.size() > 0)
        {
            if(elements.size() > 1)
            {
                ArrayList<String> l = new ArrayList<>();
                for(Element element : elements)
                    l.add(element.text().trim());

                return String.join(", ", l);
            }
            else
                return elements.get(0).text();
        }
        else
        {
            elements = html.select("#StudyOrganisationLocation span");

            for(Element element : elements)
            {
                if(element.text().trim().equals("Online"))
                    return "Online";
            }

            return null;
        }
    }

    public static String scrapeLocationMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        Elements elements = html.select("span.NameLocation .LocationItems a");

        if(elements.size() > 0)
        {
            if(elements.size() > 1)
            {
                ArrayList<String> l = new ArrayList<>();
                for(Element element : elements)
                    l.add(element.text().trim());

                return String.join(", ", l);
            }
            else
                return elements.get(0).text();
        }
        return null;
    }

    /* ********************************************************************* */

    public static String scrapeKeyFacts(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        html.select("#StudyKeyFacts button").remove();
        String htmlStr = deleteDataAttributes(scrapeElementHtml("#StudyKeyFacts", html));
        htmlStr = htmlStr.replaceAll("<a class=\"TextOnly LandingPageLink.+?<\\/a>", "");
        htmlStr = htmlStr.replaceAll("(?<=<a class=\"TextOnly\" href=\"\\/).+?(?=\")", "");
        return htmlStr;
    }

    public static String scrapeKeyFactsMaster(Document html) {
        return scrapeKeyFacts(html);
    }

    public static String scrapeProgrammeOutline(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return deleteDataAttributes(scrapeElementHtml("#StudyContents", html));
    }

    public static String scrapeProgrammeOutlineMaster(Document html) {
        return scrapeProgrammeOutline(html);
    }

    public static String scrapeOverview(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return deleteDataAttributes(scrapeElementHtml("#StudyDescription", html));
    }

    public static String scrapeOverviewMaster(Document html) {
        return scrapeOverview(html);
    }

    public static String scrapeProgramNameAndDegreeType(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementText("#StudyHeader h1", html);
    }

    public static String scrapeProgramNameAndDegreeTypeMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        String programName = scrapeElementText(".StudyTitle .StudyTitleText", html);
        StringBuilder degreeType = new StringBuilder();
        Elements elements = html.select("div.DegreeTags span.Tag");
        if(elements.size() > 0)
        {
            for(Element element : elements)
            {
                if(degreeType.toString().length() > 1)
                    degreeType.append("/");
                degreeType.append(element.text());
            }
        }
        else
            return null;
        return programName + ", " + degreeType.toString();
    }

    public static String scrapeLinkToImage(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementAttribute("#StudyLogo img", "src", html);
    }

    public static String scrapeLinkToImageMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementAttribute("a.LogoLink img", "src", html);
    }

    public static String scrapeProgramWebsite(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementAttribute("a[title=\"Programme website\"]", "href", html);
    }

    public static String scrapeProgramWebsiteMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementAttribute("span.OfficialWebsite a", "href", html);
    }

    public static String scrapeDeadline(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementText(".QFDetails time", html);
    }

    public static String scrapeDeadlineMaster(Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");

        return scrapeElementText("div.QuickFact .TimingContainer time", html);
    }

    /* ********************************************************************* */

    private static String scrapeElementText(String cssSelector, Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");
        if(cssSelector == null)
            return null;

        Elements elements = html.select(cssSelector);
        if(elements.size() > 0)
            return elements.get(0).text();
        return null;
    }

    private static String scrapeElementAttribute(String cssSelector, String attribute, Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");
        if(cssSelector == null || attribute == null)
            return null;

        Elements elements = html.select(cssSelector);
        if(elements.size() > 0)
            return elements.get(0).attr(attribute);
        return null;
    }

    private static String scrapeElementHtml(String cssSelector, Document html)
    {
        if(html == null)
            throw new NullPointerException("Document = null");
        if(cssSelector == null)
            return null;

        Elements elements = html.select(cssSelector);
        if(elements.size() > 0)
            return elements.get(0).html();
        return null;
    }

    /* ********************************************************************* */

    private static String deleteDataAttributes(String html)
    {
        if(html != null) {
            html = html.replaceAll("style=\".+?\"", "");
            return html.replaceAll("data-[a-z_-]+=\".+?\"", "");
        }
        return null;
    }

    private static String replaceLinks(String html, String link)
    {
        if(html != null) {
            if(link != null)
                html = html.replaceAll("href=\"https:\\/\\/sl\\.prtl\\.co.+?\"", "href=\"" + link + "\"");
            html = html.replaceAll("<a.+?href=\"https:\\/\\/www\\.bachelorsportal.+?<\\/a>", "");
            html = html.replaceAll("<a.+?href=\"https:\\/\\/www\\.mastersportal.+?<\\/a>", "");
            return html;
        }
        return null;
    }

    public static String removeImg(String html)
    {
        if(html != null)
            return html.replaceAll("<img .+?>", "");
        return null;
    }

    /* ********************************************************************* */

    private ScrapeStudyPortals() {}
}
