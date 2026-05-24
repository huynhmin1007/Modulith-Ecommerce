package com.dev.minn.ecommerce.common.application.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private int code = 200;

    private String message;
    private T data;

    @Builder.Default
    private Metadata meta = new Metadata();

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Metadata {
        @Builder.Default
        private Instant timestamp = Instant.now();

        @Builder.Default
        private String traceId = UUID.randomUUID().toString();

        @JsonIgnore
        @Builder.Default
        private Map<String, Object> extra = new HashMap<>();

        @JsonAnyGetter
        public Map<String, Object> getExtra() {
            return this.extra;
        }

        @JsonAnySetter
        public void add(String key, Object value) {
            if (extra == null)
                extra = new HashMap<>();

            this.extra.put(key, value);
        }
    }

    public static <T> ApiResponse<T> success(String message, T data, Map<String, Object> metaExtra) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .meta(Metadata.builder()
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .extra(metaExtra)
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, T data, Map<String, Object> metaExtra) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .meta(Metadata.builder()
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .extra(metaExtra)
                        .build())
                .build();
    }
}
