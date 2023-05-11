package com.shai.to_do;

import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
public class Context {
    private int idCounter;
    private int requestCounter;
    private long currentRequestStartTime;

    public Context() {
        idCounter = 0;
        requestCounter = 0;
    }

    public int getIdCounterAndIncrement() {
        idCounter++;
        return idCounter;
    }

    public void initLogsInfo() {
        setCurrentRequestStartTime(System.currentTimeMillis());
        incrementRequestCounter();
    }

    private void incrementRequestCounter() {
        requestCounter++;
    }

    public int getRequestCounter() {
        return requestCounter;
    }

    private void setCurrentRequestStartTime(long currentRequestStartTime) {
        this.currentRequestStartTime = currentRequestStartTime;
    }

    public long getCurrentRequestStartTime() {
        return currentRequestStartTime;
    }
}
