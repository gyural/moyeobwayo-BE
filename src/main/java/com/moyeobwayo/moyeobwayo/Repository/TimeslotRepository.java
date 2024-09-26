package com.moyeobwayo.moyeobwayo.Repository;

import com.moyeobwayo.moyeobwayo.Domain.Timeslot;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TimeslotRepository extends JpaRepository<Timeslot, Integer> {
    @Query("SELECT t.userEntity FROM Timeslot t WHERE t.date.date_id = :dateId AND :selectedTime BETWEEN t.selected_start_time AND t.selected_end_time")
    List<UserEntity> findUsersByDateAndTime(
            @Param("dateId") int dateId,
            @Param("selectedTime") Date selectedTime);
}
