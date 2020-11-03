package com.studyportals;

import java.util.Arrays;
import java.util.List;

public class StudyPortalsData {
    private final String link;
    private String programName;
    private List<String> disciplines;
    private List<String> attendance;
    private List<String> format;
    private String duration;
    private int durationMonth;
    private String programWebsite;
    private String universityName;
    private String location;
    private String country;
    private String degreeType;
    private String deadline;
    private String linkToImage;
    private String tuition;
    private int tuitionNumb;
    private String overview;
    private String keyFacts;
    private String programmeOutline;
    private String admissionRequirements;
    private String feesAndFunding;

    public StudyPortalsData(String link) { this.link = link; }

    public String getLink() { return link; }
    public String getProgramName() { return programName; }
    public List<String> getDisciplines() { return disciplines; }
    public List<String> getAttendance() { return attendance; }
    public List<String> getFormat() { return format; }
    public String getDuration() { return duration; }
    public int getDurationMonth() { return durationMonth; }
    public String getProgramWebsite() { return programWebsite; }
    public String getUniversityName() { return universityName; }
    public String getLocation() { return location; }
    public String getCountry() { return country; }
    public String getDegreeType() { return degreeType; }
    public String getDeadline() { return deadline; }
    public String getLinkToImage() { return linkToImage; }
    public String getTuition() { return tuition; }
    public int getTuitionNumb() { return tuitionNumb; }
    public String getOverview() { return overview; }
    public String getKeyFacts() { return keyFacts; }
    public String getProgrammeOutline() { return programmeOutline; }
    public String getAdmissionRequirements() { return admissionRequirements; }
    public String getFeesAndFunding() { return feesAndFunding; }

