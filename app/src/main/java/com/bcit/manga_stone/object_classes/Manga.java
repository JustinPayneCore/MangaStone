package com.bcit.manga_stone.object_classes;

import java.io.Serializable;

//Defines a manga object so data can be passed around and stored in a database.
public class Manga implements Serializable {

    public String id;
    public String title;
    public String description;
    public String coverImage;
    public String status;
    public String highestRatedRankings;
    public String mostPopularRankings;
    public long averageScore;

    /**
     * Represents a manga object, has parameters for the id, title, description, coverImage,
     * publishing status, ratings rank, popularity rank, and average user score.
     * @param id - String
     * @param title - String
     * @param description - String
     * @param coverImage - String
     * @param status - String
     * @param highestRatedRankings - String
     * @param mostPopularRankings - String
     * @param averageScore - long
     */
    public Manga(String id, String title, String description, String coverImage, String status,
                 String highestRatedRankings, String mostPopularRankings, long averageScore){
        this.id = id;
        this.title = title;
        this.description = description;
        this.coverImage = coverImage;
        this.status = status;
        this.highestRatedRankings = highestRatedRankings;
        this.mostPopularRankings = mostPopularRankings;
        this.averageScore = averageScore;
    }

    /**
     * Gets the manga's Id
     * @return - String
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the manga's Title
     * @return - String
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the manga's Description
     * @return - String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the manga's CoverImage
     * @return - String
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * Gets the manga's Status
     * @return - String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Gets the manga's Rating rank
     * @return - String
     */
    public String getHighestRatedRankings() {
        return highestRatedRankings;
    }

    /**
     * Gets the manga's Popularity rank
     * @return - String
     */
    public String getMostPopularRankings() {
        return mostPopularRankings;
    }

    /**
     * Gets the manga's average user score
     * @return - long
     */
    public long getAverageScore() {
        return averageScore;
    }
}
