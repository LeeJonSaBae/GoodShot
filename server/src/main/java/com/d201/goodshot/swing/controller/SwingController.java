package com.d201.goodshot.swing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swings")
@Tag(name = "swing")
public class SwingController {
}
