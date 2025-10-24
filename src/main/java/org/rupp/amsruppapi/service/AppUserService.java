package org.rupp.amsruppapi.service;

import org.rupp.amsruppapi.model.entity.AppUser;
import org.rupp.amsruppapi.model.request.AppUserRequest;
import org.rupp.amsruppapi.model.response.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
//    AppUserResponse register(AppUserRequest request);
    AppUser getUserByEmail(String email);
}
