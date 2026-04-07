package com.example.instgrania_sql.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("pipeline_executions")
public class PipelineExecution {

    @Id
    private Long id;

    private String username;
    private String status;           // SUCCESS, PARTIAL, FAILED
    private Integer postsFetched;
    private Integer postsSaved;
    private String errorMessage;
    private LocalDateTime executedAt;
}
