package com.ilp.restapi.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class MedDispatchRecDTO {

    @NotNull
    private Integer id;

    private LocalDate date;
    private LocalTime time;

    @Valid
    @NotNull
    private RequirementsDTO requirements;

    @Valid
    @NotNull
    private LngLatDTO delivery;

    public MedDispatchRecDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public RequirementsDTO getRequirements() {
        return requirements;
    }

    public void setRequirements(RequirementsDTO requirements) {
        this.requirements = requirements;
    }

    public LngLatDTO getDelivery() {
        return delivery;
    }

    public void setDelivery(LngLatDTO delivery) {
        this.delivery = delivery;
    }
}
