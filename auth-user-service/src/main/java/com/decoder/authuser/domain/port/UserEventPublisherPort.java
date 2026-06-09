package com.decoder.authuser.domain.port;

import com.decoder.authuser.domain.model.UserModel;

public interface UserEventPublisherPort {
    void publishUserCreated(UserModel user);
    void publishUserUpdated(UserModel user);
    void publishUserDeleted(UserModel user);
}
