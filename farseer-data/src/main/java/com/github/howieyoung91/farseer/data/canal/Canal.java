package com.github.howieyoung91.farseer.data.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/12 13:00]
 */
public class Canal {
    private InetSocketAddress server      = new InetSocketAddress("127.0.0.1", 11111);
    private String            destination = "example";
    private String            filter      = ".*\\..*";
    private int               batchSize   = 100;
    private String            username    = "";
    private String            password    = "";
    private CanalConnector    connector;

    public Canal connect() {
        connector = CanalConnectors.newSingleConnector(server, destination, username, password);
        connector.connect();
        return this;
    }

    public Canal disconnect() {
        connector.disconnect();
        return this;
    }

    public Canal subscribe() {
        connector.subscribe(filter);
        rollback();
        return this;
    }

    public Canal rollback() {
        connector.rollback();
        return this;
    }

    public List<CanalEntry.Entry> getNow() {
        // 获取指定数量的数据
        Message message = connector.getWithoutAck(batchSize);
        // 获取批量ID
        long batchId = message.getId();
        // 进行 batch id 的确认。确认之后，小于等于此 batchId 的 Message 都会被确认。
        connector.ack(batchId);

        return message.getEntries();
    }

    public List<CanalEntry.RowChange> get() {
        List<CanalEntry.RowChange> results = new ArrayList<>();
        Message                    message = connector.getWithoutAck(batchSize);
        long                       batchId = message.getId();
        connector.ack(batchId);
        List<CanalEntry.Entry> entries = message.getEntries();
        if (!entries.isEmpty()) {
            for (CanalEntry.Entry entry : entries) {
                if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                    || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                    continue;
                }
                try {
                    CanalEntry.RowChange     rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                    List<CanalEntry.RowData> rowData   = rowChange.getRowDatasList();
                    if (!rowData.isEmpty()) {
                        results.add(rowChange);
                    }
                }
                catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return results;
    }

    public void setServer(InetSocketAddress server) {
        this.server = server;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
