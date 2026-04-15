package com.Cts.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "conflict")
public class Conflict {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long conflictId;


        private Long scheduleId;
        private String ConflictType;

        public boolean getResolved() {
                return resolved;
        }

        public void setResolved(boolean resolved) {
                this.resolved = resolved;
        }

        private boolean resolved;


}


