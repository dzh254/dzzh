package com.cybersec.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库文档 — t_knowledge_document
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_knowledge_document")
public class KnowledgeDocument {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文档标题 */
    private String title;

    /** 文档内容 */
    private String content;

    /** 文档类型: SOP/CVE/ATTACK_CASE/THREAT_INTEL/SECURITY_REPORT/BEST_PRACTICE */
    @TableField("doc_type")
    private String docType;

    /** Milvus向量ID */
    @TableField("vector_id")
    private String vectorId;

    /** 分块数量 */
    @TableField("chunk_count")
    private Integer chunkCount;

    /** 索引状态: PENDING/INDEXING/READY/FAILED */
    private String status;

    /** 元数据 (JSON) */
    private String metadata;

    /** 创建时间 */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
