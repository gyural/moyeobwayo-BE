package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.Timeslot;
import com.moyeobwayo.moyeobwayo.Repository.TimeslotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeslotService {

    @Autowired
    private TimeslotRepository timeslotRepository;

    // 모든 유저의 타임슬롯을 조회
    public List<Timeslot> getAllTimeslots() {
        return timeslotRepository.findAll();
    }

    // 새로운 타임슬롯을 생성
    public Timeslot createTimeslot(Timeslot timeslot) {
        return timeslotRepository.save(timeslot);
    }

    // 타임슬롯 수정
    public Timeslot updateTimeslot(int id, Timeslot updatedTimeslot) {
        Timeslot existingTimeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timeslot not found"));

        // 수정된 메서드로 호출
        existingTimeslot.setSelected_start_time(updatedTimeslot.getSelected_start_time());
        existingTimeslot.setSelected_end_time(updatedTimeslot.getSelected_end_time());

        return timeslotRepository.save(existingTimeslot);
    }
}
