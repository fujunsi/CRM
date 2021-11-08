package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.service.ClueService;

public interface ClueDao {

    int save(Clue clue);

    Clue detail(String id);

    Clue getById(String clueId);

    int delete(String clueId);
}
