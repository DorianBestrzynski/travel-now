package com.zpi.transportservice.lufthansa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class LufthansaKey {

    private String lufthansaToken;

    public LufthansaKey(){
        this.lufthansaToken = "";
    }



}
