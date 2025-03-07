-- 创建序列
CREATE SEQUENCE IF NOT EXISTS public.offset_case_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999
    START WITH 1
    CACHE 1;

-- 创建案件表
CREATE TABLE IF NOT EXISTS public.offset_case (
    case_no VARCHAR(12) PRIMARY KEY,
    case_date TIMESTAMP,
    case_type VARCHAR(10),
    organization_id VARCHAR(10),
    ct_tenant_id VARCHAR(50),
    insured_name VARCHAR(100),
    insured_id VARCHAR(20),
    char_no VARCHAR(20),
    admission_no VARCHAR(20),
    admission_date DATE,
    send_date TIMESTAMP,
    calculated_amount VARCHAR(20),
    auth_agreement VARCHAR(1),
    status_code VARCHAR(3),
    update_id VARCHAR(20),
    update_tenant VARCHAR(50),
    update_time TIMESTAMP
); 