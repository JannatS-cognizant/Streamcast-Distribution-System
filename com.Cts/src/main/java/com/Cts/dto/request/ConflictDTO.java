package com.Cts.dto.request;

import com.Cts.entity.Schedule;

public class ConflictDTO {

    public Long conflictId;
    public Long scheduleId;
    public String conflictType;
    public boolean resolved;

    public boolean getResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
