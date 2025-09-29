package com.openclassrooms.ycywapi.services.interfaces;

import com.openclassrooms.ycywapi.models.UserPrincipal;

/**
 * Service to generate Intercom Identity Verification artifacts.
 * Notes:
 * - This is NOT for Intercom Admin API calls.
 * - Use it to create the short-lived JWT or HMAC user_hash for the Intercom Messenger (secure mode).
 */
public interface IIntercomService {

    /**
     * Generate a short-lived JWT for Intercom Identity Verification, signed with the
     * Intercom Identity Verification Secret using HS256.
     *
     * @param user the authenticated user
     * @return compact JWT string
     * @throws IllegalStateException if Intercom secret is not configured
     */
    String generateIdentityVerificationJwt(UserPrincipal user);

    /**
     * Generate the HMAC-SHA256 user_hash value for Intercom Identity Verification.
     * Intercom will accept either this value (with user_id/email) or the JWT above.
     *
     * @param identifier the user identifier (prefer user_id; can be email)
     * @return lowercase hex of HMAC-SHA256(secret, identifier)
     * @throws IllegalStateException if Intercom secret is not configured
     */
    String generateUserHash(String identifier);
}
