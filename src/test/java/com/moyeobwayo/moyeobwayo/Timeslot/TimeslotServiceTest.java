package com.moyeobwayo.moyeobwayo.Timeslot;

import com.moyeobwayo.moyeobwayo.Domain.DateEntity;
import com.moyeobwayo.moyeobwayo.Domain.Timeslot;
import com.moyeobwayo.moyeobwayo.Domain.UserEntity;
import com.moyeobwayo.moyeobwayo.Repository.DateEntityRepsitory;
import com.moyeobwayo.moyeobwayo.Repository.TimeslotRepository;
import com.moyeobwayo.moyeobwayo.Repository.UserEntityRepository;
import com.moyeobwayo.moyeobwayo.Service.TimeslotService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TimeslotServiceTest {

    @Autowired
    private TimeslotService timeslotService;

    @MockBean
    private TimeslotRepository timeslotRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private DateEntityRepsitory dateEntityRepsitory;

    @Test
    public void testCreateTimeslot_WithValidData() {
        UserEntity user = new UserEntity();
        user.setUser_id(9);

        DateEntity date = new DateEntity();
        date.setDate_id(2);

        Timeslot timeslot = new Timeslot();
        timeslot.setUserEntity(user);
        timeslot.setDate(date);

        // 시작 및 종료 시간 설정
        Date startTime = new Date(); // 테스트를 위한 현재 시간
        Date endTime = new Date(startTime.getTime() + 3600000); // 1시간 후

        timeslot.setSelected_start_time(startTime);
        timeslot.setSelected_end_time(endTime);

        Mockito.when(userEntityRepository.findById(9L)).thenReturn(Optional.of(user));
        Mockito.when(dateEntityRepsitory.findById(2)).thenReturn(Optional.of(date));
        Mockito.when(timeslotRepository.save(Mockito.any(Timeslot.class))).thenReturn(timeslot);

        Timeslot createdTimeslot = timeslotService.createTimeslot(timeslot);

        assertNotNull(createdTimeslot);
        assertEquals(9, createdTimeslot.getUserEntity().getUser_id());
        assertEquals(2, createdTimeslot.getDate().getDate_id());
    }
}
