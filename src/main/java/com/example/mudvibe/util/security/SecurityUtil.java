package com.example.mudvibe.util.security;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.example.mudvibe.common.exception.UnauthenticatedUserException;

@Component
public class SecurityUtil {
	
	public static final String PLAYER_ID_ATTRIBUTE = "playerId";

    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	public UUID getPlayerId() {
		return getPlayerIdMaybe()
				.orElseThrow(() -> new UnauthenticatedUserException("User is not authenticated."));
	}
	
	public Optional<UUID> getPlayerIdMaybe() {
		return getPlayerId(SecurityContextHolder.getContext().getAuthentication());
	}
	
	public Optional<UUID> getPlayerId(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.empty();
		}
		return extractPlayerId(authentication);
	}

	public Optional<UUID> getPlayerId(Principal principal) {
		if (principal instanceof Authentication authentication) {
			return getPlayerId(authentication);
		}
		return parseUuid(principal != null ? principal.getName() : null);
	}
	
	public Optional<UUID> extractPlayerId(WebSocketSession session) {
		if (session == null) {
			return Optional.empty();
		}

		Object attributeValue = session.getAttributes().get(PLAYER_ID_ATTRIBUTE);
		if (attributeValue instanceof UUID uuid) {
			return Optional.of(uuid);
		}

		Principal principal = session.getPrincipal();
		return getPlayerId(principal);
	}
	
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	private Optional<UUID> extractPlayerId(Authentication authentication) {
		if (authentication instanceof JwtAuthenticationToken jwtAuth) {
			return parseUuid(jwtAuth.getToken().getSubject());
		}
		
		if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
			if (oauthToken.getPrincipal() instanceof OidcUser oidcUser) {
				return parseUuid(oidcUser.getSubject());
			}
			
			Object subValue = oauthToken.getPrincipal().getAttributes().get("sub");
			return parseUuid(subValue != null ? subValue.toString() : null);
		}
		
		Object principal = authentication.getPrincipal();
		if (principal instanceof Jwt jwt) {
			return parseUuid(jwt.getSubject());
		}
		
		return Optional.empty();
	}
	
	private Optional<UUID> parseUuid(String candidate) {
		if (candidate == null || candidate.isBlank()) {
			return Optional.empty();
		}
		
		try {
			return Optional.of(UUID.fromString(candidate.trim()));
		} catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
	}
	
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/

}
