-- ============================================================
-- 通用网络安全智能体平台 — 数据库初始化脚本
-- Database: cybersec_platform (需先创建数据库)
-- ============================================================

CREATE TABLE IF NOT EXISTS t_alert (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id        VARCHAR(64)  NOT NULL UNIQUE COMMENT '源系统告警ID',
    source          VARCHAR(32)  NOT NULL COMMENT '来源: SIEM/EDR/WAF/NDR/DLP',
    title           VARCHAR(512) NOT NULL COMMENT '告警标题',
    description     TEXT         COMMENT '告警描述',
    severity        VARCHAR(16)  NOT NULL DEFAULT 'INFO' COMMENT '严重级别: CRITICAL/HIGH/MEDIUM/LOW/INFO',
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/ANALYZING/ANALYZED/DISMISSED/RESOLVED',
    raw_data        JSON         COMMENT '原始告警JSON',
    normalized_data JSON         COMMENT 'ECS/OCSF标准化后JSON',
    source_ip       VARCHAR(64)  COMMENT '源IP',
    dest_ip         VARCHAR(64)  COMMENT '目标IP',
    attack_type     VARCHAR(128) COMMENT '攻击类型',
    attck_id        VARCHAR(32)  COMMENT 'ATT&CK ID, 如 T1190',
    risk_score      DECIMAL(5,2) COMMENT '风险评分',
    confidence      DECIMAL(5,2) COMMENT '置信度',
    dedup_key       VARCHAR(256) COMMENT '去重键',
    assigned_to     VARCHAR(64)  COMMENT '指派分析师',
    resolved_at     DATETIME     COMMENT '处置时间',
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_source (source),
    INDEX idx_severity (severity),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_dedup_key (dedup_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='告警表';

CREATE TABLE IF NOT EXISTS t_alert_analysis (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id            BIGINT NOT NULL COMMENT '关联告警ID',
    conclusion          TEXT         COMMENT '分析结论文本',
    confidence          DECIMAL(5,2) COMMENT '分析置信度',
    attack_type         VARCHAR(128) COMMENT '确定的攻击类型',
    attck_id            VARCHAR(32)  COMMENT 'ATT&CK 技战术ID',
    risk_level          VARCHAR(16)  COMMENT '风险等级',
    evidence            JSON         COMMENT '证据列表JSON数组',
    recommended_actions JSON         COMMENT '建议处置动作JSON数组',
    reasoning_chain     TEXT         COMMENT '推理链文本',
    model_used          VARCHAR(64)  COMMENT '使用的LLM模型',
    tokens_used         INT          COMMENT '消耗Token数',
    latency_ms          BIGINT       COMMENT '分析延迟(ms)',
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_alert_id (alert_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='告警分析结果表';

CREATE TABLE IF NOT EXISTS t_knowledge_document (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(512) NOT NULL COMMENT '文档标题',
    content     LONGTEXT     COMMENT '文档内容',
    doc_type    VARCHAR(32)  NOT NULL COMMENT '类型: SOP/CVE/ATTACK_CASE/THREAT_INTEL/SECURITY_REPORT/BEST_PRACTICE',
    vector_id   VARCHAR(128) COMMENT 'Milvus向量ID',
    chunk_count INT DEFAULT 1 COMMENT '分块数量',
    status      VARCHAR(16) DEFAULT 'PENDING' COMMENT '状态: PENDING/INDEXING/READY/FAILED',
    metadata    JSON         COMMENT '元数据JSON',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_doc_type (doc_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档表';

CREATE TABLE IF NOT EXISTS t_guardrail_audit (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      VARCHAR(64)  COMMENT '操作用户ID',
    operation    VARCHAR(128) NOT NULL COMMENT '操作描述',
    level        VARCHAR(8)   NOT NULL COMMENT '操作级别: L0/L1/L2/L3',
    target       VARCHAR(256) COMMENT '操作目标',
    result       VARCHAR(16)  NOT NULL COMMENT '结果: ALLOWED/BLOCKED/PENDING_APPROVAL',
    reasoning    TEXT         COMMENT '判断理由',
    agent_output TEXT         COMMENT '智能体原始输出',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_level (level),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='护栏审计日志表';

CREATE TABLE IF NOT EXISTS t_threat_intel (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    ioc_type     VARCHAR(16)  NOT NULL COMMENT 'IOC类型: IP/DOMAIN/HASH/URL',
    ioc_value    VARCHAR(512) NOT NULL COMMENT 'IOC值',
    threat_level VARCHAR(16)  COMMENT '威胁级别',
    tags         VARCHAR(512) COMMENT '标签',
    family       VARCHAR(128) COMMENT '恶意软件家族',
    source       VARCHAR(128) COMMENT '情报来源',
    first_seen   DATETIME     COMMENT '首次发现时间',
    last_seen    DATETIME     COMMENT '最近发现时间',
    confidence   INT          COMMENT '置信度',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_ioc (ioc_type, ioc_value),
    INDEX idx_ioc_type (ioc_type),
    INDEX idx_threat_level (threat_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='威胁情报IOC表';

-- ============================================================
-- 样例数据
-- ============================================================
INSERT IGNORE INTO t_alert (alert_id, source, title, description, severity, status, raw_data, source_ip, dest_ip, attack_type, attck_id) VALUES
('WAF-001', 'WAF', 'SQL注入攻击检测', '检测到针对/login.php的SQL注入攻击，payload包含OR 1=1--', 'HIGH', 'PENDING',
 '{"src_ip":"10.0.1.100","payload":"'' OR 1=1--","rule_id":"SQLI-001","action":"block","http_method":"POST","url_path":"/login.php"}',
 '10.0.1.100', '192.168.1.50', 'SQL Injection', 'T1190'),

('WAF-002', 'WAF', 'XSS跨站脚本攻击', '检测到存储型XSS攻击，目标页面/user/profile', 'MEDIUM', 'PENDING',
 '{"src_ip":"10.0.2.50","payload":"<script>alert(1)</script>","rule_id":"XSS-001","action":"block","http_method":"POST","url_path":"/user/profile"}',
 '10.0.2.50', '192.168.1.50', 'Cross-Site Scripting', 'T1059.007'),

('SIEM-001', 'SIEM', 'SSH暴力破解攻击', '检测到来自外部IP的SSH暴力破解攻击，30分钟内登录失败200次', 'HIGH', 'PENDING',
 '{"src_ip":"45.33.32.156","dest_ip":"192.168.1.10","event_name":"SSH Brute Force","rule_name":"SSH Auth Failure Threshold","host":"web-server-01"}',
 '45.33.32.156', '192.168.1.10', 'Brute Force', 'T1110'),

('EDR-001', 'EDR', '可疑进程执行PowerShell', '检测到svchost.exe调用了powershell.exe执行编码命令，疑似无文件攻击', 'HIGH', 'PENDING',
 '{"hostname":"WIN-DC01","process_name":"powershell.exe","process_id":4521,"parent_process":"svchost.exe","command_line":"powershell.exe -enc SQBFA...","file_hash":"abc123def456"}',
 NULL, '192.168.1.100', 'Suspicious Process', 'T1059.001');
