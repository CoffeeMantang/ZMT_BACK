package com.coffeemantang.ZMT_BACK.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

// 파일 업로드 및 다운로드를 위한 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties
public class FileDTO {
    private String location;
}
