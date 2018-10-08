package com.appolica.weatherify.android.model;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by aleksandar on 24.08.16.
 */
@DatabaseTable(tableName = "forecast_data_block")
public class ForecastDataBlock {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String summary;
    @ForeignCollectionField
    private Collection<ForecastDataPoint> data;

    public ForecastDataBlock() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ForecastDataPoint> getData() {
        ArrayList<ForecastDataPoint> dataList = new ArrayList<>();
        dataList.addAll(this.data);
        return dataList;
    }

    public List<ForecastDataPoint> getDataLimited(int hours) {
        return Stream.of(this.data)
                .limit(hours)
                .collect(Collectors.toList());
    }

    public void setData(Collection<ForecastDataPoint> data) {
        this.data = data;
    }
}
