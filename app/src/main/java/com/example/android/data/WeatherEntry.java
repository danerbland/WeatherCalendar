package com.example.android.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.android.model.ChunkMain;
import com.example.android.model.ChunkWeather;
import com.example.android.model.ChunkWind;

@Entity(tableName="weather")
public class WeatherEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private long datelong;
    private ChunkMain chunkmain;
    private ChunkWeather chunkweather;
    private int clouds;
    private ChunkWind chunkwind;
    private String syspod;
    private String datetext;

    @Ignore
    public WeatherEntry(long datelong, ChunkMain chunkmain, ChunkWeather chunkweather, int clouds, ChunkWind chunkwind, String syspod, String datetext){
        this.datelong = datelong;
        this.chunkmain = chunkmain;
        this.chunkweather = chunkweather;
        this.clouds = clouds;
        this.chunkwind = chunkwind;
        this.syspod = syspod;
        this.datetext = datetext;
    }

    public WeatherEntry(int id, long datelong, ChunkMain chunkmain, ChunkWeather chunkweather, int clouds, ChunkWind chunkwind, String syspod, String datetext){
        this.id = id;
        this.datelong = datelong;
        this.chunkmain = chunkmain;
        this.chunkweather = chunkweather;
        this.clouds = clouds;
        this.chunkwind = chunkwind;
        this.syspod = syspod;
        this.datetext = datetext;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDatelong() {
        return datelong;
    }

    public void setDatelong(long datelong) {
        this.datelong = datelong;
    }

    public ChunkMain getChunkmain() {
        return chunkmain;
    }

    public void setChunkmain(ChunkMain chunkmain) {
        this.chunkmain = chunkmain;
    }

    public ChunkWeather getChunkweather() {
        return chunkweather;
    }

    public void setChunkweather(ChunkWeather chunkweather) {
        this.chunkweather = chunkweather;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public ChunkWind getChunkwind() {
        return chunkwind;
    }

    public void setChunkwind(ChunkWind chunkwind) {
        this.chunkwind = chunkwind;
    }

    public String getSyspod() {
        return syspod;
    }

    public void setSyspod(String syspod) {
        this.syspod = syspod;
    }

    public String getDatetext() {
        return datetext;
    }

    public void setDatetext(String datetext) {
        this.datetext = datetext;
    }
}

























