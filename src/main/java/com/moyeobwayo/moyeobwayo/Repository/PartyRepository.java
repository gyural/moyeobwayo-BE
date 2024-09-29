package com.moyeobwayo.moyeobwayo.Repository;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PartyRepository extends JpaRepository<Party, Integer> {
    List<Party> findByEndDateBefore(Date date);
}
