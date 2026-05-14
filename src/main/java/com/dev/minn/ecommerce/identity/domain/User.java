package com.dev.minn.ecommerce.identity.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    String id;
    String username;
    String firstName;
    String lastName;
    String email;

    boolean enable;
    Instant created;
}
