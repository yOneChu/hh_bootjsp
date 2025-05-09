package com.kyhslam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VaultController {


    @GetMapping("/vault/searchVaultData")
    public String data() {

        return"vault/searchVaultData";
    }
}
