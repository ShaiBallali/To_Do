package com.shai.to_do;

import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
public class Context {
    private int idCounter;

    public Context() {
        idCounter = 0;
    }

    public int getIdCounterAndIncrement() {
        idCounter++;
        return idCounter;
    }

}
