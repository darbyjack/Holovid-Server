package io.github.holovid.server.controller;

import io.github.holovid.server.HolovidServerApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class ResourcePackController {

    private final HolovidServerApplication server;

    public ResourcePackController(final HolovidServerApplication server) {
        this.server = server;
    }

    @GetMapping("resourcepack/download")
    public ResponseEntity<Void> downloadResourcePack(@RequestParam("videoUrl") final String videoUrl) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
