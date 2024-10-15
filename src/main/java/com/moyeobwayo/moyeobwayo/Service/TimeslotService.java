package com.moyeobwayo.moyeobwayo.Service;

import com.moyeobwayo.moyeobwayo.Domain.Timeslot;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Domain.DateEntity;
import com.moyeobwayo.moyeobwayo.Domain.dto.TimeslotResponseDTO;
import com.moyeobwayo.moyeobwayo.Repository.TimeslotRepository;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;
import com.moyeobwayo.moyeobwayo.Repository.DateEntityRepsitory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final UserEntityRepository userEntityRepository;
    private final DateEntityRepsitory dateEntityRepsitory;

    public TimeslotService(TimeslotRepository timeslotRepository, UserEntityRepository userEntityRepository, DateEntityRepsitory dateEntityRepsitory) {
        this.timeslotRepository = timeslotRepository;
        this.userEntityRepository = userEntityRepository;
        this.dateEntityRepsitory = dateEntityRepsitory;
    }

    // 특정 파티에 속한 타임슬롯 조회
    public List<TimeslotResponseDTO> getTimeslotsByPartyId(String partyId) {
        List<Timeslot> timeslots = timeslotRepository.findAllByPartyId(partyId);
        return timeslots.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 타임슬롯 생성
    public Timeslot createTimeslot(Timeslot timeslot) {
        if (timeslot.getUserEntity() == null || timeslot.getDate() == null) {
            throw new IllegalArgumentException("userEntity와 date는 필수 입력 항목입니다.");
        }

        if (timeslot.getSelected_start_time().after(timeslot.getSelected_end_time())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다.");
        }

        UserEntity user = userEntityRepository.findById((long) timeslot.getUserEntity().getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + timeslot.getUserEntity().getUser_id()));

        DateEntity date = dateEntityRepsitory.findById(timeslot.getDate().getDate_id())
                .orElseThrow(() -> new IllegalArgumentException("날짜를 찾을 수 없습니다: " + timeslot.getDate().getDate_id()));

        timeslot.setUserEntity(user);
        timeslot.setDate(date);

        return timeslotRepository.save(timeslot);
    }

    // 타임슬롯 수정
    public Timeslot updateTimeslot(int id, Timeslot updatedTimeslot) {
        Timeslot existingTimeslot = timeslotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("타임 슬롯을 찾을 수 없습니다."));

        if (updatedTimeslot.getSelected_start_time().after(updatedTimeslot.getSelected_end_time())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다.");
        }

        existingTimeslot.setSelected_start_time(updatedTimeslot.getSelected_start_time());
        existingTimeslot.setSelected_end_time(updatedTimeslot.getSelected_end_time());

        return timeslotRepository.save(existingTimeslot);
    }

    // 타임슬롯 삭제
    public void deleteTimeslot(int id) {
        if (!timeslotRepository.existsById(id)) {
            throw new RuntimeException("타임 슬롯을 찾을 수 없습니다.");
        }
        timeslotRepository.deleteById(id);
    }

    // Timeslot 객체를 TimeslotResponseDTO로 변환
    private TimeslotResponseDTO convertToDTO(Timeslot timeslot) {
        return new TimeslotResponseDTO(
                timeslot.getSlot_id(),
                timeslot.getSelected_start_time(),
                timeslot.getSelected_end_time(),
                timeslot.getUserEntity() != null ? timeslot.getUserEntity().getUser_id() : 0,
                timeslot.getDate() != null && timeslot.getDate().getParty() != null ? timeslot.getDate().getParty().getPartyId() : "0",
                timeslot.getDate() != null ? timeslot.getDate().getDate_id() : 0
        );
    }
}