    public void setProgramName(String programName) { this.programName = programName; }
    public void setDisciplines(List<String> disciplines) { this.disciplines = disciplines; }
    public void setAttendance(List<String> attendance) { this.attendance = attendance; }
    public void setFormat(List<String> format) { this.format = format; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setDurationMonth(int durationMonth) { this.durationMonth = durationMonth; }
    public void setProgramWebsite(String programWebsite) { this.programWebsite = programWebsite; }
    public void setUniversityName(String universityName) { this.universityName = universityName; }
    public void setLocation(String location) {
        this.location = location;
        if(location != null) {
            String[] address = location.split(",");
            if(address.length > 0)
                this.country = address[address.length-1].trim();
        }
    }
    public void setDegreeType(String degreeType) { this.degreeType = degreeType; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setLinkToImage(String linkToImage) { this.linkToImage = linkToImage; }
    public void setTuition(String tuition) {
        this.tuition = tuition;
        if(tuition != null) {
            tuition = tuition.replaceAll("[\\D]+", "");
            if(tuition.length() > 0)
                this.tuitionNumb = Integer.parseInt(tuition);
        }
    }
    public void setOverview(String overview) { this.overview = overview; }
    public void setKeyFacts(String keyFacts) { this.keyFacts = keyFacts; }
    public void setProgrammeOutline(String programmeOutline) { this.programmeOutline = programmeOutline; }
    public void setAdmissionRequirements(String admissionRequirements) { this.admissionRequirements = admissionRequirements; }
    public void setFeesAndFunding(String feesAndFunding) { this.feesAndFunding = feesAndFunding; }

    @Override
    public String toString() {
        return "<b>LINK:</b> " + link + "<br>" +
                "<b>PROGRAM NAME:</b> " + programName + "<br>" +
                "<b>DISCIPLINES:</b> " + Arrays.toString(disciplines.toArray()) + "<br>" +
                "<b>ATTENDANCE:</b> " + Arrays.toString(attendance.toArray()) + "<br>" +
                "<b>FORMAT:</b> " + Arrays.toString(format.toArray()) + "<br>" +
                "<b>DURATION:</b> " + duration + " (" + durationMonth + " months)<br>" +
                "<b>PROGRAM WEBSITE:</b> " + programWebsite + "<br>" +
                "<b>UNIVERSITY NAME:</b> " + universityName + "<br>" +
                "<b>LOCATION:</b> " + location + " (country: " + country + ")<br>" +
                "<b>DEGREE TYPE:</b> " + degreeType + "<br>" +
                "<b>DEADLINE:</b> " + deadline + "<br>" +
                "<b>LINK TO IMAGE:</b> " + linkToImage + "<br>" +
                "<b>TUITION:</b> " + tuition + " (" + tuitionNumb + ")<br>" +
                "============================================================ " +
                "<b>OVERVIEW:</b> " + overview + "<br>" +
                "============================================================ " +
                "<b>KEY FACTS:</b> " + keyFacts + "<br>" +
                "============================================================ " +
                "<b>PROGRAMME OUTLINE:</b> " + programmeOutline + "<br>" +
                "============================================================ " +
                "<b>ADMISSION REQUIREMENTS:</b> " + admissionRequirements + "<br>" +
                "============================================================ " +
                "<b>FEES AND FUNDING:</b> " + feesAndFunding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudyPortalsData that = (StudyPortalsData) o;

        if(hashCode() != that.hashCode()) return false;
        if (link != null ? !link.equals(that.link) : that.link != null) return false;
        if (programName != null ? !programName.equals(that.programName) : that.programName != null) return false;
        if (disciplines != null ? !disciplines.equals(that.disciplines) : that.disciplines != null) return false;
        if (attendance != null ? !attendance.equals(that.attendance) : that.attendance != null) return false;
        if (duration != null ? !duration.equals(that.duration) : that.duration != null) return false;
        if (programWebsite != null ? !programWebsite.equals(that.programWebsite) : that.programWebsite != null) return false;
        if (universityName != null ? !universityName.equals(that.universityName) : that.universityName != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (degreeType != null ? !degreeType.equals(that.degreeType) : that.degreeType != null) return false;
        if (deadline != null ? !deadline.equals(that.deadline) : that.deadline != null) return false;
        if (linkToImage != null ? !linkToImage.equals(that.linkToImage) : that.linkToImage != null) return false;
        if (tuition != null ? !tuition.equals(that.tuition) : that.tuition != null) return false;
        if (overview != null ? !overview.equals(that.overview) : that.overview != null) return false;
        if (keyFacts != null ? !keyFacts.equals(that.keyFacts) : that.keyFacts != null) return false;
        if (programmeOutline != null ? !programmeOutline.equals(that.programmeOutline) : that.programmeOutline != null) return false;
        if (admissionRequirements != null ? !admissionRequirements.equals(that.admissionRequirements) : that.admissionRequirements != null) return false;
        return feesAndFunding != null ? feesAndFunding.equals(that.feesAndFunding) : that.feesAndFunding == null;
    }

    @Override
    public int hashCode() {
        int result = link != null ? link.hashCode() : 0;
        result = 31 * result + (programName != null ? programName.hashCode() : 0);
        result = 31 * result + (disciplines != null ? disciplines.hashCode() : 0);
        result = 31 * result + (attendance != null ? attendance.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (programWebsite != null ? programWebsite.hashCode() : 0);
        result = 31 * result + (universityName != null ? universityName.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (degreeType != null ? degreeType.hashCode() : 0);
        result = 31 * result + (deadline != null ? deadline.hashCode() : 0);
        result = 31 * result + (linkToImage != null ? linkToImage.hashCode() : 0);
        result = 31 * result + (tuition != null ? tuition.hashCode() : 0);
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = 31 * result + (keyFacts != null ? keyFacts.hashCode() : 0);
        result = 31 * result + (programmeOutline != null ? programmeOutline.hashCode() : 0);
        result = 31 * result + (admissionRequirements != null ? admissionRequirements.hashCode() : 0);
        result = 31 * result + (feesAndFunding != null ? feesAndFunding.hashCode() : 0);
        return result;
    }
}