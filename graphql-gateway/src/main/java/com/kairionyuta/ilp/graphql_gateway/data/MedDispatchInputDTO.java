package com.kairionyuta.ilp.graphql_gateway.data;

import java.time.LocalDate;
import java.time.LocalTime;

public class MedDispatchInputDTO {
    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private RequirementsInputDTO requirements;
    private DeliveryInputDTO delivery;


    public MedDispatchInputDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public RequirementsInputDTO getRequirements() { return requirements; }
    public void setRequirements(RequirementsInputDTO requirements) { this.requirements = requirements; }

    public DeliveryInputDTO getDelivery() { return delivery; }
    public void setDelivery(DeliveryInputDTO delivery) { this.delivery = delivery; }
    
}
