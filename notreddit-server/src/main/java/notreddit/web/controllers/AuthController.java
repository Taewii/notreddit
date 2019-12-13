package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notreddit.auth.JwtTokenProvider;
import notreddit.domain.models.requests.SignInRequest;
import notreddit.domain.models.requests.SignUpRequest;
import notreddit.domain.models.responses.api.JwtAuthenticationResponse;
import notreddit.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpRequest model) {
        return userService.register(model);
    }

    @Transactional
    @PreAuthorize("isAnonymous()")
    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody SignInRequest model) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        model.getUsernameOrEmail(),
                        model.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        log.info("User logged in: {}", model.getUsernameOrEmail());
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
