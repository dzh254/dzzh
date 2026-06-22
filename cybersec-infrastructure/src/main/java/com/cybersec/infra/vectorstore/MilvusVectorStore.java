package com.cybersec.infrastructure.vectorstore;

import io.milvus.client.MilvusServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Milvus 向量存储实现 (桩 — Phase 2 完整实现)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MilvusVectorStore implements VectorStoreService {

    private final MilvusServiceClient milvusClient;

    @Override
    public void insert(String collection, String id, float[] embedding, Map<String, Object> metadata) {
        // TODO: Milvus insert implementation — Phase 2
        log.debug("Milvus insert stub: collection={}, id={}, dims={}", collection, id, embedding.length);
    }

    @Override
    public List<VectorSearchResult> search(String collection, float[] queryEmbedding, int topK,
                                            Map<String, String> filterMetadata) {
        // TODO: Milvus search implementation — Phase 2
        log.debug("Milvus search stub: collection={}, topK={}", collection, topK);
        return Collections.emptyList();
    }

    @Override
    public void delete(String collection, String id) {
        // TODO: Milvus delete implementation — Phase 2
        log.debug("Milvus delete stub: collection={}, id={}", collection, id);
    }

    @Override
    public boolean collectionExists(String collection) {
        // TODO: Milvus check — Phase 2
        return true;
    }

    @Override
    public void createCollection(String collection, int dimension) {
        // TODO: Milvus create collection — Phase 2
        log.info("Milvus create collection stub: name={}, dim={}", collection, dimension);
    }
}
