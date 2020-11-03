package com;

import com.studyportals.StudyPortalsData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static Connection connection;
    private static Statement statement;
    private static final String DB_URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    static 
    {
        try 
        {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            statement = connection.createStatement();
        } 
        catch (SQLException e) { e.printStackTrace(); }
    }

    public static synchronized Status insertStudyPortalsData(StudyPortalsData studyPortalsData) throws SQLException
    {
        if(!checkConnection())
            return Status.ERROR_CONNECT_TO_DB;

        Status result;
        if((result = isDataFull(studyPortalsData)) != Status.SUCCESSFUL)
            return result;

        /*
        * Get id attendance
        * Get id format
        * Get id country
        * Get id degree-type
        * Add data in subcategory_university table
        * Main request
        * Get main request's id
        * */

        String link = studyPortalsData.getLink().trim();
        if(isExistUniversityByLink(link))
            return Status.UNIVERSITY_ALREADY_IN_BD;

        String programName = prepareText(studyPortalsData.getProgramName().trim());
        String universityName = prepareText(studyPortalsData.getUniversityName().trim());
        String location = prepareText(studyPortalsData.getLocation().trim());
        int attendance = getAttendanceId(studyPortalsData.getAttendance());
        int format = getFormatId(studyPortalsData.getFormat());

        String duration = null;
        int durationMonth = 0;
        if(studyPortalsData.getDuration() != null)
        {
            duration = studyPortalsData.getDuration().trim();
            durationMonth = studyPortalsData.getDurationMonth();
        }

        String programWebsite = null;
        if(studyPortalsData.getProgramWebsite() != null)
            programWebsite = studyPortalsData.getProgramWebsite().trim();

        int country = getCountryId(studyPortalsData.getCountry());
        int degreeType = getDegreeTypeId(studyPortalsData.getDegreeType());

        if(attendance == 0)
            return Status.ATTENDANCE_ID_IS_NULL;
        if(format == 0)
            return Status.FORMAT_ID_IS_NULL;
        if(country == 0)
            return Status.COUNTRY_ID_IS_NULL;
        if(degreeType == 0)
            return Status.DEGREE_TYPE_IS_NULL;

        String deadline = null;
        String linkToImage = null;
        String tuition = null;
        int tuitionNumb = studyPortalsData.getTuitionNumb();

        if(studyPortalsData.getDeadline() != null)
            deadline = studyPortalsData.getDeadline().trim();
        if(studyPortalsData.getLinkToImage() != null)
            linkToImage = studyPortalsData.getLinkToImage().trim();
        if(studyPortalsData.getTuition() != null)
            tuition = studyPortalsData.getTuition().trim();

        String overview = prepareText(studyPortalsData.getOverview());
        String keyFacts = prepareText(studyPortalsData.getKeyFacts());
        String programmeOutline = prepareText(studyPortalsData.getProgrammeOutline());
        String admissionRequirements = prepareText(studyPortalsData.getAdmissionRequirements());
        String feesAndFunding = prepareText(studyPortalsData.getFeesAndFunding());

        StringBuilder request = new StringBuilder("INSERT INTO data (" +
                "link, program_name, attendance_id, format_id, duration, duration_month, program_website, " +
                "university_name, location, country_id, degree_type_id, deadline, link_to_image, tuition, " +
                "tuition_numb, overview, key_facts, programme_outline, admission_requirements, " +
                "fees_and_funding) VALUES (");
        request.append("'").append(link).append("', ");
        request.append("'").append(programName).append("', ");
        request.append(attendance).append(", ");
        request.append(format).append(", ");

        if(duration != null)
        {
            request.append("'").append(duration).append("', ");
            request.append(durationMonth).append(", ");
        }
        else
            request.append("null, null, ");

        if(programWebsite != null)
            request.append("'").append(programWebsite).append("', ");
        else
            request.append("null, ");

        request.append("'").append(universityName).append("', ");
        request.append("'").append(location).append("', ");
        request.append(country).append(", ");
        request.append(degreeType).append(", ");

        if(deadline != null)
            request.append("'").append(deadline).append("', ");
        else
            request.append("null, ");

        if(linkToImage != null)
            request.append("'").append(linkToImage).append("', ");
        else
            request.append("null, ");

        if(tuition != null) {
            request.append("'").append(tuition).append("', ");
            request.append(tuitionNumb).append(", ");
        }
        else
            request.append("null, null, ");


        request.append("'").append(overview).append("', ");
        request.append("'").append(keyFacts).append("', ");
        request.append("'").append(programmeOutline).append("', ");
        request.append("'").append(admissionRequirements).append("', ");
        request.append("'").append(feesAndFunding).append("'");
        request.append(")");

        List<Integer> subDisciplines = getSubDisciplinesId(studyPortalsData.getDisciplines());
        if(subDisciplines.size() == 0)
            return Status.NOT_HAVE_SUCH_DISCIPLINES_IN_DB;

        int universityId = getUniversityId(link);
        if(universityId == 0)
            return Status.UNIVERSITY_ID_IS_NULL;

        if(statement.executeUpdate(request.toString()) <= 0)
            return Status.UPDATE_RETURN_NEGATIVE_NUMB;

        for(int id : subDisciplines)
        {
            String requestSB = "INSERT INTO subcategory_university " +
                    "(subcategory_id, university_id) VALUES (" + id + ", " + universityId + ")";
            int r = statement.executeUpdate(requestSB);
            if(r <= 0)
                return Status.UPDATE_RETURN_NEGATIVE_NUMB;
        }

        return Status.SUCCESSFUL;
    }

    private static boolean checkConnection() throws SQLException
    {
        int connectionI = 0;
        while(!connection.isValid(0))
        {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            statement = connection.createStatement();
            if(connectionI > 10)
                return false;
            connectionI++;
        }
        return true;
    }

    public enum Status {
        SUCCESSFUL,
        NOT_HAVE_PROGRAM_NAME,
        NOT_HAVE_DISCIPLINES,
        NOT_HAVE_ATTENDANCE,
        NOT_HAVE_FORMAT,
        NOT_HAVE_UNIVERSITY_NAME,
        NOT_HAVE_LOCATION,
        NOT_HAVE_COUNTRY,
        NOT_HAVE_DEGREE_TYPE,
        NOT_HAVE_OVERVIEW,
        NOT_HAVE_KEY_FACTS,
        NOT_HAVE_PROGRAMME_OUTLINE,
        NOT_HAVE_ADMISSION_REQUIREMENTS,
        NOT_HAVE_FEES_AND_FUNDING,
        UNIVERSITY_ALREADY_IN_BD,
        ATTENDANCE_ID_IS_NULL,
        FORMAT_ID_IS_NULL,
        COUNTRY_ID_IS_NULL,
        DEGREE_TYPE_IS_NULL,
        UNIVERSITY_ID_IS_NULL,
        NOT_HAVE_SUCH_DISCIPLINES_IN_DB,
        UPDATE_RETURN_NEGATIVE_NUMB,
        ERROR_CONNECT_TO_DB;
    }

    public static synchronized boolean isExistUniversityByLink(String link) throws SQLException
    {
        String request = "SELECT id FROM data WHERE link=\"" + link + "\" LIMIT 1";
        ResultSet resultSet = statement.executeQuery(request);
        return resultSet.next();
    }

    /* *********************************************************************** */

    private static String prepareText(String text) { return text.replaceAll("\'", "\\\\\'"); }

    private static List<Integer> getSubDisciplinesId(List<String> subDisciplines) throws SQLException
    {
        List<Integer> result = new ArrayList<>();

        ResultSet resultSet;
        for(String subDiscipline : subDisciplines) {
            String request = "SELECT id FROM subcategories WHERE subcategory=\"" + subDiscipline + "\"";
            resultSet = statement.executeQuery(request);
            if(resultSet.next())
                result.add(resultSet.getInt(1));
        }
        return result;
    }

    private static int getUniversityId(String link) throws SQLException
    {
        String request = "SELECT id FROM data WHERE link=\"" + link + "\"";
        ResultSet resultSet = statement.executeQuery(request);
        if(resultSet.next())
            return resultSet.getInt(1);
        else
            return 0;
    }

    private static int getDegreeTypeId(String degreeType) throws SQLException
    {
        degreeType = degreeType.trim();
        String requestGetId = "SELECT id FROM degree_types WHERE degree_type=\"" + degreeType + "\"";
        String requestAddDegreeType = "INSERT INTO degree_types (degree_type) VALUES (\"" + degreeType + "\")";

        ResultSet resultSet = statement.executeQuery(requestGetId);
        if(!resultSet.next()) {
            statement.executeUpdate(requestAddDegreeType);

            resultSet = statement.executeQuery(requestGetId);
            resultSet.next();
        }
        return resultSet.getInt(1);
    }

    private static int getCountryId(String country) throws SQLException
    {
        country = country.trim();
        String requestGetId = "SELECT id FROM countries WHERE country=\"" + country + "\"";
        String requestAddCountry = "INSERT INTO countries (country) VALUES (\"" + country + "\")";

        ResultSet resultSet = statement.executeQuery(requestGetId);
        if(!resultSet.next()) {
            statement.executeUpdate(requestAddCountry);

            resultSet = statement.executeQuery(requestGetId);
            resultSet.next();
        }
        return resultSet.getInt(1);
    }

    private static int getFormatId(List<String> formats)
    {
        if(formats.size() == 1) {
            switch(formats.get(0)) {
                case "Full-time":
                    return 1;
                case "Part-time":
                    return 2;
                default:
                    return 0;
            }
        }
        else
            return 3;
    }

    private static int getAttendanceId(List<String> attendances)
    {
        if(attendances.size() == 1) {
            switch (attendances.get(0)) {
                case "On Campus":
                    return 1;
                case "Online":
                    return 2;
                case "Blended":
                    return 3;
                default:
                    return 0;
            }
        }
        else if(attendances.size() == 2) {
            if(attendances.contains("On Campus") && attendances.contains("Online"))
                return 4;
            else if(attendances.contains("On Campus") && attendances.contains("Blended"))
                return 5;
            else if(attendances.contains("Online") && attendances.contains("Blended"))
                return 6;
            else
                return 0;
        }
        else
            return 7;
    }

    /* *********************************************************************** */

    private static Status isDataFull(StudyPortalsData studyPortalsData)
    {
        if(studyPortalsData.getProgramName() == null) return Status.NOT_HAVE_PROGRAM_NAME;
        if(studyPortalsData.getDisciplines() == null || studyPortalsData.getDisciplines().size() == 0) return Status.NOT_HAVE_DISCIPLINES;
        if(studyPortalsData.getAttendance() == null || studyPortalsData.getAttendance().size() == 0) return Status.NOT_HAVE_ATTENDANCE;
        if(studyPortalsData.getFormat() == null || studyPortalsData.getFormat().size() == 0) return Status.NOT_HAVE_FORMAT;
        if(studyPortalsData.getUniversityName() == null) return Status.NOT_HAVE_UNIVERSITY_NAME;
        if(studyPortalsData.getLocation() == null) return Status.NOT_HAVE_LOCATION;
        if(studyPortalsData.getCountry() == null) return Status.NOT_HAVE_COUNTRY;
        if(studyPortalsData.getDegreeType() == null) return Status.NOT_HAVE_DEGREE_TYPE;
        if(studyPortalsData.getOverview() == null) return Status.NOT_HAVE_OVERVIEW;
        if(studyPortalsData.getKeyFacts() == null) return Status.NOT_HAVE_KEY_FACTS;
        if(studyPortalsData.getProgrammeOutline() == null) return Status.NOT_HAVE_PROGRAMME_OUTLINE;
        if(studyPortalsData.getAdmissionRequirements() == null) return Status.NOT_HAVE_ADMISSION_REQUIREMENTS;
        if(studyPortalsData.getFeesAndFunding() == null) return Status.NOT_HAVE_FEES_AND_FUNDING;
        return Status.SUCCESSFUL;
    }

    /* *********************************************************************** */

    public static int saveCategory(String category) throws SQLException
    {
        if(category == null || statement == null)
            return 0;
        category = category.trim();

        String requestGetId = "SELECT id FROM categories WHERE category=\"" + category + "\"";
        String requestInsert = "INSERT INTO categories (category) VALUES (\"" + category + "\")";

        ResultSet resultSet = statement.executeQuery(requestGetId);
        if(!resultSet.next()) {
            statement.executeUpdate(requestInsert);

            resultSet = statement.executeQuery(requestGetId);
            if(!resultSet.next())
                throw new SQLException("Does'n have category id");
        }
        return resultSet.getInt(1);
    }

    public static void saveSubcategory(int categoryID, String subcategory) throws SQLException
    {
        if(categoryID <= 0 || subcategory == null || statement == null)
            return;
        subcategory = subcategory.trim();

        String requestGetId = "SELECT id FROM subcategories WHERE subcategory=\"" + subcategory + "\"";
        String requestInsert = "INSERT INTO subcategories (subcategory, category_id) " +
                "VALUES (\"" + subcategory + "\", " + categoryID + ")";

        ResultSet resultSet = statement.executeQuery(requestGetId);
        if(!resultSet.next())
            statement.executeUpdate(requestInsert);
    }

    /* *********************************************************************** */

    private DataBase() {}
}
