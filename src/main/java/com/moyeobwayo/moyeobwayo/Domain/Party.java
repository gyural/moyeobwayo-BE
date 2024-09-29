package com.moyeobwayo.moyeobwayo.Domain;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    @Column(name = "end_date") // jpa를 통한 삭제를 위해(카멜형으로)
    private Date endDate;
    private Date decision_date;

    @OneToMany(mappedBy = "party")
    private List<Alarm> alarms;

    // @OneToMany(mappedBy = "party")
    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DateEntity> dates;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
