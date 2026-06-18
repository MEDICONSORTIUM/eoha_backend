package com.eoha.eoha_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "zone")
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "parent_zone_id")
    private Integer parentZoneId;

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "boundary_polygon")
    private String boundaryPolygon;

    @Column(name = "base_population")
    private Integer basePopulation;

    @Column(name = "dominant_habitat_code")
    private String dominantHabitatCode;

    @Column(name = "external_zone_id")
    private String externalZoneId;

    @Column(name = "zone_type")
    private String zoneType;

    private Float latitude;

    private Float longitude;

    @Column(name = "population_density_per_km2")
    private Float populationDensityPerKm2;

    @Column(name = "municipality_name")
    private String municipalityName;

    @Column(name = "province_name")
    private String provinceName;

    public Zone() {}

    public Integer getZoneId() {
        return zoneId;
    }

    public Integer getParentZoneId() {
        return parentZoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getBoundaryPolygon() {
        return boundaryPolygon;
    }

    public Integer getBasePopulation() {
        return basePopulation;
    }

    public String getDominantHabitatCode() {
        return dominantHabitatCode;
    }

    public String getExternalZoneId() {
        return externalZoneId;
    }

    public String getZoneType() {
        return zoneType;
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public Float getPopulationDensityPerKm2() {
        return populationDensityPerKm2;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public void setParentZoneId(Integer parentZoneId) {
        this.parentZoneId = parentZoneId;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void setBoundaryPolygon(String boundaryPolygon) {
        this.boundaryPolygon = boundaryPolygon;
    }

    public void setBasePopulation(Integer basePopulation) {
        this.basePopulation = basePopulation;
    }

    public void setDominantHabitatCode(String dominantHabitatCode) {
        this.dominantHabitatCode = dominantHabitatCode;
    }

    public void setExternalZoneId(String externalZoneId) {
        this.externalZoneId = externalZoneId;
    }

    public void setZoneType(String zoneType) {
        this.zoneType = zoneType;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public void setPopulationDensityPerKm2(Float populationDensityPerKm2) {
        this.populationDensityPerKm2 = populationDensityPerKm2;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}