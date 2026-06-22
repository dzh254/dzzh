package com.cybersec.infrastructure.vectorstore;

import java.util.List;
import java.util.Map;

/**
 * 向量存储服务接口
 */
public interface VectorStoreService {

    /** 单条插入 */
    void insert(String collection, String id, float[] embedding, Map<String, Object> metadata);

    /** 向量检索 */
    List<VectorSearchResult> search(String collection, float[] queryEmbedding, int topK,
                                     Map<String, String> filterMetadata);

    /** 删除向量 */
    void delete(String collection, String id);

    /** 集合是否存在 */
    boolean collectionExists(String collection);

    /** 创建集合 */
    void createCollection(String collection, int dimension);

    /**
     * 向量检索结果
     */
    record VectorSearchResult(String id, float score, Map<String, Object> metadata) {}
}
