package com.kits.tool.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Holidays")
public class HolidayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "holidayDate")
    private LocalDate holidayDate;

    @Column(name = "detail", length = 100)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private HolidayType type;

    public enum HolidayType {
        Public,
        Company,
        Others
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public HolidayType getType() {
        return type;
    }

    public void setType(HolidayType type) {
        this.type = type;
    }
}
