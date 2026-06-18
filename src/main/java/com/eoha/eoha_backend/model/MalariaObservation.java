package com.eoha.eoha_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "malaria_observation")
public class MalariaObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "env_record_id")
    private Integer envRecordId;

    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "date_recorded")
    private LocalDate dateRecorded;

    @Column(name = "lst_surface_temp_c")
    private Float lstSurfaceTempC;

    @Column(name = "soil_moisture_pct")
    private Float soilMoisturePct;

    @Column(name = "ndvi_vegetation_index")
    private Float ndviVegetationIndex;

    @Column(name = "ndwi_water_index")
    private Float ndwiWaterIndex;

    @Column(name = "air_temp_c")
    private Float airTempC;

    @Column(name = "agric_percentage")
    private Float agricPercentage;

    public MalariaObservation() {}

    public Integer getEnvRecordId() {
        return envRecordId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public LocalDate getDateRecorded() {
        return dateRecorded;
    }

    public Float getLstSurfaceTempC() {
        return lstSurfaceTempC;
    }

    public Float getSoilMoisturePct() {
        return soilMoisturePct;
    }

    public Float getNdviVegetationIndex() {
        return ndviVegetationIndex;
    }

    public Float getNdwiWaterIndex() {
        return ndwiWaterIndex;
    }

    public Float getAirTempC() {
        return airTempC;
    }

    public Float getAgricPercentage() {
        return agricPercentage;
    }

    public void setEnvRecordId(Integer envRecordId) {
        this.envRecordId = envRecordId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public void setDateRecorded(LocalDate dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public void setLstSurfaceTempC(Float lstSurfaceTempC) {
        this.lstSurfaceTempC = lstSurfaceTempC;
    }

    public void setSoilMoisturePct(Float soilMoisturePct) {
        this.soilMoisturePct = soilMoisturePct;
    }

    public void setNdviVegetationIndex(Float ndviVegetationIndex) {
        this.ndviVegetationIndex = ndviVegetationIndex;
    }

    public void setNdwiWaterIndex(Float ndwiWaterIndex) {
        this.ndwiWaterIndex = ndwiWaterIndex;
    }

    public void setAirTempC(Float airTempC) {
        this.airTempC = airTempC;
    }

    public void setAgricPercentage(Float agricPercentage) {
        this.agricPercentage = agricPercentage;
    }
}