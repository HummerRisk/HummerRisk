package com.hummer.system.controller;

import com.hummer.system.service.HistoryService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping(value = "history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

}
