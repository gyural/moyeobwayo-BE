package com.moyeobwayo.moyeobwayo.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int date_id;

    private java.util.Date selected_date;

    @ManyToOne
    @JoinColumn(name = "party_iD")
    private Party party;

    @OneToMany(mappedBy = "date")
    private List<Timeslot> timeslots;
}
