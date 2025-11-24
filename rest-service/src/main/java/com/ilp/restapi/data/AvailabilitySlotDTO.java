package com.ilp.restapi.data;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class AvailabilitySlotDTO {

    private DayOfWeek dayOfWeek;
    private LocalTime from;
    private LocalTime until;

    public AvailabilitySlotDTO() {
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getUntil() {
        return until;
    }

    public void setUntil(LocalTime until) {
        this.until = until;
    }
}
