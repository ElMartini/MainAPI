package com.example.mainapi.model;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
public class ActionStatus {
    String aid;
    String status;
    String query;
    LocalDateTime lastChange;

    @Override
    public String toString() {
        return "ActionStatus{" +
                "aid='" + aid + '\'' +
                ", status='" + status + '\'' +
                ", query='" + query + '\'' +
                ", lastChange=" + lastChange +
                '}';
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public LocalDateTime getLastChange() {
        return lastChange;
    }

    public void setLastChange(LocalDateTime lastChange) {
        this.lastChange = lastChange;
    }
}

