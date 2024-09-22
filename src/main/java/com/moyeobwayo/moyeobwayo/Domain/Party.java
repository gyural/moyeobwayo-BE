package com.moyeobwayo.moyeobwayo.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int party_id;

    private int target_num;
    private int current_num;
    private String party_name;
    private String party_description;
    private Date start_date;
    private Date end_date;
    private Date decision_date;

    @OneToMany(mappedBy = "party")
    private List<Alarm> alarms;

    @OneToMany(mappedBy = "party")
    private List<DateEntity> dates;
}
