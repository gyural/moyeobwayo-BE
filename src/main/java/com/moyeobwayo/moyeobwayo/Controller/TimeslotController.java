package com.moyeobwayo.moyeobwayo.Controller;

import com.moyeobwayo.moyeobwayo.Domain.Timeslot;
import com.moyeobwayo.moyeobwayo.Service.TimeslotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslot")
public class TimeslotController {

    @Autowired
    private TimeslotService timeslotService;

    // 같은 party_id를 가진 타임슬롯 조회 (특정 파티에 속한 타임슬롯)
    @GetMapping
    public ResponseEntity<List<Timeslot>> getAllTimeslots() {
        List<Timeslot> timeslots = timeslotService.getAllTimeslots();
        return ResponseEntity.ok(timeslots);
    }

    // 유저가 날짜 투표 (새로운 타임슬롯 생성)
    @PostMapping
    public ResponseEntity<Timeslot> createTimeslot(@RequestBody Timeslot timeslot) {
        Timeslot createdTimeslot = timeslotService.createTimeslot(timeslot);
        return ResponseEntity.status(201).body(createdTimeslot);
    }

    // 기존 타임슬롯 수정
    @PutMapping("/{timeslot_id}")
    public ResponseEntity<Timeslot> updateTimeslot(@PathVariable int timeslot_id, @RequestBody Timeslot timeslot) {
        Timeslot updatedTimeslot = timeslotService.updateTimeslot(timeslot_id, timeslot);
        return ResponseEntity.ok(updatedTimeslot);
    }
}
