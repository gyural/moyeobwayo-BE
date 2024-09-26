package com.moyeobwayo.moyeobwayo.Domain.response;

import com.moyeobwayo.moyeobwayo.Domain.Party;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartyComplteResponse {
    private Party party;
    private String message;

}
