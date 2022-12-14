package com.ra.bioskop.repository.query;

public class ScheduleQuery {
    public static final String SCHEDULE_DETAIL_BY_DATE = "SELECT " +
            "new com.ra.bioskop.dto.model.ScheduleDTO(s.filmTitle, st.name, s.startTime, s.endTime, s.showAt, s.price) "+
            "FROM Schedule AS s "+
            "JOIN s.studio AS st " +
            "WHERE s.showAt = :showAt";
}