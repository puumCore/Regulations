package com.r_n_m.kws.Regulations._interface;

import com.r_n_m.kws.Regulations._entities.Visit;
import com.r_n_m.kws.Regulations._enum.Session;

import java.sql.Timestamp;
import java.util.List;

public interface VisitOps {

    Visit create_visit();

    List<Visit> get_visits();

    List<Visit> get_visits(Session session);

    List<Visit> get_visits(String param);

    List<Visit> get_visits(Timestamp from, Timestamp to);

}
