package com.moyeobwayo.moyeobwayo.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class Timeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int slot_id;

    private Date selected_start_time;
    private Date selected_end_time;

    @ManyToOne
    @JoinColumn(name = "date_id")
    private DateEntity date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
}
