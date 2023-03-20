package vn.project.nfc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.project.nfc.request.UserRequest;
import vn.project.nfc.response.GlobalResponse;
import vn.project.nfc.service.NfcService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/nfc")
public class NfcController {

    private final NfcService nfcService;

    @PostMapping("/create")
    public ResponseEntity<GlobalResponse<Object>> create(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(nfcService.create(userRequest));
    }

    @GetMapping("/get-user")
    public ResponseEntity<GlobalResponse<Object>> getUserFromUUID (@RequestParam(name = "uuid", required = false) String uuid) {
        return ResponseEntity.ok(nfcService.getUserFromUUID(uuid));
    }
}
